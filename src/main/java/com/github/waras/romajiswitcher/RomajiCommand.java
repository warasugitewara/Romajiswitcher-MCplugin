package com.github.waras.romajiswitcher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles /romaji command
 * Usage:
 *   /romaji - Toggle on/off
 *   /romaji color <color1> <color2> - Set colors
 */
public class RomajiCommand implements CommandExecutor {
    private final UserPreferences preferences;

    public RomajiCommand(UserPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players");
            return true;
        }

        if (!player.hasPermission("romajiswitcher.use")) {
            player.sendMessage("§cYou don't have permission to use this command");
            return true;
        }

        // Handle sub-commands
        if (args.length == 0) {
            // Toggle command
            preferences.toggleEnabled(player.getUniqueId());
            boolean enabled = preferences.isEnabled(player.getUniqueId());

            if (enabled) {
                player.sendMessage("§a✔ Romaji conversion is now §2enabled§a!");
                player.sendMessage("§7Your messages will be converted from romaji to Japanese.");
            } else {
                player.sendMessage("§c✘ Romaji conversion is now §4disabled§c!");
                player.sendMessage("§7Your messages will no longer be converted.");
            }
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("color")) {
            if (args.length < 3) {
                player.sendMessage("§c使用方法: /romaji color <japanese_color> <romaji_color>");
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
            player.sendMessage("§a✔ Color preferences updated!");
            player.sendMessage("§e日本語色: §r" + japaneseColor);
            player.sendMessage("§eローマ字色: §r" + romajiColor);
            return true;
        }

        // Unknown sub-command
        player.sendMessage("§c未知のサブコマンド: " + args[0]);
        player.sendMessage("§7使用方法:");
        player.sendMessage("§e  /romaji - 変換ON/OFF");
        player.sendMessage("§e  /romaji color <japanese_color> <romaji_color> - 色設定");
        return true;
    }
}
