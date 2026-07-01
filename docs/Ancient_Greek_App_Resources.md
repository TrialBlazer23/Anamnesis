# **Technical Architecture and Resource Registry for a Multi-Dialect Ancient Greek Educational Mobile Application**

## **Dialect Segmentation and Curriculum Partitioning**

Developing a modern educational application for Ancient Greek requires a precise understanding of the diachronic and synchronic variations within the language. Ancient Greek is not a uniform linguistic entity; rather, it spans over a millennium of literary production, encompassing distinct dialects, syntactic shifts, and changing phonetic landscapes1. For a hybrid learning platform, the curriculum must be partitioned into three primary historical and literary phases to avoid cognitive interference: Epic (Homeric) Greek, Classical (Attic) Greek, and Hellenistic (Koine) Greek3.  
Epic Greek, represented primarily by the Homeric poems, is characterized by archaic morphological structures, the preservation of early Indo-European features such as the optional use of the syllabic augment, and a highly distinct metric-driven vocabulary optimized for dactylic hexameter5. Classical Attic, the dialect of Athenian philosophy, drama, and history, functions as the standard baseline for academic instruction due to its highly structured syntax and complex verbal paradigms, including the systematic use of the optative mood and the dual number4. Hellenistic Koine represents the lingua franca of the post-Alexandrian Mediterranean world, exhibiting syntactic simplification, a reduction in the optative mood, the elimination of the dual number, and a significant transition in phonology toward Modern Greek3.  
To prevent cognitive confusion, the application architecture must isolate these linguistic phases at both the database level and within the user interface. Programmatic separation is achieved by assigning a strict metadata tag to every lexicographical entry, text passage, and grammatical drill. This metadata boundary ensures that morphological parsers do not evaluate Attic texts using Homeric declensions, nor suggest classical pitch accents during Koine pronunciation exercises.

| Dialect/Era | Phonological Paradigm | Key Morphological Markers | Syntactic Profile | Target Corpora |
| :---- | :---- | :---- | :---- | :---- |
| **Epic (Homeric)** \[cite: 5, 6\] | Restored Homeric Pronunciation; residual digamma recognition6. | Unaugmented past tenses; genitive singular in *\-oio*; lack of definite article7. | Paratactic structure; fluid word order governed by hexameter metrics6. | Homer (*Iliad*, *Odyssey*); Hesiod (*Works and Days*)5. |
| **Classical (Attic)** \[cite: 7, 8\] | Restored Classical/Attic; distinction of vowel length and pitch accent10. | Systematic augment; optative mood; dual number; contract verbs4. | Hypotactic structure; complex periodic sentences with extensive particle use9. | Plato, Xenophon, Thucydides, Attic Dramatists15. |
| **Hellenistic (Koine)** \[cite: 10, 18\] | Restored Koine or Historical Lucian; collapsed vowel lengths; soft fricative consonants10. | Declining use of optative; merge of *\-mi* and *\-o* conjugations4. | Simplified hypotaxis; prepositional expansion; Semitic-influenced structures10. | New Testament, Septuagint (LXX), Apostolic Fathers18. |

## **Comprehensive Registry of Open-Source Textual and Grammatical Resources**

A comprehensive educational application relies on structured primary source texts aligned with grammatical and lexical annotations. Several high-fidelity, open-source corpora are available for digital classical philology, offering machine-readable schemas optimized for integration into mobile databases.

| Resource Name | Linguistic Focus | Licensing & Reuse | Acquisition Source / Repository | Core Utility for Mobile Integration |
| :---- | :---- | :---- | :---- | :---- |
| **Classics Viewer Core** \[cite: 21\] | Greek, Latin, & aligned English translations21. | Open-source (source code & text processing scripts)21. | https://github.com/threedlite/classicsviewer \[cite: 22\] | Complete pre-built database engine containing 100+ authors; includes Python preparation scripts to compile Perseus texts into local SQLite databases21. |
| **Greek Reference App** \[cite: 23, 24\] | Classical Lexicography & Syntax Guide23. | Apache License, Version 2.023. | https://github.com/blinskey/greek-reference \[cite: 23\] | Provides a complete Intermediate Liddell-Scott-Jones dictionary and Jeffrey A. Rydberg-Cox's *Overview of Greek Syntax* packaged for SQLite23. |
| **Diorisis Ancient Greek Corpus** \[cite: 25, 26\] | Diachronic literary Greek (Homer to 5th c. AD)25. | CC BY 4.0 (Original XML); CC BY-NC-ND 4.0 (DuckDB version)26. | https://figshare.com/articles/dataset/Diorisis\_Corpus\_-\_Preprocessed\_files/7229162 \[cite: 26, 27\] | Features 820 parsed texts with complete morphological analysis and POS tagging, available in highly queryable DuckDB or converted SQLite formats25. |
| **Lyceum Digital Library & Orchestrator** \[cite: 28, 29\] | Interlinear translations & phonetic transliteration28. | CC-BY-SA-4.0 (Texts); Apache-2.0 (Orchestrator)30. | https://github.com/lyceum-quest/texts \[cite: 30\] | Contains a 10-stage automated LLM-assisted text processing pipeline designed to output aligned interlinear translations and pre-compiled SQLite databases (texts.db, morph.db, lsj.db)29. |
| **Rouse's A Greek Boy at Home** \[cite: 32, 33\] | Immersive, direct-method Attic Greek reading32. | Creative Commons Attribution-ShareAlike 4.032. | https://github.com/fhardison/rouse-a-greek-boy-at-home \[cite: 32\] | Offers a fully digitized, manually corrected edition of W.H.D. Rouse's 1909 reader; structured with inline page/line markers and footnotes matching the original scan32. |
| **Strong's / Dodson Greek Lexicons** \[cite: 34\] | Hellenistic & Biblical Koine Lexicography34. | Public Domain / CC0-1.0 (Dodson & Sandborg-Petersen XML versions)34. | https://github.com/biblenerd/awesome-bible-developer-resources \[cite: 34\] | Delivers focused, lightweight dictionaries targeting Koine vocabulary; integrates Strong's mapping coordinates, SBL-style transliterations, and parsed root lists34. |
| **Homeric Parallel Similarity Data** \[cite: 35\] | Epic/Homeric structural formulas35. | Open Access / GitHub release assets35. | https://github.com/perseus-publications \[cite: 35\] | Links every line of the *Iliad* and *Odyssey* to structural and lexical parallels across the epic cycle, providing a foundation for parsing formulas35. |
| **Hell-Char Paleography Dataset** \[cite: 1\] | Diachronic handwriting variations (3rd–1st c. BCE)1. | Open-source research data1. | https://arxiv.org/abs/2606.24984v1 \[cite: 1\] | Curated training set of character-level annotations from papyri; ideal for building supplementary handwriting analysis or optical character recognition features1. |
| **Ithaca Text Restoration Model** \[cite: 36\] | Digital epigraphy and contextual analysis36. | Free interactive access / Google Cloud36. | https://github.com/google-deepmind/ithaca \[cite: 36\] | Deep neural network designed for text restoration and dating of Greek inscriptions; useful for advanced modules centered on translation synthesis and textual completion36. |

