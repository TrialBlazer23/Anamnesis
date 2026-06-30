from pathlib import Path

from anamnesis_pipeline.translation import parse_book_xhtml

FIXTURE = Path(__file__).parent / "fixtures" / "sample_book.xhtml"


def test_parses_numbered_chapters_keyed_by_book_chapter():
    chapters = parse_book_xhtml(FIXTURE.read_bytes(), book=1)
    assert set(chapters) == {"1.1", "1.2", "1.9"}
    assert chapters["1.1"] == (
        "From my Grandfather Verus, a kindly disposition and sweetness of temper."
    )


def test_strips_footnote_markers():
    chapters = parse_book_xhtml(FIXTURE.read_bytes(), book=1)
    assert "[1]" not in chapters["1.1"]
    assert "[2]" not in chapters["1.2"]


def test_merges_continuation_paragraphs():
    # Chapter 9 spans two paragraphs; the second has no leading number.
    chapters = parse_book_xhtml(FIXTURE.read_bytes(), book=1)
    assert chapters["1.9"].startswith("From Sextus, kindliness")
    assert chapters["1.9"].endswith("symptom of anger.")


def test_skips_headers_before_first_chapter():
    chapters = parse_book_xhtml(FIXTURE.read_bytes(), book=1)
    assert not any("MARCUS AURELIUS" in v or "BOOK I" in v for v in chapters.values())
