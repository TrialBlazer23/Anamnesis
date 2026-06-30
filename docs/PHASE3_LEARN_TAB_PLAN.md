# Phase 3 — "Learn" Tab (Ancient Greek On-Ramp): Build Plan

Implements the pedagogical spec at the repo root
(`Anamnesis Phase 3 - Ancient Greek On-Ramp Pedagogical Specification.md`).
A third bottom-nav destination — **📖 Read / 🧠 Train / 🎓 Learn** — that takes a
true beginner from the alphabet to reading Plato's *Euthyphro* with training wheels.

## Source data (authored — `pipeline/data/lessons/`)
- `letters.csv` — the 24 letters: glyph, capital, name, restored-Attic IPA, teaching
  batch (1–4), Latin look-alike + `false_friend` flag, `multistroke` flag, and audio ids.
- `diphthongs.csv` — 8 proper + 3 improper (iota-subscript) diphthongs.
- `minimal_pairs.csv` — length (ε/η, ο/ω), aspiration (π/φ, τ/θ, κ/χ), hidden-quantity pairs.
- `units.json` — the 9-unit curriculum (objective / taught / drills / SRS feed / advance).
- `audio_script.md` — the **recording script** for the restored-Attic audio (critical path).

The DCC core vocab (`pipeline/data/dcc_greek_core.csv`) is reused for the Unit-5+ word deck.

## Architecture (mirrors the content-pack approach)
1. **Lessons pipeline** (`pipeline/build_lessons.py`, to build): read `data/lessons/*`
   → emit a read-only **lessons pack** (SQLite or JSON) + an **audio manifest** (every
   required `snd_*/nm_*/voc_*` id). Bundle the pack in app assets like the content pack;
   keep it out of the encrypted DB.
2. **`:feature:learn`** (new Compose module, depends on `:core:domain`, `:core:ui`, and
   the SRS engine in `:feature:srs`): a data-driven **unit list** with lock/unlock gating,
   and per-drill screens (letter→sound, sound→letter, name recall, minimal-pair
   "same/different", S-Pen tracing, transliteration).
3. **SRS reuse**: letter card families and vocab lemmas flow through the existing FSRS-6
   `ReviewScheduler`. This needs the card model extended with a **deck/source** ("letters",
   "vocab", "euthyphro") and a **card-type** (sound/recognition/name), plus distractor
   metadata for the sound→letter grid. Skill drills (tracing, minimal pairs) run on a
   lighter mastery loop, not FSRS, until reliable.
4. **Audio**: bundled assets keyed by id; a small player in `:core:ui` or `:feature:learn`.
   Until real audio exists, drills run in a **visual-only** mode (and may use Wikimedia
   CC-BY-SA scaffold files with attribution).
5. **Progress / gating**: per-unit progress + the aspirate-default toggle live in DataStore
   (or a small Room table). Advance criteria from `units.json` (≥90% letters, ≥80% length).

## Build order
- [ ] **Audio (start now — external dependency):** record the `audio_script.md` set in
      restored Attic (own or licensed reciter); license it CC-BY-SA/CC-BY so it's bundleable.
- [ ] `build_lessons.py` → lessons pack + audio manifest + pytest (mirror the content pipeline).
- [ ] `:feature:learn` skeleton: unit-list screen (data-driven), gating off a progress store;
      add the 🎓 Learn destination to `MainActivity`.
- [ ] Letter drills (visual-first): letter→sound, sound→letter (distractor grid), name recall;
      wire to FSRS once the card model gains deck/source + card-type.
- [ ] Vowel-quantity / diphthong / minimal-pair listening drills (need audio).
- [ ] Breathings + the "three kinds of variation" (§5) + lemma-vs-form (§4) explainer cards;
      wire tap-to-parse to surface lemma + form + reason-for-difference (depends on Phase-2
      lookup, and on morphology — see Phase 3 morphology item).
- [ ] S-Pen tracing overlays (stroke-order arrows, ghost letterforms) — valued, not gating.
- [ ] **Handoff:** build a *Euthyphro* content pack (Plato `tlg0059.tlg001`) via the existing
      pipeline; seed its opening vocab; Unit 8 reads 2a with full gloss + parse + audio.

## Open decisions
- **Audio sourcing**: record vs. license a reciter (Ranieri / SORGLL) — and the audio license.
- **Card model**: extend the existing `cards` table with `deck`/`cardType`/distractors, or a
  separate `drill_cards` table — decide when wiring letters into FSRS.
- **Aspirate default**: ship true aspirates [pʰ tʰ kʰ] (per spec) with a fallback toggle.
- **Pitch accent**: recognition-only by default; productive pitch as optional "stretch".
