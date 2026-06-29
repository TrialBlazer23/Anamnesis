# CLAUDE.md

Guidance for Claude Code (and humans) working in this repository.

## What this is

**Anamnesis** is an Ancient Greek Android app — a reader for polytonic Greek
texts with facing public-domain translations and an FSRS-6 spaced-repetition
trainer — plus a **Python data pipeline** that builds the read-only content
databases the app ships.

The authoritative spec is the design document at the repo root:
`Anamnesis Ancient Greek Android App_ Verified 2026 Production St.md`. It pins a
fully self-consistent 2026 tech stack and explains every version choice. **Read
it before changing build configuration or dependencies.** The actionable build
order lives in `IMPLEMENTATION_PLAN.md`.

## Layout

```
app/            Compose UI, navigation, entry point (com.anamnesis)
core/domain/    Pure-Kotlin models & use-cases (no Android/Room deps)
core/data/      Room 2.8.4 + sqlcipher-android — KSP + room plugin applied HERE ONLY
feature/reader/ Text reading & lookup (Compose)
feature/srs/    Vendored FSRS-Kotlin (FSRS-6) scheduler
pipeline/       Python content-pack builder (CI/desktop only)
gradle/libs.versions.toml   Version catalog — single source of truth for versions
.github/workflows/          build.yml (APK), pipeline.yml (content packs)
```

## Build & test

The app builds in **GitHub Actions** with JDK 17 + the checked-in Gradle wrapper
(9.6.1) + `android-actions/setup-android`. There is no expectation of a local
Android Studio. The CI runner provisions the Android SDK so `compileSdk 37`
resolves.

```bash
./gradlew assembleDebug          # debug APK
./gradlew test                   # JVM unit tests
./gradlew connectedAndroidTest   # instrumentation tests (needs a device/emulator)
```

Pipeline:
```bash
cd pipeline && pip install -r requirements-ci.txt
python build_database.py --work tlg0562.tlg001 --out out/meditations.db
```

> Note: the design stack targets **June-2026 versions** (Kotlin 2.4.0, AGP 9.1.1,
> Gradle 9.6.1). Some are not resolvable in older sandboxes — trust CI for the
> real build. Several AndroidX patch versions in the catalog are marked
> `# verify` and should be confirmed against the AndroidX release page.

## Non-negotiable constraints (from the design doc)

These are easy to get wrong; they break the build or violate a license.

- **Kotlin version == Compose Compiler plugin version** (both `2.4.0`). The
  Compose compiler is the `org.jetbrains.kotlin.plugin.compose` plugin; the old
  `composeOptions { kotlinCompilerExtensionVersion }` is gone.
- **AGP 9 has built-in Kotlin.** Do NOT apply `org.jetbrains.kotlin.android` in
  Android modules. Use the top-level `kotlin { compilerOptions {} }` block, not
  `android { kotlinOptions {} }`. **kapt is incompatible** — use KSP only.
- **KSP2 only** (KSP1 is dead on Kotlin 2.3+/AGP 9+). KSP `2.3.9`.
- **Room 2.8.4, NOT `androidx.room3`.** Room 3.0 removes the `SupportFactory`
  path SQLCipher needs. Never mix `androidx.room` and `androidx.room3`.
- **SQLCipher = `net.zetetic:sqlcipher-android` 4.16.0.** The legacy
  `android-database-sqlcipher` is EOL and lacks 16KB-page support (now required
  by Google Play). Passphrase goes via the constructor / `SupportOpenHelperFactory`.
- **SDK levels:** compileSdk 37, targetSdk 35, minSdk 24. (The design doc says
  compileSdk 36, but `androidx.lifecycle` 2.11.0 requires an API-37 floor and
  AGP 9.1 supports up to 37; compileSdk may run ahead of targetSdk.)
- **Fonts:** bundle **Gentium Plus 7.000** (SIL OFL). New Athena Unicode is the
  secondary fallback. **Never bundle Brill** (EULA forbids it).
- **Greek text is NFC-normalized** for display; keep a separate diacritic-stripped
  column/key for accent-insensitive search.
- **Translations:** Haines 1916 (public domain). **Not** Farquharson 1944.
- **CLTK runs in CI/desktop only** (it pulls torch/stanza) — never on-device.
- **GitHub Actions:** `actions/setup-java@v4`, `actions/upload-artifact@v4`
  (v3 is deprecated).

## Licensing

Project license is **GPLv3** (see `LICENSE`) — compatible with the GPLv3
Alpheios/Diogenes lineage and CC BY-SA data. Every bundled font/library/text has attribution or ShareAlike
obligations tracked in `THIRD_PARTY_LICENSES.md`; keep it current and ship it in
the app. Bundle only Morpheus/Diogenes *generated data*, never the GPL C
binaries, to keep GPL code out of the APK.

## Conventions

- Kotlin sources live under `src/main/kotlin/...`.
- Package root is `com.anamnesis`; module packages are `com.anamnesis.<module>`.
- Add/raise versions in `gradle/libs.versions.toml` only — don't hardcode
  versions in module build files.
- When bumping Kotlin, bump the Compose Compiler plugin in lockstep (same `ref`).
