"""Build an Anamnesis content pack (SQLite + FTS5).

Skeleton for Phase 0. Each stage below is a stub to be filled in; the CLI and
the diacritic-stripping helper are real so the shape is testable from day one.

Run in CI/desktop only — NOT on-device (CLTK pulls torch/stanza).
"""

from __future__ import annotations

import argparse
import sqlite3
import unicodedata


def strip_diacritics(text: str) -> str:
    """Return an accent-insensitive search key.

    Decompose to NFD, drop combining marks, lowercase. The original text is
    stored separately in NFC form for display (HarfBuzz/ccmp render NFC best).
    """
    decomposed = unicodedata.normalize("NFD", text)
    stripped = "".join(ch for ch in decomposed if not unicodedata.combining(ch))
    return stripped.lower()


def fetch_tei(work_urn: str) -> str:
    """Fetch canonical-greekLit TEI XML for a CTS work URN. TODO (Phase 0)."""
    raise NotImplementedError("Wire up canonical-greekLit / Scaife fetch")


def parse_passages(tei_xml: str) -> list[dict]:
    """Parse TEI into passages, Beta Code -> Unicode, NFC-normalize. TODO."""
    raise NotImplementedError("Parse TEI with lxml; betacode + NFC normalize")


def create_schema(conn: sqlite3.Connection) -> None:
    conn.executescript(
        """
        CREATE TABLE IF NOT EXISTS vocabulary (
            lemma TEXT PRIMARY KEY,
            part_of_speech TEXT,
            gloss TEXT,
            semantic_group TEXT,
            frequency_rank INTEGER
        );
        CREATE VIRTUAL TABLE IF NOT EXISTS passage_fts USING fts5(
            cts_urn, reference, greek, search_key, translation
        );
        """
    )


def main() -> None:
    parser = argparse.ArgumentParser(description="Build an Anamnesis content pack.")
    parser.add_argument("--work", required=True, help="CTS work URN, e.g. tlg0562.tlg001")
    parser.add_argument("--out", required=True, help="Output .db path")
    args = parser.parse_args()

    conn = sqlite3.connect(args.out)
    try:
        create_schema(conn)
        # tei = fetch_tei(args.work)
        # for p in parse_passages(tei):
        #     p["search_key"] = strip_diacritics(p["greek"])
        #     conn.execute("INSERT INTO passage_fts VALUES (?,?,?,?,?)", ...)
        conn.commit()
        print(f"Created content pack schema at {args.out} for {args.work}")
    finally:
        conn.close()


if __name__ == "__main__":
    main()
