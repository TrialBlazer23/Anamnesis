"""Fetch and parse the Middle Liddell (Intermediate Greek-English Lexicon).

Source: PerseusDL/canonical-pdlrefwk, `viaf66541464.001.perseus-eng1.xml`
(single ~20 MB TEI P4 file; CC BY-SA 4.0 per the repo README — credit Perseus,
keep the availability statement, offer modifications back; the file itself was
verified to carry no embedded restrictions). Unicode polytonic headwords in
`<orth>`, curated one-line glosses in `<trans><tr>` — the student's lexicon,
ideal for tap-to-lookup popups.
"""

from __future__ import annotations

import re
from dataclasses import dataclass
from pathlib import Path

import httpx
from lxml import etree

from .normalize import nfc, strip_diacritics

ML_URL = (
    "https://raw.githubusercontent.com/PerseusDL/canonical-pdlrefwk/master/"
    "data/viaf66541464/001/viaf66541464.001.perseus-eng1.xml"
)

# Entities the P4 DTD would define; substituted so recovery loses no text.
_ENTITIES = {
    "&lpar;": "(",
    "&rpar;": ")",
    "&ast;": "*",
    "&dagger;": "†",
    "&mdash;": "—",
    "&ndash;": "–",
    "&deg;": "°",
    "&sect;": "§",
}

_MAX_GLOSS_CHARS = 250


@dataclass
class LexiconEntry:
    lemma: str
    gloss: str
    search_key: str = ""

    def __post_init__(self) -> None:
        if not self.search_key:
            self.search_key = strip_diacritics(self.lemma)


def fetch_middle_liddell(cache_dir: str | Path) -> str:
    """Download the ML TEI once into ``cache_dir`` and return its text."""
    cache = Path(cache_dir)
    cache.mkdir(parents=True, exist_ok=True)
    target = cache / "middle_liddell.xml"
    if not target.exists() or target.stat().st_size == 0:
        response = httpx.get(ML_URL, timeout=180, follow_redirects=True).raise_for_status()
        target.write_text(response.text, encoding="utf-8")
    return target.read_text(encoding="utf-8")


def _substitute_entities(xml: str) -> str:
    for entity, char in _ENTITIES.items():
        xml = xml.replace(entity, char)
    return xml


def parse_middle_liddell(xml: str) -> list[LexiconEntry]:
    """Parse the TEI into (lemma, short-gloss) entries.

    An entry's gloss is its ``<tr>`` translation snippets joined in sense
    order (deduplicated, capped) — pedagogically curated one-liners.
    Entries with no ``<tr>`` (pure cross-references etc.) are skipped.
    """
    parser = etree.XMLParser(
        load_dtd=False, no_network=True, resolve_entities=False, recover=True, huge_tree=True
    )
    root = etree.fromstring(_substitute_entities(xml).encode("utf-8"), parser)

    entries: list[LexiconEntry] = []
    for entry in root.iterfind(".//entry"):
        orth = entry.find(".//orth")
        lemma = nfc((orth.text or "").strip()) if orth is not None else ""
        if not lemma:
            continue
        glosses: list[str] = []
        seen: set[str] = set()
        for tr in entry.iterfind(".//tr"):
            text = re.sub(r"\s+", " ", "".join(tr.itertext())).strip(" ,;")
            if text and text.lower() not in seen:
                seen.add(text.lower())
                glosses.append(text)
        if not glosses:
            continue
        gloss = "; ".join(glosses)
        if len(gloss) > _MAX_GLOSS_CHARS:
            gloss = gloss[:_MAX_GLOSS_CHARS].rsplit(";", 1)[0].strip(" ,;")
        entries.append(LexiconEntry(lemma=lemma, gloss=nfc(gloss)))
    return entries
