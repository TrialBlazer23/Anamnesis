"""Build a per-line recitation audio pack (Iliad, David Chamberlain, CC BY).

Reads the CTS-URN → audio-URL manifest (from Perseus' Scaife/Beyond-Translation
annotations), downloads one book's per-line MP4/AAC files from the public
mirror, and packages them as a zip whose paths follow the classicsviewer
convention (`book_N/line_M.mp4` — the path is the manifest) plus an embedded
`manifest.json` carrying URNs, license, and attribution. An outer
`.manifest.json` with the zip's SHA-256 is emitted for download verification
(hybrid delivery), like the text content packs.

License: "Audio and text annotations licensed as CC-BY, © 2016, 2017 by David
Chamberlain" — primary-source evidence at docs/licensing-evidence/. Files are
packaged unmodified.

Example:
    python build_audio_pack.py --manifest data/audio/iliad_audio_manifest_books1-2.csv \
        --book 1 --out out/iliad_book1_audio.zip
"""

from __future__ import annotations

import argparse
import csv
import json
import re
import zipfile
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path
from typing import Callable

import httpx

from anamnesis_pipeline.manifest import sha256_file, write_manifest

ATTRIBUTION = (
    "Recitations © 2016, 2017 David Chamberlain (hypotactic.com), "
    "CC BY (creativecommons.org/licenses/by/4.0/). Packaged unmodified."
)

_URN_LINE = re.compile(r":(\d+)\.(\d+)$")  # ...perseus-grc2:BOOK.LINE


def parse_audio_manifest(csv_text: str, book: int) -> list[dict[str, str]]:
    """Rows of ``urn,url`` filtered to ``book``; adds book/line/zip path."""
    entries: list[dict[str, str]] = []
    for row in csv.reader(csv_text.splitlines()):
        if len(row) < 2:
            continue
        urn, url = row[0].strip(), row[1].strip()
        match = _URN_LINE.search(urn)
        if not match or int(match.group(1)) != book:
            continue
        line = int(match.group(2))
        entries.append(
            {
                "urn": urn,
                "url": url,
                "path": f"book_{book}/line_{line}.mp4",
                "line": str(line),
            }
        )
    entries.sort(key=lambda e: int(e["line"]))
    return entries


def _default_fetch(url: str) -> bytes:
    response = httpx.get(url, timeout=120, follow_redirects=True).raise_for_status()
    return response.content


def build_audio_pack(
    entries: list[dict[str, str]],
    out_path: str | Path,
    *,
    work_urn: str,
    book: int,
    fetch: Callable[[str], bytes] = _default_fetch,
    workers: int = 8,
) -> dict:
    """Download every entry and write the zip. Returns the embedded manifest."""
    out = Path(out_path)
    out.parent.mkdir(parents=True, exist_ok=True)

    def download(entry: dict[str, str]) -> tuple[dict[str, str], bytes]:
        data = fetch(entry["url"])
        if not data:
            raise ValueError(f"empty audio file: {entry['url']}")
        return entry, data

    with ThreadPoolExecutor(max_workers=workers) as pool:
        results = list(pool.map(download, entries))

    embedded = {
        "pack_id": f"{work_urn.split(':')[-1]}-book{book}-audio",
        "work_urn": work_urn,
        "book": book,
        "license": "CC BY 4.0",
        "attribution": ATTRIBUTION,
        "files": [
            {"urn": entry["urn"], "path": entry["path"]} for entry, _ in results
        ],
    }
    with zipfile.ZipFile(out, "w", compression=zipfile.ZIP_STORED) as zf:
        # ZIP_STORED: AAC audio is already compressed; deflate wastes CPU.
        for entry, data in results:
            zf.writestr(entry["path"], data)
        zf.writestr("manifest.json", json.dumps(embedded, ensure_ascii=False, indent=2))
    return embedded


def main() -> None:
    parser = argparse.ArgumentParser(description="Build a recitation audio pack.")
    parser.add_argument("--manifest", required=True, help="CSV of 'urn,url' rows")
    parser.add_argument("--book", type=int, required=True, help="Book number to pack")
    parser.add_argument("--out", required=True, help="Output .zip path")
    parser.add_argument(
        "--work-urn", default="urn:cts:greekLit:tlg0012.tlg001.perseus-grc2"
    )
    parser.add_argument("--limit", type=int, help="Only first N lines (smoke builds)")
    args = parser.parse_args()

    entries = parse_audio_manifest(
        Path(args.manifest).read_text(encoding="utf-8"), args.book
    )
    if args.limit:
        entries = entries[: args.limit]
    if not entries:
        raise SystemExit(f"No manifest entries for book {args.book}")

    embedded = build_audio_pack(
        entries, args.out, work_urn=args.work_urn, book=args.book
    )
    outer = {
        "pack_id": embedded["pack_id"],
        "kind": "audio",
        "license": embedded["license"],
        "attribution": embedded["attribution"],
        "file_name": Path(args.out).name,
        "size_bytes": Path(args.out).stat().st_size,
        "sha256": sha256_file(args.out),
        "counts": {"files": len(embedded["files"])},
    }
    manifest_path = write_manifest(outer, Path(args.out).with_suffix(".manifest.json"))
    print(
        f"Built {args.out}: {len(embedded['files'])} lines, "
        f"{outer['size_bytes'] / 1e6:.1f} MB\nManifest: {manifest_path} "
        f"(sha256 {outer['sha256'][:12]}…)"
    )


if __name__ == "__main__":
    main()
