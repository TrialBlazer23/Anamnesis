from pathlib import Path

from anamnesis_pipeline.rouse import load_rouse, parse_rouse_text, parse_rouse_vocab

DATA = Path(__file__).parent.parent / "data" / "rouse"

SAMPLE = """\
2.1.1.text ἐγὼ μέν εἰμι παιδίον Ἑλληνικόν, οἰκῶ δ' ἐν ἀγροῖς.
2.1.2.text ἐνταῦθα γὰρ **γεωργός** τις[^3] Θράσυλλός ἐστιν.

3.2.1.text ἄλλο τι.
"""


def test_parses_addressed_sentences_and_strips_markdown():
    sentences = parse_rouse_text(SAMPLE)
    assert [s.ref for s in sentences] == ["2.1.1", "2.1.2", "3.2.1"]
    assert sentences[0].greek.startswith("ἐγὼ μέν εἰμι")
    assert "**" not in sentences[1].greek and "[^3]" not in sentences[1].greek
    assert sentences[1].greek == "ἐνταῦθα γὰρ γεωργός τις Θράσυλλός ἐστιν."


def test_sentences_have_search_keys():
    sentences = parse_rouse_text(SAMPLE)
    assert "παιδιον" in sentences[0].search_key  # diacritics stripped


def test_vocab_parser_reads_js_array():
    js = 'const vocab = [{"head": "ἄγκῡρα", "deff": "anchor"}, {"head": "", "deff": "x"}];'
    vocab = parse_rouse_vocab(js)
    assert vocab == [{"head": "ἄγκῡρα", "deff": "anchor"}]


def test_real_rouse_file_loads_completely():
    sentences = load_rouse(DATA / "rouse_text.txt")
    assert len(sentences) > 2000  # 2,128 addressed lines upstream
    chapters = {s.chapter for s in sentences}
    assert min(chapters) == 1 and max(chapters) >= 100
    assert all("[^" not in s.greek for s in sentences)


def test_real_vocab_file_loads():
    vocab = parse_rouse_vocab((DATA / "vocab.js").read_text(encoding="utf-8"))
    assert len(vocab) > 2000  # 2,136 upstream
    assert all(v["head"] for v in vocab)
