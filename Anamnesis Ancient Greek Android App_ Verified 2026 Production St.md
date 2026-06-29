# Verified Production-Ready Tech Stack for "Anamnesis" — Ancient Greek Android App + Python Pipeline (June 2026)

## TL;DR
- A fully self-consistent 2026 stack exists: **Kotlin 2.4.0 / AGP 9.1.1 / Gradle 9.6.1 / JDK 17 / Compose BOM 2026.06.00 (Material3 1.4.0) / KSP 2.3.9 / Room 2.8.4**, with the Compose Compiler now pinned to the Kotlin version via `org.jetbrains.kotlin.plugin.compose`. AGP 9 has built-in Kotlin, so you do **not** apply `org.jetbrains.kotlin.android`.
- Use **`net.zetetic:sqlcipher-android` 4.16.0** (the old `android-database-sqlcipher` is deprecated and EOL), **Gentium Plus / Gentium 7.000** as the bundled polytonic font (SIL OFL — Brill is NOT bundleable), and the **FSRS-Kotlin (FSRS-6) source port** for spaced repetition. Run **CLTK only in CI** (it pulls torch/stanza); keep on-device Python to lxml + sqlite3 + betacode.
- Target **compileSdk 36 / targetSdk 35 / minSdk 24**; build debug+release APKs entirely in GitHub Actions with `ubuntu-latest`, `actions/setup-java@v4` (Temurin 17), and the checked-in Gradle wrapper — no local Android Studio needed.

---

## Key Findings — Verified Version Table

### Android build chain (compatibility-critical)
| Component | Coordinate / ID | Version | Source |
|---|---|---|---|
| Kotlin | `org.jetbrains.kotlin` | **2.4.0** (released June 2026 per the JetBrains Kotlin blog "Kotlin 2.4.0 Released", blog.jetbrains.com/kotlin/2026/06/; developer coverage dates it ~June 3, 2026. As of Jun 24 2026 the next release, 2.4.20, is still in beta) | kotlinlang.org/docs/whatsnew24.html; JetBrains blog |
| Kotlin Compose Compiler plugin | `org.jetbrains.kotlin.plugin.compose` | **pinned to Kotlin = 2.4.0** | developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler |
| Android Gradle Plugin | `com.android.application` | **9.1.1** (April 2026); 9.2 in alpha; 9.0.1 was Jan 2026 | developer.android.com/build/releases/agp-9-1-0-release-notes |
| Gradle | wrapper | **9.6.1** — "the first patch release for Gradle 9.6.0 (released 2026-06-27)" per docs.gradle.org release notes | gradle.org/releases |
| JDK | — | **17** (required minimum and default for AGP 9.x) | AGP 9.0/9.1 notes |
| KSP | `com.google.devtools.ksp` | **2.3.9** (KSP2; since KSP 2.3.0 the version is decoupled from Kotlin and works with Kotlin 2.2+) | github.com/google/ksp/releases |
| Compose BOM | `androidx.compose:compose-bom` | **2026.06.00** (stable BOM maps core Compose modules to **1.11.3**) | developer.android.com/develop/ui/compose/bom; jetc.dev |
| Material3 | `androidx.compose.material3:material3` | **1.4.0** (the stable version pulled by BOM 2026.06.00) | developer.android.com/jetpack/androidx/releases/compose-material3 |

**Note on AGP 9 + Kotlin 2.4.0:** AGP 9.0 carries a runtime dependency on Kotlin Gradle Plugin (KGP) **2.2.10** and will auto-upgrade KGP/KSP to at least 2.2.10 / 2.2.10-2.0.2 if you declare lower. To use Kotlin 2.4.0 you must bring the newer KGP onto the build classpath — either declare `kotlin("jvm") version "2.4.0" apply false` in the root `plugins{}` block, or add `classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.0")` in `buildscript`. **KSP1 is dead** — incompatible with Kotlin 2.3+ and AGP 9+; KSP2 only.

