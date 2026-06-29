# Third-party licenses & attribution

Anamnesis bundles or derives from the works below. Each carries obligations
(attribution, ShareAlike, OFL embedding terms). Keep this file in sync as
dependencies and data sources are added, and ship it inside the app.

## Fonts
| Work | License | Obligation |
|---|---|---|
| Gentium / Gentium Plus 7.000 (SIL) | SIL OFL 1.1 | Bundle `OFL.txt`; reserved-name rules; embedding/redistribution permitted |
| New Athena Unicode 5.x (secondary fallback) | SIL OFL 1.1 | Same OFL terms; reserved name "New Athena Unicode" |

> **Brill is NOT bundled** — its EULA prohibits redistribution / commercial
> embedding in a non-Brill product. Do not add it without a paid commercial
> license (Tiro Typeworks).

## Libraries
| Work | License | Notes |
|---|---|---|
| FSRS-Kotlin (vendored source, FSRS-6) | MIT | Retain LICENSE in `:feature:srs`; from open-spaced-repetition/FSRS-Kotlin |
| SQLCipher (`net.zetetic:sqlcipher-android`) | BSD-style (Zetetic LLC) | Ship Zetetic notice |

## Texts & data
| Work | License | Obligation |
|---|---|---|
| Perseus / canonical-greekLit TEI | CC BY-SA (per text) | Attribution + ShareAlike |
| Marcus Aurelius *Meditations*, Haines 1916 (Loeb) | Public domain (US, pre-1929) | Use Haines — **NOT** Farquharson 1944 |
| DCC Greek Core Vocabulary | CC BY-SA 3.0 | Attribution + ShareAlike (commercial bundling OK) |
| LSJ — lsj9 (ciscoriordan) | CC BY 4.0 | Attribution |
| LSJ — LSJLogeion (helmadik) | Credit Perseus Tufts + Helma Dik/Logeion | Attribution |
| Perseus treebank_data | CC BY-SA 3.0 US | Attribution + ShareAlike |
| Morpheus / Diogenes generated morphology | data CC BY-SA; **code GPL-3.0** | Bundle generated DATA only — do NOT link the GPL C binaries |

## Per-text audit required
- Perseus UD conversion (`UD_Ancient_Greek-Perseus`): **CC BY-NC-SA 2.5** (non-commercial).
- GLAUx trees declare CC0, but the source GLAUx corpus is mostly CC BY-SA with
  some texts CC BY-NC — check per-text metadata before redistributing.
