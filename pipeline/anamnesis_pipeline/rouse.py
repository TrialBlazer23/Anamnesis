"""Parse the digitized Rouse, *A Greek Boy at Home* (1909) graded reader.

Source: fhardison/rouse-a-greek-boy-at-home (CC BY-SA 4.0 per its README; the
underlying 1909 text is US public domain). `src/rouse_text.txt` is the canonical
form: one sentence per line, addressed `chapter.paragraph.sentence.text ...`,
chapters 1-105 in increasing difficulty — ideal Learn-tab reading material.

The companion `docs/vocab.js` holds Rouse's own (mostly Greek-to-Greek)
glossary as `const vocab = [{"head": ..., "deff": ...}, ...]`.
"""

from __future__ import annotations

import json
import re
from dataclasses import dataclass
from pathlib import Path

from .normalize import nfc, strip_diacritics

_LINE = re.compile(r"^(\d+)\.(\d+)\.(\d+)\.text (.*)$")
# Footnote refs like [^3] and markdown emphasis markers.
_MARKDOWN = re.compile(r"\[\^\d+\]|\*\*|__|(?<!\w)\*(?!\w)")
_VOCAB_ARRAY = re.compile(r"=\s*(\[.*\])\s*;?\s*$", re.S)


@dataclass
class RouseSentence:
    chapter: int
    paragraph: int
    sentence: int
    greek: str
    search_key: str = ""

    def __post_init__(self) -> None:
        if not self.search_key:
            self.search_key = strip_diacritics(self.greek)

    @property
    def ref(self) -> str:
        return f"{self.chapter}.{self.paragraph}.{self.sentence}"


def _clean(text: str) -> str:
    return nfc(re.sub(r"\s+", " ", _MARKDOWN.sub("", text)).strip())


def parse_rouse_text(text: str) -> list[RouseSentence]:
    """Parse `rouse_text.txt` into NFC-normalized, markdown-stripped sentences."""
    sentences: list[RouseSentence] = []
    for line in text.splitlines():
        match = _LINE.match(line)
        if not match:
            continue
        greek = _clean(match.group(4))
        if not greek:
            continue
        sentences.append(
            RouseSentence(
                chapter=int(match.group(1)),
                paragraph=int(match.group(2)),
                sentence=int(match.group(3)),
                greek=greek,
            )
        )
    return sentences


def parse_rouse_vocab(vocab_js: str) -> list[dict[str, str]]:
    """Parse `vocab.js` (`const vocab = [...]`) into {head, deff} dicts, NFC."""
    match = _VOCAB_ARRAY.search(vocab_js)
    if not match:
        raise ValueError("vocab.js: could not locate the vocab array literal")
    entries = json.loads(match.group(1))
    return [
        {"head": nfc(e["head"].strip()), "deff": nfc(e.get("deff", "").strip())}
        for e in entries
        if e.get("head")
    ]


def load_rouse(text_path: str | Path) -> list[RouseSentence]:
    return parse_rouse_text(Path(text_path).read_text(encoding="utf-8"))
