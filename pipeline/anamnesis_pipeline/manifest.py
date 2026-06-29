"""Content-pack manifest generation for hybrid delivery.

Each pack ships with a JSON manifest carrying its identity, source/license
attribution, row counts, and the DB's SHA-256. The app fetches a versioned
manifest, verifies the checksum after download, and opens the pack read-only.
"""

from __future__ import annotations

import hashlib
import json
from pathlib import Path


def sha256_file(path: str | Path, chunk_size: int = 1 << 20) -> str:
    digest = hashlib.sha256()
    with open(path, "rb") as fh:
        for chunk in iter(lambda: fh.read(chunk_size), b""):
            digest.update(chunk)
    return digest.hexdigest()


def build_manifest(
    db_path: str | Path,
    *,
    pack_id: str,
    work: str,
    edition: str,
    source_url: str,
    license: str,
    counts: dict[str, int],
    schema_version: int,
) -> dict:
    db = Path(db_path)
    return {
        "pack_id": pack_id,
        "work": work,
        "edition": edition,
        "source_url": source_url,
        "license": license,
        "schema_version": schema_version,
        "file_name": db.name,
        "size_bytes": db.stat().st_size,
        "sha256": sha256_file(db),
        "counts": counts,
    }


def write_manifest(manifest: dict, path: str | Path) -> Path:
    out = Path(path)
    out.write_text(json.dumps(manifest, ensure_ascii=False, indent=2), encoding="utf-8")
    return out
