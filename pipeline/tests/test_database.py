import sqlite3
from pathlib import Path

from anamnesis_pipeline.database import build_content_pack
from anamnesis_pipeline.tei import parse_passages
from anamnesis_pipeline.vocab import load_dcc_vocab

FIXTURES = Path(__file__).parent / "fixtures"


def _build(tmp_path) -> Path:
    passages = parse_passages((FIXTURES / "sample_tei.xml").read_text(encoding="utf-8"))
    vocab = load_dcc_vocab(FIXTURES / "sample_vocab.csv")
    out = tmp_path / "pack.db"
    counts = build_content_pack(out, passages, vocab, {"work": "tlg0562.tlg001"})
    assert counts == {"passages": 3, "vocabulary": 3, "lexicon": 0, "morphology": 0}
    return out


def test_build_content_pack_populates_tables(tmp_path):
    out = _build(tmp_path)
    conn = sqlite3.connect(out)
    try:
        assert conn.execute("SELECT COUNT(*) FROM passages").fetchone()[0] == 3
        assert conn.execute("SELECT COUNT(*) FROM vocabulary").fetchone()[0] == 3
        from anamnesis_pipeline.database import SCHEMA_VERSION

        assert (
            conn.execute("SELECT value FROM meta WHERE key='schema_version'").fetchone()[0]
            == str(SCHEMA_VERSION)
        )
    finally:
        conn.close()


def test_lexicon_table_populates_and_queries_by_normalized_lemma(tmp_path):
    from anamnesis_pipeline.middle_liddell import LexiconEntry

    passages = parse_passages((FIXTURES / "sample_tei.xml").read_text(encoding="utf-8"))
    out = tmp_path / "pack.db"
    counts = build_content_pack(
        out,
        passages,
        [],
        {},
        lexicon=[LexiconEntry(lemma="λόγος", gloss="word; account")],
    )
    assert counts["lexicon"] == 1
    conn = sqlite3.connect(out)
    try:
        row = conn.execute(
            "SELECT lemma, gloss FROM lexicon WHERE normalized_lemma = ?", ("λογοσ",)
        ).fetchone()
        assert row == ("λόγος", "word; account")
    finally:
        conn.close()


def test_fts_search_is_accent_insensitive(tmp_path):
    out = _build(tmp_path)
    conn = sqlite3.connect(out)
    try:
        # Unaccented query matches the accented passage ('Οὐήρου' in 1.1.1).
        refs = {
            r[0]
            for r in conn.execute(
                "SELECT p.ref FROM passages p JOIN passage_fts f ON p.id = f.rowid "
                "WHERE passage_fts MATCH ?",
                ("ουηρου",),
            ).fetchall()
        }
        assert refs == {"1.1.1"}
    finally:
        conn.close()
