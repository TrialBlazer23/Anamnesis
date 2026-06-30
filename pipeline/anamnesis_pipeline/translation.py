"""Parse the Wikisource Haines 1916 EPUB into book.chapter -> English.

The EPUB has one XHTML file per book (`..._Book_N.xhtml`); each numbered
chapter starts with a `N.` paragraph and may span several paragraphs. Inline
`[n]` footnote markers are stripped. Output is keyed `"book.chapter"` (e.g.
`"7.59"`) to align with the Greek edition's `book.chapter.section` refs.

Haines 1916 is public domain (pre-1929); use it, not Farquharson 1944.
"""

from __future__ import annotations

import re
import zipfile
from pathlib import Path

from lxml import etree

_XHTML = "{http://www.w3.org/1999/xhtml}"
_CHAPTER = re.compile(r"^(\d+)\.\s+(.*)$", re.S)
_FOOTNOTE = re.compile(r"\[\d+\]")
_BOOK_FILE = re.compile(r"Book_(\d+)\.xhtml$")


def _clean(text: str) -> str:
    return _FOOTNOTE.sub("", re.sub(r"\s+", " ", text)).strip()


def parse_book_xhtml(data: bytes | str, book: int) -> dict[str, str]:
    """Parse one book's XHTML into `{"book.chapter": english}`."""
    raw = data.encode("utf-8") if isinstance(data, str) else data
    root = etree.fromstring(raw)

    chapters: dict[str, str] = {}
    current: str | None = None
    buffer: list[str] = []

    def flush() -> None:
        if current is not None and buffer:
            chapters[f"{book}.{current}"] = _clean(" ".join(buffer))

    for p in root.iter(f"{_XHTML}p"):
        text = re.sub(r"\s+", " ", "".join(p.itertext())).strip()
        if not text:
            continue
        match = _CHAPTER.match(text)
        if match:
            flush()
            current, buffer = match.group(1), [match.group(2)]
        elif current is not None:
            buffer.append(text)  # continuation paragraph of the current chapter
    flush()
    return chapters


def parse_haines_epub(path: str | Path) -> dict[str, str]:
    """Parse every book file in the EPUB into a `book.chapter -> English` map."""
    out: dict[str, str] = {}
    with zipfile.ZipFile(path) as zf:
        for name in zf.namelist():
            match = _BOOK_FILE.search(name)
            if match:
                out.update(parse_book_xhtml(zf.read(name), int(match.group(1))))
    return out
