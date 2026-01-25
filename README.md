# RomajiSwitcher-MCplugin

A lightweight Paper Minecraft plugin (1.21.6+) that automatically converts romaji (Latin alphabet) to Japanese characters in chat messages.

**Inspired by**: [LunaChat](https://github.com/ucchyocean/LunaChat) - Core romaji conversion logic adapted from their japanization system.

## Features

- 🔤 **Automatic Romaji-to-Japanese Conversion**: Converts latin characters to hiragana and common kanji
- 💬 **Chat Integration**: Seamlessly converts player messages
- 👤 **Per-Player Toggle**: Players can enable/disable conversion with `/romaji` (settings persist across server restarts)
- 🎨 **Color Customization**: Players can customize the color of Japanese and romaji text with `/romaji color <color1> <color2>`
- 🔧 **Comprehensive Romanization**: Full support for all Japanese romanization styles including:
  - Sokuon (促音): `kitte` → `きって`
  - Small kana (小書き仮名): `lya` → `ゃ`, `xyo` → `ょ`
  - N particle: `san` → `さん`, `n` → `ん`
  - Long vowels: `ou` → `おう`, `ei` → `えい`
- ⚡ **Lightweight**: Minimal performance impact
- 🤝 **Plugin Compatible**: Works with Translator series and EssentialsX Discord
- 💾 **Persistent Storage**: Player settings are saved to JSON and restored on server restart
- 🇯🇵 **Japanese Output**: Converts `aiueo` → `あいうえお(aiueo)`, `arigatou` → `有難う(arigatou)`

## Installation

1. Download the latest JAR from [Releases](https://github.com/waras/Romajiswitcher-MCplugin/releases)
2. Place it in your Paper server's `plugins` folder
3. Restart the server

## Usage

### Player Commands

- `/romaji` - Toggle romaji conversion on/off for yourself (default: ON)
- `/romaji color <japanese_color> <romaji_color>` - Customize text colors

**Colors Available**: white, silver, gray, black, red, maroon, yellow, olive, lime, green, aqua, teal, blue, navy, fuchsia, purple

### Command Examples

```
/romaji
→ ✔ Romaji conversion is now enabled!
  Your messages will be converted from romaji to Japanese.

/romaji color white gray
→ ✔ Color preferences updated!
  日本語色: white
  ローマ字色: gray
```

### Output Format

The plugin converts messages while preserving the original romaji in parentheses for reference:

```
Player Input:  waras: arigatou
Server Output: waras: 有難う(arigatou)
```

### Example Conversions

| Input | Output |
|-------|--------|
| `aiueo` | `あいうえお(aiueo)` |
| `arigatou` | `有難う(arigatou)` |
| `konnichiwa` | `こんにちは(konnichiwa)` |
| `oyasumi` | `お休み(oyasumi)` |
| `sugoi` | `凄い(sugoi)` |

## Permissions

- `romajiswitcher.use` - Allow player to use romaji conversion (default: true)
- `romajiswitcher.admin` - Admin permission (default: op)

## Configuration

### Persistent User Settings

Player preferences are automatically saved to `plugins/RomajiSwitcher/user_settings.json`:

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

- `enabled` = Romaji conversion enabled/disabled
- `japaneseColor` = Color for converted Japanese text (default: white)
- `romajiColor` = Color for parenthesized romaji text (default: gray)

Settings are loaded on server startup and saved whenever a player changes settings.

## Compatibility

✅ **Compatible with**:
- Translator series plugins
- EssentialsX
- EssentialsX Discord
- Other chat-related plugins that use Bukkit chat events

**Note**: RomajiSwitcher uses a normal event priority (not highest or lowest), ensuring it works well alongside other plugins.

## Building

Requirements:
- Java 21+
- Maven

```bash
mvn clean package
```

Output JAR will be in `target/RomajiSwitcher-1.0.0.jar`

## Supported Romanization

### Hiragana Conversion
- **Vowels**: a, i, u, e, o
- **Consonants**: k, g, s, z, t, d, n, h, b, p, m, y, r, w
- **Combined Sounds**: kya, sha, cha, nya, hya, etc.
- **Small Kana**: lya/xya → ゃ, lyu/xyu → ゅ, lyo/xyo → ょ, la/xa → ぁ, etc.
- **Special Handling**:
  - Sokuon (促音): `kitte` → `きって`, `matte` → `まって`
  - N particle: `san` → `さん`, `n` (standalone) → `ん`, `nn` → `ん`
  - Long vowels: `ou` → `おう`, `ei` → `えい`

### Kanji Conversion
Supports ~70 common Japanese words including:
- Greetings: `konnichiwa` → `こんにちは`, `arigatou` → `有難う`
- School: `gakkou` → `学校`, `sensei` → `先生`
- Family: `otousan` → `お父さん`, `okaasan` → `お母さん`
- Verbs: `taberu` → `食べる`, `yomu` → `読む`
- Adjectives: `sugoi` → `凄い`, `kawaii` → `可愛い`

Extended dictionary can be modified in `src/main/resources/kanji_dictionary.txt`

## License

MIT License

## Credits

Inspired by and adapted from [LunaChat](https://github.com/ucchyocean/LunaChat) by ucchyocean
