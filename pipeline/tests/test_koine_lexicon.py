from pathlib import Path

from anamnesis_pipeline.koine_lexicon import load_dodson

DATA = Path(__file__).parent.parent / "data" / "koine"


def test_dodson_loads_with_unicode_lemmas():
    entries = load_dodson(DATA / "dodson.csv")
    assert len(entries) > 5000  # 5,408 upstream
    by_strongs = {e.strongs: e for e in entries}
    # G0002 is Aaron: Beta Code "*)aarw/n, o(" -> Ἀαρών (article tag dropped).
    aaron = by_strongs["0002"]
    assert aaron.lemma == "Ἀαρών"
    assert aaron.brief.startswith("Aaron")
    # Every entry has a Unicode lemma (no Beta Code escapes) and a brief gloss.
    assert all("(" not in e.lemma and "/" not in e.lemma for e in entries)
    assert sum(1 for e in entries if e.brief) > 5000
