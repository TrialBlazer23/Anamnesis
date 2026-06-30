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

## Continuous integration

Everything builds and tests in **GitHub Actions** — no local Android Studio needed:

| Workflow | Trigger | Does |
|---|---|---|
| `build.yml` | every push / PR | JVM unit tests (`./gradlew test`, incl. FSRS-6) + debug APK; uploads APK & test reports |
| `pipeline.yml` | PRs touching `pipeline/`, pushes to `main`, manual | pytest + builds the Meditations content pack; uploads DB + manifest |
| `release.yml` | version tag `v*` / manual | signed **release** APK |

**What's automated:** debug builds, all unit tests, content-pack builds. Nothing
is needed from you for those.

**What needs you (one-time / when relevant):**
- **Release signing** — add four repo secrets before tagging a release:
  `SIGNING_KEY_BASE64`, `SIGNING_KEY_ALIAS`, `SIGNING_STORE_PASSWORD`,
  `SIGNING_KEY_PASSWORD` (see the header of `release.yml` for how to make a keystore).
- **Full content data** — the Greek text is fetched automatically; the **DCC
  vocabulary CSV** and **Haines 1916 translation** sources are network-blocked
  from the build sandbox, so drop those files in (or add a fetch step) when ready.
  The pipeline already accepts them via `--vocab-csv` / `--translation`.
- **Fonts** — bundle Gentium Plus 7.000 + `OFL.txt` (Phase 2 task).

## License

**GPLv3** — see [`LICENSE`](LICENSE). Bundled third-party works retain their own
licenses; see `THIRD_PARTY_LICENSES.md` and `NOTICE`.
