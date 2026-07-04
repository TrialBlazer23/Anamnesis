"""Build the Learn-tab lessons pack from the authored data in `data/lessons/`.

Reads `letters.csv`, `diphthongs.csv`, `minimal_pairs.csv`, `words.csv`,
`accent_items.csv`, `accent_pairs.csv`, and `units.json`, validates them
against each other (batch membership, breathing/accent marks vs the declared
flags, audio-id shape), and emits a single JSON lessons pack plus an audio
manifest listing every recording id the pack references. Stdlib only.
"""

from __future__ import annotations

import csv
import json
import unicodedata
from dataclasses import dataclass
from pathlib import Path

from .normalize import nfc

LESSONS_SCHEMA_VERSION = 1

# Combining marks (NFD): breathings, pitch accents, iota subscript.
_SMOOTH = "̓"
_ROUGH = "̔"
_ACUTE = "́"
_GRAVE = "̀"
_CIRCUMFLEX = "͂"
_IOTA_SUBSCRIPT = "ͅ"

_VOWELS = set("αεηιουω")
_BREATHINGS = {"rough", "smooth", "none"}
_ACCENTS = {"acute": _ACUTE, "grave": _GRAVE, "circumflex": _CIRCUMFLEX}
_PAIR_TYPES = {"vowel-length", "aspiration", "hidden-quantity"}

KNOWN_DRILLS = {
    # units 1-3 (alphabet)
    "letter-to-sound",
    "sound-to-letter",
    "letter-name-recall",
    "spen-tracing",
    "two-sigma-placement",
    "aspirate-minimal-pair",
    "contrastive-discrimination",
    "mixed-review",
    "audio-to-letter",
    # unit 4
    "length-minimal-pair",
    "diphthong-to-sound",
    "long-or-short",
    # unit 5
    "read-the-word",
    "breathing-identification",
    "transliteration",
    # unit 6
    "identify-the-accent",
    "same-word-or-different",
    # units 7-8 (not built yet, but authored in units.json)
    "guided-reader-micropassage",
    "form-to-lemma",
    "guided-reader-euthyphro-2a",
}


@dataclass
class Letter:
    order: int
    lower: str
    upper: str
    name_greek: str
    name_translit: str
    ipa: str
    batch: int
    latin_lookalike: str | None
    false_friend: bool
    multistroke: bool
    audio_sound_id: str
    audio_name_id: str
    teaching_note: str


@dataclass
class Diphthong:
    order: int
    glyph: str
    ipa: str
    kind: str  # "proper" | "improper"
    audio_id: str
    note: str


@dataclass
class MinimalPair:
    id: str
    a: str
    b: str
    contrast: str
    type: str
    audio_a_id: str
    audio_b_id: str
    note: str


@dataclass
class WordItem:
    dcc_rank: int
    greek: str
    translit: str
    ipa: str
    breathing: str  # "rough" | "smooth" | "none"
    gloss: str
    pos: str
    audio_id: str
    note: str


@dataclass
class AccentItem:
    id: str
    word: str
    accent: str  # "acute" | "grave" | "circumflex"
    gloss: str
    audio_id: str
    note: str


@dataclass
class AccentPair:
    id: str
    a: str
    b: str
    same: bool
    audio_a_id: str
    audio_b_id: str
    note: str


@dataclass
class Unit:
    number: int
    title: str
    objective: str
    taught: str
    drills: list[str]
    srs_feed: str
    advance: str
    batch: int | None = None
    letters: list[str] | None = None


@dataclass
class LessonsData:
    scheme: str
    aspirate_default: str
    units: list[Unit]
    letters: list[Letter]
    diphthongs: list[Diphthong]
    minimal_pairs: list[MinimalPair]
    words: list[WordItem]
    accent_items: list[AccentItem]
    accent_pairs: list[AccentPair]


def _bool(raw: str) -> bool:
    return raw.strip().lower() == "true"


def _rows(path: Path) -> list[dict[str, str]]:
    with open(path, newline="", encoding="utf-8-sig") as fh:
        return [
            {k: (v or "").strip() for k, v in row.items()}
            for row in csv.DictReader(fh)
        ]


