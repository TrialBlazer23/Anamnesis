import json
import zipfile

from build_audio_pack import ATTRIBUTION, build_audio_pack, parse_audio_manifest

MANIFEST_CSV = """\
urn:cts:greekLit:tlg0012.tlg001.perseus-grc2:1.2,https://mirror/audio/1/line_2.mp4
urn:cts:greekLit:tlg0012.tlg001.perseus-grc2:1.1,https://mirror/audio/1/line_1.mp4
urn:cts:greekLit:tlg0012.tlg001.perseus-grc2:2.1,https://mirror/audio/2/line_1.mp4
urn:cts:greekLit:tlg0012.tlg001.perseus-grc2:1.10,https://mirror/audio/1/line_10.mp4
not-a-urn-row
"""


def test_parse_filters_to_book_and_sorts_numerically():
    entries = parse_audio_manifest(MANIFEST_CSV, book=1)
    assert [e["line"] for e in entries] == ["1", "2", "10"]  # numeric, not lexicographic
    assert entries[0]["path"] == "book_1/line_1.mp4"
    assert entries[0]["urn"].endswith(":1.1")
    assert all(e["url"].startswith("https://mirror/") for e in entries)


def test_parse_other_book_and_missing_book():
    assert len(parse_audio_manifest(MANIFEST_CSV, book=2)) == 1
    assert parse_audio_manifest(MANIFEST_CSV, book=3) == []


def test_build_audio_pack_writes_zip_with_embedded_manifest(tmp_path):
    entries = parse_audio_manifest(MANIFEST_CSV, book=1)
    fetched = []

    def fake_fetch(url: str) -> bytes:
        fetched.append(url)
        return b"AAC:" + url.encode()

    out = tmp_path / "iliad_book1_audio.zip"
    embedded = build_audio_pack(
        entries,
        out,
        work_urn="urn:cts:greekLit:tlg0012.tlg001.perseus-grc2",
        book=1,
        fetch=fake_fetch,
    )

    assert embedded["pack_id"] == "tlg0012.tlg001.perseus-grc2-book1-audio"
    assert embedded["license"] == "CC BY 4.0"
    assert embedded["attribution"] == ATTRIBUTION
    assert "Chamberlain" in ATTRIBUTION
    assert len(fetched) == 3

    with zipfile.ZipFile(out) as zf:
        names = set(zf.namelist())
        assert names == {
            "book_1/line_1.mp4",
            "book_1/line_2.mp4",
            "book_1/line_10.mp4",
            "manifest.json",
        }
        # Audio is stored, not deflated (AAC is already compressed).
        assert all(i.compress_type == zipfile.ZIP_STORED for i in zf.infolist())
        assert zf.read("book_1/line_1.mp4") == b"AAC:https://mirror/audio/1/line_1.mp4"
        manifest = json.loads(zf.read("manifest.json"))
        assert manifest == embedded
        assert [f["path"] for f in manifest["files"]] == [
            "book_1/line_1.mp4",
            "book_1/line_2.mp4",
            "book_1/line_10.mp4",
        ]


def test_build_audio_pack_rejects_empty_download(tmp_path):
    entries = parse_audio_manifest(MANIFEST_CSV, book=2)
    try:
        build_audio_pack(
            entries,
            tmp_path / "bad.zip",
            work_urn="urn:cts:greekLit:tlg0012.tlg001.perseus-grc2",
            book=2,
            fetch=lambda url: b"",
        )
    except ValueError as err:
        assert "empty audio file" in str(err)
    else:
        raise AssertionError("expected ValueError for empty download")
