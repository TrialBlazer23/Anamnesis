import json
from pathlib import Path

from anamnesis_pipeline.lessons import (
    LESSONS_SCHEMA_VERSION,
    build_audio_manifest,
    build_pack,
    initial_breathing,
    load_lessons,
    strip_accents,
    validate,
)

# The authored data set is small, so tests run against the real thing —
# they double as a validation gate for content edits.
DATA_DIR = Path(__file__).parent.parent / "data" / "lessons"


def _load():
    data = load_lessons(DATA_DIR)
    assert validate(data) == []
    return data


def test_authored_data_is_valid_and_complete():
    data = _load()
    assert len(data.letters) == 24
    assert len(data.diphthongs) == 11
    assert sum(d.kind == "proper" for d in data.diphthongs) == 8
    assert len(data.minimal_pairs) >= 7
    assert len(data.words) >= 40
    assert len(data.accent_items) >= 18
    assert len(data.accent_pairs) >= 12
    assert [u.number for u in data.units] == list(range(9))


def test_pack_shape_and_schema_version():
    pack = build_pack(_load())
    assert pack["schema_version"] == LESSONS_SCHEMA_VERSION
    assert len(pack["units"]) == 9
    assert len(pack["letters"]) == 24
    assert pack["letters"][0]["lower"] == "α"
    assert pack["letters"][0]["audio_sound_id"] == "snd_alpha"
    unit4 = pack["units"][4]
    assert unit4["drills"] == ["length-minimal-pair", "diphthong-to-sound", "long-or-short"]
    # kotlinx-serialization on the app side needs stable field presence
    assert all("audio_id" in d for d in pack["diphthongs"])
    assert all(w["breathing"] in ("rough", "smooth", "none") for w in pack["words"])


def test_build_is_deterministic():
    data = _load()
    first = json.dumps(build_pack(data), ensure_ascii=False, indent=2)
    second = json.dumps(build_pack(load_lessons(DATA_DIR)), ensure_ascii=False, indent=2)
    assert first == second


def test_audio_manifest_covers_every_referenced_id():
    data = _load()
    manifest = build_audio_manifest(data)
    ids = {entry["id"] for entry in manifest["entries"]}
    assert manifest["count"] == len(ids)
    for letter in data.letters:
        assert letter.audio_sound_id in ids
        assert letter.audio_name_id in ids
    for diphthong in data.diphthongs:
        assert diphthong.audio_id in ids
    for pair in data.minimal_pairs:
        assert {pair.audio_a_id, pair.audio_b_id} <= ids
    for word in data.words:
        assert word.audio_id in ids
    # minimal pairs reuse letter-sound clips; both sources are recorded
    epsilon = next(e for e in manifest["entries"] if e["id"] == "snd_epsilon")
    assert set(epsilon["sources"]) == {"letters:sound", "minimal_pairs"}


def test_breathing_detection():
    assert initial_breathing("ὁ") == "rough"
    assert initial_breathing("οὗτος") == "rough"  # mark on the diphthong's 2nd vowel
    assert initial_breathing("ῥᾴδιος") == "rough"  # initial rho
    assert initial_breathing("αὐτός") == "smooth"
    assert initial_breathing("καί") == "none"


def test_strip_accents_keeps_breathings():
    assert strip_accents("εἰμί") == strip_accents("εἶμι")
    assert strip_accents("ὁ") == "ὁ"


def test_validate_rejects_wrong_breathing_flag():
    data = _load()
    word = next(w for w in data.words if w.breathing == "rough")
    word.breathing = "smooth"
    errors = validate(data)
    assert any("marks say" in e for e in errors)


def test_validate_rejects_circumflex_on_short_vowel():
    data = _load()
    item = data.accent_items[0]
    item.word = "λο͂γος"  # fabricated: circumflex forced onto omicron
    item.accent = "circumflex"
    errors = validate(data)
    assert any("circumflex on short vowel" in e for e in errors)


def test_validate_rejects_duplicate_audio_id():
    data = _load()
    data.words[1].audio_id = data.words[0].audio_id
    errors = validate(data)
    assert any("audio_id" in e or "audio id" in e for e in errors)


def test_validate_rejects_accent_pair_that_differs_beyond_accents():
    data = _load()
    data.accent_pairs[0].b = "λόγος"
    errors = validate(data)
    assert any("differ beyond their accents" in e for e in errors)
