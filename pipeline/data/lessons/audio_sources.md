# Audio sourcing options (Learn tab) — research & decision

**Decision for now: build the Learn tab audio-OPTIONAL.** The alphabet + visual
learning path needs no audio (letters are learned by glyph + name + IPA). Audio is
a later enhancement; options below, ranked for a *free / bundleable* result.

## Options

| Source | License | Restored Attic? | Coverage | Verdict |
|---|---|---|---|---|
| **Wikimedia Commons / Lingua Libre `grc`** | CC-BY-SA 4.0 (some CC0) | varies per file | thin (~25 files in "Ancient Greek pronunciation"); not guaranteed full 24-letter set | ✅ **Bundleable scaffold** with attribution; fill gaps later |
| **Mastronarde / atticgreek.org** | © UC Regents (audio) | Yes (5th–4th c. compromise) | per-letter + 100+ words | ✉️ **Ask permission** — best quality; free if granted for a non-commercial/educational app |
| First Greek Book (daedalus.umkc.edu) | CC **BY-NC-SA** 4.0 | classroom | letters + texts | ⚠️ **NonCommercial** → don't bundle (NC clashes with a possibly-commercial GPLv3 app) |
| Ranieri / SORGLL / Hagel | All rights reserved | Yes | recitations | 💲 Direct (likely paid) license only |
| eSpeak-NG `grc` TTS | GPL-v3 | no (robotic) | any | ❌ Avoid (copyleft + poor quality) |

## Recommended path (free-first)
1. **Now:** ship the visual alphabet/learning path with no audio.
2. **Free-ish audio, in order to try:**
   - **Email DCC / Mastronarde (UC)** for permission to use their restored-Attic
     letter (and word) audio in this educational app — best quality, free if granted.
   - Use **Wikimedia Lingua Libre `grc`** (CC-BY-SA) for whatever letters/words are
     covered, with attribution, as an immediate scaffold.
   - **Record our own** against `audio_script.md` for complete, consistent coverage —
     the most reliable option, and doubles as the maintainer's own pronunciation practice.
3. **Avoid bundling:** First Greek Book (NC), SORGLL/Ranieri/Hagel (ARR) without
   written permission.

## App design implication
The drill/data model treats audio ids as **optional**: a letter/word renders and is
drillable from glyph + name + IPA alone; an audio button appears only when an asset
for that id is present. This lets the Learn tab ship fully now and gain sound later
with zero schema changes.

Sources:
- Wikimedia Commons — Category: Lingua Libre pronunciation-grc / Ancient Greek pronunciation (CC-BY-SA/CC0).
- atticgreek.org Pronunciation Guide (Mastronarde; audio © UC Regents).
- First Greek Book digital tutorial (daedalus.umkc.edu; CC BY-NC-SA 4.0).