def load_lessons(data_dir: str | Path) -> LessonsData:
    data_dir = Path(data_dir)
    spec = json.loads((data_dir / "units.json").read_text(encoding="utf-8"))

    units = [
        Unit(
            number=u["number"],
            title=u["title"],
            objective=u["objective"],
            taught=u.get("taught", ""),
            drills=list(u.get("drills", [])),
            srs_feed=u.get("srs_feed", ""),
            advance=u.get("advance", ""),
            batch=u.get("batch"),
            letters=[nfc(l) for l in u["letters"]] if "letters" in u else None,
        )
        for u in spec["units"]
    ]

    letters = [
        Letter(
            order=int(r["order"]),
            lower=nfc(r["lower"]),
            upper=nfc(r["upper"]),
            name_greek=nfc(r["name_greek"]),
            name_translit=r["name_translit"],
            ipa=r["ipa"],
            batch=int(r["batch"]),
            latin_lookalike=r["latin_lookalike"] or None,
            false_friend=_bool(r["false_friend"]),
            multistroke=_bool(r["multistroke"]),
            audio_sound_id=r["audio_sound_id"],
            audio_name_id=r["audio_name_id"],
            teaching_note=r["teaching_note"],
        )
        for r in _rows(data_dir / "letters.csv")
    ]

    diphthongs = [
        Diphthong(
            order=int(r["order"]),
            glyph=nfc(r["glyph"]),
            ipa=r["ipa"],
            kind=r["kind"],
            audio_id=r["audio_id"],
            note=r["note"],
        )
        for r in _rows(data_dir / "diphthongs.csv")
    ]

    minimal_pairs = [
        MinimalPair(
            id=r["id"],
            a=nfc(r["a"]),
            b=nfc(r["b"]),
            contrast=r["contrast"],
            type=r["type"],
            audio_a_id=r["audio_a_id"],
            audio_b_id=r["audio_b_id"],
            note=r["note"],
        )
        for r in _rows(data_dir / "minimal_pairs.csv")
    ]

    words = [
        WordItem(
            dcc_rank=int(r["dcc_rank"]),
            greek=nfc(r["greek"]),
            translit=r["translit"],
            ipa=r["ipa"],
            breathing=r["breathing"],
            gloss=r["gloss"],
            pos=r["pos"],
            audio_id=r["audio_id"],
            note=r["note"],
        )
        for r in _rows(data_dir / "words.csv")
    ]

    accent_items = [
        AccentItem(
            id=r["id"],
            word=nfc(r["word"]),
            accent=r["accent"],
            gloss=r["gloss"],
            audio_id=r["audio_id"],
            note=r["note"],
        )
        for r in _rows(data_dir / "accent_items.csv")
    ]

    accent_pairs = [
        AccentPair(
            id=r["id"],
            a=nfc(r["a"]),
            b=nfc(r["b"]),
            same=_bool(r["same"]),
            audio_a_id=r["audio_a_id"],
            audio_b_id=r["audio_b_id"],
            note=r["note"],
        )
        for r in _rows(data_dir / "accent_pairs.csv")
    ]

    return LessonsData(
        scheme=spec["scheme"],
        aspirate_default=spec.get("aspirate_default", "aspirate"),
        units=units,
        letters=letters,
        diphthongs=diphthongs,
        minimal_pairs=minimal_pairs,
        words=words,
        accent_items=accent_items,
        accent_pairs=accent_pairs,
    )


def initial_breathing(word: str) -> str:
    """Breathing carried by the word's initial vowel cluster (or initial ρ).

    Scans the NFD form: an initial ρ or up to two leading vowels may carry
    a breathing mark (on diphthongs it sits on the second vowel).
    """
    decomposed = unicodedata.normalize("NFD", word)
    bases_seen = 0
    for ch in decomposed:
        if unicodedata.combining(ch):
            if ch == _ROUGH:
                return "rough"
            if ch == _SMOOTH:
                return "smooth"
            continue
        low = ch.lower()
        if bases_seen == 0 and low == "ρ":
            bases_seen += 1
            continue
        if low in _VOWELS and bases_seen < 2:
            bases_seen += 1
            continue
        break
    return "none"


