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
| LSJ — lsj9 (ciscoriordan) | CC BY 4.0 | Attribution |
| LSJ — LSJLogeion (helmadik) | Credit Perseus Tufts + Helma Dik/Logeion | Attribution |
| Perseus treebank_data | CC BY-SA 3.0 US | Attribution + ShareAlike |
| Morpheus / Diogenes generated morphology | data CC BY-SA; **code GPL-3.0** | Bundle generated DATA only — do NOT link the GPL C binaries |

## Bundled content pack
The shipped starter pack `app/src/main/assets/content/meditations.db` is a
derivative combining the Perseus Greek (CC BY-SA 4.0), the Haines 1916
translation (public domain), and the DCC Greek Core Vocabulary (CC BY-SA 3.0);
the ShareAlike + attribution obligations above apply to it.

## Per-text audit required
- Perseus UD conversion (`UD_Ancient_Greek-Perseus`): **CC BY-NC-SA 2.5** (non-commercial).
- GLAUx trees declare CC0, but the source GLAUx corpus is mostly CC BY-SA with
  some texts CC BY-NC — check per-text metadata before redistributing.
