import sqlite3
from pathlib import Path

from anamnesis_pipeline.lyceum_morph import extract_morphology, tokens_from_passages
from anamnesis_pipeline.tei import Passage

FIXTURES = Path(__file__).parent / "fixtures"

_SCHEMA = """
CREATE TABLE morphology (
    id INTEGER PRIMARY KEY,
    form TEXT NOT NULL,
    form_normalized TEXT NOT NULL,
    lemma TEXT NOT NULL,
    definition TEXT,
    pos TEXT,
    morphology TEXT,
    tense TEXT, voice TEXT, mood TEXT, person TEXT,
    number TEXT, case_name TEXT, gender TEXT, degree TEXT
);
"""


def _fixture_morph_db(tmp_path) -> Path:
    db = tmp_path / "morph.db"
    conn = sqlite3.connect(db)
    conn.executescript(_SCHEMA)
    rows = [
        # (form, lemma, definition, pos, morphology, case_name, number, gender)
        ("λόγῳ", "λόγος", "word, account", "noun", "dat sg masc", "dative", "singular", "masculine"),
        ("λόγον", "λόγος", "word, account", "noun", "acc sg masc", "accusative", "singular", "masculine"),
        ("ἄλλος", "ἄλλος", "other", "adjective", "nom sg masc", "nominative", "singular", "masculine"),
        # duplicate (form, lemma) — must dedup
        ("λόγῳ", "λόγος", "word, account", "noun", "dat sg masc", "dative", "singular", "masculine"),
        # not attested in our passages — must be filtered out
        ("θάλαττα", "θάλαττα", "sea", "noun", "nom sg fem", "nominative", "singular", "feminine"),
        # combined morphology empty -> parse composed from split fields
        ("λόγος", "λόγος", "word, account", "noun", "", "nominative", "singular", "masculine"),
    ]
    conn.executemany(
        "INSERT INTO morphology(form, form_normalized, lemma, definition, pos, morphology, "
        "case_name, number, gender) VALUES (?, lower(?), ?, ?, ?, ?, ?, ?, ?)",
        [(f, f, l, d, p, m, c, n, g) for (f, l, d, p, m, c, n, g) in rows],
    )
    conn.commit()
    conn.close()
    return db


def _passage(greek: str) -> Passage:
    return Passage(cts_urn="urn:x", ref="1.1", levels=["1", "1"], greek=greek)


def test_tokens_from_passages_normalizes_words():
    tokens = tokens_from_passages([_passage("τῷ λόγῳ, καὶ ὁ λόγος.")])
    assert "λογω" in tokens and "λογοσ" in tokens and "και" in tokens
    assert "," not in tokens


def test_extract_filters_to_attested_tokens_and_dedups(tmp_path):
    db = _fixture_morph_db(tmp_path)
    tokens = tokens_from_passages([_passage("τῷ λόγῳ καὶ τὸν λόγον ἄλλος λόγος")])
    rows = extract_morphology(db, tokens)

    forms = sorted(r.form for r in rows)
    assert forms == ["λόγον", "λόγος", "λόγῳ", "ἄλλος"]  # θάλαττα filtered; dup collapsed
    by_form = {r.form: r for r in rows}
    assert by_form["λόγῳ"].lemma == "λόγος"
    assert by_form["λόγῳ"].form_key == "λογω"
    assert by_form["λόγῳ"].gloss == "word, account"


def test_parse_composition(tmp_path):
    db = _fixture_morph_db(tmp_path)
    rows = extract_morphology(db, {"λογω", "λογοσ"})
    by_form = {r.form: r for r in rows}
    # combined morphology string present -> "pos: combined"
    assert by_form["λόγῳ"].parse == "noun: dat sg masc"
    # combined empty -> composed from split fields
    assert by_form["λόγος"].parse == "noun: nominative singular masculine"