def accent_marks(word: str) -> set[str]:
    """The set of accent kinds ("acute"/"grave"/"circumflex") in the word."""
    decomposed = unicodedata.normalize("NFD", word)
    found = set()
    for name, mark in _ACCENTS.items():
        if mark in decomposed:
            found.add(name)
    return found


def _circumflex_bases(word: str) -> list[str]:
    """Base vowels that carry a circumflex (NFD walk)."""
    decomposed = unicodedata.normalize("NFD", word)
    bases: list[str] = []
    last_base = ""
    for ch in decomposed:
        if unicodedata.combining(ch):
            if ch == _CIRCUMFLEX:
                bases.append(last_base)
        else:
            last_base = ch.lower()
    return bases


def strip_accents(word: str) -> str:
    """Remove the three pitch-accent marks only (breathings etc. stay)."""
    decomposed = unicodedata.normalize("NFD", word)
    kept = "".join(ch for ch in decomposed if ch not in (_ACUTE, _GRAVE, _CIRCUMFLEX))
    return nfc(kept)


def _graves_as_acutes(word: str) -> str:
    decomposed = unicodedata.normalize("NFD", word)
    return nfc(decomposed.replace(_GRAVE, _ACUTE))


_AUDIO_PREFIXES = ("snd_", "nm_", "voc_")


def _audio_ok(audio_id: str) -> bool:
    return audio_id.startswith(_AUDIO_PREFIXES) and audio_id == audio_id.lower()