## **Offline Lexical Databases and Morphological Analysis Pipelines**

To transform raw texts into an interactive educational experience, the application must run a localized dictionary lookup and morphological parsing engine21. Without these, users cannot resolve inflected forms back to their lexicographical lemmas when reading continuous text37.

### **Lexicographical Compilation**

The gold standard for classical lexicography is the Liddell-Scott-Jones (LSJ) Greek-English Lexicon28. For mobile distribution, several variants of this database can be parsed into SQLite:

1. **Intermediate LSJ (The Middle Liddell):** A condensed version of the lexicon containing definitions stripped of complex academic citations23. It is highly recommended for mobile screens and fast query speeds, as featured in the open-source *Greek Reference* database (lexicon.db)23.  
2. **Abbott-Smith and Dodson Lexicons:** Focused dictionaries specifically designed for Koine and Biblical Greek34. These provide high-quality public domain translations in TEI XML and CSV formats, which are significantly smaller in storage size than the full classical LSJ34.

### **Computational Morphological Engines**

Ancient Greek is a highly inflected language where a single verb can take thousands of morphological forms41. Programmatic parsing is required to map these forms to their dictionary entries:

* **Morpheus Morphological Parser:** Developed by the Perseids Project, this C-based engine analyzes paradigms and forms the basis of the morphological tracking within the Perseus Digital Library21.  
* **Lemming Parser:** A Ruby-based system designed to generate complete SQLite databases mapping inflected wordforms to lemmas and part-of-speech tags37. It operates by first running localized database lookups for known words, then falling back to programmatic decomposition rules for complex compound verbs37.  
* **BabyLemmatizer:** A neural-network-based tagger and lemmatizer optimized for ancient languages41. It is highly useful for managing spelling variations and diachronic orthographic shifts across different dialects41.

To organize these elements into a performance-driven relational database on Android, the tables must be structured to support efficient lookups.

SQL  
\-- Core document registry mapping works to their linguistic classifications.  
CREATE TABLE IF NOT EXISTS documents (  
    document\_id VARCHAR PRIMARY KEY,    \-- Unique TLG or canonical ID (e.g., "tlg0012.tlg001")  
    author VARCHAR NOT NULL,             \-- Author name (e.g., "Homer | Ὅμηρος")  
    title VARCHAR NOT NULL,              \-- Work title (e.g., "Iliad | Ἰλιάς")  
    dialect VARCHAR NOT NULL,            \-- Metadata: 'EPIC', 'ATTIC', or 'KOINE'  
    genre VARCHAR,                       \-- Genre classification: 'Epic Poetry', 'Philosophy'  
    sentence\_count INTEGER,              \-- Statistical count for text navigation  
    word\_count INTEGER NOT NULL          \-- Total word count for tracking student progress  
);

\-- Word-level token table mapping individual wordforms to their morphological profiles.  
CREATE TABLE IF NOT EXISTS words (  
    word\_key VARCHAR PRIMARY KEY,        \-- Unique token identifier (e.g., "doc\_001\_sent\_001\_w\_005")  
    document\_id VARCHAR,                 \-- Foreign key referencing documents  
    sentence\_id INTEGER NOT NULL,        \-- Sentence index for aligned display  
    sequence\_id INTEGER NOT NULL,        \-- Absolute position of the token in the sentence  
    raw\_form VARCHAR NOT NULL,           \-- Word form exactly as written in the manuscript  
    normalized\_form VARCHAR NOT NULL,    \-- Lowercase Unicode form with normalized diacritics  
    transliteration VARCHAR,             \-- Romanized transliteration for pronunciation support  
    lemma\_id VARCHAR,                    \-- Foreign key referencing the lexicon database  
    part\_of\_speech VARCHAR,              \-- Grammatical tag (Noun, Verb, Adjective, etc.)  
    morph\_code VARCHAR(10),              \-- Compressed morphological vector string  
    FOREIGN KEY(document\_id) REFERENCES documents(document\_id),  
    FOREIGN KEY(lemma\_id) REFERENCES lexicon(lemma\_id)  
);

\-- Lexicon database for root word translations and grammatical reference.  
CREATE TABLE IF NOT EXISTS lexicon (  
    lemma\_id VARCHAR PRIMARY KEY,        \-- Canonical dictionary headword (e.g., "ἀνήρ")  
    normalized\_lemma VARCHAR NOT NULL,   \-- Normalized lemma for instant search lookup  
    short\_definition VARCHAR NOT NULL,   \-- Concise, screen-friendly translation gloss  
    full\_definition\_lsj TEXT,            \-- Extensive classical LSJ dictionary definition  
    full\_definition\_koine TEXT,          \-- Focused Koine-specific dictionary definition  
    frequency\_rank INTEGER               \-- Frequency marker to prioritize vocabulary learning  
);

\-- Performance-critical indices to guarantee sub-millisecond query execution on Android.  
CREATE INDEX IF NOT EXISTS idx\_words\_normalized ON words(normalized\_form);  
CREATE INDEX IF NOT EXISTS idx\_words\_document ON words(document\_id, sentence\_id);  
CREATE INDEX IF NOT EXISTS idx\_lexicon\_normalized ON lexicon(normalized\_lemma);

## **Audio Synchronization and Pronunciation Frameworks**

Integrating speech capability requires a clear division of the four primary pronunciation systems of Ancient Greek. These systems represent different historical periods, and their implementation determines the auditory assets required by the application.

### **Phonological Reconstructions**

* **Reconstructed Classical / Attic Pronunciation:** This system attempts to reproduce the pitch accent and vowel-length distinctions of 5th-century BCE Athenian speech3. Consonants such as *beta*, *delta*, and *gamma* are pronounced as voiced stops (![][image1]), and *phi*, *theta*, and *chi* are executed as aspirated voiceless stops (![][image2])7. This system is phonologically accurate for reading Attic philosophy and drama, though it requires significant training to master the pitch accents3.  
* **The Erasmian System:** Formulated by Desiderius Erasmus in the 16th century, this artificial Western European reconstruction assigns a unique phoneme to every Greek letter3. It ignores historical pitch accentuation, replacing it with a stress accent10. While highly functional for distinguishing homophones in classroom settings, it represents a pronunciation that was never spoken by native speakers in antiquity3.  
* **Restored Koine Pronunciation:** Optimized for the Roman and Byzantine Hellenistic periods, this system reconstructs the phonology of the Mediterranean lingua franca4. It recognizes the merging of diphthongs (e.g., ![][image3], ![][image4], ![][image5]) and the transition of stops into fricatives, dropping pitch accents in favor of stress accents4. This provides an authentic audial representation of the early Christian era10.  
* **Modern Greek / Byzantine Pronunciation:** Representing the historical evolution of the language, this system applies modern phonology directly to ancient texts3. Vowels undergo extensive iotacism, reducing several historical vowels to the single phoneme ![][image6]4. While it introduces homophonic ambiguity, it aligns ancient texts with a living language tradition3.

