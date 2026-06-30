import unicodedata

from anamnesis_pipeline.normalize import nfc, strip_diacritics


def test_strip_diacritics_removes_accents_and_breathings():
    assert strip_diacritics("ἄνθρωπος") == "ανθρωποσ"
    assert strip_diacritics("Οὐήρου") == "ουηρου"


def test_strip_diacritics_folds_final_sigma():
    # Accent-insensitive key should match regardless of sigma position.
    assert strip_diacritics("λόγος") == strip_diacritics("λογοσ")


def test_nfc_is_precomposed():
    decomposed = unicodedata.normalize("NFD", "ἄ")
    assert len(decomposed) > 1
    assert nfc(decomposed) == unicodedata.normalize("NFC", "ἄ")
