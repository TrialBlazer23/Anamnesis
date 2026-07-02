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
- [x] **Phase 0** — pipeline builds the full *Meditations* content pack from
  live Perseus TEI: generic CTS parser, NFC + diacritic-stripped keys, FTS5
  (accent-insensitive), DCC Greek core vocab (524), Haines 1916 facing
  translation (485/577, ~84%), versioned SHA-256 manifest, pytest suite in CI.
  Remaining (optional): LSJ gloss; translate grc2 sub-sections beyond `.1`.
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
- [x] Attach Haines 1916 facing English (public domain) from the Wikisource
      EPUB (`data/haines_1916.epub`) via `--haines-epub`; `translation.py` parses
      book/chapter and attaches each to its chapter's first section. 485/577
      passages translated (~84%).
- [x] Load the real DCC Greek Core Vocabulary (CC BY-SA 3.0,
      `data/dcc_greek_core.csv`, 524 lemmas) via `--vocab-csv`.
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
- [x] Release job: `release.yml` runs `assembleRelease` +
      `r0adkll/sign-android-release@v1` on version tags. Needs the four signing
      secrets (see `release.yml`); keystores stay out of git.
- [x] CI runs `./gradlew test` (JVM unit tests) on every push/PR.
- [ ] Minimal navigation graph (Compose Navigation) with placeholder routes for
      reader and SRS.

**Done when:** the GitHub Actions debug build produces an APK. This gate comes
*before* feature work, per the design doc.

---

## Phase 2 — Data + SRS

Goal: encrypted on-device DB, working scheduler, polytonic rendering.

- [x] **Encrypted user DB:** `DatabaseKeyManager` derives the SQLCipher
      passphrase from an Android Keystore AES/GCM key (random passphrase, wrapped
      ciphertext in prefs); `DatabaseFactory` wires it via `SupportOpenHelperFactory`.
      The encrypted DB now holds private SRS cards only (content stays in the
      read-only pack).
- [x] **Content import:** the app opens the Phase 0 content pack read-only via
      `ContentPackDataSource` (plain SQLite), provisioned from the bundled asset
      into `filesDir`. (Encrypted-DB import path not needed — packs stay read-only.)
- [x] **FSRS-6:** clean-room Kotlin port in `:feature:srs` (`Fsrs.kt` +
      `Models.kt`), no Android deps, validated against py-fsrs/fsrs-rs reference
      values with a JUnit suite (`FsrsTest.kt`, runs under `./gradlew test`).
      Fixed the upstream FSRS-Kotlin exponential-forgetting-curve bug.
  - [ ] Wire the scheduler to the review UI + persist memory state in the
        encrypted DB (card table, due-card queries).
- [x] **Fonts:** bundled Gentium Plus (6.101) Regular/Bold/Italic/BoldItalic +
      `OFL.txt`; exposed as the `GentiumPlus` Compose `FontFamily`; renders NFC
      Greek in the reader. (Upgrade to 7.000 when SIL's site is reachable; add
      New Athena Unicode fallback only if a glyph is missing.)
- [x] **Reader (UI):** `ReaderScreen` renders a passage in Gentium Plus with a
      facing-translation slot and prev/next navigation; hosted in `MainActivity`
      over real sample passages; pure navigation logic unit-tested.
  - [x] Wire `ReaderScreen` to the content-pack DB: `ReaderRepository` +
        `ContentPackReaderRepository` (read-only SQLite in `:core:data`) +
        `ReaderViewModel`/`ReaderRoute`; the starter pack is bundled in
        `app/src/main/assets/content/meditations.db` (sample data is the
        fallback). VM state logic unit-tested.
  - [ ] Tap-a-word → LSJ/vocab lookup.
  - [ ] FTS5 search UI over the content pack.
- [x] **Review flow:** `ReviewScheduler` (FSRS-6 + card), `ReviewViewModel`,
      `ReviewScreen`/`ReviewRoute`; seeds cards from the content-pack vocab on
      first run, serves the due queue, grades (Again/Hard/Good/Easy), persists to
      the encrypted DB. Bottom-nav (Read/Train) in `MainActivity`. Scheduler +
      VM unit-tested (StandardTestDispatcher).