### **Audio Resource Ingestion**

The application can incorporate several high-quality, open-license audio databases mapped to specific authors and texts:

1. **David Chamberlain's Homeric Recitations:** Under a Creative Commons Attribution 4.0 license, Chamberlain has recorded the entire *Iliad* and approximately one-third of the *Odyssey*6. This audio uses a stichic hexameter rhythm with reconstructed classical phonology and pitch-accent control6. The source files are organized line-by-line, allowing the application to align the audio tracks directly with individual text lines in the interface46.  
2. **SORGLL Recitations:** The Society for the Oral Reading of Greek and Latin Literature (SORGLL) provides open-access recordings of Classical Greek poetry and prose read by scholars using Restored Classical pronunciation47. The archive features readings from Aeschylus, Aristophanes, Demosthenes, and Sappho, complete with pitch accentuation and classical metrics47.  
3. **Bedwere's Erasmian Readings:** Available on Archive.org, Bedwere's recordings offer clear Erasmian recitations of standard pedagogical readers, including fables and illustrated comics9. These files are valuable for introductory modules and direct-method training9.  
4. **Koine and Biblical Greek Audio:** The *Found in Antiquity* project provides a Creative Commons audiobook of the Gospel of Matthew using the Lucian Koine pronunciation system, based on the public-domain Nestle-Aland 1904 text18. For larger scale Koine texts, the Theo Karvounakis recordings provide the entire New Testament in Byzantine pronunciation20. Additionally, the Bible Brain API from Faith Comes By Hearing provides free programmatic access to parsed Koine audiobooks20.

To supplement gaps in pre-recorded human recitations, developers can integrate modern neural Text-to-Speech (TTS) solutions52. Platforms such as SpeechGen.io provide neural voice engines (e.g., the *Nestoras* or *Athina* profiles) trained specifically on polytonic Greek orthography52. These models accurately parse monotonic and polytonic stress accents, vowel digraphs (such as ![][image7], ![][image8], and ![][image9]), and complex consonant clusters (such as ![][image10] and ![][image11]), providing a robust automated fallback for vocabulary drills and custom sentences52.

## **Pedagogical Execution: Graded Input and Adaptive Spaced Repetition**

Modern pedagogical research emphasizes that vocabulary acquisition and grammar comprehension are maximized when students are exposed to high volumes of comprehensible input rather than isolated paradigms9. The application can programmatically operationalize digitized graded readers and pair them with an adaptive review cycle14.

### **Integrating Graded Readers**

W.H.D. Rouse's *A Greek Boy at Home* represents the premier public-domain resource for immersive Attic Greek learning32. The digitized GitHub repository maintained by Fletcher Hardison contains manually verified raw markdown files mapped using a standard coordinate reference format32.  
In this repository, each line of the reader begins with an address mapped as Chapter-number.Line-number, followed by a single space and the markdown-styled Attic text32. Original pagination from the printed 1909 version is preserved using in-line markers like {page-number.line-number}32.  
Footnotes containing monolingual definitions and grammatical explanations are appended at the end of each chapter file32. These are mapped to the core text using explicit anchor links, as illustrated in the following sample:  
3.10 ... τὸ δ' αὐτὸ ποιοῦμεν καὶ ἡμᾶς \[fn1.add\]· οὐδὲ θαυμάζει .... 3.10.fn1.add \> ἡμεῖς  
By parsing these markdown files, the application's ingestion engine can import each line into the SQLite database, dynamically link the \[fn1.add\] marker to its target definition, and display context-aware annotations when a user taps a difficult word in the interface21.

### **Dynamic Flashcard Schedules**

To optimize vocabulary retention, the application should deploy a Spaced Repetition System (SRS) based on the SuperMemo SM-2 algorithm28. This model calculates the optimal interval ![][image12] in days for subsequent reviews based on the user's past performance and an active ease factor (![][image13])28.  
The initial intervals are hardcoded to establish a baseline:  
![][image14]  
Following each active recall review, the user assesses their recall performance using a qualitative score ![][image15], where 5 represents perfect, instant recall and 0 indicates a total lack of recognition28. The ease factor is dynamically updated after each trial:  
![][image16]  
By establishing this feedback loop, the application adjusts the study deck dynamically. High-frequency words that are easily recalled are pushed into the future, whereas difficult verbal forms are presented at shorter intervals to prevent them from dropping below the user's cognitive retention threshold28.

## **Technical Android Implementation and Core Source Code**

To build a high-performance Android application, developers must address offline database initialization, polytonic font configuration, and synchronized audio playback.

### **Caching and Initializing Databases**

To deploy the database, developers can choose between two main initialization architectures:

* **Option A (Pre-packaged SQLite Asset Asset):** The database files (e.g., lexicon.db and syntax.db) are compiled during build-time, compressed into .zip files, and saved inside the Android assets/ directory23. On first launch, the database is unpacked into the device's local system directory using SQLiteAssetHelper or Room23.  
* **Option B (Dynamic JSON Hydration):** The application downloads lightweight JSON resource files from a remote web server or storage account on first boot, then programmatically parses and writes the objects to a fresh local SQLite database53.

While Option B keeps the initial application package small, Option A is highly recommended for educational tools as it guarantees immediate, error-free offline access and eliminates dependencies on network conditions during initial startup21.  
The following code illustrates an asset-caching bootstrapper designed to extract a zipped SQLite file upon first execution:

Java  
package com.example.greekapp.data;

import android.content.Context;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.io.File;  
import java.util.zip.ZipInputStream;  
import java.util.zip.ZipEntry;

public class DatabaseBootstrapper extends SQLiteOpenHelper {  
    private static final String DATABASE\_NAME \= "ancient\_greek.db";  
    private static final int DATABASE\_VERSION \= 1;  
    private final Context context;  
    private final String databasePath;

    public DatabaseBootstrapper(Context context) {  
        super(context, DATABASE\_NAME, null, DATABASE\_VERSION);  
        this.context \= context;  
        this.databasePath \= context.getDatabasePath(DATABASE\_NAME).getPath();  
    }

