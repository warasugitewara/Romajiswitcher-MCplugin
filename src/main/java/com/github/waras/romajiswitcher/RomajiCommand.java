package com.github.waras.romajiswitcher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles /romaji command
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
}
