# Restored-Attic Audio Recording Script

**This is the critical-path dependency for the Learn tab** (spec §9): there is no
freely-licensable complete restored-Attic audio set and no usable Ancient Greek
TTS, so this audio must be **recorded** (own recording or a licensed reciter).
A single voice session against this fixed script yields the entire offline asset set.

## Pronunciation scheme
**Restored Classical Attic, 5th–4th c. BCE, per Allen's *Vox Graeca*.** Key points
the reciter must honour:
- Aspirates **φ θ χ = [pʰ tʰ kʰ]** (puff of air), NOT fricatives [f θ x]. *(The app
  also ships a "fricative fallback" set as an option — see note at the end.)*
- Plain voiced stops **β δ γ = [b d g]**; γ before velar = [ŋ].
- **ζ = [zd]**; **υ = [y]/[yː]** (French *u*); **η = [ɛː]**, **ω = [ɔː]**.
- **ει = [eː]**, **ου = [uː]** — pure long monophthongs, no glide.
- Distinguish **long vs short** vowels clearly (~2× duration); pitch accent may be a
  light stress (productive pitch is optional/stretch).

## Asset id convention
Filenames = `<id>.<ext>` (e.g. `snd_alpha.opus`). IDs are referenced from
`letters.csv`, `diphthongs.csv`, and `minimal_pairs.csv`.

## 1. Letter sounds (24) — `snd_<name>`
Record the **sound** of each letter (not the name): α β γ δ ε ζ η θ ι κ λ μ ν ξ ο π
ρ σ τ υ φ χ ψ ω. Aspirates with a clear puff; ρ trilled; ζ as [zd]; υ as [y].

## 2. Letter names (24) — `nm_<name>`
The Greek letter names: ἄλφα, βῆτα, γάμμα, δέλτα, ἒ ψιλόν, ζῆτα, ἦτα, θῆτα, ἰῶτα,
κάππα, λάμβδα, μῦ, νῦ, ξεῖ, ὂ μικρόν, πεῖ, ῥῶ, σῖγμα, ταῦ, ὖ ψιλόν, φεῖ, χεῖ, ψεῖ,
ὦ μέγα.

## 3. Diphthongs (8 proper + 3 improper) — `snd_dip_*`
αι [ai̯], ει [eː], οι [oi̯], υι [yi̯], αυ [au̯], ευ [eu̯], ηυ [ɛːu̯], ου [uː].
Improper (iota subscript, NOT pronounced): ᾳ [aː], ῃ [ɛː], ῳ [ɔː].

## 4. Minimal-pair / quantity exemplars — for the listening drills
- Length pairs (say each clearly long vs short): ε vs η, ο vs ω.
- Hidden-quantity exemplars: short ᾰ vs long ᾱ (`snd_alpha_short` / `snd_alpha_long`);
  short ῐ vs long ῑ (`snd_iota_short` / `snd_iota_long`).
- Aspirate pairs (reuse the letter-sound clips): π/φ, τ/θ, κ/χ.

## 5. DCC core vocabulary (~500 words)
Record each lemma in `pipeline/data/dcc_greek_core.csv` (the headword form), restored
Attic, with correct breathings. IDs: `voc_<rank>` (by frequency rank). Start with the
top ~50 (ὁ/ἡ/τό, καί, αὐτός, εἰμί, λέγω …) since those are seeded first (Unit 5).

## 6. Euthyphro 2a (the handoff passage)
Record the opening of Plato's *Euthyphro* as (a) the whole passage and (b) per-word
clips for tap-to-hear. IDs: `euthy_2a_full`, `euthy_2a_w<NN>`. First line:
> Τί νεώτερον, ὦ Σώκρατες, γέγονεν, ὅτι σὺ τὰς ἐν Λυκείῳ καταλιπὼν διατριβὰς …

## Optional second pass — "fricative fallback" (φ θ χ as [f θ x])
For the aspirate-fallback toggle (spec rec. #2), optionally record an alternate set
`snd_phi_fric`, `snd_theta_fric`, `snd_chi_fric`. Default ships the true aspirates.

---
**Licensing:** ship recorded audio under a license compatible with the app (GPLv3
project; CC-BY-SA or CC-BY recommended for the audio so it stays bundleable). Do
**not** bundle SORGLL / Ranieri / Mastronarde / Hagel audio without written
permission. Wikimedia/Wiktionary "Grc-" files (CC-BY-SA 4.0) may be used as a
temporary scaffold with attribution while the real set is recorded.
