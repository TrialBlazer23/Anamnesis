"""Build the read-only content-pack SQLite database (FTS5).

Schema (content pack — opened read-only by the app, NOT the encrypted user DB):

    passages(id, cts_urn, ref, greek, search_key, translation)
    passage_fts  -- FTS5 external-content index over passages
    vocabulary(lemma, part_of_speech, gloss, semantic_group, frequency_rank)
    meta(key, value)  -- schema_version, work, edition, source, license

The FTS5 index uses unicode61 with remove_diacritics=2, so a query is
accent-insensitive even before consulting search_key; search_key is also stored
so a direct column scan stays accent-insensitive.
"""

from __future__ import annotations

import sqlite3
from pathlib import Path
from typing import Iterable

from .tei import Passage
from .vocab import VocabEntry

SCHEMA_VERSION = 1

_SCHEMA = """
CREATE TABLE passages (
    id          INTEGER PRIMARY KEY,
    cts_urn     TEXT NOT NULL UNIQUE,
    ref         TEXT NOT NULL,
    greek       TEXT NOT NULL,
    search_key  TEXT NOT NULL,
    translation TEXT
);

CREATE VIRTUAL TABLE passage_fts USING fts5(
    greek, search_key, translation,
    content='passages', content_rowid='id',
    tokenize="unicode61 remove_diacritics 2"
);

CREATE TABLE vocabulary (
    lemma          TEXT PRIMARY KEY,
    part_of_speech TEXT,
    gloss          TEXT,
    semantic_group TEXT,
    frequency_rank INTEGER
);

CREATE TABLE meta (
    key   TEXT PRIMARY KEY,
    value TEXT
);
"""


def build_content_pack(
    out_path: str | Path,
    passages: Iterable[Passage],
    vocab: Iterable[VocabEntry] = (),
    meta: dict[str, str] | None = None,
) -> dict[str, int]:
    """Create the content pack at ``out_path``. Returns row counts."""
    out = Path(out_path)
    out.parent.mkdir(parents=True, exist_ok=True)
    if out.exists():
        out.unlink()

    conn = sqlite3.connect(out)
    try:
        conn.executescript(_SCHEMA)

        passage_rows = [
            (p.cts_urn, p.ref, p.greek, p.search_key, p.translation) for p in passages
        ]
        conn.executemany(
            "INSERT INTO passages(cts_urn, ref, greek, search_key, translation) "
            "VALUES (?, ?, ?, ?, ?)",
            passage_rows,
        )
        # Populate the external-content FTS index from passages.
        conn.execute("INSERT INTO passage_fts(passage_fts) VALUES ('rebuild')")

        vocab_rows = [
            (v.lemma, v.part_of_speech, v.gloss, v.semantic_group, v.frequency_rank)
            for v in vocab
        ]
        conn.executemany(
            "INSERT OR REPLACE INTO vocabulary"
            "(lemma, part_of_speech, gloss, semantic_group, frequency_rank) "
            "VALUES (?, ?, ?, ?, ?)",
            vocab_rows,
        )

        meta_rows = {"schema_version": str(SCHEMA_VERSION), **(meta or {})}
        conn.executemany(
            "INSERT OR REPLACE INTO meta(key, value) VALUES (?, ?)",
            list(meta_rows.items()),
        )
        conn.commit()
        return {"passages": len(passage_rows), "vocabulary": len(vocab_rows)}
    finally:
        conn.close()
