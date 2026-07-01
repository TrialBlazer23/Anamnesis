# Resource Registry — Verified Analysis & Incorporation Plan

Verification of every resource in `docs/Ancient_Greek_App_Resources.md` against
this project's constraints (GPLv3 app, possibly commercial, offline-first).
Each entry below was **probed from the build sandbox**: reachability tested,
licenses read from actual files where possible (not assumed), formats and sizes
measured. Needs shorthand: **N1** lexicon glosses · **N2** form→lemma
morphology · **N3** graded-reader texts · **N4** bundleable audio · **N5**
parsed corpora/aligned translations.

## Verdict summary

| Resource | Verdict | Need | License (verified how) |
|---|---|---|---|
| Rouse *A Greek Boy at Home* (fhardison) | ✅ **incorporated now** (`pipeline/data/rouse/`) | N3 | CC BY-SA 4.0 (upstream README; base text 1909 = US PD) |
| Dodson Koine Lexicon | ✅ **incorporated now** (`pipeline/data/koine/`) | N1 (Koine) | **CC0 1.0 — LICENSE file verified** |
| Chamberlain Iliad audio (hypotactic) | ✅ fetchable from sandbox — manifest incorporated; audio packs planned | N4 | CC BY 4.0 (via Perseus/Scaife attribution; confirm on hypotactic.com) |
| Lyceum / `archeion` (lsj.db, morph.db, CoNLL-U) | 🟡 **top candidate for N1+N2** — release DBs need manual download | N1, N2, N3, N5 | CC BY-SA 4.0 (repo LICENSE verified) |
| Diorisis corpus | 🟡 manual download (figshare blocked) | N2, N5 | CC BY 4.0 claimed — **verify on figshare page** |
| Middle Liddell via blinskey/greek-reference | ❌ **do not bundle this copy** | (N1) | Embedded Perseus notice: "non-commercial purposes only" |
| Strong's / Abbott-Smith | 🟢 optional later (Koine) | N1 | CC0 (README) / PD (README; 1922) |
| SM-2 SRS, DB bootstrapper, font/audio code samples | ➖ skip — we ship better (FSRS-6, ContentPackProvisioner, `:core:ui`) | — | — |
| SpeechGen "Ancient Greek TTS" | ❌ rejected (Modern-Greek voices; see `audio_sources.md`) | N4 | — |
| Hell-Char / Ithaca (ML) | ➖ out of scope (research) | — | — |

## Incorporated in this change

1. **Rouse graded reader** → `pipeline/data/rouse/` (`rouse_text.txt` 381 KB,
   `vocab.js` 2,136 glossary entries, `UPSTREAM_README.md` as license evidence).
   2,128 addressed sentences (`chapter.paragraph.sentence`), chapters 1–105 in
   increasing difficulty — the canonical direct-method Attic reader, exactly the
   Learn tab's post-alphabet reading material. Parser: `anamnesis_pipeline/rouse.py`
   (NFC + markdown-stripped + search keys), tested. Note: deliberately monolingual
   Greek — no aligned English exists; Rouse's own glossary is mostly Greek-to-Greek.
2. **Dodson Koine lexicon** → `pipeline/data/koine/` (5,408 entries, two gloss
   tiers, Strong's-keyed). Loader: `anamnesis_pipeline/koine_lexicon.py` (Beta
   Code → Unicode via `betacode`), tested. Fits the existing *Meditations* reader
   (Koine!) as a lookup layer beyond DCC's 524 headwords.
3. **Chamberlain audio manifest** → `pipeline/data/audio/iliad_audio_manifest_books1-2.csv`
   (1,488 rows: CTS URN → per-line MP4 URL). The **entire 24-book Iliad audio is on a
   publicly reachable GCS mirror** (`storage.googleapis.com/explorehomer-prod-media/...`),
   verified live from this sandbox — so CI can build audio packs automatically.
   Restored pronunciation with pitch accent, per-line files (~72 KB each; Book 1 =
   611 lines ≈ 44 MB, ~15 MB re-encoded to Opus). This is the first genuinely
   bundleable recitation audio found. It is **Epic/Homeric**, not Attic letter
   drills — label the dialect; it does not replace the Learn tab's letter-sound
   recordings (see `pipeline/data/lessons/audio_script.md`).

