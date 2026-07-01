"""Load the Dodson Koine Greek lexicon (CC0 1.0 — LICENSE verified upstream).

Source: biblicalhumanities/Dodson-Greek-Lexicon `dodson.csv` — tab-separated,
5,408 entries with Strong's/GK numbers, Beta Code headwords, and two gloss
tiers (brief for popups, longer for detail views). Useful for Koine texts —
including the *Meditations* — as a complement to the DCC core and LSJ.
"""

from __future__ import annotations

import csv
from dataclasses import dataclass
from pathlib import Path

import betacode.conv

from .normalize import nfc


@dataclass
class KoineEntry:
    lemma: str
    brief: str
    full: str
    strongs: str


def _headword_to_unicode(beta: str) -> str:
    """Convert a Dodson Beta Code headword to NFC Unicode Greek.

    Headwords may carry trailing article/gender tags after a comma
    (``*)aarw/n, o(``) — keep only the headword itself.
    """
    head = beta.split(",")[0].strip()
    return nfc(betacode.conv.beta_to_uni(head))


def load_dodson(csv_path: str | Path) -> list[KoineEntry]:
    entries: list[KoineEntry] = []
    with open(csv_path, newline="", encoding="utf-8-sig") as fh:
        reader = csv.DictReader(fh, delimiter="\t")
        for row in reader:
            beta = (row.get("Greek Word") or "").strip()
            if not beta:
                continue
            entries.append(
                KoineEntry(
                    lemma=_headword_to_unicode(beta),
                    brief=(row.get("English Definition (brief)") or "").strip(),
                    full=(row.get("English Definition (longer)") or "").strip(),
                    strongs=(row.get("Strong's") or "").strip(),
                )
            )
    return entries
