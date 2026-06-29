# Anamnesis data pipeline

Builds the read-only SQLite + FTS5 **content packs** the Android app ships /
downloads. Runs in CI (GitHub Actions) or on a desktop — **not on-device**,
because CLTK pulls `torch`/`stanza`.

Content packs are plain read-only SQLite, kept separate from the app's
SQLCipher-encrypted user DB (which holds private SRS state only).

## Layout
```
build_database.py            CLI entry point (orchestrator)
anamnesis_pipeline/
  normalize.py               NFC + diacritic-stripped search keys
  tei.py                     fetch + generic CTS textpart parser
  vocab.py                   DCC core-vocab CSV loader (column-tolerant)
  database.py                content-pack schema + writers (FTS5)
  manifest.py                versioned manifest + SHA-256 (hybrid delivery)
tests/                       pytest suite + fixtures
requirements.txt             lightweight deps (Termux-safe)
requirements-ci.txt          adds CLTK + pytest for CI/desktop
out/                         generated packs (git-ignored)
```

## Content-pack schema (v1)
- `passages(id, cts_urn, ref, greek, search_key, translation)` — `greek` is
  NFC; `search_key` is diacritic-stripped.
- `passage_fts` — FTS5 external-content index over `passages`, tokenized
  `unicode61 remove_diacritics 2`, so search is accent-insensitive.
- `vocabulary(lemma, part_of_speech, gloss, semantic_group, frequency_rank)`.
- `meta(key, value)` — `schema_version`, `work`, `edition`, `source_url`, `license`.

Each build also emits `<out>.manifest.json` with the pack's SHA-256, size, and
row counts for download verification.

## Run
```bash
python -m venv .venv && source .venv/bin/activate
pip install -r requirements-ci.txt          # or requirements.txt for the light path
python -m pytest                            # run the suite
python build_database.py --work tlg0562.tlg001 --out out/meditations.db \
    [--vocab-csv path/to/dcc.csv] [--translation path/to/haines.json]
```
This fetches the Perseus `perseus-grc2` (Leopold 1908) edition — CC BY-SA 4.0 —
and builds all 12 books of the *Meditations* (~577 passages).

## Data still to wire (Phase 0 remainder)
- **DCC Greek Core Vocabulary** (CC BY-SA 3.0): the loader is column-tolerant;
  supply the real CSV via `--vocab-csv`. `dcc.dickinson.edu` is not reachable
  from the build sandbox, so fetch it in CI or commit a vetted copy.
- **Haines 1916 facing translation** (public domain): supply a `ref -> English`
  JSON via `--translation`. **Use Haines 1916, not Farquharson 1944.**

## Licensing
Every bundled source carries an attribution/ShareAlike obligation — see the
repo-root `THIRD_PARTY_LICENSES.md`. Audit per-text treebank/GLAUx licenses
(some CC BY-NC) before redistributing.
