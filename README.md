# RomajiSwitcher-MCplugin

A lightweight Paper Minecraft plugin (1.21.6+) that automatically converts romaji (Latin alphabet) to Japanese characters in chat messages.

**Inspired by**: [LunaChat](https://github.com/ucchyocean/LunaChat) - Core romaji conversion logic adapted from their japanization system.

## Features

- 🔤 **Automatic Romaji-to-Japanese Conversion**: Converts latin characters to hiragana and common kanji
- 💬 **Chat Integration**: Seamlessly converts player messages
- 👤 **Per-Player Toggle**: Players can enable/disable conversion with `/romaji` (settings persist across server restarts)
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

**Output Example**:
```
Player Input:  waras: aiueo
Server Output: waras: あいうえお(aiueo)
```

### Toggle Example

```
/romaji
→ ✔ Romaji conversion is now enabled!
  Your messages will be converted from romaji to Japanese.

/romaji
→ ✘ Romaji conversion is now disabled!
  Your messages will no longer be converted.
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
  "550e8400-e29b-41d4-a716-446655440000": true,
  "6ba7b810-9dad-11d1-80b4-00c04fd430c8": false
}
```

- `true` = Romaji conversion enabled for this player
- `false` = Romaji conversion disabled for this player

Settings are loaded on server startup and saved whenever a player toggles with `/romaji`.

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

## Supported Romaji

- Basic hiragana: a, i, u, e, o, ka, ki, ku, ke, ko, sa, si, su, se, so, etc.
- Combined sounds: kya, kyu, kyo, sha, shu, sho, cha, chu, cho, etc.
- Common words: arigatou, konnichiwa, oyasumi, sumimasen, etc.

## License

MIT License

## Credits

Inspired by and adapted from [LunaChat](https://github.com/ucchyocean/LunaChat) by ucchyocean