### AndroidX libraries
| Library | Coordinate | Version | Source |
|---|---|---|---|
| Core KTX | `androidx.core:core-ktx` | ~1.16.x (current stable family) | AndroidX versions page |
| Activity Compose | `androidx.activity:activity-compose` | current stable ~1.10.x | AndroidX |
| Lifecycle (viewmodel-compose, runtime-compose) | `androidx.lifecycle:*` | **2.11.0** [Android Developers](https://developer.android.com/jetpack/androidx/releases/lifecycle) | developer.android.com/jetpack/androidx/releases/lifecycle |
| Navigation Compose | `androidx.navigation:navigation-compose` | current stable 2.9.x | AndroidX |
| DataStore Preferences | `androidx.datastore:datastore-preferences` | current stable 1.2.x (1.3.0 in alpha) | developer.android.com/jetpack/androidx/releases/datastore |
| Room | `androidx.room:room-runtime` / `room-ktx` / `room-compiler` | **2.8.4** | developer.android.com/kotlin/multiplatform/room |
| SQLite (AndroidX) | `androidx.sqlite:sqlite` / `sqlite-bundled` | **2.6.2** | Room KMP docs / Zetetic integration guide |
| Coroutines | `org.jetbrains.kotlinx:kotlinx-coroutines-android` | **1.11.0** | kotlinlang.org; mvnrepository |
| Serialization JSON | `org.jetbrains.kotlinx:kotlinx-serialization-json` | **1.11.0** (plugin `org.jetbrains.kotlin.plugin.serialization` pinned to Kotlin) | github.com/Kotlin/kotlinx.serialization |

### Room + FTS + encryption
- Room **2.8.4** is the current 2.x stable; the 2.x line is now in **maintenance mode** (patch releases only). **Room 3.0 / `androidx.room3`** is the new package (new Maven group `androidx.room3:room3-runtime`) with breaking changes: **KSP-only, coroutine-first DAOs, drops SupportSQLite, Kotlin-only code generation, requires coroutines**.
- **Recommendation: use Room 2.8.4**, because SQLCipher integration relies on the SupportSQLite `SupportFactory`/`openHelperFactory` path that Room 3.0 removes. Re-evaluate only when Room 3.0 ships a documented `SQLiteDriver`-based SQLCipher path.
- FTS: declare an entity with **`@Fts4`** (Room supports FTS3/FTS4 annotations; `@Fts4(tokenizer = ...)`). FTS5 is available in the underlying SQLite/SQLCipher engine, but Room's annotation surface is `@Fts4`. For Greek, store a normalized, diacritic-stripped column alongside the original for accent-insensitive full-text search.
- Room 2.8 turns on Kotlin code generation by default when using KSP, targets Kotlin language 2.0+, and recommends KSP2.

### SQLCipher
- **`net.zetetic:sqlcipher-android` 4.16.0** is current — confirmed via the Maven Central / Sonatype POM (`central.sonatype.com/artifact/net.zetetic/sqlcipher-android`): `<artifactId>sqlcipher-android</artifactId> <version>4.16.0</version>`. Community Edition usage: `implementation 'net.zetetic:sqlcipher-android:4.16.0@aar'` + `implementation 'androidx.sqlite:sqlite:2.6.2'`. License: BSD-style from Zetetic LLC. Min API 23 (armeabi-v7a, x86, x86_64, arm64-v8a).
- **`net.zetetic:android-database-sqlcipher` is DEPRECATED and end-of-life.** Per Zetetic's June 26 2025 advisory: the modern `sqlcipher-android` library "has fully supported 16KB page sizes since version 4.6.1, [while] the legacy Community Edition of `android-database-sqlcipher` … was deprecated in 2022 and reached end-of-life in 2023" (zetetic.net/blog/2025/06/26/). Google Play now requires 16KB-page compatibility, making the migration mandatory.
- Integration with Room: pass the passphrase through `SupportOpenHelperFactory`/`SupportFactory` into Room's `openHelperFactory(...)`. Note in the new library the passphrase is supplied via the constructor (not via `getWritableDatabase(password)`).

### FSRS (spaced repetition)
- Latest algorithm: **FSRS-6**, a 21-parameter model. Confirmed by the FSRS-Kotlin README and py-fsrs 6.3.1: 21 model weights with defaults `[0.212, 1.2931, … 0.0658, 0.1542]`, the 21st parameter (decay) = **0.1542**.
- **Java-FSRS** — `io.github.open-spaced-repetition:fsrs` **1.0.0** (released July 30 2025), **MIT**, requires Java 17, published on Maven Central. Pure-Java reimplementation, usable directly from Kotlin. ~14 stars, single primary maintainer (joshdavham); class namespace `io.github.openspacedrepetition`. ⚠️ The README/Maven metadata do **not** explicitly state the FSRS algorithm version — verify in source before depending on the binary.
- **FSRS-Kotlin** — `open-spaced-repetition/FSRS-Kotlin`, **MIT**, explicitly **FSRS-6**. **Source-only** (no Maven coordinate); small repo; idiomatic Kotlin (`FSRS(deck.retention, deck.params)`).
- **Recommendation:** **Vendor/port the FSRS-Kotlin source** (explicit FSRS-6, idiomatic Kotlin, MIT, easy to audit) into `:feature:srs`. Alternatively depend on the published **Java-FSRS 1.0.0** Maven artifact if you prefer a binary dependency — but first confirm its algorithm version. Canonical references to port/validate against: `py-fsrs` and `fsrs-rs` (both FSRS-6).

### Testing
- **JUnit 4** (`junit:junit:4.13.2`) remains the default for Android instrumentation; JUnit 5/Jupiter is fine for pure-JVM unit tests, but Android instrumentation tooling still centers on JUnit 4.
- `androidx.test.ext:junit` (~1.2.x), `androidx.test.espresso:espresso-core` (~3.6.x), and Compose UI test `androidx.compose.ui:ui-test-junit4` + `ui-test-manifest` (versions from the BOM).
- ⚠️ The **Jetpack Compose April '26 release made the v2 testing APIs the default** (StandardTestDispatcher), deprecating the v1 APIs. Tests that relied on the old immediate-execution dispatcher (UnconfinedTestDispatcher) must advance the virtual clock explicitly.

### SDK levels
- Google Play (since Aug 31 2025): new apps and updates must **target API level 35+** (Android 15). compileSdk can be **36** (Android 16; AGP 9.1 supports up to API 37). 
- **Recommended: compileSdk 36, targetSdk 35, minSdk 24.** minSdk 24 supports Compose, the broad device base, and clears SQLCipher's minimum of API 23; many AndroidX libraries moved their floor to minSdk 23 in mid-2025, so 24 is safe everywhere.

---

## Polytonic Greek Fonts
| Font | Version | License | Bundleable? | Polytonic coverage |
|---|---|---|---|---|
| **Gentium / Gentium Plus** (SIL) | **7.000** (major release unifying Gentium Plus/Basic/Book; SIL release Jun 2 2025, CTAN records 2025-07-13) | SIL OFL 1.1 | ✅ Yes | Full polytonic + monotonic Greek [SIL Language Technology](https://software.sil.org/gentium/) ; careful diacritic positioning |
| New Athena Unicode | 5.x | SIL OFL 1.1 (reserved name "New Athena Unicode") | ✅ Yes | Full polytonic + Coptic, metrical [Font Squirrel](https://www.fontsquirrel.com/fonts/new-athena-unicode) , epigraphic |
| Greek Font Society (GFS) | varies | OFL (most faces) | ✅ Yes | Many faces full polytonic |
| Brill | — | Proprietary EULA | ❌ **NO** | Full polytonic |

**Recommendation: bundle Gentium Plus / Gentium 7.000** as the single primary font. Justification: SIL OFL 1.1 explicitly permits embedding, bundling, and redistribution within software/apps (the document or app produced is unrestricted); it has complete, well-tested polytonic coverage (breathing marks, accents, iota subscript) with careful diacritic positioning; it is actively maintained by SIL [SIL Language Technology](https://software.sil.org/gentium/) and is the same font Diogenes ships. **Do NOT bundle Brill** — its EULA permits only free, non-commercial, unmodified embedding, explicitly prohibits redistribution and commercial embedding in any non-Brill product (Open Access counts as commercial), and treats bundling in a third-party app as prohibited. A paid commercial license is available via Tiro Typeworks if Brill's specific design is ever required. Keep **New Athena Unicode** (also OFL) as a secondary fallback for rare epigraphic/metrical symbols Gentium may lack.

**Rendering caveat (Android/Compose):** Compose text uses HarfBuzz, which renders OpenType `ccmp`/`mark` features correctly, so prefer **NFC-normalized (precomposed) text**. Some combining-diacritic sequences (e.g., alpha + breve + breathing + accent) render correctly only via the font's `ccmp` tables — Gentium and New Athena both ship these. Normalize all pipeline output to NFC and maintain a separate diacritic-stripped key column for search.

---

## Python Data Pipeline
| Component | Version | Notes / Source |
|---|---|---|
| Python | 3.12 (installs in Termux) | CLTK 1.5.0 supports 3.12 [GitHub](https://github.com/cltk/cltk/releases) |
| CLTK | **1.5.0** (MIT) [Libraries.io](https://libraries.io/pypi/cltk) | pulls **stanza/torch** (heavy, GPU-oriented) — **run in CI/desktop only, NOT on-device** |
| lxml | current stable | TEI XML parsing (preferred over BeautifulSoup for TEI's namespaces/XPath) |
| httpx or requests | current | fetching ATLAS / CTS passages |
| sqlite3 (stdlib) | bundled | **FTS5 is available** in standard CPython sqlite3 |
| betacode | **1.0** (matgrioni/betacode, PyPI) [PyPI](https://pypi.org/project/betacode/) | Beta Code ↔ Unicode; lightweight, no heavy deps; uses polytonic oxeîa accents |
| greek-accentuation | **1.2.0** (jtauber, MIT) [Libraries.io](https://libraries.io/pypi/greek-accentuation) | accentuation analysis + syllabification |
| unicodedata | stdlib | NFC/NFD normalization |

**Recommendation:** Run **CLTK only in CI/desktop** to build the databases — it depends on torch/stanza, which are problematic to install in Termux/proot on a phone. On-device Python (if any) stays minimal: `lxml` + `sqlite3` + `betacode` + `greek-accentuation` + `unicodedata`. Build the SQLite/FTS5 content databases in CI and ship them as downloadable/bundled content packs the Android app opens read-only (or imports into the encrypted user DB).

---

## Data Sources (availability, access method, license)
- **Perseus / canonical-greekLit:** `github.com/PerseusDL/canonical-greekLit` — TEI XML, CTS URN scheme (`urn:cts:greekLit:tlgXXXX.tlgXXX...`). Scaife Viewer (`scaife.perseus.org`) and ATLAS endpoints serve passages as text/xml. CC-BY-SA / CC licensing. Marcus Aurelius = textgroup `tlg0562`, work **`tlg0562.tlg001`** ("Ad Se Ipsum"); editions include `perseus-grc2` and the Leopold OCT-based `opp-grc2`.
- **Morpheus (Alpheios fork):** `github.com/alpheios-project/morpheus` — includes built binaries + Greek stemlibs; [GitHub](https://github.com/alpheios-project/morpheus) [Digital Classicist](https://wiki.digitalclassicist.org/Morpheus) code **GPL-3.0**, data **CC-BY-SA** (Alpheios policy). Binaries are old (may not run on modern OSes; 2024 build workflow present). **Diogenes alternative:** `github.com/pjheslin/diogenes` (**GPLv3**) with prebuilt morphology downloadable at `github.com/pjheslin/diogenes-prebuilt-data` and **`github.com/pjheslin/morpheus-v3`** (Morpheus v3 output, easiest to reuse for those who cannot compile Morpheus). **GPL copyleft applies if you link/bundle the C binaries** — use only the *generated morphology data* (with its own license) to avoid linking GPL code into the app.
- **Treebanks:** `github.com/PerseusDL/treebank_data` — **CC BY-SA 3.0 US** (Greek data under `/v2.1/Greek`, `/AGDT2/`). The Universal Dependencies conversion `UD_Ancient_Greek-Perseus` is more restrictive: **CC BY-NC-SA 2.5**. **GLAUx trees:** `github.com/perseids-publications/glaux-trees` declares **CC0 1.0**, but the source GLAUx corpus (`github.com/alekkeersmaekers/glaux`) is mostly CC BY-SA with **some texts CC BY-NC** — check per-text metadata before redistribution.
- **LSJ / lexica:** `github.com/helmadik/LSJLogeion` (Logeion-corrected LSJ XML; credit Perseus Tufts + Helma Dik/Logeion); `github.com/perseids-project/lsj-js` (single JSON blob, offline-ready web app); `github.com/ciscoriordan/lsj9` (structured/OCR-corrected, **CC BY 4.0**, includes POS/genitive/etymology fields). The base LSJ 9th ed. (1940) text is public domain; the digital versions derive from Perseus's openly-licensed electronic text. Middle Liddell ("Intermediate") is available via Perseus.
- **DCC Greek Core Vocabulary:** `dcc.dickinson.edu/vocab/core-vocabulary` — "about 500 of the most common words in ancient Greek, the lemmas that generate approximately 65% of the word forms in a typical Greek text" (created summer 2012 by Christopher Francese; per the print edition, 66% of the words in Sophocles' *Antigone* and 82% of Plato's *Euthyphro* are in the DCC Greek core). Downloadable as CSV/xls with columns Headword, Definition, Part of Speech, Semantic Group, Frequency. License **CC BY-SA 3.0** — **NOT NonCommercial**, so commercial app bundling is permitted with attribution + ShareAlike. (This corrects the task's "CC BY-SA-NC?" assumption.)
- **Marcus Aurelius, *Meditations*:** Haines 1916 Loeb (Greek + facing English) on Internet Archive (`archive.org/details/thecommuningswit00marcuoft`, IA metadata flags "Out_of_copyright") and Wikisource (`en.wikisource.org/wiki/Marcus_Aurelius_(Haines_1916)`) — **public domain in the US** (1916 publication, pre-1929). Greek text also on Scaife/canonical-greekLit (`tlg0562.tlg001`), itself out of copyright. **Farquharson 1944 is NOT public domain** (first published 1944, post-1929; 95-year term, likely still under copyright) — **use Haines 1916** (or the older Long translation) for the bundled facing text.

---

## Compatibility Constraints (the critical matrix)
- **Kotlin ↔ Compose Compiler:** identical version (**2.4.0**). Since Kotlin 2.0 the compiler moved to the `org.jetbrains.kotlin.plugin.compose` Gradle plugin with `version.ref = "kotlin"`. The old `composeOptions { kotlinCompilerExtensionVersion }` is gone.
- **Kotlin ↔ KSP:** KSP 2.3.x is **decoupled** from the Kotlin version (KSP 2.3.0+ works with Kotlin 2.2+). Use **KSP 2.3.9** with Kotlin 2.4.0. ⚠️ KSP 2.3.9 + Kotlin 2.4.0 has one reported codegen issue with `internal` Hilt/Dagger `@Provides` functions (triggered by Kotlin 2.4.0's "consistent module names across platforms" change) — watch for it if you use Hilt.
- **AGP ↔ Gradle ↔ JDK:** AGP **9.1.1** requires **Gradle ≥ 9.1.0** (use **9.6.1**) and **JDK 17**. Coming from AGP 8.x → Gradle 8.x is a hard requirement to upgrade to Gradle 9.x.
- **AGP 9 built-in Kotlin:** do **NOT** apply `org.jetbrains.kotlin.android`; replace `android { kotlinOptions {} }` with top-level `kotlin { compilerOptions {} }`; **kapt is incompatible** with built-in Kotlin (use KSP, or `com.android.legacy-kapt` only as a temporary fallback). To run Kotlin 2.4.0 above AGP's bundled KGP 2.2.10, bring the newer KGP onto the classpath (see note above).
- **Room ↔ KSP/Kotlin:** Room 2.8.4 works with KSP2 + Kotlin 2.0+.
- **minSdk alignment:** sqlcipher-android requires ≥ API 23; several AndroidX libraries moved to minSdk 23 in mid-2025. **minSdk 24 satisfies all** dependencies in this stack.

---

## 2026-Specific Gotchas / Deprecations
- **`android-database-sqlcipher` → deprecated & EOL.** Migrate to `sqlcipher-android` (passphrase via constructor; 16KB-page support since 4.6.1, required by Google Play).
- **KSP1 removed** from the Kotlin 2.3+/AGP 9+ world — KSP2 only.
- **AGP 9 built-in Kotlin** breaks legacy build scripts (`kotlinOptions`, `kotlin-android` plugin, kapt). The opt-out flags (`android.builtInKotlin=false`, `android.newDsl=false`) will be **removed in AGP 10** (expected ~H2 2026).
- **Room 3.0 (`androidx.room3`)** is a separate Maven package with breaking changes — do not accidentally mix `androidx.room` and `androidx.room3` (and note WorkManager and other libraries still depend transitively on Room 2.x).
- **Compose v2 test APIs** are now default (StandardTestDispatcher); v1 deprecated.
- **`actions/upload-artifact@v3` is deprecated** in GitHub Actions — use v4.

---

## Recommended GitHub Actions APK-build workflow (outline)
```yaml
name: Build APK
on:
  push: { branches: [ main ] }
  workflow_dispatch:
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'   # JDK 17 matches AGP 9 requirement
          java-version: '17'
      - name: Setup Android SDK
        uses: android-actions/setup-android@v3
      - name: Cache Gradle
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/libs.versions.toml') }}
      - run: chmod +x ./gradlew
      - run: ./gradlew assembleDebug
      - uses: actions/upload-artifact@v4
        with:
          name: anamnesis-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
```
**For release:** add a job running `./gradlew assembleRelease`, sign with `r0adkll/sign-android-release@v1` (base64 keystore + alias/passwords stored as GitHub Secrets), then upload as an artifact or attach to a GitHub Release. 

**Gotchas:** use `actions/setup-java@v4` and `actions/upload-artifact@v4` (v3 deprecated); JDK 17 matches AGP 9; the checked-in Gradle wrapper (9.6.1) runs cleanly on `ubuntu-latest` without Android Studio. The wrapper is what the runner executes — commit `gradle/wrapper/gradle-wrapper.jar` and `gradle-wrapper.properties`. The `android-actions/setup-android@v3` action provisions the SDK/build-tools so `compileSdk 36` resolves. Compose builds in CI are unremarkable in 2026 as long as the Compose Compiler plugin version equals the Kotlin version.

---

## Repo best practices
- **Use a Gradle version catalog (`gradle/libs.versions.toml`)** — this is the current best practice; pin Kotlin / AGP / KSP / Room / Compose BOM / coroutines / serialization there, and reference the Compose Compiler and serialization plugins with `version.ref = "kotlin"`.
- **Multi-module layout** (recommended for a Compose + Room app, and to concentrate the GPL/KSP boundaries):
  - `:app` — Compose UI, navigation, entry point.
  - `:core:data` — Room 2.8.4 + sqlcipher-android (KSP + `androidx.room` plugin applied **here only**).
  - `:core:domain` — pure-Kotlin models, use-cases.
  - `:feature:reader` — text reading/lookup.
  - `:feature:srs` — vendored FSRS-Kotlin (FSRS-6) scheduler.
- **`.gitignore` essentials:** `.gradle/`, `build/`, `local.properties`, `*.keystore`/`*.jks`, `.idea/`, `*.iml`, `captures/`; Python: `__pycache__/`, `.venv/`/`venv/`, `*.pyc`, `*.egg-info/`, and generated `*.db` build artifacts.
- **Licensing/attribution:** recommend the **app license be GPLv3**, the safe open-source choice given Alpheios/Diogenes code is GPLv3 and several data sources are CC BY-SA (compatible with copyleft + attribution). Ship a `THIRD_PARTY_LICENSES`/`NOTICE` file covering: OFL fonts (bundle `OFL.txt` with Gentium), CC BY-SA data (DCC Greek core, Perseus treebanks, LSJLogeion), CC BY 4.0 (lsj9), the FSRS MIT notice, and Zetetic's BSD-style SQLCipher notice. If you bundle only Morpheus/Diogenes *generated data* (not the binaries), you avoid linking GPL C code into the APK.

---

## Recommendations (staged)
1. **Phase 0 — pipeline (CI only):** Stand up the Python pipeline in GitHub Actions. Fetch canonical-greekLit TEI for `tlg0562.tlg001`, convert Beta Code → Unicode with `betacode`, NFC-normalize with `unicodedata`, attach the Haines 1916 facing English translation, build a SQLite + FTS5 database (with a diacritic-stripped search column), and publish it as a content pack. Add DCC core vocab (CC BY-SA) and an openly-licensed LSJ (lsj9 CC BY 4.0 or LSJLogeion).
2. **Phase 1 — app skeleton:** Scaffold the multi-module project with the version catalog pinned to the exact versions above. **Verify the GitHub Actions debug build produces an APK before adding any features.**
3. **Phase 2 — data + SRS:** Wire Room 2.8.4 + sqlcipher-android 4.16.0 encrypted user DB via `SupportFactory`; port FSRS-Kotlin (FSRS-6) into `:feature:srs`; bundle Gentium Plus 7.000 and load it as a Compose `FontFamily`.
4. **Benchmarks that change the plan:** If **Room 3.0 ships a documented SQLCipher/`SQLiteDriver` path**, reconsider migrating to `androidx.room3`. If **Java-FSRS confirms FSRS-6 and shows sustained maintenance**, switch from the vendored source to the Maven artifact. If you hit **epigraphic/metrical symbols Gentium lacks**, add New Athena Unicode as a secondary `FontFamily`. If Kotlin 2.4.20 reaches stable and KSP/Room/AGP catch up, bump the catalog together (Kotlin = Compose Compiler must stay equal).

---

## Caveats
- Several "current stable" minor versions (core-ktx, activity-compose, navigation-compose, datastore) are given as version **families** because the AndroidX versions page (last updated Jun 23–24 2026) lists exact patch versions that shift weekly. **Verify the exact patch in `libs.versions.toml` against the AndroidX release page before locking** — the BOM-managed Compose/Material3 and the explicitly-versioned Room/lifecycle/coroutines/serialization numbers above are firm.
- **Java-FSRS's exact FSRS algorithm version was not confirmed** in its README/Maven metadata — verify in source before depending on the binary artifact. FSRS-Kotlin *is* explicitly FSRS-6.
- **GPL-3.0 on Morpheus/Diogenes C code** has copyleft implications if you link/bundle the binaries; using only their generated data (with its own license) avoids linking GPL code into the app.
- **Treebank/GLAUx per-text licenses vary** (some CC BY-NC, and the Perseus UD conversion is CC BY-NC-SA 2.5) — audit per-text metadata before redistributing in a commercial app. The base PerseusDL `treebank_data` itself is CC BY-SA 3.0 US.
- **Brill is not usable** as a bundled font under its free EULA; Gentium Plus (OFL) is the recommended substitute and is fully bundleable.
- **Farquharson 1944** Meditations translation should not be assumed public domain; use Haines 1916.