    public void initializeDatabase() throws IOException {  
        if (\!checkDatabaseExists()) {  
            // Force creation of empty database system directory  
            this.getReadableDatabase();  
            this.close();  
            try {  
                extractZippedDatabase();  
            } catch (IOException e) {  
                throw new IOException("Critical Error: Failed to extract assets database.", e);  
            }  
        }  
    }

    private boolean checkDatabaseExists() {  
        File dbFile \= new File(databasePath);  
        return dbFile.exists() && dbFile.length() \> 0;  
    }

    private void extractZippedDatabase() throws IOException {  
        InputStream rawInputStream \= context.getAssets().open("prepopulated\_assets/ancient\_greek.zip");  
        ZipInputStream zipInputStream \= new ZipInputStream(rawInputStream);  
        ZipEntry entry \= zipInputStream.getNextEntry();  
          
        if (entry \!= null) {  
            OutputStream outputStream \= new FileOutputStream(databasePath);  
            byte\[\] buffer \= new byte\[4096\];  
            int length;  
            while ((length \= zipInputStream.read(buffer)) \> 0) {  
                outputStream.write(buffer, 0, length);  
            }  
            outputStream.flush();  
            outputStream.close();  
        }  
        zipInputStream.closeEntry();  
        zipInputStream.close();  
        rawInputStream.close();  
    }

    @Override  
    public void onCreate(SQLiteDatabase db) {  
        // Database tables are pre-populated within the asset file.  
    }

    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        // Upgrade pathways managed via migrations.  
    }  
}

### **Font Asset Integration**

To render polytonic accents and breathing marks correctly on Android devices, developers must bundle a custom classicist typeface. Failing to specify a custom font family often results in broken glyphtext blocks and mismatched accent placements in default Android text renderers.  
To solve this, developers should download NewAthenaUnicode.ttf or GentiumPlus.ttf and save them under the res/font/ resource directory8. The font can then be programmatically registered within Jetpack Compose8.

Kotlin  
package com.example.greekapp.ui.theme

import androidx.compose.ui.text.font.Font  
import androidx.compose.ui.text.font.FontFamily  
import androidx.compose.ui.text.font.FontWeight  
import com.example.greekapp.R

val PolytonicGreekFontFamily \= FontFamily(  
    Font(R.font.new\_athena\_unicode\_regular, FontWeight.Normal),  
    Font(R.font.new\_athena\_unicode\_bold, FontWeight.Bold)  
)

Kotlin  
package com.example.greekapp.ui.components

import androidx.compose.material3.Text  
import androidx.compose.runtime.Composable  
import androidx.compose.ui.Modifier  
import androidx.compose.ui.text.TextStyle  
import androidx.compose.ui.unit.sp  
import com.example.greekapp.ui.theme.PolytonicGreekFontFamily

@Composable  
fun PolytonicTextView(text: String, modifier: Modifier \= Modifier) {  
    Text(  
        text \= text,  
        modifier \= modifier,  
        style \= TextStyle(  
            fontFamily \= PolytonicGreekFontFamily,  
            fontSize \= 18\.sp,  
            lineHeight \= 26\.sp  
        )  
    )  
}

### **Audio Synchronization Engine**

To enable synchronized "read along" text tracking, the application needs an audio player that matches playback timestamps with corresponding sentence or line IDs. Below is a Jetpack-compatible audio synchronization manager that tracks playback progress and updates the active sentence UI state in real-time.

Kotlin  
package com.example.greekapp.audio

import android.content.Context  
import android.media.MediaPlayer  
import android.os.Handler  
import android.os.Looper

class AudioSyncEngine(private val context: Context) {  
    private var mediaPlayer: MediaPlayer? \= null  
    private val handler \= Handler(Looper.getMainLooper())  
    private var sentenceDurationMap \= ArrayList\<SentenceTimeMarker\>()  
    private var onSentenceActiveListener: ((Int) \-\> Unit)? \= null

    data class SentenceTimeMarker(val sentenceId: Int, val startMs: Int)

    fun initializeAudio(audioAssetPath: String, markers: ArrayList\<SentenceTimeMarker\>) {  
        sentenceDurationMap \= markers  
        mediaPlayer \= MediaPlayer().apply {  
            val descriptor \= context.assets.openFd(audioAssetPath)  
            setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)  
            descriptor.close()  
            prepare()  
        }  
    }

    fun startPlayback(onSentenceActive: (Int) \-\> Unit) {  
        this.onSentenceActiveListener \= onSentenceActive  
        mediaPlayer?.start()  
        runProgressTracker()  
    }

    fun stopPlayback() {  
        mediaPlayer?.apply {  
            if (isPlaying) {  
                stop()  
            }  
            release()  
        }  
        mediaPlayer \= null  
        handler.removeCallbacksAndMessages(null)  
    }

    private fun runProgressTracker() {  
        handler.postDelayed(object : Runnable {  
            override fun run() {  
                mediaPlayer?.let { player \-\>  
                    if (player.isPlaying) {  
                        val currentPos \= player.currentPosition  
                        val activeSentence \= determineActiveSentence(currentPos)  
                        onSentenceActiveListener?.invoke(activeSentence)  
                        handler.postDelayed(this, 100) // Poll every 100ms  
                    }  
                }  
            }  
        }, 100)  
    }

    private fun determineActiveSentence(currentPositionMs: Int): Int {  
        var activeId \= sentenceDurationMap.firstOrNull()?.sentenceId ?: 0  
        for (marker in sentenceDurationMap) {  
            if (currentPositionMs \>= marker.startMs) {  
                activeId \= marker.sentenceId  
            } else {  
                break  
            }  
        }  
        return activeId  
    }  
}

#### **Works cited**

