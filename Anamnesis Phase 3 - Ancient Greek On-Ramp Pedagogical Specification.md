# Anamnesis Phase 3: The Ancient Greek On-Ramp — Pedagogical Specification & Content Reference

## TL;DR
- **Teach RESTORED CLASSICAL ATTIC (5th–4th c. BCE) using Allen's *Vox Graeca* scheme as the authoritative reference**, with one honest pedagogical compromise flagged (aspirates φ θ χ): the linguistically correct value is true aspirated stops [pʰ tʰ kʰ], and the module should teach these but offer a "fricative fallback" toggle, because Mastronarde and others document that purist aspirates cause spelling errors in beginners.
- **Sequence the on-ramp in 9 short units (0–8)**: orientation → familiar letters → the rest of the alphabet in difficulty-graded batches with interactive SRS drills → vowel quantity, diphthongs & breathings → accents/pitch (taught for recognition, not production) → "how Greek words work" (inflection/lemma concept — the single biggest conceptual hurdle) → the three kinds of "different written versions of the same word" + punctuation, ending with a scaffolded handoff into Plato's *Euthyphro* (82% of whose word-forms are covered by the DCC core).
- **The audio problem is real and must be solved by recording your own**: every well-known restored-Attic recording (SORGLL/Daitz, Ranieri, Mastronarde, Hagel) is all-rights-reserved; the only freely bundleable audio is sparse CC-BY-SA Wikimedia/Wiktionary files; there is no good Ancient Greek TTS (only robotic GPL-v3 eSpeak-NG "grc"). Plan to commission/record human restored-Attic audio for the ~24 letters + 8 diphthongs + ~500 DCC core words.

## Key Findings

### A. The pronunciation system is settled science at the core, uncertain at the edges
W. Sidney Allen's *Vox Graeca* (3rd ed., 1987, Cambridge UP) is the universally cited reference for reconstructed 5th-c. Attic, and scholars broadly agree on the consonants and the vowel system; the genuinely uncertain area is the precise phonetic realization of the **pitch accent** and fine vowel qualities. The reconstructed Attic vowel system has **5 short and 7 long vowels** (/a e i y o/ and /aː ɛː eː iː yː uː ɔː/), with **phonemic vowel length** — length distinguishes words and governs meter. This is the through-line of the whole module: the difference between ε/η and ο/ω *is* a length-plus-quality difference, and α/ι/υ hide a length difference not shown in spelling.

### B. The biggest *conceptual* hurdle is inflection, not the alphabet
The user's stated confusion — seeing "the same word" in different written forms — has three distinct causes that must be taught as three separate things: (a) **inflection** (λόγος/λόγου/λόγῳ/λόγον/λόγε — same lemma, different endings), (b) **accent shift in context** (acute→grave, recessive vs. persistent accent, enclitics/proclitics), and (c) **genuine orthographic/dialectal variation** (Attic θάλαττα vs. Ionic/Koine θάλασσα; elision, crasis, movable nu). Conflating these is the known beginner trap.

### C. Comprehensible-input pedagogy + SRS is the right hybrid
The Ancient Language Institute (comprehensible input + active pedagogy, after Krashen/VanPatten), Ranieri's "Ancient Greek in Action," and the broader CI tradition all argue for getting the learner reading real, comprehensible text early rather than front-loading exhaustive grammar. This validates the app's design: use *interactive SRS drills* for the mechanical letter-sound layer (where rote mastery genuinely helps), then hand off fast to the *reader* for everything conceptual, gloss-supported.

---

## Details

### 1. RESTORED ATTIC PRONUNCIATION — Complete Phonetic Reference

All values below are reconstructed Classical Attic of the 5th–4th c. BCE, following Allen's *Vox Graeca* (the scheme that Wikipedia's "Ancient Greek phonology," Mastronarde's *atticgreek.org*, and the *Cambridge Grammar of Classical Greek* all reproduce). IPA in brackets.

#### 1a. The 24 letters

