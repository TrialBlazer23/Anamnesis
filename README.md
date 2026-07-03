# Anamnesis (Ἀνάμνησις)

An Ancient Greek Android app: a reader for polytonic Greek texts with facing
public-domain translations, an FSRS-6 spaced-repetition trainer, and a beginner
alphabet-to-reading curriculum — backed by an encrypted on-device database,
plus a Python pipeline that builds the read-only content packs the app ships.

> *anamnesis* (ἀνάμνησις): "recollection" — Plato's idea that learning is
> remembering.

## Status

Working app. Functional today:

- **Read** — *Meditations* (Greek, NFC-normalized, with the facing Haines 1916
  translation), accent-insensitive FTS5 search, and tap-to-parse word lookup
  (DCC core vocab → Middle Liddell → Lyceum morphology).
- **Train** — FSRS-6 spaced repetition over vocabulary and alphabet decks.
- **Learn** — the Ancient Greek On-Ramp: alphabet by batches with
  confusion-aware drills and unit gating.
- **Library** — SHA-256-verified downloads of extra packs (Iliad text pack,
  Iliad Book 1 recitations by David Chamberlain, CC BY).

CI publishes debug APKs and all content packs to the rolling
[`latest` release](../../releases/tag/latest). Remaining work is tracked in
**[IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md)** (next up: audio playback
with read-along highlighting).

## Documentation

- **[Design document](Anamnesis%20Ancient%20Greek%20Android%20App_%20Verified%202026%20Production%20St.md)** — the authoritative, version-pinned 2026 tech-stack spec.
- **[IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md)** — phased, actionable build order.
- **[CLAUDE.md](CLAUDE.md)** — working guide and non-negotiable build constraints.
- **[THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md)** — bundled fonts/data/libraries and their obligations.

## Tech stack (summary)

Kotlin 2.4.0 · AGP 9.1.1 · Gradle 9.6.1 · JDK 17 · Compose BOM 2026.06.00
(Material3 1.4.0) · KSP 2.3.9 · Room 2.8.4 · `net.zetetic:sqlcipher-android`
4.16.0 · FSRS-6 (clean-room Kotlin port) · Gentium Plus (SIL OFL).
compileSdk 37 / targetSdk 35 / minSdk 24. Exact versions live in
[`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Modules

| Module | Purpose |
|---|---|
| `:app` | Compose UI, bottom nav (Read / Train / Learn / Library), entry point |
| `:core:domain` | Pure-Kotlin models, use-cases & the verified pack installer |
| `:core:data` | Room + SQLCipher encrypted SRS DB · content-pack reader · pack library |
| `:core:ui` | Shared design system (bundled Gentium Plus) |
| `:feature:reader` | Text reading, search & tap-to-parse lookup |
| `:feature:srs` | FSRS-6 scheduler & review flow |
| `:feature:learn` | Beginner alphabet curriculum with unit gating |
| `pipeline/` | Python content-pack builder (CI/desktop only) |

## Building

The APK builds in GitHub Actions (no local Android Studio required):

```bash
./gradlew assembleDebug
```

Content packs (CI/desktop):

```bash
cd pipeline && pip install -r requirements-ci.txt
python build_database.py --work tlg0562.tlg001 --out out/meditations.db \
    --title "Meditations" --vocab-csv data/dcc_greek_core.csv \
    --haines-epub data/haines_1916.epub --middle-liddell
python build_audio_pack.py --manifest data/audio/iliad_audio_manifest_books1-2.csv \
    --book 1 --out out/iliad_book1_audio.zip
```

## Continuous integration

Everything builds and tests in **GitHub Actions** — no local Android Studio needed:

| Workflow | Trigger | Does |
|---|---|---|
| `build.yml` | every push / PR | JVM unit tests + debug APK; on `main`, uploads the APK to the `latest` release |
| `pipeline.yml` | PRs/pushes touching `pipeline/` or the workflow, manual | pytest + builds all content packs (Meditations, Iliad, Book 1 audio); on `main`, refreshes the bundled starter pack and publishes everything to the `latest` release |
| `release.yml` | version tag `v*` / manual | signed **release** APK (needs the four signing secrets — see the workflow header) |

## License

**GPLv3** — see [`LICENSE`](LICENSE). Bundled third-party works retain their own
licenses; see `THIRD_PARTY_LICENSES.md` and `NOTICE`.
