"""Extract a per-text morphology table from Lyceum's `morph.db`.

Source: lyceum-quest/archeion release asset `morph.db` (CC BY-SA 4.0; Perseus/
Morpheus lineage) — table `morphology` with 1.55M surface form → lemma + parse
rows (schema documented in archeion `db/schemas/morph.md`). Mirrored for this
project as the `lyceum-data-v2026.04.09` release on the Anamnesis repo.

We do NOT bundle it wholesale: `extract_morphology` filters to the word forms
attested in the pack's passages, so each content pack carries exactly the
morphology its text needs (hybrid-delivery principle). Surface forms are
re-normalized with OUR key convention (NFC → strip diacritics → fold sigma) so
app lookups never depend on Lyceum's normalization matching ours.
"""

from __future__ import annotations

import re
import sqlite3
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

from .normalize import nfc, strip_diacritics
from .tei import Passage

# Cap analyses kept per surface form (homographs are real; noise is too).
_MAX_ANALYSES_PER_FORM = 3

_WORD = re.compile(r"[^\W\d_]+", re.UNICODE)


@dataclass
class MorphRow:
    form: str
    form_key: str
    lemma: str
    parse: str
    gloss: str


def tokens_from_passages(passages: Iterable[Passage]) -> set[str]:
    """All distinct normalized word keys attested in the passages' Greek."""
    keys: set[str] = set()
    for passage in passages:
        for match in _WORD.finditer(passage.greek):
            key = strip_diacritics(match.group())
            if key:
                keys.add(key)
    return keys


def _compose_parse(row: sqlite3.Row) -> str:
    combined = (row["morphology"] or "").strip()
    pos = (row["pos"] or "").strip()
    if combined:
        return f"{pos}: {combined}" if pos and pos.lower() not in combined.lower() else combined or pos
    parts = [
        (row[field] or "").strip()
        for field in ("tense", "voice", "mood", "person", "case_name", "number", "gender", "degree")
    ]
    detail = " ".join(p for p in parts if p)
    return f"{pos}: {detail}".strip(": ") if detail else pos


def extract_morphology(morph_db: str | Path, tokens: set[str]) -> list[MorphRow]:
    """Scan Lyceum's morphology table; keep rows whose re-normalized surface
    form is attested in ``tokens``. Deduplicates identical (form, lemma) pairs
    and caps analyses per form."""
    conn = sqlite3.connect(f"file:{Path(morph_db)}?mode=ro", uri=True)
    conn.row_factory = sqlite3.Row
    kept: dict[str, list[MorphRow]] = {}
    seen: set[tuple[str, str]] = set()
    try:
        cursor = conn.execute(
            "SELECT form, lemma, definition, pos, morphology, tense, voice, mood, "
            "person, number, case_name, gender, degree FROM morphology"
        )
        for row in cursor:
            form = nfc((row["form"] or "").strip())
            if not form:
                continue
            key = strip_diacritics(form)
            if key not in tokens:
                continue
            lemma = nfc((row["lemma"] or "").strip())
            if not lemma or (key, lemma) in seen:
                continue
            analyses = kept.setdefault(key, [])
            if len(analyses) >= _MAX_ANALYSES_PER_FORM:
                continue
            seen.add((key, lemma))
            analyses.append(
                MorphRow(
                    form=form,
                    form_key=key,
                    lemma=lemma,
                    parse=_compose_parse(row),
                    gloss=nfc((row["definition"] or "").strip()),
                )
            )
    finally:
        conn.close()
    return [row for analyses in kept.values() for row in analyses]