| Letter | Name (Attic) | IPA value | Teaching note |
|---|---|---|---|
| Α α | ἄλφα | [a] short, [aː] long | "hidden quantity" — length not shown in spelling |
| Β β | βῆτα | [b] | true voiced stop (NOT [v] of Modern Greek) |
| Γ γ | γάμμα | [g]; [ŋ] before γ κ χ ξ | nasal "γ" in γγ/γκ/γχ (ἄγγελος) |
| Δ δ | δέλτα | [d] | true voiced stop (NOT [ð]) |
| Ε ε | ἒ (εἶ) | [e] short | "epsilon" = ἒ ψιλόν, a post-classical name |
| Ζ ζ | ζῆτα | [zd] (see note) | double consonant; most likely [zd] in Attic |
| Η η | ἦτα | [ɛː] long | long open-e; the length-pair of ε |
| Θ θ | θῆτα | [tʰ] aspirated | true aspirate, NOT [θ] (Erasmian/Modern) |
| Ι ι | ἰῶτα | [i] / [iː] | hidden quantity |
| Κ κ | κάππα | [k] | unaspirated |
| Λ λ | λάμβδα | [l] | |
| Μ μ | μῦ | [m] | |
| Ν ν | νῦ | [n] | |
| Ξ ξ | ξεῖ (ξῖ) | [ks] | double consonant |
| Ο ο | οὖ | [o] short | "omicron" = ὂ μικρόν, a post-classical name |
| Π π | πεῖ (πῖ) | [p] | unaspirated |
| Ρ ρ | ῥῶ | [r], initial [r̥] (voiceless) | trilled; initial ρ takes rough breathing ῥ |
| Σ σ/ς | σῖγμα | [s], [z] before voiced | ς word-finally, σ elsewhere |
| Τ τ | ταῦ | [t] | unaspirated |
| Υ υ | ὖ (ὗ ψιλόν) | [y] / [yː] | French *u*/German *ü*; NOT [u] |
| Φ φ | φεῖ (φῖ) | [pʰ] aspirated | true aspirate, NOT [f] |
| Χ χ | χεῖ (χῖ) | [kʰ] aspirated | true aspirate, NOT [x]/[ç] |
| Ψ ψ | ψεῖ (ψῖ) | [ps] | double consonant |
| Ω ω | ὦ (μέγα) | [ɔː] long | long open-o; the length-pair of ο |

#### 1b. Vowel quantity (long vs. short)
- **Marked pairs**: ε [e] vs η [ɛː] (η is both longer *and* more open); ο [o] vs ω [ɔː] (same relationship). Allen reconstructs the long vowels written ει [eː] and ου [uː] as *close* long vowels, distinct in height from η/ω.
- **Hidden quantity**: α, ι, υ each represent both a short and a long vowel with the *same letter*. Teaching/dictionary convention marks them with a macron (ᾱ ῑ ῡ) for long, breve (ᾰ ῐ ῠ) for short. Long vowels were pronounced roughly twice as long as short.
- **Why it matters, for a beginner**: (1) it changes meaning (vowel length is phonemic — some word pairs differ only in length); (2) it determines accent placement (a circumflex can only sit on a long vowel/diphthong; the accent rules depend on whether the last syllable is long or short); (3) it is the basis of all Greek poetic meter. **Teach length from day one** as part of each vowel's identity, not as an afterthought — Ranieri and Allen both stress that retrofitting length later is painful.

#### 1c. Diphthongs (reconstructed Attic values)
| Diphthong | IPA | Note |
|---|---|---|
| αι | [ai̯] | like English "eye" |
| ει (genuine) | [eː] | a *long close-e monophthong* in Classical Attic, NOT [ei̯] |
| οι | [oi̯] | like "boy" |
| υι | [yi̯] | rare; monophthongized to [yː] in Attic |
| αυ | [au̯] | like "ow" |
| ευ | [eu̯] | "eh-oo" glide |
| ηυ | [ɛːu̯] | long-first-element diphthong |
| ου (genuine) | [uː] | a *long close-u monophthong*, NOT [ou̯] |

- **Genuine vs. spurious diphthongs**: ει and ου each have two origins. A *genuine* diphthong comes from an actual ε+ι / ο+υ sequence (λείπω, "I leave"). A *spurious* diphthong is spelled ει/ου but arose from **contraction** (ἐφίλεε → ἐφίλει) or **compensatory lengthening** (after a lost consonant: *dont-s → δούς). Crucially, by the Classical period **both genuine and spurious ει were pronounced identically as [eː], and both kinds of ου as [uː]** — the spelling ΕΙ/ΟΥ was actually adopted in late-5th-c. Attic precisely to write these long close vowels and distinguish them from short ε/ο. For the beginner: *don't try to hear a glide in ει/ου — they are pure long vowels.* (Smyth §6–7; *Cambridge Grammar* §1.23; UC Press *Introduction to Attic Greek*, Unit 1.)
- **Long ("improper") diphthongs** ᾳ ῃ ῳ — the iota was originally pronounced as an offglide [aːi̯ ɛːi̯ ɔːi̯], lost it by the Hellenistic period, and is written *subscript* (iota subscript) from the Byzantine period on. In conventional pronunciation the subscript iota is not pronounced.

#### 1d. Consonants — the key restored-Attic contrasts
- **Aspirated stops φ θ χ = [pʰ tʰ kʰ]** (a puff of air after p/t/k, as in English "pot, top, kit"), **NOT** the fricatives [f θ x] of Erasmian/Modern Greek. Allen's evidence is decisive: Latin transcribed Greek φ first as *p*, later as *ph* (Philippus), **never as *f*** in classical times, which would have been the natural rendering of a fricative. This is a *defining* feature of the restored system and the main place it diverges from what most learners have heard.
- **Plain voiced stops β δ γ = [b d g]**, NOT [v ð ɣ] (Modern Greek). γ before a velar (γγ, γκ, γχ) = velar nasal **[ŋ]** (ἄγγελος [áŋgelos], ἀνάγκη).
- **ζ = [zd]** (most likely; some argue [dz]). Evidence: Ἀθήναζε < Ἀθήνας-δε; ζ behaves as a *double consonant* metrically (makes position). The scholarly debate is genuine and worth a one-line honest note, but [zd] is the standard teaching value (Allen p. 56 ff.).
- **Double consonants ξ = [ks], ψ = [ps]**; ζ is the third "double" letter.
- **ρ** = trilled [r]; **initial ρ is voiceless [r̥] and written with a rough breathing (ῥ)**; internal ρρ is often written ῤῥ.
- **σ** = [s], voiced to [z] before voiced consonants (e.g. in κόσμος).

