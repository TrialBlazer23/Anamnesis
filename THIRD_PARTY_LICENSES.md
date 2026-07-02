# Third-party licenses & attribution

Anamnesis bundles or derives from the works below. Each carries obligations
(attribution, ShareAlike, OFL embedding terms). Keep this file in sync as
dependencies and data sources are added, and ship it inside the app.

## Fonts
| Work | License | Obligation |
|---|---|---|
| Gentium Plus (SIL) — **6.101** bundled (doc targets 7.000) | SIL OFL 1.1 | `OFL.txt` shipped at `core/ui/src/main/assets/licenses/gentium_ofl.txt`; from the SIL/Google Fonts mirror; embedding/redistribution permitted |
| New Athena Unicode 5.x (secondary fallback) | SIL OFL 1.1 | Same OFL terms; reserved name "New Athena Unicode" |

> **Brill is NOT bundled** — its EULA prohibits redistribution / commercial
> embedding in a non-Brill product. Do not add it without a paid commercial
> license (Tiro Typeworks).

## Libraries
| Work | License | Notes |
|---|---|---|
| FSRS-6 scheduler (clean-room Kotlin port in `:feature:srs`) | MIT | Ported from open-spaced-repetition/FSRS-Kotlin (MIT) and validated against py-fsrs/fsrs-rs; attribution in `Fsrs.kt` header |
| SQLCipher (`net.zetetic:sqlcipher-android`) | BSD-style (Zetetic LLC) | Ship Zetetic notice |

## Texts & data
| Work | License | Obligation |
|---|---|---|
| Perseus / canonical-greekLit TEI | CC BY-SA (per text) | Attribution + ShareAlike |
| Marcus Aurelius *Meditations*, Haines 1916 (Loeb) — from Wikisource (`pipeline/data/haines_1916.epub`) | Public domain (US, pre-1929) | Use Haines — **NOT** Farquharson 1944 |
| DCC Greek Core Vocabulary (`pipeline/data/dcc_greek_core.csv`) | CC BY-SA 3.0 | Attribution + ShareAlike (commercial bundling OK) |
| **Middle Liddell** (Intermediate Greek-English Lexicon, Liddell & Scott) — from PerseusDL/canonical-pdlrefwk; **bundled** in the content pack's `lexicon` table (34,348 entries) | CC BY-SA 4.0 (repo README; file verified free of embedded restrictions) | Credit Perseus (Tufts); keep the availability statement intact; **offer Perseus any modifications**; ShareAlike |
| LSJ — LSJLogeion (helmadik), planned downloadable full-definition pack | CC BY-SA 4.0 (LICENSE.md verified) | Credit Perseus Tufts **and** Helma Dik/Logeion; ShareAlike |
| LSJ — lsj9 (ciscoriordan), possible v2 coverage extension | CC BY 4.0 (LICENSE verified) | Attribution; note upstream base-text provenance is self-described as unverified |
| Perseus treebank_data | CC BY-SA 3.0 US | Attribution + ShareAlike |
| Morpheus / Diogenes generated morphology | data CC BY-SA; **code GPL-3.0** | Bundle generated DATA only — do NOT link the GPL C binaries |
| Rouse, *A Greek Boy at Home* (1909) — digitization by Maddock/Jasinski/Hardison/Binns (`pipeline/data/rouse/`) | Base text: US public domain; digitization: CC BY-SA 4.0 (declared in upstream README, snapshotted as `UPSTREAM_README.md`) | Attribution to Rouse + the digitizers; ShareAlike on the correction layer |
| Dodson Greek Lexicon (`pipeline/data/koine/dodson.csv`) | CC0 1.0 (LICENSE file verified, kept as `DODSON_LICENSE`) | None (public-domain dedication); credit courteous |
| Lyceum / archeion `morph.db` (v2026.04.09) — per-text extractions **bundled** in the content pack's `morphology` table; mirrored as the `lyceum-data-v2026.04.09` release on this repo | CC BY-SA 4.0 (repo LICENSE verified) | Attribution to Lyceum + upstream Perseus/Morpheus lineage; ShareAlike |
| Lyceum / archeion other datasets (lsj.db, CoNLL-U texts) — planned | CC BY-SA 4.0 (repo LICENSE verified) | Attribution + ShareAlike; audit per-edition provenance before shipping `texts.db` wholesale (GLAUx upstream has some CC BY-NC texts) |
| David Chamberlain, Iliad recitations (hypotactic.com) — planned audio packs; manifest at `pipeline/data/audio/` | CC BY 4.0 (per Perseus/Scaife production attribution; **confirm on hypotactic.com before shipping**) | Attribution: "© 2016–2017 David Chamberlain, hypotactic.com, CC BY 4.0" |

> **Do NOT bundle** the blinskey/greek-reference Middle Liddell copies
> (`lexicon.zip` / `Perseus_text_1999.04.0058.xml`): their embedded Perseus
> availability statement carries a **"non-commercial purposes only"** clause.
> Source Middle Liddell/LSJ from PerseusDL/lexica or Lyceum instead.
> Likewise do NOT use the Zenodo **DuckDB** conversion of Diorisis (CC BY-NC-ND);
> only the original figshare XML/JSON (CC BY, verify on download).

## Bundled content pack
The shipped starter pack `app/src/main/assets/content/meditations.db` is a
derivative combining the Perseus Greek (CC BY-SA 4.0), the Haines 1916
translation (public domain), and the DCC Greek Core Vocabulary (CC BY-SA 3.0);
the ShareAlike + attribution obligations above apply to it.

## Audio (Phase 3 "Learn" tab)
Restored-Attic pronunciation audio is **recorded for this project** (see
`pipeline/data/lessons/audio_script.md`); ship it under a bundle-compatible
license (CC-BY-SA or CC-BY recommended). Sourcing rules:
- **Bundleable scaffold:** Wikimedia/Wiktionary "Grc-" files (CC-BY-SA 4.0, some
  CC0) — attribution + ShareAlike; thin coverage, stopgap only.
- **Do NOT bundle without written permission:** SORGLL/Daitz, Ranieri/Polymathy,
  Mastronarde/atticgreek.org (audio is ©UC Regents), Hagel — all all-rights-reserved.
- **Avoid:** eSpeak-NG "grc" TTS is **GPL-v3** (copyleft) and robotic; commercial
  "Ancient Greek TTS" is Modern-Greek voices, not restored Attic.

## Per-text audit required
- Perseus UD conversion (`UD_Ancient_Greek-Perseus`): **CC BY-NC-SA 2.5** (non-commercial).
- GLAUx trees declare CC0, but the source GLAUx corpus is mostly CC BY-SA with
  some texts CC BY-NC — check per-text metadata before redistributing.
