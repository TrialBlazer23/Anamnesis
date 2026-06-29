from pathlib import Path

from anamnesis_pipeline.database import build_content_pack
from anamnesis_pipeline.manifest import build_manifest, sha256_file, write_manifest
from anamnesis_pipeline.tei import parse_passages

FIXTURES = Path(__file__).parent / "fixtures"


def test_manifest_has_checksum_and_counts(tmp_path):
    passages = parse_passages((FIXTURES / "sample_tei.xml").read_text(encoding="utf-8"))
    out = tmp_path / "pack.db"
    counts = build_content_pack(out, passages, [], {})

    manifest = build_manifest(
        out,
        pack_id="sample",
        work="tlg0562.tlg001",
        edition="perseus-grc2",
        source_url="https://example/tei.xml",
        license="CC BY-SA 4.0",
        counts=counts,
        schema_version=1,
    )
    assert manifest["sha256"] == sha256_file(out)
    assert manifest["counts"]["passages"] == 3
    assert manifest["size_bytes"] > 0

    path = write_manifest(manifest, tmp_path / "pack.manifest.json")
    assert path.exists()
