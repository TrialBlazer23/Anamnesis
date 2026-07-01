from pathlib import Path

from anamnesis_pipeline.middle_liddell import LexiconEntry, parse_middle_liddell

FIXTURE = Path(__file__).parent / "fixtures" / "sample_ml.xml"


def test_parses_entries_with_glosses():
    entries = parse_middle_liddell(FIXTURE.read_text(encoding="utf-8"))
    assert [e.lemma for e in entries] == ["ἀαγής", "ἄαπτος"]  # xref-only entry skipped
    assert entries[0].gloss == "unbroken, not to be broken, hard, strong"
    assert entries[1].gloss == "not to be touched; resistless, invincible"


def test_entries_carry_normalized_search_keys():
    entries = parse_middle_liddell(FIXTURE.read_text(encoding="utf-8"))
    assert entries[0].search_key == "ααγησ"


def test_long_glosses_are_capped():
    tr = "very long gloss segment here"
    senses = "".join(
        f'<sense id="n9.{i}"><trans><tr>{tr} {i}</tr></trans></sense>' for i in range(30)
    )
    xml = (
        '<TEI.2><text><body><entry id="n9" key="x">'
        '<form><orth lang="greek">λόγος</orth></form>'
        f"{senses}</entry></body></text></TEI.2>"
    )
    (entry,) = parse_middle_liddell(xml)
    assert len(entry.gloss) <= 250


def test_lexicon_entry_defaults():
    e = LexiconEntry(lemma="λόγος", gloss="word")
    assert e.search_key == "λογοσ"
