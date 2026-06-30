"""Unicode normalization helpers for polytonic Greek.

Display text is NFC-normalized (HarfBuzz/ccmp render precomposed best). Search
uses a separate diacritic-stripped key so lookups are accent-insensitive.
"""

from __future__ import annotations

import unicodedata


def nfc(text: str) -> str:
    """Normalize to NFC (precomposed) for display."""
    return unicodedata.normalize("NFC", text)


def strip_diacritics(text: str) -> str:
    """Return an accent-insensitive search key.

    Decompose to NFD, drop combining marks (accents, breathings, iota
    subscript, diaeresis), and lowercase. Greek final sigma is folded to a
    medial sigma so word-final matches behave consistently.
    """
    decomposed = unicodedata.normalize("NFD", text)
    stripped = "".join(ch for ch in decomposed if not unicodedata.combining(ch))
    return stripped.lower().replace("ς", "σ")  # ς -> σ