def validate(data: LessonsData) -> list[str]:
    errors: list[str] = []

    # --- letters ---
    if len(data.letters) != 24:
        errors.append(f"letters: expected 24, got {len(data.letters)}")
    if [l.order for l in data.letters] != sorted(l.order for l in data.letters):
        errors.append("letters: rows out of order")
    if len({l.lower for l in data.letters}) != len(data.letters):
        errors.append("letters: duplicate glyphs")
    batches: dict[int, set[str]] = {}
    for l in data.letters:
        if l.batch not in (1, 2, 3, 4):
            errors.append(f"letters: {l.lower} has unknown batch {l.batch}")
        batches.setdefault(l.batch, set()).add(l.lower)
    for b in (1, 2, 3, 4):
        if not batches.get(b):
            errors.append(f"letters: batch {b} is empty")

    # --- units ---
    numbers = [u.number for u in data.units]
    if numbers != list(range(len(numbers))) or not numbers or numbers[-1] != 8:
        errors.append(f"units: expected contiguous 0..8, got {numbers}")
    for u in data.units:
        for drill in u.drills:
            if drill not in KNOWN_DRILLS:
                errors.append(f"unit {u.number}: unknown drill '{drill}'")
        if u.batch is not None:
            expected = batches.get(u.batch, set())
            actual = set(u.letters or [])
            if actual != expected:
                errors.append(
                    f"unit {u.number}: letters {sorted(actual)} != batch {u.batch} "
                    f"letters {sorted(expected)}"
                )

    # --- diphthongs ---
    proper = [d for d in data.diphthongs if d.kind == "proper"]
    improper = [d for d in data.diphthongs if d.kind == "improper"]
    if len(proper) != 8 or len(improper) != 3:
        errors.append(
            f"diphthongs: expected 8 proper + 3 improper, got "
            f"{len(proper)} + {len(improper)}"
        )
    for d in data.diphthongs:
        if d.kind not in ("proper", "improper"):
            errors.append(f"diphthong {d.glyph}: unknown kind '{d.kind}'")
    if len({d.glyph for d in data.diphthongs}) != len(data.diphthongs):
        errors.append("diphthongs: duplicate glyphs")

    # --- minimal pairs ---
    for p in data.minimal_pairs:
        if p.type not in _PAIR_TYPES:
            errors.append(f"minimal pair {p.id}: unknown type '{p.type}'")
        if not p.a or not p.b or p.a == p.b:
            errors.append(f"minimal pair {p.id}: sides must be distinct and non-empty")

    # --- words ---
    ranks = [w.dcc_rank for w in data.words]
    if len(set(ranks)) != len(ranks):
        errors.append("words: duplicate dcc_rank")
    for w in data.words:
        if w.breathing not in _BREATHINGS:
            errors.append(f"word {w.greek}: unknown breathing '{w.breathing}'")
            continue
        if w.audio_id != f"voc_{w.dcc_rank}":
            errors.append(f"word {w.greek}: audio_id {w.audio_id} != voc_{w.dcc_rank}")
        actual = initial_breathing(w.greek)
        if actual != w.breathing:
            errors.append(
                f"word {w.greek}: declared breathing '{w.breathing}' but marks say "
                f"'{actual}'"
            )
        starts_h = w.translit.startswith(("h", "rh"))
        if w.breathing == "rough" and not starts_h:
            errors.append(f"word {w.greek}: rough breathing but translit '{w.translit}' lacks h")
        if w.breathing != "rough" and w.translit.startswith("h"):
            errors.append(f"word {w.greek}: no rough breathing but translit '{w.translit}' has h")
        if not w.gloss or not w.translit or not w.ipa:
            errors.append(f"word {w.greek}: translit/ipa/gloss must be non-empty")

    # --- accent items ---
    if len({a.id for a in data.accent_items}) != len(data.accent_items):
        errors.append("accent_items: duplicate ids")
    for a in data.accent_items:
        if a.accent not in _ACCENTS:
            errors.append(f"accent item {a.id}: unknown accent '{a.accent}'")
            continue
        marks = accent_marks(a.word)
        if marks != {a.accent}:
            errors.append(
                f"accent item {a.id} ({a.word}): declared '{a.accent}' but marks are "
                f"{sorted(marks) or ['none']}"
            )
        if a.accent == "circumflex":
            for base in _circumflex_bases(a.word):
                if base in ("ε", "ο"):
                    errors.append(
                        f"accent item {a.id} ({a.word}): circumflex on short vowel {base}"
                    )

    # --- accent pairs ---
    if len({p.id for p in data.accent_pairs}) != len(data.accent_pairs):
        errors.append("accent_pairs: duplicate ids")
    for p in data.accent_pairs:
        if p.a == p.b:
            errors.append(f"accent pair {p.id}: sides are identical")
        if strip_accents(p.a) != strip_accents(p.b):
            errors.append(
                f"accent pair {p.id}: {p.a}/{p.b} differ beyond their accents"
            )
        normalized_equal = _graves_as_acutes(p.a) == _graves_as_acutes(p.b)
        if p.same and not normalized_equal:
            errors.append(
                f"accent pair {p.id}: marked same but {p.a}/{p.b} differ beyond "
                f"acute/grave alternation"
            )
        if not p.same and normalized_equal:
            errors.append(
                f"accent pair {p.id}: marked different but {p.a}/{p.b} are just "
                f"acute/grave variants"
            )

    # --- audio ids ---
    defining: list[tuple[str, str]] = []  # (id, where)
    for l in data.letters:
        defining.append((l.audio_sound_id, f"letter {l.lower}"))
        defining.append((l.audio_name_id, f"letter {l.lower}"))
    for d in data.diphthongs:
        defining.append((d.audio_id, f"diphthong {d.glyph}"))
    for w in data.words:
        defining.append((w.audio_id, f"word {w.greek}"))
    for a in data.accent_items:
        defining.append((a.audio_id, f"accent item {a.id}"))
    for p in data.accent_pairs:
        defining.append((p.audio_a_id, f"accent pair {p.id}"))
        defining.append((p.audio_b_id, f"accent pair {p.id}"))
    seen: dict[str, str] = {}
    for audio_id, where in defining:
        if audio_id in seen:
            errors.append(f"audio id {audio_id} defined by both {seen[audio_id]} and {where}")
        seen[audio_id] = where
    referencing = defining + [
        (p.audio_a_id, f"minimal pair {p.id}") for p in data.minimal_pairs
    ] + [(p.audio_b_id, f"minimal pair {p.id}") for p in data.minimal_pairs]
    for audio_id, where in referencing:
        if not _audio_ok(audio_id):
            errors.append(f"{where}: malformed audio id '{audio_id}'")

    return errors


