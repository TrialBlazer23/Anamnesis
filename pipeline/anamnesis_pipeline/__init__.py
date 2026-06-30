"""Anamnesis content-pack pipeline.

Builds the read-only SQLite content packs the Android app ships / downloads.
Runs in CI or on a desktop — never on-device (CLTK pulls torch/stanza).

Content packs are plain read-only SQLite (FTS5), kept separate from the app's
SQLCipher-encrypted user DB, which holds private SRS state only.
"""

from .normalize import nfc, strip_diacritics

__all__ = ["nfc", "strip_diacritics"]
