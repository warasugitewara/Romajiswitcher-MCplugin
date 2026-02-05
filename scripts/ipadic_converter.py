#!/usr/bin/env python3
"""
IPADICデータを JSON 形式に変換するスクリプト

実運用では、以下から IPADIC データを取得：
https://github.com/taku910/mecab/tree/master/mecab-ipadic

このスクリプトは最小限のテスト辞書を生成します
"""

import json

def generate_ipadic_json(output_file: str):
    """
    IPADIC互換 JSON を生成
    実際の運用では MeCab/Janome の辞書を JSON に変換する
    """
    print("Generating IPADIC-compatible dictionary...")
    
    # 最小限の辞書
    # 実運用では、以下のようなデータを含める：
    # - 標準的な日本語単語
    # - 品詞とよみがな
    # - 基本スコア
    
    ipadic_entries = [
        {
            "kanji": "凄い",
            "hiragana": "すごい",
            "baseScore": 100
        },
        {
            "kanji": "可愛い",
            "hiragana": "かわいい",
            "baseScore": 100
        },
        {
            "kanji": "美味しい",
            "hiragana": "おいしい",
            "baseScore": 100
        },
        {
            "kanji": "新しい",
            "hiragana": "あたらしい",
            "baseScore": 100
        },
        {
            "kanji": "古い",
            "hiragana": "ふるい",
            "baseScore": 100
        },
        {
            "kanji": "大きい",
            "hiragana": "おおきい",
            "baseScore": 100
        },
        {
            "kanji": "小さい",
            "hiragana": "ちいさい",
            "baseScore": 100
        },
        {
            "kanji": "速い",
            "hiragana": "はやい",
            "baseScore": 100
        },
        {
            "kanji": "遅い",
            "hiragana": "おそい",
            "baseScore": 100
        },
        {
            "kanji": "強い",
            "hiragana": "つよい",
            "baseScore": 100
        },
        {
            "kanji": "弱い",
            "hiragana": "よわい",
            "baseScore": 100
        },
        {
            "kanji": "高い",
            "hiragana": "たかい",
            "baseScore": 100
        },
        {
            "kanji": "低い",
            "hiragana": "ひくい",
            "baseScore": 100
        },
        {
            "kanji": "深い",
            "hiragana": "ふかい",
            "baseScore": 100
        },
        {
            "kanji": "浅い",
            "hiragana": "あさい",
            "baseScore": 100
        },
    ]
    
    ipadic_dict = {"entries": ipadic_entries}
    
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(ipadic_dict, f, ensure_ascii=False, indent=2)
    
    print(f"Generated: {output_file}")
    print(f"Entries: {len(ipadic_entries)}")

if __name__ == "__main__":
    import sys
    output = "ipadic-subset.json"
    if len(sys.argv) > 1:
        output = sys.argv[1]
    
    generate_ipadic_json(output)
    print("Done!")