## The big one to fetch manually — Lyceum `archeion` release DBs

CC BY-SA 4.0 (verified). GitHub Releases are blocked from the sandbox; on any
normal machine:

```bash
gh release download --repo lyceum-quest/archeion --pattern '*.db'
# or browse https://github.com/lyceum-quest/archeion/releases/latest
```

What you get (schemas documented in-repo under `db/schemas/`, fetchable):
- **`lsj.db`** — 110,826 full LSJ entries + **100,207 short definitions** keyed by
  normalized lemma → solves **N1** outright (vs our 524 DCC headwords).
- **`morph.db`** — **1,546,804 rows** of surface form → lemma + full parse
  (tense/voice/mood/person/number/case/gender), normalized-form indexed → solves
  **N2** (tap-to-parse) with the exact Morpheus lineage the design doc planned.
  Likely 150–400 MB — we'll trim to bundled-text vocabulary or ship as a
  downloadable pack per the hybrid-delivery decision.
- `texts.db` / `editions.db` — 373 authors incl. aligned translations; audit
  per-edition provenance before wholesale use (upstream GLAUx has some CC BY-NC
  texts). The in-git **Aesop fables (52, difficulty-tiered, with sentence
  translations + per-word glosses)** are fetchable from the sandbox already and
  are ideal Learn-tab material — ingestion planned.

Drop the `.db` files in `pipeline/data/lyceum/` and I'll build the extraction
(trimmed short-gloss table + form→lemma pack) into the pipeline.

## Manual-download list (everything you'd need to get)

| What | Where | Check before download |
|---|---|---|
| Lyceum `lsj.db`, `morph.db` (+ optionally `editions.db`) | github.com/lyceum-quest/archeion → Releases → latest | note release version for attribution |
| Diorisis corpus (only if we want broader N2 than Lyceum) | figshare **DOI 10.6084/m9.figshare.6187256** (annotated XML) or the JSON variant (article 12251468) | license badge must say **CC BY** (the Zenodo DuckDB variant is CC BY-NC-ND — skip it) |
| hypotactic.com license evidence | https://hypotactic.com/my-reading-of-homer-work-in-progress/ | screenshot/save the CC-BY statement for our records before we ship audio |

## Design ideas adopted from the document (no download needed)

- **Dialect tagging** (EPIC / ATTIC / KOINE) on every text and lesson — our
  *Meditations* is Koine while the Learn tab teaches Attic; the content-pack
  `meta` table and passage rows will carry a `dialect` field (pipeline change).
- Its `words`-token-table schema confirms our planned tap-to-parse shape
  (surface form + normalized form + lemma_id + morph code); Lyceum's `morph.db`
  is that table, prebuilt.
- Its audio-sync (per-line timestamps → highlighted sentence) is prior art for a
  read-along mode once Chamberlain packs land; per-line files make it trivial
  (no timestamp alignment needed).

## Rejected / superseded

- **SM-2 SRS** — we ship FSRS-6 (the design doc's and spec's choice; strictly better).
- **blinskey Middle Liddell copies** — embedded Perseus "non-commercial only"
  notice (verified in the TEI header bytes); replaced by PerseusDL/lexica or
  Lyceum `lsj.db`. The Apache-2.0 db-creator code remains useful reference.
- **Diorisis DuckDB (Zenodo)** — CC BY-NC-ND; never use. HF mirror — no license
  tag + structure destroyed; format reference only.
- **SpeechGen / commercial "Ancient Greek TTS"** — Modern-Greek voices, not
  restored; already rejected in `audio_sources.md`.
- **eSpeak-NG grc** — GPL-v3 + robotic; already rejected.