#### 1e. Breathings
Every word beginning with a **vowel** carries a breathing mark: **rough breathing** (῾, *spiritus asper*) = a preceding [h] (ὁ = [ho]); **smooth breathing** (᾿, *spiritus lenis*) = no sound (ἐν = [en]). Rules:
- Initial **ρ** always takes rough breathing (ῥ-) → voiceless/aspirated r.
- Initial **υ** almost always takes rough breathing (ὑ-).
- On a **diphthong**, the breathing sits over the *second* vowel (αἱ, οἱ).
- On a **capital**, the breathing sits to the *left* of the letter (Ἀ, Ὁ).
Historically the rough breathing descends from the letter Η when it still wrote [h] in the old Attic alphabet; the [h] sound was lost in Koine.

#### 1f. Pitch accent — what it was, and how much to teach
Greek had a **pitch (melodic) accent, not a stress accent**. The accent marks were invented by **Aristophanes of Byzantium** (c. 257 – c. 185/180 BCE), head librarian at the Library of Alexandria — i.e., they are *not* present in 5th-c. texts; the first papyri with accent marks date from his era. They encode pitch:
- **Acute (´, ὀξύς)** = high pitch on that syllable (a rise to a High tone).
- **Circumflex (῀, περισπωμένη)** = a rise-then-fall *within one long syllable* (High-Low); only on long vowels/diphthongs, only on the last two syllables.
- **Grave (`, βαρύς)** = replaces a final-syllable acute when another word follows without pause; represents a *lowered/suppressed* pitch (probably no full rise).
The interval was substantial: Dionysius of Halicarnassus (*De Compositione Verborum* §11) describes the melody of speech as confined to an interval of "about a fifth" — usually read as the maximum normal high-low spread — and the surviving Delphic hymns track the word accents closely. **Scholarly honesty**: the *system* (which syllable, what mark) is certain; the *precise phonetic contour* is reconstructed and debated (recent work questions whether the acute is a rise or a level High mapped across the syllable). The accent had become a *stress* accent by roughly the 2nd–4th c. CE.

**Recommendation for the beginner**: Teach the *names and meaning* of the three marks and the fact that they were musical, for **recognition only**. Do **not** require the learner to produce correct pitch contours to advance — Mastronarde and most textbooks explicitly let students use a stress approximation. Defer productive pitch to optional "stretch" content. This is the honest, evidence-based position: attempting full pitch production is a known source of discouragement and is not necessary to start reading.

#### 1g. Comparison table — Restored Attic vs Erasmian vs Modern Greek

| Letter/sign | Restored Attic (Allen) | Erasmian (classroom) | Modern Greek |
|---|---|---|---|
| η | [ɛː] long open-e | [ɛː]/[eː] "ay" | [i] |
| υ | [y]/[yː] (French *u*) | [y] or [u] | [i] |
| ζ | [zd] | [dz]/[z] | [z] |
| θ | [tʰ] aspirate | [θ] "th" | [θ] |
| φ | [pʰ] aspirate | [f] | [f] |
| χ | [kʰ] aspirate | [x]/[k] | [x]/[ç] |
| β | [b] | [b] | [v] |
| γ | [g] | [g] | [ɣ]/[ʝ] |
| δ | [d] | [d] | [ð] |
| αι | [ai̯] "eye" | [ai̯] | [e] |
| ει | [eː] long-e | [ei̯] | [i] |
| οι | [oi̯] "boy" | [oi̯] | [i] |
| αυ | [au̯] | [au̯] | [av]/[af] |
| ευ | [eu̯] | [eu̯] | [ev]/[ef] |
| rough breathing | [h] | [h] | silent |
| accents | pitch | stress | stress |

The pattern to teach: **Modern Greek** has collapsed η, υ, ει, οι, υι all into [i] ("iotacism") — beautiful but ambiguous; **Erasmian** keeps the vowels distinct (good for spelling) but turns the aspirates into fricatives and uses stress — an artificial classroom hybrid that no historical Greek ever spoke; **Restored Attic** is the historically grounded reconstruction of how Plato's contemporaries actually sounded.

### 2. SEQUENCE FOR TEACHING THE ALPHABET

The pedagogy literature (Danny Bate's historical-linguistic approach; standard NT-Greek primers; Omilo's modern-Greek teaching experience) converges on **graded difficulty by familiarity and confusability**, not strict alphabetical order, for adults. Recommended order in four batches:

- **Batch 1 — "free" lookalikes (same shape, ~same sound)**: Α, Β, Ε, Ι, Κ, Ο, Τ. These map cleanly to Latin and build instant confidence; the learner can read a few real words almost immediately.
- **Batch 2 — new shapes, new sounds (low confusion)**: Δ, Λ, Μ, Π, Σ/ς, Φ, Ψ, Ω, Θ, Γ. These look distinctively Greek and don't collide with Latin expectations.
- **Batch 3 — the "false friends" (look like a Latin letter but sound different)** — taught deliberately late and contrastively: **Η** (looks like Latin H/n but = [ɛː]), **Ν** (looks like v but = [n]), **Ρ** (looks like p but = [r]), **Χ** (looks like x but = [kʰ]), **Υ** (looks like y/u but = [y]), **Ζ** (= [zd]). These are the documented confusion set and must each be drilled against their Latin "twin."
- **Batch 4 — the special cases**: the two sigmas (σ medial/initial, ς final), the velar-nasal γ, and ξ/ψ as double consonants.

**Capitals vs. lowercase**: Ancient texts were written in **majuscule (capitals) only**, in *scriptio continua* (no spaces, no lowercase, no punctuation, no accents); minuscule (lowercase) is a 9th-c. CE Byzantine bookhand and is what every modern edition — including the app's reader — uses. **Recommendation**: teach **lowercase first and primarily** (that's what the reader shows), introduce capitals as a secondary recognition layer (needed for proper names: Σωκράτης, and sentence starts in modern editions). Show one "this is what an ancient manuscript actually looked like" example (all-caps, no spaces) as a memorable one-off so the learner understands that punctuation/case/spacing are *editorial*, not original — but do **not** drill majuscule scriptio continua as a skill.

### 3. WRITING / FORMING THE LETTERS

Yes, teach handwriting — it has real value here. Motor encoding (writing by hand) is a well-established aid to letter recognition, and the user explicitly values S-Pen writing. Guidance:
- **Lowercase first**, single-stroke where possible. Resources (inthebeginning.org; greekalphabet.net) note most minuscules are one continuous stroke; the common multi-stroke letters are **τ, χ, ψ** (and θ).
- **Baseline/descender awareness**: most letters sit between baseline and x-height; **β, γ, η, μ, ρ, φ, χ, ψ, ζ, ξ** descend below the baseline; **β, δ, θ, λ, ξ** rise above.
- **Common malformations to coach against** (from math-handwriting and Greek-handwriting guides): ν vs υ (keep ν pointed at the bottom, υ rounded/with a tail); ζ vs ξ; ν vs Latin v; ρ tail vs Latin p; χ (make the rising stroke larger than the falling one to distinguish from x); ω kept rounded vs Latin w; final ς vs σ.
- **App support**: S-Pen tracing overlays with animated stroke-order arrows and start-point dots (the standard convention), light "ghost" letterforms to trace, and *optional* handwriting recognition for the "type/write the letter" drill. Mark writing practice as **valued but not gating** — recognition and sound, not calligraphy, are the success criteria.

### 4. HOW GREEK WORDS WORK (the conceptual core)

This unit is **reading-based and conceptual**, delivered in the app as short explanatory cards interleaved with reader examples — *not* paradigm memorization.

- **Inflection**: Greek signals a word's grammatical job by **changing its ending**, so a single dictionary word (lemma) appears in many surface forms. Concrete anchor paradigm (2nd-declension masculine, λόγος "word/argument"):
  - **λόγος** — nominative (subject): *the word [does something]*
  - **λόγου** — genitive (of/from): *of the word*
  - **λόγῳ** — dative (to/for/with/by): *to/for the word*
  - **λόγον** — accusative (object): *[I see] the word*
  - **λόγε** — vocative (address): *O word!*
  All five are "the same word." This is **why tap-to-parse shows a dictionary form (λόγος) different from what's on the page (λόγῳ)** — and naming that explicitly defuses the user's confusion.
- **Lemma vs. inflected form**: the *lemma* is the headword you look up (nominative singular for nouns, 1st-person singular present for verbs: λέγω); the *form on the page* is whatever the grammar of the sentence requires. The app's lexica (Middle Liddell/LSJ) and SRS should both key on the lemma, while the reader shows the inflected form — teach the learner that the parser's job is to bridge the two.
- **A gentle first pass at the cases** (conceptual only): **Nominative** = the subject / who's doing it; **Genitive** = possession, source, "of"; **Dative** = indirect object, "to/for," and the object of many prepositions, instrument/means; **Accusative** = direct object, goal of motion; **Vocative** = direct address (ὦ Σώκρατες, "O Socrates" — which the learner meets in the very first line of *Euthyphro*). Verbs likewise change for **person, number, tense, mood, voice**; introduce only person/number/tense at this stage.
- **Word-order freedom**: because the *endings* carry the grammatical roles, Greek word order is far freer than English and is used for **emphasis and flow**, not grammar. A reader must therefore **read to the end of the clause and use the endings — not the position — to find subject and object.** This is a mindset shift worth stating explicitly.

### 5. THE THREE KINDS OF "DIFFERENT WRITTEN VERSIONS OF THE SAME WORD"

Teach these as three clearly labeled, separate phenomena (this directly resolves the user's stated confusion):

**(a) Inflectional variation** — *same lemma, different ending* (λόγος → λόγῳ). Covered in §4. The most common reason a word "looks different." Handled by tap-to-parse.

**(b) Accent/diacritic variation in context** — the *same* word appears with *different accent marks* depending on its surroundings:
- **Acute → grave**: a word with an acute on its **last syllable** (ἀγαθός) changes that acute to a **grave** (ἀγαθὸς) when another word follows without an intervening pause — but keeps the acute before a pause or punctuation. So ἀγαθός and ἀγαθὸς are the *same word*.
- **Recessive vs. persistent accent**: **verbs** mostly have *recessive* accent (it falls as far back as the rules allow, so it moves as endings change); **nouns/adjectives** have *persistent* accent (it stays where the lemma has it unless a rule forces a shift). This is why the accent seems to "jump around" across forms of one word.
- **Enclitics and proclitics**: little words that lean on a neighbor. **Proclitics** (ὁ, ἡ, οἱ, αἱ, ἐν, εἰς, ἐκ, εἰ, ὡς, οὐ) have *no accent of their own*. **Enclitics** (τις/τι, με/μου/μοι, ἐστι, the particles γε/τε, etc.) throw their accent back onto the preceding word, which is why you'll see a word carry a *second* accent (ἄνθρωπός τις) or an enclitic appear with/without an accent in different spots.
For the beginner: **accent differences usually do NOT mean a different word.** (Rare minimal pairs exist — e.g. τις "someone" enclitic vs. τίς "who?" interrogative — and the app can flag those specifically.) The takeaway card: *"If two forms differ only in accent marks, it is almost always the same word in a different position, not a new word."*

**(c) Orthographic / dialectal / phonological variation** — *genuinely different spellings* of the same root, mostly across dialects/eras:
- **Attic vs. Ionic/Koine**: Attic **-ττ-** where Ionic/Koine has **-σσ-** (θάλαττα vs θάλασσα "sea"; πράττω vs πράσσω "do"); Attic **-ρρ-** vs Ionic **-ρσ-** (θάρρος vs θάρσος). Attic **ξύν** vs Koine **σύν** ("with"). These matter when the learner moves from **Attic (Plato's *Euthyphro*)** to **Koine (Marcus Aurelius, Epictetus)**: Koine is essentially simplified Attic-with-Ionic, drops most of the -ττ-/ξύν Atticisms (Attic ττ usually becomes σσ), loses the dual, and reduces the optative.
- **Movable nu (ν ephelkystikon)**: certain endings add a final -ν before a vowel or pause (ἐστί ~ ἐστίν; λέγουσι ~ λέγουσιν) — same word, euphonic -ν.
- **Elision**: a final short vowel drops before a word starting with a vowel, marked with an apostrophe (δέ + ἐστι → δ' ἐστι; ἀπό + αὐτοῦ → ἀπ' αὐτοῦ). The apostrophe is a post-classical editorial mark.
- **Crasis**: two words *fuse* across a word boundary, marked with a coronis (which looks like a smooth breathing): τὸ + ἐναντίον → τοὐναντίον; καὶ + ἐγώ → κἀγώ. The learner meets ἐγᾦμαι (= ἐγὼ οἶμαι) in the opening page of *Euthyphro*.
Teach at the "don't panic" level: the learner needs to *recognize* these so they aren't thrown, and know the reader/parser will resolve them — not master the historical phonology.

### 6. PUNCTUATION & TEXT CONVENTIONS

- **Question mark = ;** (a semicolon-shaped sign). τί φῄς; = "What do you say?" This is the single most important convention — the learner will misread questions otherwise.
- **Ano teleia = · (raised dot)** = a colon/semicolon-level pause (a "high dot," *stigmḕ teleía* in origin).
- **Period and comma** are the same as English.
- **Scriptio continua**: original ancient texts had **no word spaces, no lowercase, no punctuation, and no accents** — all of these (spacing, case, punctuation, the apostrophes/coronides of §5, even the accent marks) are **editorial additions** by later (Hellenistic→Byzantine→modern) editors. Stating this once is genuinely clarifying: it explains *why* the same text can be punctuated differently in different editions, and why accents/breathings are "added."
- **Iota subscript (ᾳ ῃ ῳ)**: a small iota written *under* a long vowel α/η/ω. It marks a historical long diphthong whose iota offglide was lost in pronunciation by the Hellenistic era; it's written subscript from the Byzantine period. It is **not pronounced** in conventional reading but is **grammatically informative** — e.g., the dative singular λόγῳ, τῇ. (On capitals it's written beside the letter as *iota adscript*: ΩΙ.)

### 7. PEDAGOGICAL LESSON SEQUENCE (the buildable spec)

Nine units (0–8). Each: **Objective · Taught · Drill/Activity · SRS feed · Advance criterion.**

**Unit 0 — Orientation (no drills).** *Objective*: set expectations. *Taught*: what restored Attic is, why this app teaches it, the "ancient texts were ALL CAPS with no spaces" reveal, and the promise that the reader + tap-to-parse will carry them. *Advance*: acknowledge.

**Unit 1 — Familiar letters (Batch 1: Α Β Ε Ι Κ Ο Τ).** *Objective*: read/sound 7 letters; read first real syllables. *Taught*: letterform + name + restored sound + lowercase handwriting. *Drills*: letter→sound, sound→letter (audio), letter-name recall, S-Pen tracing. *SRS*: each letter becomes a 3-sided card set (see §8) entered into FSRS. *Advance*: ≥90% recognition accuracy across a session and all 7 cards reaching first successful review.

**Unit 2 — Distinctive Greek letters (Batch 2: Δ Λ Μ Π Σ/ς Φ Ψ Ω Θ Γ).** *Objective*: add 10 letters incl. the aspirate φ/θ and double ψ. *Taught*: forms, restored sounds ([pʰ tʰ], aspirate contrast demoed with audio minimal pairs), the two sigmas, handwriting (note multi-stroke ψ θ). *Drills*: as Unit 1 + **two-sigma placement drill** + **aspirate minimal-pair listening** (πῖ vs φῖ). *SRS*: new cards added; FSRS interleaves with Unit 1. *Advance*: ≥90% recognition; sigma-placement drill passed.

**Unit 3 — False friends (Batch 3: Η Ν Ρ Χ Υ Ζ).** *Objective*: defeat the Latin-lookalike trap. *Taught*: each letter explicitly paired against its Latin "twin" (Η≠h/n, Ν≠v, Ρ≠p, Χ≠x, Υ≠u, Ζ); restored sounds incl. η=[ɛː], υ=[y], ζ=[zd]. *Drills*: **contrastive discrimination** ("you see Ρ — is it [r] or [p]?"), mixed review with Batches 1–2, audio→letter. *SRS*: these cards flagged higher-difficulty; expect more frequent FSRS scheduling. *Advance*: ≥90% on a mixed full-alphabet recognition quiz.

**Unit 4 — Vowel quantity & diphthongs.** *Objective*: hear/encode long vs short and the 8 diphthongs. *Taught*: ε/η, ο/ω as length+quality pairs; hidden quantity of α/ι/υ (macron/breve); diphthong values; genuine vs spurious ει/ου = pure long vowels [eː]/[uː]; iota subscript preview. *Drills*: **long/short minimal-pair listening** (ε vs η, ο vs ω), diphthong→sound, "is this vowel long or short?" with macron cues. *SRS*: diphthongs as cards; quantity contrasts as listening cards. *Advance*: ≥80% on length discrimination (set lower — this is genuinely hard) + diphthong recognition ≥90%.

**Unit 5 — Breathings & the sound system in words.** *Objective*: read whole short real words aloud. *Taught*: rough vs smooth breathing, initial ρ/υ rules, breathing on diphthongs/capitals; γ-nasal; ρ trill. *Drills*: **read-the-word** (show a DCC core word, learner records/self-checks against human audio), breathing identification, transliteration practice (Greek→Latin letters and back). *SRS*: begin **DCC core vocabulary** as lemma cards (form + sound + gloss), starting with the highest-frequency ~50 (ὁ/ἡ/τό, καί, αὐτός, εἰμί, λέγω…). *Advance*: read 10 core words with correct breathings/sounds.

**Unit 6 — Accents (recognition).** *Objective*: recognize the three marks and know they're musical, without production pressure. *Taught*: acute/grave/circumflex = pitch; circumflex only on long vowel/diphthong; acute→grave-in-context rule; one honest slide on reconstruction uncertainty; "use a light stress, don't stress about pitch." *Drills*: identify-the-accent, "same word or different?" cards contrasting accent-only differences (§5b). *SRS*: light; accent-recognition cards only. *Advance*: name the three accents and correctly judge 8/10 "same word?" accent items.

**Unit 7 — How Greek words work (reading-based).** *Objective*: internalize inflection + lemma so tap-to-parse makes sense. *Taught*: §4 content via the λόγος paradigm and live reader examples; the five cases conceptually; word-order freedom. *Activity*: **guided reader micro-passages** — tap each word, see lemma vs form, predict the case's job. *SRS*: core vocab continues as **lemmas**; add a few "form→lemma" recognition cards. *Advance*: correctly identify lemma vs inflected form for 8/10 tapped words; explain in one sentence why endings matter.

**Unit 8 — Variation, punctuation & handoff to *Euthyphro*.** *Objective*: not be confused by the three kinds of variation + Greek punctuation; begin reading. *Taught*: §5 (three variation types) + §6 (punctuation, scriptio continua, iota subscript), each as a short card with a *Euthyphro* example (ἐγᾦμαι crasis; τί φῄς; question mark; ὦ Σώκρατες vocative). *Activity*: read the **opening lines of *Euthyphro*** (Τί νεώτερον, ὦ Σώκρατες, γέγονεν, ὅτι σὺ τὰς ἐν Λυκείῳ καταλιπὼν διατριβὰς…) in the reader with **training wheels ON**: every word glossed, tap-to-parse for all, audio playback of the passage, accent/breathing tooltips. *SRS*: *Euthyphro* opening vocabulary seeded into FSRS. *Advance/Exit*: read the first Stephanus section (2a) with parse support and self-rated comprehension — **the on-ramp is complete; the learner is now "reading Plato with training wheels."**

**Using SRS specifically**: For letter-sound mastery, FSRS-6 is ideal because the deck is small and heterogeneous in difficulty (false-friend letters are harder and FSRS will naturally schedule them more often, targeting ~90% retention). Note the documented caveat: FSRS guidance (RemNote/Anki) recommends **at least ~1,000 reviews with default weights before optimizing** ("until you have plenty of data for the optimizer to work with, the default weights will be more effective than ones based on your study history"; Anki lowered the optimization threshold to 400 reviews in v24.04). For a single learner with ~24 letter cards × 3 card types + ~500 vocab lemmas, start with sensible default parameters and a ~90% target retention, and let the model adapt as the vocab deck grows. Letters should "graduate" out of frequent review quickly once stable; vocabulary is the long-tail deck.

### 8. INTERACTIVE DRILL DESIGN (Android specs)

Each **letter** generates a small card family in FSRS:
1. **Letter→sound** (recognition): show ζ → learner taps/says [zd]; reveal + audio. (Recall direction: production.)
2. **Sound→letter** (audio cue): play [zd] → learner taps ζ from a grid of distractors (distractors chosen from the confusion set — e.g., for Η offer Latin-H-primed wrong answers).
3. **Letter-name recall**: show ζ → "zeta / ζῆτα."
Plus non-SRS *skill* drills: **type/write-the-letter** (soft keyboard tap **or** S-Pen handwriting recognition), **transliteration** (Greek↔Latin), **minimal-pair discrimination** (long/short vowels ε–η, ο–ω; aspirate–plain π–φ, τ–θ, κ–χ — play two clips, "same or different?"), and **two-sigma / breathing placement** micro-drills.
- **Card scheduling**: drive all recognition/recall cards through FSRS-6; keep skill drills (writing, minimal pairs) on a lighter mastery-based loop, not the SRS, until the contrast is reliable.
- **Audio is mandatory and is the hard constraint**: every sound→X and X→sound card needs a **real human restored-Attic recording** (see §9). Because there is no usable Ancient Greek TTS, you cannot generate these on the fly. Record: the 24 letter sounds, letter names, the 8 diphthongs, long/short vowel exemplars, aspirate vs plain minimal pairs, and the ~500 DCC core words (and the *Euthyphro* opening). Store as bundled audio assets (offline-first requirement).

### 9. AUDIO SOURCING — honest assessment

**The gap is real: there is no off-the-shelf, freely licensable, complete restored-Attic audio set, and no good Ancient Greek TTS.** Specifics (licensing as publicly posted — confirm legally before shipping):

- **SORGLL / Stephen Daitz** (rhapsodoi.org, rhapsodes.fll.vt.edu): the best-known restored-classical recitations, but **all-rights-reserved** ("Copyright … All rights reserved"; "Copyright 2015 The Society for the Oral Reading of Greek and Latin Literature"); Daitz's recordings were also commercially published by Audio-Forum. **Not bundleable without written permission.** They are recitations of literature, not alphabet/word drills, anyway.
- **Luke Ranieri / Polymathy / "Ancient Greek in Action"**: high-quality, uses his reconstructed "Lucian"/restored-Attic schemes, but content is **all-rights-reserved / commercial** (Patreon, audiobook store, standard YouTube license); **no posted CC license.** Would require **direct negotiation**; willingness/terms unknown. Note his default "Lucian" is Koine-leaning; his *Archaic Variant* and his dedicated Attic recitations are the restored-5th-c. ones.
- **Mastronarde / atticgreek.org**: per-letter and 100+ word audio targeting 5th–4th-c. Attic (an explicit "pedagogically practical compromise"), but **©2013 UC Regents**; only the *JavaScript* of the drills is CC-BY-NC-SA 3.0 (NonCommercial), **not the audio**. Permission required.
- **Stefan Hagel / "Sound of Ancient Greek" (Austrian Academy of Sciences)**: excellent restored-classical samples *with* reconstructed pitch accent, but **no open license stated** → permission required.
- **Wikimedia Commons / Wiktionary ("Grc-" audio, via Lingua Libre)**: the **only genuinely bundleable** option — typically **CC-BY-SA 4.0** (some CC0). **But** coverage is thin (~dozens of files), quality and exact pronunciation scheme vary per file, and ShareAlike imposes obligations on any derivative of that audio. Usable as a stopgap with attribution; not a complete solution.
- **"Found in Antiquity" Gospel of Matthew**: genuinely **CC-BY-4.0** and a good model of openness — but it's **Koine/Lucian, not Attic**, and running text, not drills.
- **TTS reality**: the only Ancient Greek ("grc") TTS is **eSpeak-NG**, which is **GPL-v3** (copyleft — bundling it imposes obligations) and **robotic**. Commercial "Ancient Greek TTS" products are Modern-Greek-voices-on-polytonic-text and are *not* restored-classical — do not trust them.

**Recommendation**: **Record your own** restored-Attic audio (or commission a recording from a qualified reciter — potentially licensing from Ranieri or a SORGLL reciter via direct agreement). This is the only way to get complete, consistent, correctly-licensed, restored-Attic coverage of the letters, diphthongs, contrasts, and the DCC core. Use Wikimedia CC-BY-SA files only as a temporary scaffold. Budget a single voice session against a fixed script (24 letters + names + 8 diphthongs + ~20 minimal-pair items + ~500 core words + *Euthyphro* 2a) — a few hours of recording yields the entire offline asset set.

### 10. AUTHORITATIVE SOURCES & CONFIDENCE LEVELS

- **Primary phonetic authority**: W. Sidney Allen, *Vox Graeca: The Pronunciation of Classical Greek* (3rd ed., Cambridge UP, 1987) — the reconstruction every other source builds on.
- **Standard grammars**: Smyth, *Greek Grammar* (genuine/spurious diphthongs §6–7; accent/enclitics §181 ff.); Mastronarde, *Introduction to Attic Greek* (2nd ed.) and its companion atticgreek.org (the practical-compromise pronunciation position); the *Cambridge Grammar of Classical Greek* (§1.23 on spurious diphthongs); Hansen & Quinn, *Greek: An Intensive Course* (rigorous traditional sequence).
- **Restored-pronunciation practitioners**: Luke Ranieri (*Lucian Pronunciation*; restored-Attic "Archaic Variant"; *Ranieri's Greek Pronunciation Chronology*); SORGLL/Daitz (restored-classical recitation tradition).
- **Pedagogy**: Ancient Language Institute (comprehensible input + active pedagogy, after Krashen/VanPatten); Athenaze (Balme & Lawall); JACT *Reading Greek*; DCC core vocabulary (Christopher Francese and collaborators, created summer 2012, CC-BY-SA 3.0): "About 500 of the most common words in ancient Greek, the lemmas that generate approximately 65% of the word forms in a typical Greek text," and per the publisher's description, **"the figure for Plato's Euthyphro is 82%"** of word-forms covered (vs. 66% for Sophocles' *Antigone*, 91% for Caesar's *Gallic War*).
- **Confidence**: **Firm** — consonant values (incl. aspirates φθχ = [pʰ tʰ kʰ]), the vowel system and phonemic length, breathings, the *structure* of the accent system, genuine/spurious diphthong pronunciation. **Genuinely uncertain (teach honestly)** — ζ as [zd] vs [dz]; the precise *phonetic contour* of the pitch accent and exact long-vowel qualities; fine dating of sound changes across the 5th–4th c. Teach the firm parts as fact and the uncertain parts as "our best reconstruction," which is itself a valuable lesson in how historical linguistics works.

## Recommendations

1. **Build the 9-unit sequence (0–8) as specified**, gating on recognition accuracy (≥90% for letters, ≥80% for the genuinely hard length-discrimination), not on handwriting or pitch production.
2. **Adopt restored Attic per Allen, with a visible "aspirate vs fricative" toggle** for φ θ χ — default to the correct aspirates [pʰ tʰ kʰ] but let a struggling learner fall back, exactly as Mastronarde recommends, to avoid spelling errors and discouragement. Re-evaluate the default only if the learner's minimal-pair discrimination on aspirates stays below ~70% after Unit 3.
3. **Teach pitch accent for recognition only**; ship productive-pitch content as optional "stretch" modules. Change this only if the learner explicitly opts into prosody/meter work.
4. **Resolve the audio gap before launch by recording a single fixed script** in restored Attic (own recording or licensed reciter). Treat Wikimedia CC-BY-SA files as a temporary scaffold only. This is the critical-path dependency — start it first.
5. **Make "the three kinds of variation" (§5) and "lemma vs form" (§4) explicit, named lessons**, since they are the user's stated confusion and the documented beginner wall. Wire tap-to-parse to always surface lemma + form + reason-for-difference.
6. **Seed the SRS with the DCC core by frequency**, starting in Unit 5, so that by the *Euthyphro* handoff the learner already knows the highest-frequency words among the 82% of *Euthyphro* word-forms the core covers.
7. **Handoff benchmark**: learner reads *Euthyphro* 2a with full gloss + parse support and self-rates ≥"mostly understood." Below that, loop back to vocabulary, not grammar.

## Caveats
- Ranieri's flagship "Lucian Pronunciation" is **Koine-era**, not 5th-c. Attic; use his *Archaic Variant* / dedicated Attic recitations for this module, and don't conflate the two. The decision record specifies *restored Attic*, which is Allen's scheme, not default Lucian.
- The pitch-accent **contour** and the exact qualities of some long vowels are reconstructions under active scholarly debate; the module should present them as such. The "musical fifth" figure derives from Dionysius of Halicarnassus and is best read as the *maximum* normal pitch spread, not a fixed interval.
- ζ = [zd] is the standard teaching value but not unanimous; a minority argues [dz]. Flagged, not hidden.
- FSRS personalization is weak below ~1,000 reviews (Anki: ~400 minimum) — the letter deck alone won't trigger it; rely on defaults early and let the vocab deck mature the model.
- All licensing statements about third-party audio reflect what is publicly posted; **any bundling of SORGLL, Ranieri, Mastronarde, or Hagel audio requires direct written permission** and should be legally confirmed before shipping.
