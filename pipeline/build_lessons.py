"""Build the Learn-tab lessons pack (JSON) and its audio manifest.

Reads the authored lesson data in `data/lessons/` (letters, diphthongs,
minimal pairs, unit-5 words, unit-6 accent items, and the 9-unit curriculum),
validates it, and writes:

- the lessons pack the app bundles at
  `feature/learn/src/main/assets/lessons/lessons.json`, and
- an audio manifest listing every recording id the pack references
  (the to-record list for the restored-Attic audio session).

Output is deterministic: rebuilding unchanged inputs is byte-identical, so CI
can `cmp` the committed asset against a fresh build.

Example:
    python build_lessons.py --data data/lessons \
        --out out/lessons.json --audio-manifest out/lessons_audio_manifest.json
"""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path

from anamnesis_pipeline.lessons import (
    LESSONS_SCHEMA_VERSION,
    build_audio_manifest,
    build_pack,
    load_lessons,
    validate,
)
from anamnesis_pipeline.manifest import build_manifest, write_manifest

# The authored data is original to this repo except the unit-5 word list,
# which derives lemmas/glosses from the DCC Greek Core Vocabulary.
SOURCE_LICENSE = "GPLv3 (project data); word glosses derived from DCC Core (CC BY-SA 3.0)"


def _write_json(payload: dict, path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(
        json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )


def main() -> None:
    parser = argparse.ArgumentParser(description="Build the Anamnesis lessons pack.")
    parser.add_argument("--data", default="data/lessons", help="Authored lesson data dir")
    parser.add_argument("--out", required=True, help="Output lessons.json path")
    parser.add_argument(
        "--audio-manifest", required=True, help="Output audio manifest JSON path"
    )
    args = parser.parse_args()

    data = load_lessons(args.data)
    errors = validate(data)
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        raise SystemExit(f"lessons data failed validation with {len(errors)} error(s)")

    out = Path(args.out)
    pack = build_pack(data)
    _write_json(pack, out)

    audio = build_audio_manifest(data)
    _write_json(audio, Path(args.audio_manifest))

    counts = {
        "units": len(pack["units"]),
        "letters": len(pack["letters"]),
        "diphthongs": len(pack["diphthongs"]),
        "minimal_pairs": len(pack["minimal_pairs"]),
        "words": len(pack["words"]),
        "accent_items": len(pack["accent_items"]),
        "accent_pairs": len(pack["accent_pairs"]),
    }
    manifest = build_manifest(
        out,
        pack_id=out.stem,
        work="lessons",
        edition="authored",
        source_url="https://github.com/TrialBlazer23/Anamnesis/tree/main/pipeline/data/lessons",
        license=SOURCE_LICENSE,
        counts=counts,
        schema_version=LESSONS_SCHEMA_VERSION,
    )
    manifest_path = write_manifest(manifest, out.with_suffix(".manifest.json"))

    print(
        f"Built {out}: {counts['units']} units, {counts['letters']} letters, "
        f"{counts['diphthongs']} diphthongs, {counts['minimal_pairs']} minimal pairs, "
        f"{counts['words']} words, {counts['accent_items']} accent items, "
        f"{counts['accent_pairs']} accent pairs\n"
        f"Audio manifest: {args.audio_manifest} ({audio['count']} recordings)\n"
        f"Manifest: {manifest_path} (sha256 {manifest['sha256'][:12]}…)"
    )


if __name__ == "__main__":
    main()
