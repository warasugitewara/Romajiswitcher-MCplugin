package com.github.waras.romajiswitcher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Provides tab completion for /romaji command
 */
public class RomajiTabCompleter implements TabCompleter {
    
    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;

        // First argument - main sub-commands
        if (args.length == 1) {
            return getMainCommands(args[0]);
        }

        // Handle sub-commands
        if (args.length >= 2) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("switch")) {
                if (args.length == 2) {
                    return getSwitchOptions(args[1]);
                }
            } else if (subCommand.equals("color")) {
                if (args.length == 2) {
                    return getColorOptions(args[1]);
                } else if (args.length == 3) {
                    return getColorOptions(args[2]);
                }
            } else if (subCommand.equals("dictionary")) {
                if (args.length == 2) {
                    return getDictionaryActions(args[1]);
                }
            }
        }

        return null;
    }

    private List<String> getMainCommands(String prefix) {
        List<String> commands = Arrays.asList(
                "switch",
                "color",
                "dictionary"
        );
        return filterMatches(commands, prefix);
    }

    private List<String> getSwitchOptions(String prefix) {
        List<String> options = Arrays.asList("on", "off");
        return filterMatches(options, prefix);
    }

    private List<String> getColorOptions(String prefix) {
        List<String> colors = new ArrayList<>(ColorManager.getAvailableColors());
        return filterMatches(colors, prefix);
    }

    private List<String> getDictionaryActions(String prefix) {
        List<String> actions = Arrays.asList("add", "del", "list");
        return filterMatches(actions, prefix);
    }

    private List<String> filterMatches(List<String> options, String prefix) {
        List<String> matches = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();

        for (String option : options) {
            if (option.toLowerCase().startsWith(lowerPrefix)) {
                matches.add(option);
            }
        }

        return matches.isEmpty() ? null : matches;
    }
}
