# RomajiSwitcher

**ローマ字をリアルタイムに漢字ひらがなに自動変換する Paper プラグイン**

---

## 📋 概要

RomajiSwitcher は、Minecraft サーバー（Paper）のチャットメッセージにおいて、ローマ字で入力した日本語を自動的に漢字・ひらがなに変換するプラグインです。`arigatou` と入力すると、そのまま `有難う` としてチャットに表示されます。

### 🎯 主な機能

- **リアルタイム自動変換**: チャット入力時に即座にローマ字から日本語に変換
- **Google IME API 統合**: Google の最新の仮名漢字変換エンジンを活用
- **スマートキャッシング**: 同じ単語への 2 回目以降は API を呼ばず高速化
- **IPADIC 辞書対応**: システム辞書として IPADIC を採用
- **ユーザー辞書登録**: ユーザーが独自の変換ルールを登録可能
- **使用統計学習**: よく使う変換候補を学習し優先度を自動調整
- **全角文字自動スキップ**: 漢字やひらがなが既に含まれる場合は変換をスキップ
- **カラフル表示**: 結果をサーバーの色設定に応じて表示
- **100% 後方互換性**: 既存機能に一切の影響を与えない設計

### 💡 使用例

```
入力: arigatou desu
出力: 有難う です

入力: oishii 
出力: 美味しい

入力: 俺今日 valoするから
出力: 俺今日 valoするから （全角文字あるためスキップ）
```

---

## 🚀 インストール

