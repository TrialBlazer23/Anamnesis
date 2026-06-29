# Anamnesis — Implementation Plan

Derived from the staged recommendations in the design document
(`Anamnesis Ancient Greek Android App_ Verified 2026 Production St.md`,
"Recommendations (staged)"). The design doc gives the *what* and the *why* and
the verified versions; this file is the actionable *order of work* with concrete
checklists. Check items off as they land.

## Status

- [x] **Phase 1a — repo scaffold**: multi-module Gradle layout, version catalog,
  wrapper (9.6.1), `.gitignore`, CI workflows, license/notice files, `CLAUDE.md`.
  Debug APK builds green in CI.
- [x] **Phase 0 (core)** — pipeline builds the real *Meditations* content pack
  from live Perseus TEI: generic CTS parser, NFC + diacritic-stripped keys,
  FTS5 (accent-insensitive), versioned SHA-256 manifest, pytest suite in CI.
  Remaining: real DCC vocab CSV + Haines translation (sources blocked from the
  build sandbox — wire in CI).
- [ ] Everything below.

Phases 0 and 1 can proceed in parallel (pipeline is independent of the app).

---

## Phase 0 — Data pipeline (CI only)

Goal: produce the first **content pack** (SQLite + FTS5) and publish it as a CI
artifact. No app code required.

- [x] `fetch_tei()` — pull canonical-greekLit TEI for `tlg0562.tlg001` from the
      raw GitHub TEI (cached).
- [x] `parse_passages()` — generic `lxml` CTS textpart walker (URN per passage),
      NFC-normalize via `unicodedata`. (Beta Code via `betacode` reserved for
      Beta Code editions; `perseus-grc2` is already Unicode.)
- [x] Populate `search_key` with `strip_diacritics()`.
- [x] Verify FTS5 accent-insensitive search returns expected passages (test +
      live run).
- [x] `pipeline.yml` runs pytest then publishes `out/*.db` + `*.manifest.json`.
- [ ] Attach Haines 1916 facing English (public domain) via `--translation`
      JSON. Source (Wikisource/archive.org) is blocked from the build sandbox —
      fetch/commit in CI.
- [ ] Load real DCC Greek Core Vocabulary (CC BY-SA 3.0) via `--vocab-csv`
      (loader done; `dcc.dickinson.edu` blocked from the sandbox).
- [ ] Load an openly-licensed LSJ gloss (lsj9 CC BY 4.0, or LSJLogeion).
- [ ] Ship content packs per the **hybrid delivery** decision (see below):
  - [ ] Bundle the starter pack (Meditations + DCC core vocab) in the APK as a
        plain read-only SQLite asset — **not** inside the encrypted user DB.
  - [ ] Serve larger/optional packs (full LSJ, additional works) as downloads
        behind a versioned manifest with per-pack SHA-256 checksums.

**Done when:** CI produces a queryable `meditations.db` with passages + vocab and
working diacritic-insensitive FTS.

---

## Phase 1 — App skeleton

Goal: a debug APK that builds green in CI **before any features are added**.

- [ ] Confirm the version catalog resolves in CI; pin every `# verify` AndroidX
      patch against the AndroidX release page.
- [ ] Bring KGP 2.4.0 onto the build classpath above AGP's bundled 2.2.10
      (root `plugins { kotlin-jvm … apply false }` already declared — confirm it
      takes effect; fall back to a `buildscript` classpath entry if not).
- [ ] `./gradlew assembleDebug` is green in GitHub Actions; artifact uploads.
- [ ] Add a release job: `assembleRelease` + `r0adkll/sign-android-release@v1`
      with a base64 keystore + secrets. (Keep keystores out of git.)
- [ ] Minimal navigation graph (Compose Navigation) with placeholder routes for
      reader and SRS.

**Done when:** the GitHub Actions debug build produces an APK. This gate comes
*before* feature work, per the design doc.

---

## Phase 2 — Data + SRS

Goal: encrypted on-device DB, working scheduler, polytonic rendering.

- [ ] **Encrypted user DB:** finish `core/data/DatabaseFactory` — derive the
      passphrase from an Android Keystore-backed key (not a literal); wire
      `sqlcipher-android` 4.16.0 via `SupportOpenHelperFactory`.
- [ ] **Content import:** open the Phase 0 content pack read-only, or import it
      into the encrypted DB. Confirm FTS works through Room (`@Fts4`).
- [ ] **FSRS-6:** vendor `open-spaced-repetition/FSRS-Kotlin` source into
      `:feature:srs` (no Maven coord), retaining its MIT LICENSE and recording it
      in `THIRD_PARTY_LICENSES.md`. Validate against `py-fsrs`/`fsrs-rs`.
      Replace the placeholder in `feature/srs/Fsrs.kt`.
- [ ] **Fonts:** bundle Gentium Plus 7.000 + `OFL.txt`; load as a Compose
      `FontFamily`; render NFC text. Add New Athena Unicode as fallback only if a
      glyph is missing.
- [ ] **Reader:** wire `ReaderScreen` to the DB; tap-a-word → LSJ/vocab lookup.
- [ ] **Review flow:** present due cards, grade (Again/Hard/Good/Easy), persist
      FSRS state. Mind the Compose **v2 test APIs** (StandardTestDispatcher) —
      advance the virtual clock in tests.

**Done when:** a user can read *Meditations* in polytonic Greek with facing
translation, look up words, and review vocabulary on an FSRS-6 schedule, all
backed by the encrypted DB.

---

## Phase 3 — Polish & expansion

- [ ] More texts/content packs beyond *Meditations*.
- [ ] DataStore-backed settings (font size, retention target, theme).
- [ ] Morphology lookup from Morpheus/Diogenes **generated data** (no GPL binaries).
- [ ] Accessibility, RTL/large-font support, instrumentation test coverage.

---

## Re-evaluation triggers (from the design doc)

Revisit these decisions if the ecosystem moves:

- **Room 3.0 ships a documented `SQLiteDriver` SQLCipher path** → consider
  migrating to `androidx.room3`.
- **Java-FSRS 1.0.0 confirms FSRS-6 + sustained maintenance** → switch from the
  vendored source to the Maven artifact.
- **Gentium lacks an epigraphic/metrical glyph** → add New Athena Unicode as a
  secondary `FontFamily`.
- **Kotlin 2.4.20 reaches stable with KSP/Room/AGP caught up** → bump the catalog
  together (Kotlin == Compose Compiler must stay equal).

## Resolved decisions

- **Project license: GPLv3** (`LICENSE`). Compatible with the GPLv3
  Alpheios/Diogenes lineage and the CC BY-SA data sources.
- **Content-pack delivery: hybrid.** Bundle a small starter pack (first text +
  DCC core vocab) so the app works offline on first launch; deliver larger /
  optional content (full LSJ, more works) as downloads behind a versioned
  manifest (per-pack SHA-256). Content packs are plain read-only SQLite opened
  separately — the SQLCipher-encrypted DB is reserved for private SRS state, not
  public open data. Download transport: Play Asset Delivery if shipping via Play,
  otherwise HTTPS + checksum (e.g. GitHub Releases).
