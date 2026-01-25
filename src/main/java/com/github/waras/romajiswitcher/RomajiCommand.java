package com.github.waras.romajiswitcher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles /romaji command
 * Usage:
 *   /romaji - Toggle on/off
 *   /romaji color <color1> <color2> - Set colors
 *   /romaji dictionary add <romaji> <kanji> - Add dictionary entry
 *   /romaji dictionary del <romaji> - Delete dictionary entry
 *   /romaji dictionary list [page] - List dictionary entries
 */
public class RomajiCommand implements CommandExecutor {
    private final UserPreferences preferences;
    private static final int ENTRIES_PER_PAGE = 10;

    public RomajiCommand(UserPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ使用できます");
            return true;
        }

        if (!player.hasPermission("romajiswitcher.use")) {
            player.sendMessage("§cこのコマンドを使用する権限がありません");
            return true;
        }

        // Handle sub-commands
        if (args.length == 0) {
            // Toggle command
            preferences.toggleEnabled(player.getUniqueId());
            boolean enabled = preferences.isEnabled(player.getUniqueId());

            if (enabled) {
                player.sendMessage("§a✔ ローマ字変換が有効になりました");
                player.sendMessage("§7メッセージがローマ字から日本語に変換されます");
            } else {
                player.sendMessage("§c✘ ローマ字変換が無効になりました");
                player.sendMessage("§7メッセージは変換されなくなります");
            }
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("switch")) {
            return handleSwitchCommand(player, args);
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("color")) {
            return handleColorCommand(player, args);
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("dictionary")) {
            return handleDictionaryCommand(player, args);
        }

        // Unknown sub-command
        player.sendMessage("§c未知のサブコマンド: " + args[0]);
        player.sendMessage("§7使用方法:");
        player.sendMessage("§e  /romaji - 変換ON/OFF トグル");
        player.sendMessage("§e  /romaji switch on|off - 変換を有効/無効に設定");
        player.sendMessage("§e  /romaji color <色1> <色2> - 色設定");
        player.sendMessage("§e  /romaji dictionary add <ローマ字> <漢字> - 辞書に追加");
        player.sendMessage("§e  /romaji dictionary del <ローマ字> - 辞書から削除");
        player.sendMessage("§e  /romaji dictionary list [ページ] - 辞書一覧");
        return true;
    }

    private boolean handleSwitchCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c使用方法: /romaji switch on|off");
            return true;
        }

        String action = args[1].toLowerCase();

        if (action.equals("on")) {
            preferences.setEnabled(player.getUniqueId(), true);
            player.sendMessage("§a✔ ローマ字変換が有効になりました");
            player.sendMessage("§7メッセージがローマ字から日本語に変換されます");
            return true;
        } else if (action.equals("off")) {
            preferences.setEnabled(player.getUniqueId(), false);
            player.sendMessage("§c✘ ローマ字変換が無効になりました");
            player.sendMessage("§7メッセージは変換されなくなります");
            return true;
        } else {
            player.sendMessage("§c不明なオプション: " + action);
            player.sendMessage("§7使用方法: /romaji switch on|off");
            return true;
        }
    }

    private boolean handleColorCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c使用方法: /romaji color <日本語色> <ローマ字色>");
            player.sendMessage("§7利用可能な色: " + String.join(", ", ColorManager.getAvailableColors()));
            return true;
        }

        String japaneseColor = args[1];
        String romajiColor = args[2];

        if (!ColorManager.isValidColor(japaneseColor)) {
            player.sendMessage("§c無効な色です: " + japaneseColor);
            player.sendMessage("§7利用可能な色: " + String.join(", ", ColorManager.getAvailableColors()));
            return true;
        }

        if (!ColorManager.isValidColor(romajiColor)) {
            player.sendMessage("§c無効な色です: " + romajiColor);
            player.sendMessage("§7利用可能な色: " + String.join(", ", ColorManager.getAvailableColors()));
            return true;
        }

        preferences.setColors(player.getUniqueId(), japaneseColor, romajiColor);
        player.sendMessage("§a✔ 色設定が更新されました");
        player.sendMessage("§e日本語色: §r" + japaneseColor);
        player.sendMessage("§eローマ字色: §r" + romajiColor);
        return true;
    }

    private boolean handleDictionaryCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c使用方法:");
            player.sendMessage("§e  /romaji dictionary add <ローマ字> <漢字>");
            player.sendMessage("§e  /romaji dictionary del <ローマ字>");
            player.sendMessage("§e  /romaji dictionary list [ページ]");
            return true;
        }

        String action = args[1].toLowerCase();

        if (action.equals("add")) {
            return handleDictionaryAdd(player, args);
        } else if (action.equals("del")) {
            return handleDictionaryDel(player, args);
        } else if (action.equals("list")) {
            return handleDictionaryList(player, args);
        } else {
            player.sendMessage("§c不明なアクション: " + action);
            return true;
        }
    }

    private boolean handleDictionaryAdd(Player player, String[] args) {
        if (!player.hasPermission("romajiswitcher.admin")) {
            player.sendMessage("§cこのコマンドを使用する権限がありません");
            return true;
        }

        if (args.length < 4) {
            player.sendMessage("§c使用方法: /romaji dictionary add <ローマ字> <漢字>");
            return true;
        }

        String romaji = args[2].toLowerCase();
        String kanji = args[3];

        RomajiConverter.addKanjiEntry(romaji, kanji);
        player.sendMessage("§a✔ 辞書に追加しました");
        player.sendMessage("§e" + romaji + " → " + kanji);
        return true;
    }

    private boolean handleDictionaryDel(Player player, String[] args) {
        if (!player.hasPermission("romajiswitcher.admin")) {
            player.sendMessage("§cこのコマンドを使用する権限がありません");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage("§c使用方法: /romaji dictionary del <ローマ字>");
            return true;
        }

        String romaji = args[2].toLowerCase();

        if (RomajiConverter.removeKanjiEntry(romaji)) {
            player.sendMessage("§a✔ 辞書から削除しました: " + romaji);
        } else {
            player.sendMessage("§c辞書に見つかりません: " + romaji);
        }
        return true;
    }

    private boolean handleDictionaryList(Player player, String[] args) {
        Map<String, String> entries = RomajiConverter.getKanjiEntries();
        
        if (entries.isEmpty()) {
            player.sendMessage("§c辞書は空です");
            return true;
        }

        int page = 1;
        if (args.length >= 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cページ番号が無効です");
                return true;
            }
        }

        int totalPages = (int) Math.ceil((double) entries.size() / ENTRIES_PER_PAGE);
        if (page < 1 || page > totalPages) {
            player.sendMessage("§cページ " + page + " は存在しません（全 " + totalPages + " ページ）");
            return true;
        }

        player.sendMessage("§e========== 辞書一覧 (" + page + "/" + totalPages + ") ==========");

        int start = (page - 1) * ENTRIES_PER_PAGE;
        int end = Math.min(start + ENTRIES_PER_PAGE, entries.size());

        entries.entrySet().stream()
                .skip(start)
                .limit(ENTRIES_PER_PAGE)
                .forEach(e -> player.sendMessage("§7" + e.getKey() + " §f→ §b" + e.getValue()));

        player.sendMessage("§e=====================================");
        return true;
    }
}