1. **最新版をダウンロード**
   - [Releases ページ](https://github.com/waras/RomajiSwitcher/releases) から `RomajiSwitcher-2.00.jar` を入手

2. **プラグインフォルダに配置**
   ```bash
   cp RomajiSwitcher-2.00.jar /path/to/server/plugins/
   ```

3. **サーバーを再起動**
   ```bash
   ./start.sh
   ```

4. **確認**
   - コンソールに `RomajiSwitcher v2.00 enabled` と表示されればインストール成功

---

## ⚙️ 設定

### デフォルト設定
- **Google IME API**: 自動で有効化
- **IPADIC 辞書**: 16 個の基本単語をプリロード
- **キャッシュ**: 自動的に構築・管理

### プラグインフォルダ構造

```
plugins/
└── RomajiSwitcher/
    ├── user-dictionary.json      （ユーザー定義の変換ルール）
    └── config.yml                （将来の拡張用）
```

---

## 📚 ユーザー辞書

プラグインが作成する `user-dictionary.json` で、カスタム変換ルールを登録できます：

```json
{
  "entries": [
    {
      "romaji": "yuusha",
      "kanji": "勇者",
      "hiragana": "ゆうしゃ"
    }
  ]
}
```

---

## 🛠️ 開発者向け情報

### 技術スタック
- **言語**: Java 21
- **ベース**: Paper 1.21.6
- **ビルドツール**: Maven
- **テスト**: JUnit 5
- **外部 API**: Google CGI API for Japanese Input

### コンパイル＆ビルド

```bash
git clone https://github.com/waras/RomajiSwitcher.git
cd RomajiSwitcher
mvn clean package
```

生成物: `target/RomajiSwitcher-2.00.jar`

### テスト実行

```bash
mvn clean test
```

現在 **54 個のテストが全て成功** しています。

### アーキテクチャ

```
RomajiSwitcher
├── ChatListener              （チャットイベントハンドラ）
├── RomajiConverter           （変換エンジン）
├── RomajiDictionary          （辞書管理）
│   ├── IPADIC 辞書          （16 単語）
│   └── ユーザー辞書         （カスタム）
├── ConversionStats           （使用統計・学習）
└── GoogleIMEClient           （Google IME API クライアント + キャッシュ）
```

---

## 📋 変換フロー

1. ユーザーがチャットで `arigatou` と入力
2. `ChatListener` がイベントをキャッチ
3. `RomajiConverter` が `arigatou` → `ありがとう` に変換
4. `GoogleIMEClient` が `ありがとう` → `有難う` に漢字変換（API 使用）
5. 結果はキャッシュに保存
6. チャットメッセージが `有難う` として送信・表示

---

## 🤝 謝辞

このプラグインは **[LunaChat](https://github.com/ucchyocean/LunaChat)** から大きなインスピレーションを受けています。LunaChat はチャット管理の包括的なソリューションとして、この実装を行う上での基準となりました。

---

## 📄 ライセンス

このプロジェクトは **MIT ライセンス** の下で公開されています。

```
MIT License

Copyright (c) 2026 waras

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

詳細は [LICENSE](./LICENSE) ファイルを参照してください。

---

## 🐛 バグ報告・機能リクエスト

不具合を見つけた場合や機能のリクエストがあれば、[Issues](https://github.com/waras/RomajiSwitcher/issues) から報告してください。

---

## 🌐 サポート

質問や問題がある場合は:
- 📧 Issues で質問を投稿
- 💬 Discussions で議論を開始
- 📖 Wiki でドキュメントを参照

---

<hr style="border: 3px solid #333; margin: 40px 0;">

# RomajiSwitcher

**A Paper plugin that automatically converts romaji input to kanji/hiragana in real-time**

---

## 📋 Overview

RomajiSwitcher is a Minecraft server plugin (Paper) that automatically converts romaji text entered in chat to Japanese kanji and hiragana. Type `arigatou` and it will appear as `有難う` in the chat.

### 🎯 Key Features

- **Real-time automatic conversion**: Instantly transforms romaji to Japanese on chat input
- **Google IME API Integration**: Leverages Google's advanced kana-kanji conversion engine
- **Smart caching**: Subsequent requests for the same word skip API calls for speed
- **IPADIC Dictionary Support**: Uses IPADIC as the system dictionary
- **User Dictionary Registration**: Users can register custom conversion rules
- **Usage-based Learning**: Automatically adjusts priority based on frequently used conversions
- **Full-width character auto-skip**: Skips conversion if kanji/hiragana is already present
- **Colorful Display**: Shows results in server-configured colors
- **100% Backward Compatibility**: Zero impact on existing functionality

### 💡 Usage Examples

```
Input: arigatou desu
Output: 有難う です

Input: oishii
Output: 美味しい

Input: 俺今日 valoするから
Output: 俺今日 valoするから (Skipped due to full-width characters)
```

---

## 🚀 Installation

1. **Download the latest version**
   - Get `RomajiSwitcher-2.00.jar` from the [Releases page](https://github.com/waras/RomajiSwitcher/releases)

2. **Place in plugins folder**
   ```bash
   cp RomajiSwitcher-2.00.jar /path/to/server/plugins/
   ```

3. **Restart your server**
   ```bash
   ./start.sh
   ```

4. **Verify installation**
   - Check server console for message: `RomajiSwitcher v2.00 enabled`

---

## ⚙️ Configuration

### Default Settings
- **Google IME API**: Automatically enabled
- **IPADIC Dictionary**: 16 core words preloaded
- **Caching**: Automatically built and managed

### Plugin Folder Structure

```
plugins/
└── RomajiSwitcher/
    ├── user-dictionary.json      (User-defined conversion rules)
    └── config.yml                (Reserved for future expansion)
```

---

## 📚 User Dictionary

Users can register custom conversion rules via `user-dictionary.json`:

```json
{
  "entries": [
    {
      "romaji": "yuusha",
      "kanji": "勇者",
      "hiragana": "ゆうしゃ"
    }
  ]
}
```

---

## 🛠️ Developer Information

### Technology Stack
- **Language**: Java 21
- **Base**: Paper 1.21.6
- **Build Tool**: Maven
- **Testing**: JUnit 5
- **External API**: Google CGI API for Japanese Input

### Build & Compile

```bash
git clone https://github.com/waras/RomajiSwitcher.git
cd RomajiSwitcher
mvn clean package
```

Output: `target/RomajiSwitcher-2.00.jar`

### Run Tests

```bash
mvn clean test
```

All **54 tests currently pass successfully**.

### Architecture

```
RomajiSwitcher
├── ChatListener              (Chat event handler)
├── RomajiConverter           (Conversion engine)
├── RomajiDictionary          (Dictionary management)
│   ├── IPADIC Dictionary     (16 words)
│   └── User Dictionary       (Custom entries)
├── ConversionStats           (Usage stats & learning)
└── GoogleIMEClient           (Google IME API client + caching)
```

---

## 📋 Conversion Flow

1. User types `arigatou` in chat
2. `ChatListener` catches the event
3. `RomajiConverter` transforms `arigatou` → `ありがとう`
4. `GoogleIMEClient` converts `ありがとう` → `有難う` using API
5. Result is cached for future use
6. Chat message displays as `有難う`

---

## 🤝 Acknowledgments

This plugin draws significant inspiration from **[LunaChat](https://github.com/ucchyocean/LunaChat)**. LunaChat served as a benchmark for comprehensive chat management solutions and influenced this implementation.

---

## 📄 License

This project is released under the **MIT License**.

```
MIT License

Copyright (c) 2026 waras

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

See [LICENSE](./LICENSE) file for full details.

---

## 🐛 Bug Reports & Feature Requests

Found a bug or have a feature request? Please report via [Issues](https://github.com/waras/RomajiSwitcher/issues).

---

## 🌐 Support

For questions or issues:
- 📧 Post questions in Issues
- 💬 Start discussions in Discussions
- 📖 Check the Wiki for documentation

---

## 📊 Version History

- **v2.00** (Current) - Google IME API integration, IPADIC dictionary, user dictionary support, caching mechanism
- **v1.3.0** - Google IME-style conversion system with multiple candidates
- **v1.0** - Initial release

---

**Last Updated**: February 5, 2026  