**Done when:** a user can read *Meditations* in polytonic Greek with facing
translation, look up words, and review vocabulary on an FSRS-6 schedule, all
backed by the encrypted DB.

---

## Phase 3 — Polish & expansion

- [ ] **Beginner "Learn" tab (Ancient Greek On-Ramp)** — a guided 9-unit
      curriculum (alphabet → most-common words → by part of speech → reading
      Plato's *Euthyphro* with training wheels), per the root spec
      `Anamnesis Phase 3 - Ancient Greek On-Ramp Pedagogical Specification.md`
      and the build plan in `docs/PHASE3_LEARN_TAB_PLAN.md`.
  - [x] Authored source data: `pipeline/data/lessons/` (letters, diphthongs,
        minimal-pairs, 9-unit `units.json`) + restored-Attic `audio_script.md`.
  - [ ] `build_lessons.py` → bundled lessons pack + audio manifest (+ pytest).
  - [x] **Visual path (audio-optional):** `:feature:learn` module + 🎓 Learn
        bottom-nav tab; roadmap, alphabet browser (24 letters by batch) with
        per-letter detail, and a recognition drill (name/sound → letter). Quiz
        logic + data unit-tested. Audio ids are optional (see `audio_sources.md`).
  - [x] Confusion-set distractors (visual look-alikes + same-batch weighted) and
        batch-scoped practice (drill All or a single batch).
  - [ ] Per-unit gating/progress (persisted) so units unlock on the advance criteria.
  - [ ] Wire letter/vocab drills through FSRS-6 (extend the card model with
        deck/source + card-type); S-Pen tracing (valued, not gating).
  - [ ] **Record restored-Attic audio** (critical path — see `audio_script.md`).
  - [ ] *Euthyphro* content pack (Plato `tlg0059.tlg001`) for the handoff.
- [ ] **Resource-registry incorporation** (see `docs/RESOURCE_ANALYSIS.md` for
      verified licenses/paths):
  - [x] Rouse *A Greek Boy at Home* (CC BY-SA 4.0) fetched to
        `pipeline/data/rouse/` + parser `rouse.py` (tested) — Learn-tab reading.
  - [x] Dodson Koine lexicon (CC0, verified) fetched to `pipeline/data/koine/` +
        loader `koine_lexicon.py` (tested) — Koine glosses for the *Meditations*.
  - [x] Chamberlain Iliad audio manifest (CC BY 4.0) — per-line audio confirmed
        fetchable from the public GCS mirror; audio packs to build later.
  - [x] **Lyceum `morph.db` → tap-to-parse**: maintainer mirrored morph.db as
        the `lyceum-data-v2026.04.09` release; `lyceum_morph.py` extracts
        form→lemma+parse rows filtered to the pack's tokens into a `morphology`
        table (schema v3); CI downloads + SHA-256-verifies the asset; app lookup
        chain is now DCC → lexicon → morphology (inflected form resolves to its
        lemma with parse + gloss). Note: the committed bundled asset gains
        morphology on the next CI pack build (release-asset host is blocked from
        the dev sandbox) — replace `app/src/main/assets/content/meditations.db`
        with the CI artifact.
  - [ ] Lyceum `lsj.db` (optional) — full-definition downloadable pack later
        (LSJLogeion is the CI-fetchable alternative).
  - [ ] Lyceum Aesop fables (52, difficulty-tiered CoNLL-U, in-git) — ingest as
        graded-reading pack for the Learn tab.
  - [ ] Add `dialect` (EPIC/ATTIC/KOINE) tagging to content-pack meta + passages.
  - [x] **Middle Liddell bundled** (PerseusDL/canonical-pdlrefwk, CC BY-SA 4.0,
        sandbox/CI-fetchable): `middle_liddell.py` parses 34,348 headword →
        short-gloss entries into a new content-pack `lexicon` table (schema v2);
        reader tap-to-lookup now falls back DCC → lexicon. Pack grew 2 → 6.1 MB.
  - [ ] Wire Dodson (Koine) into the lookup chain between DCC and the lexicon;
        LSJLogeion full definitions as a downloadable pack later.
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
