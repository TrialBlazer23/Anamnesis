# Anamnesis (Ἀνάμνησις)

An Ancient Greek Android app: a reader for polytonic Greek texts with facing
public-domain translations and an FSRS-6 spaced-repetition trainer, backed by an
encrypted on-device database — plus a Python pipeline that builds the read-only
content packs the app ships.

> *anamnesis* (ἀνάμνησις): "recollection" — Plato's idea that learning is
> remembering.

## Status

Early scaffold. The repository structure, build configuration, CI, and plan are
in place; feature work has not started. See **[IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md)**.

## Documentation

- **[Design document](Anamnesis%20Ancient%20Greek%20Android%20App_%20Verified%202026%20Production%20St.md)** — the authoritative, version-pinned 2026 tech-stack spec.
- **[IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md)** — phased, actionable build order.
- **[CLAUDE.md](CLAUDE.md)** — working guide and non-negotiable build constraints.
- **[THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md)** — bundled fonts/data/libraries and their obligations.

## Tech stack (summary)

Kotlin 2.4.0 · AGP 9.1.1 · Gradle 9.6.1 · JDK 17 · Compose BOM 2026.06.00
(Material3 1.4.0) · KSP 2.3.9 · Room 2.8.4 · `net.zetetic:sqlcipher-android`
4.16.0 · FSRS-6 (vendored FSRS-Kotlin) · Gentium Plus 7.000 (SIL OFL).
compileSdk 37 / targetSdk 35 / minSdk 24. Exact versions live in
[`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Modules

| Module | Purpose |
|---|---|
| `:app` | Compose UI, navigation, entry point |
| `:core:domain` | Pure-Kotlin models & use-cases |
| `:core:data` | Room + SQLCipher encrypted DB |
| `:feature:reader` | Text reading & lookup |
| `:feature:srs` | FSRS-6 scheduler |
| `pipeline/` | Python content-pack builder (CI/desktop only) |

## Building

The APK builds in GitHub Actions (no local Android Studio required):

```bash
./gradlew assembleDebug
```

Content packs (CI/desktop — pulls CLTK):

```bash
cd pipeline && pip install -r requirements-ci.txt
python build_database.py --work tlg0562.tlg001 --out out/meditations.db
```

## License

Intended: **GPLv3** (per the design doc) — to be confirmed. Bundled third-party
works retain their own licenses; see `THIRD_PARTY_LICENSES.md` and `NOTICE`.
