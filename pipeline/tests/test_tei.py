from pathlib import Path

from anamnesis_pipeline.tei import parse_passages, source_url

FIXTURE = Path(__file__).parent / "fixtures" / "sample_tei.xml"


def test_source_url_builds_canonical_path():
    assert source_url("tlg0562.tlg001", "perseus-grc2") == (
        "https://raw.githubusercontent.com/PerseusDL/canonical-greekLit/master/"
        "data/tlg0562/tlg001/tlg0562.tlg001.perseus-grc2.xml"
    )


def test_parse_passages_walks_textparts():
    passages = parse_passages(FIXTURE.read_text(encoding="utf-8"))
    # Leaf is the section level, so refs are book.chapter.section.
    assert [p.ref for p in passages] == ["1.1.1", "1.2.1", "2.1.1"]


def test_parse_passages_builds_cts_urn_and_search_key():
    passages = parse_passages(FIXTURE.read_text(encoding="utf-8"))
    first = passages[0]
    assert first.cts_urn == "urn:cts:greekLit:tlg0562.tlg001.perseus-grc2:1.1.1"
    assert first.greek.startswith("Παρὰ τοῦ πάππου")
    assert "ουηρου" in first.search_key  # diacritic-stripped
    assert first.translation is None
