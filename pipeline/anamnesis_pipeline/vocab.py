"""Load the DCC Greek Core Vocabulary (CC BY-SA 3.0) from CSV.

The DCC list is distributed as CSV/XLSX with columns Headword, Definition,
Part of Speech, Semantic Group, Frequency. Acquire the CSV from
dcc.dickinson.edu (CC BY-SA 3.0 — attribution + ShareAlike) and drop it in, or
fetch it in CI; this loader is column-name tolerant.
"""

from __future__ import annotations

import csv
from dataclasses import dataclass
from pathlib import Path

from .normalize import nfc


@dataclass
class VocabEntry:
    lemma: str
    part_of_speech: str
    gloss: str
    semantic_group: str | None = None
    frequency_rank: int | None = None


# Accept a few header spellings so the real DCC export loads without edits.
_ALIASES = {
    "lemma": ("headword", "lemma", "word", "greek"),
    "gloss": ("definition", "gloss", "meaning", "english"),
    "part_of_speech": ("part of speech", "pos", "part_of_speech"),
    "semantic_group": ("semantic group", "semantic_group", "group"),
    "frequency_rank": ("frequency", "rank", "frequency_rank", "freq"),
}


def _pick(row: dict[str, str], key: str) -> str | None:
    lowered = {k.strip().lower(): v for k, v in row.items() if k}
    for alias in _ALIASES[key]:
        if alias in lowered and lowered[alias] not in (None, ""):
            return lowered[alias].strip()
    return None


def load_dcc_vocab(csv_path: str | Path) -> list[VocabEntry]:
    entries: list[VocabEntry] = []
    with open(csv_path, newline="", encoding="utf-8-sig") as fh:
        for row in csv.DictReader(fh):
            lemma = _pick(row, "lemma")
            if not lemma:
                continue
            rank_raw = _pick(row, "frequency_rank")
            rank: int | None = None
            if rank_raw:
                digits = "".join(c for c in rank_raw if c.isdigit())
                rank = int(digits) if digits else None
            entries.append(
                VocabEntry(
                    lemma=nfc(lemma),
                    part_of_speech=_pick(row, "part_of_speech") or "",
                    gloss=_pick(row, "gloss") or "",
                    semantic_group=_pick(row, "semantic_group"),
                    frequency_rank=rank,
                )
            )
    return entries