def build_pack(data: LessonsData) -> dict:
    """The lessons.json dict — key order is fixed so output is deterministic."""
    return {
        "schema_version": LESSONS_SCHEMA_VERSION,
        "scheme": data.scheme,
        "aspirate_default": data.aspirate_default,
        "units": [
            {
                "number": u.number,
                "title": u.title,
                "objective": u.objective,
                "taught": u.taught,
                "drills": u.drills,
                "srs_feed": u.srs_feed,
                "advance": u.advance,
                **({"batch": u.batch} if u.batch is not None else {}),
                **({"letters": u.letters} if u.letters is not None else {}),
            }
            for u in sorted(data.units, key=lambda u: u.number)
        ],
        "letters": [
            {
                "order": l.order,
                "lower": l.lower,
                "upper": l.upper,
                "name_greek": l.name_greek,
                "name_translit": l.name_translit,
                "ipa": l.ipa,
                "batch": l.batch,
                "latin_lookalike": l.latin_lookalike,
                "false_friend": l.false_friend,
                "multistroke": l.multistroke,
                "audio_sound_id": l.audio_sound_id,
                "audio_name_id": l.audio_name_id,
                "teaching_note": l.teaching_note,
            }
            for l in sorted(data.letters, key=lambda l: l.order)
        ],
        "diphthongs": [
            {
                "order": d.order,
                "glyph": d.glyph,
                "ipa": d.ipa,
                "kind": d.kind,
                "audio_id": d.audio_id,
                "note": d.note,
            }
            for d in sorted(data.diphthongs, key=lambda d: d.order)
        ],
        "minimal_pairs": [
            {
                "id": p.id,
                "a": p.a,
                "b": p.b,
                "contrast": p.contrast,
                "type": p.type,
                "audio_a_id": p.audio_a_id,
                "audio_b_id": p.audio_b_id,
                "note": p.note,
            }
            for p in data.minimal_pairs
        ],
        "words": [
            {
                "dcc_rank": w.dcc_rank,
                "greek": w.greek,
                "translit": w.translit,
                "ipa": w.ipa,
                "breathing": w.breathing,
                "gloss": w.gloss,
                "pos": w.pos,
                "audio_id": w.audio_id,
                "note": w.note,
            }
            for w in sorted(data.words, key=lambda w: w.dcc_rank)
        ],
        "accent_items": [
            {
                "id": a.id,
                "word": a.word,
                "accent": a.accent,
                "gloss": a.gloss,
                "audio_id": a.audio_id,
                "note": a.note,
            }
            for a in data.accent_items
        ],
        "accent_pairs": [
            {
                "id": p.id,
                "a": p.a,
                "b": p.b,
                "same": p.same,
                "audio_a_id": p.audio_a_id,
                "audio_b_id": p.audio_b_id,
                "note": p.note,
            }
            for p in data.accent_pairs
        ],
    }


def build_audio_manifest(data: LessonsData) -> dict:
    """Every recording id the pack references, with the tables that use it."""
    sources: dict[str, set[str]] = {}

    def add(audio_id: str, source: str) -> None:
        sources.setdefault(audio_id, set()).add(source)

    for l in data.letters:
        add(l.audio_sound_id, "letters:sound")
        add(l.audio_name_id, "letters:name")
    for d in data.diphthongs:
        add(d.audio_id, "diphthongs")
    for p in data.minimal_pairs:
        add(p.audio_a_id, "minimal_pairs")
        add(p.audio_b_id, "minimal_pairs")
    for w in data.words:
        add(w.audio_id, "words")
    for a in data.accent_items:
        add(a.audio_id, "accent_items")
    for p in data.accent_pairs:
        add(p.audio_a_id, "accent_pairs")
        add(p.audio_b_id, "accent_pairs")

    return {
        "schema_version": LESSONS_SCHEMA_VERSION,
        "scheme": data.scheme,
        "count": len(sources),
        "entries": [
            {"id": audio_id, "sources": sorted(tables)}
            for audio_id, tables in sorted(sources.items())
        ],
    }
