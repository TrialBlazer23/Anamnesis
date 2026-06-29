# Anamnesis data pipeline

Builds the read-only SQLite + FTS5 **content packs** the Android app ships /
downloads. Runs in CI (GitHub Actions) or on a desktop — **not on-device**,
because CLTK pulls `torch`/`stanza`.

## Layout
- `build_database.py` — entry point; fetches sources, normalizes, writes the DB.
- `requirements.txt` — lightweight deps (Termux-safe).
- `requirements-ci.txt` — adds CLTK for CI/desktop morphology work.
- `out/` — generated `.db` artifacts (git-ignored).

## Pipeline stages (Phase 0)
1. Fetch canonical-greekLit TEI for `tlg0562.tlg001` (Marcus Aurelius, *Meditations*).
2. Beta Code → Unicode via `betacode`; NFC-normalize via `unicodedata`.
3. Attach the Haines 1916 (public-domain) facing English translation.
4. Build SQLite + FTS5, including a diacritic-stripped search column.
5. Add DCC Greek Core Vocabulary (CC BY-SA 3.0) and an openly-licensed LSJ
   (lsj9 CC BY 4.0, or LSJLogeion).
6. Emit the content pack into `out/` and publish it as a build artifact.

## Run
```bash
python -m venv .venv && source .venv/bin/activate
pip install -r requirements-ci.txt   # or requirements.txt for the light path
python build_database.py --work tlg0562.tlg001 --out out/meditations.db
```

## Licensing
Every bundled source carries an attribution/ShareAlike obligation — see the
repo-root `THIRD_PARTY_LICENSES.md`. Audit per-text treebank/GLAUx licenses
(some CC BY-NC) before redistributing.
