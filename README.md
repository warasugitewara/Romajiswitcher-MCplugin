# RomajiSwitcher-MCplugin
<img width="645" height="368" alt="{02AB2A1A-374B-4B39-8F19-6789D662D8B3}" src="https://github.com/user-attachments/assets/3d2d0125-7c45-4c25-8db5-e119f6271c6c" />

Paper Minecraft 1.21.6対応のローマ字自動日本語化プラグイン

チャットメッセージのローマ字（ラテン文字）を自動的に日本語に変換します。

**インスパイア元**: [LunaChat](https://github.com/ucchyocean/LunaChat) - 日本語化ロジックをベースに開発

## 機能

- 🔤 **ローマ字自動変換**: ラテン文字をひらがなと常用漢字に自動変換
- 💬 **チャット統合**: プレイヤーのメッセージを自動変換
- 👤 **個人単位ON/OFF**: `/romaji` コマンドで有効/無効を切り替え可能（設定は永続化）
- 🎨 **色カスタマイズ**: `/romaji color` コマンドで日本語とローマ字の色を個別指定
- 🔧 **包括的なローマ字対応**:
  - 促音（小さいつ）: `kitte` → `きって`
  - 小書き仮名: `lya` → `ゃ`, `xyo` → `ょ`
  - ん の智的判定: `san` → `さん`, `n` → `ん`
  - 長音: `ou` → `おう`, `ei` → `えい`
- ⚡ **軽量**: パフォーマンス負荷最小化
- 🤝 **互換性**: Translator シリーズ、EssentialsX Discord に対応
- 💾 **設定永続化**: プレイヤー設定はJSON保存（サーバー再起動後も保持）
- 🇯🇵 **日本語出力**: `aiueo` → `あいうえお(aiueo)`, `arigatou` → `有難う(arigatou)`

## インストール

1. [Releases](https://github.com/warasugitewara/Romajiswitcher-MCplugin/releases) から最新の JAR をダウンロード
2. Paper サーバーの `plugins` フォルダに配置
3. サーバーを再起動

## 使い方

### コマンド

- `/romaji` - ローマ字変換の ON/OFF トグル
- `/romaji switch on|off` - ローマ字変換を有効/無効に設定
- `/romaji color <color1> <color2>` - テキストの色をカスタマイズ
- `/romaji dictionary add <romaji> <kanji>` - 辞書に単語を追加（管理者用）
- `/romaji dictionary del <romaji>` - 辞書から単語を削除（管理者用）
- `/romaji dictionary list [ページ]` - 辞書一覧を表示（ページネーション対応）

**✨ タブ補完対応**: すべてのコマンドでタブキー補完が使用可能

**利用可能な色**: black, dark_blue, dark_green, dark_aqua, dark_red, dark_purple, gold, gray, dark_gray, blue, green, aqua, red, light_purple, yellow, white

### コマンド例

```
/romaji
→ ✔ ローマ字変換が有効になりました
  メッセージがローマ字から日本語に変換されます

/romaji color white gray
→ ✔ 色設定が更新されました
  日本語色: white
  ローマ字色: gray

/romaji dictionary add bokoku 母国
→ ✔ 辞書に追加しました
  bokoku → 母国

/romaji dictionary list
→ ========== 辞書一覧 (1/10) ==========
  arigatou → 有難う
  domo → どうも
  ...
  =====================================
```

### 出力例

プラグインはメッセージを変換し、元のローマ字を括弧内に保持します:

```
プレイヤー入力:  waras: arigatou
サーバー出力: waras: 有難う(arigatou)
```

### 変換例

| 入力 | 出力 |
|------|------|
| `aiueo` | `あいうえお(aiueo)` |
| `arigatou` | `有難う(arigatou)` |
| `konnichiwa` | `こんにちは(konnichiwa)` |
| `oyasumi` | `お休み(oyasumi)` |
| `sugoi` | `凄い(sugoi)` |

## パーミッション（権限）

- `romajiswitcher.use` - ローマ字変換機能の使用（デフォルト: true）
- `romajiswitcher.admin` - 管理者権限（デフォルト: op）

## 設定

### プレイヤー設定の永続化

プレイヤーの設定は `plugins/RomajiSwitcher/user_settings.json` に自動保存されます:

```json
{
  "550e8400-e29b-41d4-a716-446655440000": {
    "enabled": true,
    "japaneseColor": "white",
    "romajiColor": "gray"
  },
  "6ba7b810-9dad-11d1-80b4-00c04fd430c8": {
    "enabled": false,
    "japaneseColor": "white",
    "romajiColor": "gray"
  }
}
```

- `enabled` - ローマ字変換の ON/OFF
- `japaneseColor` - 日本語テキストの色（デフォルト: white）
- `romajiColor` - 括弧内ローマ字の色（デフォルト: gray）

## 互換性

✅ **対応プラグイン**:
- Translator シリーズ
- EssentialsX
- EssentialsX Discord
- その他のチャット関連プラグイン

**注記**: RomajiSwitcher は通常優先度で実行されるため、他のプラグインとの競合が少なくなります。

## サポートしているローマ字

### ひらがな変換
- **単母音**: a, i, u, e, o
- **子音**: k, g, s, z, t, d, n, h, b, p, m, y, r, w
- **拗音**: kya, sha, cha, nya, hya など
- **小書き仮名**: lya/xya → ゃ, lyu/xyu → ゅ, lyo/xyo → ょ, la/xa → ぁ など
- **特殊処理**:
  - 促音（小さいつ）: `kitte` → `きって`, `matte` → `まって`, `xtu` → `っ`
  - ん の処理: `san` → `さん`, `n` （単独）→ `ん`, `nn` → `ん`
  - じゅ の処理: `ju` → `じゅ`, `zyu` → `じゅ`, `jyu` → `じゅ`
  - 長音: `ou` → `おう`, `ei` → `えい`

### 漢字変換
100+ 個の常用単語に対応:
- **挨拶**: `konnichiwa` → `こんにちは`, `arigatou` → `有難う`
- **学校**: `gakkou` → `学校`, `sensei` → `先生`
- **家族**: `otousan` → `お父さん`, `okaasan` → `お母さん`
- **動詞**: `taberu` → `食べる`, `yomu` → `読む`
- **形容詞**: `sugoi` → `凄い`, `kawaii` → `可愛い`
- **その他**: `bokoku` → `母国`, `kaisha` → `会社` など

**カスタム辞書**: `/romaji dictionary add` コマンドでいつでも新しい単語を追加可能

## ビルド

要件:
- Java 21 以上
- Maven

```bash
mvn clean package
```

出力 JAR は `target/RomajiSwitcher-1.0.0.jar` に生成されます。

## ライセンス

MIT License

## クレジット

[LunaChat](https://github.com/ucchyocean/LunaChat) by ucchyocean からインスパイアされ、開発されました。

