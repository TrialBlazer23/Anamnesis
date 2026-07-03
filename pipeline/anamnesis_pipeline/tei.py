"""Fetch and parse Perseus canonical-greekLit TEI into passages.

The CTS textpart walker is generic: it descends nested
`<div type="textpart">` elements (book / chapter / section / …) and emits one
Passage per leaf, building the reference from the chain of `@n` values. Verse
editions (Homer et al.) put `<l n="…">` line elements — sometimes wrapped in
`<q>` for quoted speech — inside the deepest textpart div; each line becomes
its own passage so refs align with CTS URNs like `…:1.611` (and with the
per-line recitation audio packs).
"""

from __future__ import annotations

import re
from dataclasses import dataclass, field
from pathlib import Path

import httpx
from lxml import etree

from .normalize import nfc, strip_diacritics

TEI_NS = "http://www.tei-c.org/ns/1.0"
_NS = {"t": TEI_NS}

RAW_BASE = "https://raw.githubusercontent.com/PerseusDL/canonical-greekLit/master/data"


@dataclass
class Passage:
    cts_urn: str
    ref: str
    levels: list[str]
    greek: str
    search_key: str = ""
    translation: str | None = None

    def __post_init__(self) -> None:
        if not self.search_key:
            self.search_key = strip_diacritics(self.greek)


def source_url(work: str, edition: str) -> str:
    """Build the raw TEI URL for a CTS work id like ``tlg0562.tlg001``."""
    textgroup, work_part = work.split(".", 1)
    return f"{RAW_BASE}/{textgroup}/{work_part}/{work}.{edition}.xml"


def fetch_tei(work: str, edition: str, cache_dir: Path | None = None) -> str:
    """Fetch TEI XML, caching to ``cache_dir`` to avoid refetching."""
    url = source_url(work, edition)
    if cache_dir is not None:
        cache_dir.mkdir(parents=True, exist_ok=True)
        cached = cache_dir / f"{work}.{edition}.xml"
        if cached.exists():
            return cached.read_text(encoding="utf-8")
        text = httpx.get(url, timeout=60, follow_redirects=True).raise_for_status().text
        cached.write_text(text, encoding="utf-8")
        return text
    return httpx.get(url, timeout=60, follow_redirects=True).raise_for_status().text


def _normalize_ws(text: str) -> str:
    return re.sub(r"\s+", " ", text).strip()


def _passage_text(leaf: etree._Element) -> str:
    """Concatenated, whitespace-normalized text under a leaf textpart."""
    return _normalize_ws("".join(leaf.itertext()))


def parse_passages(tei: str | bytes) -> list[Passage]:
    """Parse TEI into leaf passages with CTS URNs and search keys."""
    data = tei.encode("utf-8") if isinstance(tei, str) else tei
    root = etree.fromstring(data)

    edition = root.find(".//t:body/t:div[@type='edition']", _NS)
    if edition is None:
        raise ValueError("No <div type='edition'> found — not a CTS edition TEI?")
    base_urn = edition.get("n")
    if not base_urn:
        raise ValueError("Edition div is missing its @n CTS URN")

    passages: list[Passage] = []

    def walk(elem: etree._Element, levels: list[str]) -> None:
        children = elem.findall("t:div[@type='textpart']", _NS)
        if not children:
            return
        for child in children:
            n = child.get("n")
            if n is None:
                continue
            chain = levels + [n]
            grandchildren = child.findall("t:div[@type='textpart']", _NS)
            if grandchildren:
                walk(child, chain)
                continue
            # Verse leaf: one passage per <l> (descendant axis catches lines
            # wrapped in <q> quoted speech); prose leaf: the div's whole text.
            lines = child.findall(".//t:l", _NS)
            if lines:
                for line in lines:
                    line_n = line.get("n")
                    if line_n is None:
                        continue
                    line_chain = chain + [line_n]
                    ref = ".".join(line_chain)
                    greek = nfc(_passage_text(line))
                    if not greek:
                        continue
                    passages.append(
                        Passage(
                            cts_urn=f"{base_urn}:{ref}",
                            ref=ref,
                            levels=line_chain,
                            greek=greek,
                        )
                    )
            else:
                ref = ".".join(chain)
                greek = nfc(_passage_text(child))
                if not greek:
                    continue
                passages.append(
                    Passage(cts_urn=f"{base_urn}:{ref}", ref=ref, levels=chain, greek=greek)
                )

    walk(edition, [])
    if not passages:
        raise ValueError("No leaf textpart passages found in edition")
    return passages
