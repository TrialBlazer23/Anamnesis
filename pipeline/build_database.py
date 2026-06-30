"""Build an Anamnesis content pack (read-only SQLite + FTS5) and its manifest.

Fetches Perseus canonical-greekLit TEI for a CTS work, parses it into passages
(NFC display text + diacritic-stripped search key), optionally attaches a DCC
core-vocab CSV and a facing translation, writes the content pack, and emits a
versioned manifest with the DB's SHA-256 for hybrid delivery.

Run in CI/desktop only — NOT on-device (CLTK pulls torch/stanza).

Example:
    python build_database.py --work tlg0562.tlg001 --out out/meditations.db
"""

from __future__ import annotations

import argparse
import json
from pathlib import Path

from collections import defaultdict

from anamnesis_pipeline.database import SCHEMA_VERSION, build_content_pack
from anamnesis_pipeline.manifest import build_manifest, write_manifest
from anamnesis_pipeline.tei import Passage, fetch_tei, parse_passages, source_url
from anamnesis_pipeline.translation import parse_haines_epub
from anamnesis_pipeline.vocab import load_dcc_vocab

# canonical-greekLit edition license (per the TEI <availability>).
SOURCE_LICENSE = "CC BY-SA 4.0 (Perseus canonical-greekLit)"


def load_translation(path: str | Path) -> dict[str, str]:
    """Load a ref -> English mapping (JSON object). Public-domain only."""
    return json.loads(Path(path).read_text(encoding="utf-8"))


def _section_key(ref: str) -> int:
    """Numeric value of a ref's last component (for picking the first section)."""
    last = ref.split(".")[-1]
    return int(last) if last.isdigit() else 0


def attach_chapter_translations(passages: list[Passage], chapters: dict[str, str]) -> int:
    """Attach a `book.chapter -> English` map to passages.

    Haines is divided by book.chapter while the Greek edition adds a section
    level, so attach each chapter's translation to its lowest-numbered section.
    Returns the number of passages that received a translation.
    """
    groups: dict[str, list[Passage]] = defaultdict(list)
    for p in passages:
        groups[".".join(p.ref.split(".")[:2])].append(p)

    attached = 0
    for book_chapter, group in groups.items():
        english = chapters.get(book_chapter)
        if english:
            min(group, key=lambda p: _section_key(p.ref)).translation = english
            attached += 1
    return attached


def main() -> None:
    parser = argparse.ArgumentParser(description="Build an Anamnesis content pack.")
    parser.add_argument("--work", required=True, help="CTS work id, e.g. tlg0562.tlg001")
    parser.add_argument("--edition", default="perseus-grc2", help="Edition suffix")
    parser.add_argument("--out", required=True, help="Output .db path")
    parser.add_argument("--vocab-csv", help="DCC core vocab CSV (CC BY-SA 3.0)")
    parser.add_argument("--translation", help="ref->English JSON (public domain)")
    parser.add_argument("--haines-epub", help="Wikisource Haines 1916 EPUB (public domain)")
    parser.add_argument("--cache", default="cache", help="TEI cache dir")
    parser.add_argument("--pack-id", help="Manifest pack id (default: out file stem)")
    args = parser.parse_args()

    tei = fetch_tei(args.work, args.edition, cache_dir=Path(args.cache))
    passages = parse_passages(tei)

    translated = 0
    if args.translation:
        mapping = load_translation(args.translation)
        for p in passages:
            p.translation = mapping.get(p.ref)
        translated = sum(1 for p in passages if p.translation)
    if args.haines_epub:
        translated = attach_chapter_translations(passages, parse_haines_epub(args.haines_epub))

    vocab = load_dcc_vocab(args.vocab_csv) if args.vocab_csv else []

    meta = {
        "work": args.work,
        "edition": args.edition,
        "source_url": source_url(args.work, args.edition),
        "license": SOURCE_LICENSE,
    }
    counts = build_content_pack(args.out, passages, vocab, meta)

    pack_id = args.pack_id or Path(args.out).stem
    manifest = build_manifest(
        args.out,
        pack_id=pack_id,
        work=args.work,
        edition=args.edition,
        source_url=meta["source_url"],
        license=SOURCE_LICENSE,
        counts=counts,
        schema_version=SCHEMA_VERSION,
    )
    manifest_path = write_manifest(manifest, Path(args.out).with_suffix(".manifest.json"))

    print(
        f"Built {args.out}: {counts['passages']} passages "
        f"({translated} translated), {counts['vocabulary']} vocab entries\n"
        f"Manifest: {manifest_path} (sha256 {manifest['sha256'][:12]}…)"
    )


if __name__ == "__main__":
    main()