1. Learning Diachronic Representations of Ancient Greek Letterforms \- arXiv, [https://arxiv.org/html/2606.24984v1](https://arxiv.org/html/2606.24984v1)  
2. Representation Learning of Ancient Greek Letterforms across Time \- OpenReview, [https://openreview.net/forum?id=vXt4OFsw6j](https://openreview.net/forum?id=vXt4OFsw6j)  
3. What is the current scholarly status on the ancient pronunciation of Greek? \- Reddit, [https://www.reddit.com/r/AncientGreek/comments/1selks1/what\_is\_the\_current\_scholarly\_status\_on\_the/](https://www.reddit.com/r/AncientGreek/comments/1selks1/what_is_the_current_scholarly_status_on_the/)  
4. Learning Ancient Greek Vs Koine Greek ? \- Logos and Verbum Forum \- Logos Community, [https://community.logos.com/forums/topic/77057-learning-ancient-greek-vs-koine-greek/](https://community.logos.com/forums/topic/77057-learning-ancient-greek-vs-koine-greek/)  
5. The Iliad : Homer : Free Download, Borrow, and Streaming \- Internet Archive, [https://archive.org/details/illiad\_0801\_librivox3](https://archive.org/details/illiad_0801_librivox3)  
6. A Reading of Homer (work in progress) \- Greek and Roman Verse, [https://hypotactic.com/my-reading-of-homer-work-in-progress/](https://hypotactic.com/my-reading-of-homer-work-in-progress/)  
7. Ancient Greek phonology \- Wikipedia, [https://en.wikipedia.org/wiki/Ancient\_Greek\_phonology](https://en.wikipedia.org/wiki/Ancient_Greek_phonology)  
8. Ancient Greek Tutorials @ AtticGreek.org, [http://atticgreek.org/](http://atticgreek.org/)  
9. What book should a beginner have ? : r/AncientGreek \- Reddit, [https://www.reddit.com/r/AncientGreek/comments/3jpftr/what\_book\_should\_a\_beginner\_have/](https://www.reddit.com/r/AncientGreek/comments/3jpftr/what_book_should_a_beginner_have/)  
10. Koine Greek Pronunciation \- Biblical Language Center, [https://www.biblicallanguagecenter.com/koine-greek-pronunciation/](https://www.biblicallanguagecenter.com/koine-greek-pronunciation/)  
11. Greek and Latin Meter, [https://hypotactic.com/latin/](https://hypotactic.com/latin/)  
12. Ἔργα καὶ Ἡμέραι (Works and Days) : Hesiod : Free Download, Borrow, and Streaming \- Internet Archive, [https://archive.org/details/ergakaihemerai\_2512\_librivox](https://archive.org/details/ergakaihemerai_2512_librivox)  
13. public domain audio recording of Matthew in ancient Greek (the non-modern pronunciation) \- Page 2 \- B-Greek: The Biblical Greek Forum \- Ibiblio, [https://www.ibiblio.org/bgreek/forum/viewtopic.php?t=4473\&start=10](https://www.ibiblio.org/bgreek/forum/viewtopic.php?t=4473&start=10)  
14. Greek \- OER Commons, [https://oercommons.org/browse?f.keyword=greek](https://oercommons.org/browse?f.keyword=greek)  
15. Plato's Republic : Plato : Free Download, Borrow, and Streaming \- Internet Archive, [https://archive.org/details/platos\_republic\_0902\_librivox1](https://archive.org/details/platos_republic_0902_librivox1)  
16. Ιστορίαι (Histories) Βιβλίοv 6 (Book 6\) : Thucydides (Θουκυδίδης) \- Internet Archive, [https://archive.org/details/histories6\_thucydides\_1506\_librivox](https://archive.org/details/histories6_thucydides_1506_librivox)  
17. Traditional Second Year Greek Text, [https://www.textkit.com/t/traditional-second-year-greek-text/12106](https://www.textkit.com/t/traditional-second-year-greek-text/12106)  
18. The Gospel of Matthew, Greek audiobook in Lucian \- Found in Antiquity, [https://foundinantiquity.com/2022/04/23/the-gospel-of-matthew-greek-audiobook-in-lucian/](https://foundinantiquity.com/2022/04/23/the-gospel-of-matthew-greek-audiobook-in-lucian/)  
19. biblical-studies · GitHub Topics, [https://github.com/topics/biblical-studies?l=python\&o=asc\&s=updated](https://github.com/topics/biblical-studies?l=python&o=asc&s=updated)  
20. GREEK Audio Recordings of the Old & New Testaments, [https://www.mrgreekgeek.com/2020/09/02/greek-audio-recordings-of-the-old-new-testaments/](https://www.mrgreekgeek.com/2020/09/02/greek-audio-recordings-of-the-old-new-testaments/)  
21. threedlite/classicsviewer: Ancient Greek classic texts with translations and dictionary \- GitHub, [https://github.com/threedlite/classicsviewer](https://github.com/threedlite/classicsviewer)  
22. Ancient Greek lit mobile app : r/AncientGreek \- Reddit, [https://www.reddit.com/r/AncientGreek/comments/1o44jva/ancient\_greek\_lit\_mobile\_app/](https://www.reddit.com/r/AncientGreek/comments/1o44jva/ancient_greek_lit_mobile_app/)  
23. blinskey/greek-reference: \[Mirror\] An ancient Greek lexicon and grammar for Android. \- GitHub, [https://github.com/blinskey/greek-reference](https://github.com/blinskey/greek-reference)  
24. Greek Reference: Ancient Greek \- Apps on Google Play, [https://play.google.com/store/apps/details?id=com.benlinskey.greekreference](https://play.google.com/store/apps/details?id=com.benlinskey.greekreference)  
25. (PDF) The Diorisis Ancient Greek Corpus \- ResearchGate, [https://www.researchgate.net/publication/328791830\_The\_Diorisis\_Ancient\_Greek\_Corpus](https://www.researchgate.net/publication/328791830_The_Diorisis_Ancient_Greek_Corpus)  
26. Diorisis.duckdb \- Zenodo, [https://zenodo.org/records/11261146](https://zenodo.org/records/11261146)  
27. Diorisis Corpus \- Preprocessed files \- figshare, [https://figshare.com/articles/dataset/Diorisis\_Corpus\_-\_Preprocessed\_files/7229162](https://figshare.com/articles/dataset/Diorisis_Corpus_-_Preprocessed_files/7229162)  
28. Lyceum \- A New Digital Library for Ancient Greek, [https://lyceum.quest/](https://lyceum.quest/)  
29. Lyceum: A New Ancient Greek Digital Library For A Post-LLM World | Brandon Lucas, [https://blu.cx/posts/articles/2026-03-31-lyceum-pipeline/](https://blu.cx/posts/articles/2026-03-31-lyceum-pipeline/)  
30. GitHub \- lyceum-quest/texts: Lyceum Ancient Greek Digital Library data produced by the orchestrator agent pipeline, [https://github.com/lyceum-quest/texts](https://github.com/lyceum-quest/texts)  
31. Lyceum \- GitHub, [https://github.com/lyceum-quest](https://github.com/lyceum-quest)  
32. A repo of Rouse's A Greek Boy at Home from Archive.org's copy and OCR with manual correction \- GitHub, [https://github.com/fhardison/rouse-a-greek-boy-at-home](https://github.com/fhardison/rouse-a-greek-boy-at-home)  
33. Rouse, A Greek Boy At Home \- LatinPerDiem, [https://latinperdiem.com/rouse-a-greek-boy-at-home/](https://latinperdiem.com/rouse-a-greek-boy-at-home/)  
34. biblenerd/awesome-bible-developer-resources \- GitHub, [https://github.com/biblenerd/awesome-bible-developer-resources](https://github.com/biblenerd/awesome-bible-developer-resources)  
35. Perseus Digital Library Updates » News, Essays & Information » Page 3 \- Tufts University, [https://sites.tufts.edu/perseusupdates/page/3/](https://sites.tufts.edu/perseusupdates/page/3/)  
36. Historians apply state-of-the-art AI to transform the study of ancient texts | Faculty of Classics, [https://www.classics.ox.ac.uk/article/historians-apply-state-of-the-art-ai-to-transform-the-study-of-ancient-texts](https://www.classics.ox.ac.uk/article/historians-apply-state-of-the-art-ai-to-transform-the-study-of-ancient-texts)  
37. machine-readable lexicographical info for ancient Greek, a case study on part-of-speech tagging : r/AncientGreek \- Reddit, [https://www.reddit.com/r/AncientGreek/comments/1ezey06/machinereadable\_lexicographical\_info\_for\_ancient/](https://www.reddit.com/r/AncientGreek/comments/1ezey06/machinereadable_lexicographical_info_for_ancient/)  
38. fpsvogel/learn-latin-and-greek: Resources for learning Latin and ancient Greek. \- GitHub, [https://github.com/fpsvogel/learn-latin-and-greek](https://github.com/fpsvogel/learn-latin-and-greek)  
39. pyaegean/README.md at main · ryanpavlicek/pyaegean · GitHub, [https://github.com/ryanpavlicek/pyaegean/blob/main/README.md](https://github.com/ryanpavlicek/pyaegean/blob/main/README.md)  
40. Greek Study App \- Credits, [https://sites.google.com/claypotfrog.com/greekstudyapp/credits](https://sites.google.com/claypotfrog.com/greekstudyapp/credits)  
41. ancient-greek · GitHub Topics, [https://github.com/topics/ancient-greek?o=asc](https://github.com/topics/ancient-greek?o=asc)  
42. ancient-greek · GitHub Topics, [https://github.com/topics/ancient-greek](https://github.com/topics/ancient-greek)  
43. Proceedings of the Fourth Workshop on Language Technologies for Historical and Ancient Languages (LT4HALA 2026\) @ LREC 2026, [http://lrec-conf.org/proceedings/lrec2026/workshops/lt4hala/2026.lt4hala-1.0.pdf](http://lrec-conf.org/proceedings/lrec2026/workshops/lt4hala/2026.lt4hala-1.0.pdf)  
44. Learning ancient-greek with modern-greek pronunciation?, [https://www.textkit.com/t/learning-ancient-greek-with-modern-greek-pronunciation/14110](https://www.textkit.com/t/learning-ancient-greek-with-modern-greek-pronunciation/14110)  
45. An entire audio of Homer in AG : r/AncientGreek \- Reddit, [https://www.reddit.com/r/AncientGreek/comments/hm7bv2/an\_entire\_audio\_of\_homer\_in\_ag/](https://www.reddit.com/r/AncientGreek/comments/hm7bv2/an_entire_audio_of_homer_in_ag/)  
46. Ancient Greek Classics phone app \- Reddit, [https://www.reddit.com/r/classics/comments/1mt60ov/ancient\_greek\_classics\_phone\_app/](https://www.reddit.com/r/classics/comments/1mt60ov/ancient_greek_classics_phone_app/)  
47. AWOL Index: Society for the Oral Reading of Greek and Latin Literature (SORGLL, [https://isaw.nyu.edu/publications/awol-index/html/www.rhapsodes.fll.vt.edu/index-php.html](https://isaw.nyu.edu/publications/awol-index/html/www.rhapsodes.fll.vt.edu/index-php.html)  
48. About The Society | Rhapsodoi, [https://rhapsodoi.org/biography/](https://rhapsodoi.org/biography/)  
49. Stephen G. Daitz \- CAMWS, [https://camws.org/node/271](https://camws.org/node/271)  
50. Rouse's 'Greek Boy at Home' in a modern format, [https://www.textkit.com/t/rouses-greek-boy-at-home-in-a-modern-format/19518](https://www.textkit.com/t/rouses-greek-boy-at-home-in-a-modern-format/19518)  
51. Books : r/AncientGreek \- Reddit, [https://www.reddit.com/r/AncientGreek/comments/1kqxo0r/books/](https://www.reddit.com/r/AncientGreek/comments/1kqxo0r/books/)  
52. Greek Text to Speech — Ancient & Modern Greek Voice Online \- SpeechGen.io, [https://speechgen.io/en/tts-greek/](https://speechgen.io/en/tts-greek/)  
53. android dictionary application with offline database \- Stack Overflow, [https://stackoverflow.com/questions/12369879/android-dictionary-application-with-offline-database](https://stackoverflow.com/questions/12369879/android-dictionary-application-with-offline-database)

[image1]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHQAAAAaCAYAAABmZHgNAAABv0lEQVR4Xu2RW2rEQAwE9/6XTpiAjFBarceI4IAK9mO65bLW8/ksy7IsL+HLBk3e5hGmfFMeYdr3YMXnrH9ZKrOMrCe7Y9RnmfII074HT+zlHtV5j4pnL9TgSTMfSlOZZVQ90XzUZ5nyCNO+B0/8Hy40s2PUZ5nyCNO+B0+sP5Y3o8nMZMh49F7RfNRnmfII074fmFQ+VuZSWVch8th9ogtlXYWMR+/zV3v9whOjhVAmeHkV5kHvt2dL1GeJPLZHu2pYd4UnRguhTPDyKp7HezfKNFGfhXnQbijTsK4Nk6KFUHZAWQfmQe9GmYZ1FZjH2wFlAuuuYGLUdZavwDyo8/YRWFeBebwdUCawrk0kRX026xB5UK8z29tzl8iDLhRlgpdfE4lt7y2JMo08l5lj2F47bXdAmWZqr4OdYV4vvyYjzvxp1gmR4xD1B7uPPWtQZvGe1UT9Qe8ROVnXZkpa8bBZ1nWo+Ngs6xjec15+zZS44mGzrOtQ8bFZ1gl25pxtJnj5NVPirCeai/oqWV80l+ntjD1rWNdmSlrxsFnWdaj42CzrNHKp6HI1rLtiSvw2jzDlm/II075lWZZlWZp8A/wm/QP64hgGAAAAAElFTkSuQmCC>

[image2]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAI8AAAAaCAYAAACOyA9jAAACAElEQVR4Xu2UgWrEMAxD7/9/eqOwQKo5lmwnvQbyoNBElqJ0xz6fw+GF/Pw9u7BbX2T3/jd2u8hufZHX9a8UynqzPiSaE51vZH1INSfrz/ooleD2b7Q9KpFZj2jOG/tGOkVmeyKzISrBvTeSE5n18HIsbbe+yLf736iEohfXI9Q5hpdzaaiz9Qh1juHlWH0R1HE9Qp0LUwlGL65HqHMML8f6Y7D1CHWO4eVYfRHUcT1CnQtTCe697V3JU2YUvBxL260v8u3+N0ah1357cG1doL2jbsF0FSsHe2IffEfdgukqLGfUDff7d9QtmJ5mFNz2rXK4jlL1N7wcq3eWJ3L6vvjtPZ9C1T/ECu73mJ6h6m94OTM+euOJnNEPBdcZZmT8g4Val7mw9lQq3h6Ww3SVp3L6Hw+bjTAz6wYLti5i7UWoeHu8nGrHnqdyev2N/W8oodZM5WJZH8JysCObH5H1ISzH6ovrDFkfRQm2LqH4RjBvy1fmPDCDzY9gvpV925p5PSpeFxbcfxj1IzEUv3KOos/orHiVMzK6ksuo+k2U0BnleyJZ3qynzSRyjjfraStZdq4SfH48Ot6sp61k2blKsDITQc1jc0yfhXoOm2P6Kpacy0IvHZ8qkQxv1tNmEjnHm/W0lSw7d1mww6wzZ+UwZp0zKyfKt849HA6Hw+Fw2JpfUk031yagE/IAAAAASUVORK5CYII=>

[image3]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAaCAYAAAD1wA/qAAAAc0lEQVR4Xu3QQQrAIAxE0dz/0u0qRQbDpKWKi/8gC3UIgxEAAJzn+jC/cstnd7vZDlX55N53sB1sIHqZ1WyHDIyjZncV3dWZDpuzgehlVrMdXEB/zeVXaHXQkNI3Pavc92Yczej5oYvHYHW/0wkdAAA42w2EdlykrT4k9QAAAABJRU5ErkJggg==>

[image4]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADgAAAAaCAYAAADi4p8jAAAAgklEQVR4Xu3Pyw6AIAxEUf//p3VVYyZ9UCPg4p6EBe1A4TgAAMBs54s1hQ6pBlX9FYbf6wWetay3y/AbsuBfP9ian4WjnleL2B2dVdF8eiZrRoe92kqt+VnY6+mnvcxsrZlRWD9itKZ7Zfd0VmU0d9MBdlj3WmsN+Zi+Y+dbAADAahf5dW6Sn9HypwAAAABJRU5ErkJggg==>

[image5]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADgAAAAaCAYAAADi4p8jAAAAhklEQVR4Xu3PSQ7EMAhE0dz/0skKS/kqPEQyzqKe5AUFHejrMjMzsx3uD287LlRLVVald5vKXtQAM9bVYre6Q2VN1mTOuhLvULI8PZy5msnEb1fejN5slqcNfiybq8SbgsqarMk/x/oE3hFU1qgms1FNccjKm6FmWUujZaN+pT/dYmZmZqc8mE9wkGCzKe4AAAAASUVORK5CYII=>

[image6]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB0AAAAaCAYAAABLlle3AAAAjklEQVR4Xu2RQQqAMAwE/f+nFQ8poWabjJSK0AEv29kt6nFsPuTsgxfgjVFhdOapeg1VuHN7MipOA8kCvIELAXgDFwLQhpL9v1SOkZ0/UAXLl13qs+i8p+I0MnnZW3qmX1qRs88cZUOygn9L5apcUinYxcpVeQiSBXgDFwLwBi4EoA0kC/AGLgTM2Nj8lAs7jD7CkL1DBwAAAABJRU5ErkJggg==>

[image7]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAE4AAAAaCAYAAAAZtWr8AAAA7ElEQVR4Xu2Q4QrDQAiD+/4vvbGBo4Sod9esh8UP7kejRtPjaJqm2cILhUVUPmVQBVb5lEEVWOVTAlVYlU8ZVIFVPpf5HDL7Vsjmsrox2vcDj88MsvqdRLecs2S5ohqFGeLCM6x/J94t7E6mGZ5OGTHCejSzA+8Wpke3ezolM2I1pnmYx8ybIepH32gH00KiAdmSPxLdEtWQmd4v0QCr4c9kPXeR7cZbDdTwewhvSLXUfGbeKFmv54cafg+Dh5sRfqO2vFDEyH68l80w7bGowqp8yqAKrPIpgyqwyqcEqrAqnzKoAqt8mqZpHsMbdBCdYzYGPXgAAAAASUVORK5CYII=>

[image8]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFMAAAAaCAYAAADL5WCkAAAA+ElEQVR4Xu2P4QrEMAiD9/4vfcd+CCKJtbulWw8/GKNRozmOpmma7fhE4QYUnlugCK7w3AJFcIXn61GEVnhugSK4wvNnzqNmv1muzIy47BnDeCOm+9qToP3sZqQhRnUKMvea/2d9T5HtR/chLTKqQ5ix6b6G+k6Yvopsf8zAtMioDmHGUY9vD9MNm535qox6kR/SPFkthQ3GhfFtIG0lo/3obqR5sloKG4w6OwBpq6jsRncjzWB6CTTMtKjHN8NmZ74KlT7khzSD6WWqQap9q6jeEO/OMiDt71GEVnhugSK4wnMLFMEVnq9HEVrhuQWK4ArPpmmaBvAFo0isVMnNbOAAAAAASUVORK5CYII=>

[image9]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAAAaCAYAAAA38EtuAAABA0lEQVR4Xu2Q4QrDQAiD+/4vvdEfggSjHmOpbH5w0IunMb2uZVmWv+eFwpdR+41BHVztNwZ1cLXfCNSh1X5jUAdX+7W4lzo9p1Q9Vf2Uj+axsEz3tSfJ/P1+0TfmijSE6S06g6M3kaaG+Ue7oYZ30zKqOiUyM3CpCKarYP6RjlnxblpGVaeYmT8I02+YbuDszumSvcWZbL6/Yw2p6imd5mjBm0hTkvlnNY/PVvVU9ZRO88QfXXmf7Nz50VmtBVvIE73BO8N6T06H6h2bxbRI91T1Fhg0GlrV1XR2wJ1ZT1YzqvpPog6t9huDOrjabwzq4Gq/EahDq/3GoA6u9luWZVmEvAGW2LlHt0e01AAAAABJRU5ErkJggg==>

[image10]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA8AAAAaCAYAAABozQZiAAAAUklEQVR4Xu2MAQoAIAgD/f+niwJjLckMIoIOBB07RT4WiYMID8pLYinhYI67+UxDLvA+yFxAvLvBn62ilVU8me+OY/JUVPQBT4iwgNyRt8WPQwbynS/RQaDMJgAAAABJRU5ErkJggg==>

[image11]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAaCAYAAACO5M0mAAAARElEQVR4XmNgGLrgP7oALgBSCMNYAbIkToU4JdABbRXi1QSTRMYYAKcEOiBaEXUVggBeD6ADdB+TpIkogFchSdYOWQAADVgr1eLIQ/0AAAAASUVORK5CYII=>

[image12]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAZCAYAAADnstS2AAAALElEQVR4XmNgGB7gPw6MExBUgAxoq5goQLKptFNMFCDZVLyKYQqw4VEw2AEArCEe4iFD4cMAAAAASUVORK5CYII=>

[image13]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACEAAAAaCAYAAAA5WTUBAAAAe0lEQVR4Xu3PQQrAMAhE0dz/0i1ZBGTyxanL4gMXUUfIWmPUng8V6SwrWxXIZt0c6h7r5lD3WDeHaDn2aL5R38ld6DfUU7Sjb9s5plXRfTeHKKxv0s0hCmpP35vbK9FvFM2dnM05RnMnZ6uOZbMqZzlHnIp0RjtjjPEPL6ZVapZfbPYKAAAAAElFTkSuQmCC>

[image14]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAmwAAAAxCAYAAABnGvUlAAACFklEQVR4Xu3dQY7CMAwFUO5/6Rl1USmy7JBQSht4T6rUOK4bZjZfbHg8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4KP+YgEAgGtsway9Zsz2n+EOZwAAOF0MPXG9yWqbqn7EzMyZXgCAr5GFoKy2qepHjMzce7LerAYAsKzRcFP1VfUjns1sw1rb266zevUcAMCtjQaXqu+M8NObF99X9fb6qmcAAG5pNLxUfTEYvUNvXtyL681+pmwPAGA5o6Gm6qvqR/RmxiDWu49zshoAwG2130LFEJOtR3uPqt7TiufZe7N169lcAIBlzISamd6rrXRWAICfIqgBAD9rpSC00lkBAAAAAAAAAAAAAAAAAAAAAACAc2U/8ZTJ9rLau4zOHu0DAFhaDD3tOt73et9hnzc6d7QPAGBpMfRUIe3swPbKrFeeAQBYykzg+URga69KFSDbdVavnttrAAC3NBNUqqATa5s2JGVXpq33eqq+du9ZX5TVAABuYTSoVH0xGB0R58T1JtbierOfKduLZnoBAC4xElTantgf10eMzIrhqncf52W1Vm8PAOAyIyFlDzpZ4Inrd6nmxjP07uOMrNbq7QEA3MpMcJnpvdpKZwUA6PrWYPOtnwsAoLRSAFrprAAAAAAAAAAAAAAAAAAAAAAAAPCK/SeIsuuurjpf9rcZPcdo36yr/2efeGf8jNnnjfW4D5zkHzpO/ASn1gSRAAAAAElFTkSuQmCC>

[image15]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAE8AAAAaCAYAAAD2dwHCAAABQElEQVR4Xu2TwQ7DIAxD9/8/vYkDEvLsYAgbPeRJPdS1ndB1r1dRFMU27+G6we35KaKlTxyK5V3t8bCl8aUxj8v4ZWHviNIfDS6tDsg0B+fFNaJnPwOXcxYdQZ/KMs3Bzbm+Y5wYiB3Ry2P6DDfj+r7oi+EVMXvugj1qttJn4JlUh9JDWCHeMxyPA/awfRpKn4EZ1cO0kEyR43HAnmgnpq+iepgWwoqYxui+6HJAn8oqfQfWw7QQFnCXdDwO2KPmKz1CZZiO91NYgGkM1zcDe9jBGkybwbqY1mBaCAZUsWLFq2AdqLG9uob6CHumMkybMi6hiiNW/QjL4z7M04iedbBH+ZVuE5VH4HIrPa5Pkc130j3pgg2yM7P5Tqpn5Ws5SWZmJotsd+Hfbbtog8ysTBY52fU3bvxgI7fnF0VRPJoPX4jRL1sSDgkAAAAASUVORK5CYII=>

[image16]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAmwAAAAxCAYAAABnGvUlAAADb0lEQVR4Xu3V22rsOBAF0PP/Pz2DHwRiU7LKHTudTtaCBldp62IHlH//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADgc/yXjV/qr7xn5Yl3f2JN3sPfEvgo3UtrlVv1f7I881FnL83ju+xQ5bKX9Z1y7VFnfza+xVnmqlzviT1W7twj17ryPc/sxq/I/T79W5+tucqs+ofsZQ3wY3UvrCpX9T5Bde6qN+Q/vLPsrMplL+uO7pxXznzI9/2qXCvrp921X37PrCurzCq/0s1nLutP0Dnz2fdfPa9kJmuAH6d7UXVzT3vlHHnRD1VvpZut/nHk3Cqz08nle+acrGc596tyrayfttqv6le9Ib9n1pXMjDrn73Szmcv6E+SZsz7k9zt7rubPcjxrgLcbl9nuUuvmUs7LX2Xunz2POtdarTusxlf9We6103nXXabSyWZmV8+unueqee279jlbM+uVbu6Q32g1NzNVvZo762Qqud8dztZ5dWyWuawP+d2qzKHqZ6+qswfwVp0L79DN3WG112rf6nKtesPVfqWb3eXmc+6ys042M7t6dvb97nbXPvM6uWbW6ZX3zTmr+ZnJXNWrdDI737HGbvywy+R41of8bqtMJftVnT2At6oupW6vY1x8q19l7p89j7paq+oNV/uHXO8sO9yVqXTmZWZXz/J975RrP7FPrpl1yjN15JzV/MyMOvNZp914pXvGK3brVb20y+R41ofdu3XHVnX2AN6qeyl1c3dYXbRnz1lXz0PVO2R/Xne3R849dHpZd3XmnWV25696d8l1s/6qar2qN5yN7Yy5+b2uPndczR9yTtavePUdMpv1Su6X9ao//3YykzXA2+SFtrrczsaekPvN+2Yvx+bMkPWuV/Xn51Vm1at+1fhVnTnV2qs95/qrZ6vkOnevfzhbs+odqn7VG3JstedcrzKHs7HUyRwyd2WPrifWTKs95vossxqrZCZrAN7k7gv57vV2uvt1c0/7znPkXll/xZ1rXdXdu5t7wjv37sozZg3AL+XC7/mO73Tskftk/Rd8xztXe1S9nybPmDUAP8xfuaj/yntWnnj3J9bkPfwtAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA+B3+B78fAB2hPfYWAAAAAElFTkSuQmCC>