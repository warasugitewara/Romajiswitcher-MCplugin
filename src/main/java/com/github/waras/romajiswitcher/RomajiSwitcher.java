package com.github.waras.romajiswitcher;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * RomajiSwitcher - A Paper plugin that converts romaji to Japanese in chat
 */
public class RomajiSwitcher extends JavaPlugin {

    private UserPreferences preferences;

    @Override
    public void onEnable() {
        // Ensure data folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.preferences = new UserPreferences(getDataFolder());

        // Register listeners
        getServer().getPluginManager().registerEvents(new ChatListener(preferences), this);

        // Register commands
        RomajiCommand romajiCommand = new RomajiCommand(preferences);
        getCommand("romaji").setExecutor(romajiCommand);
        getCommand("romaji").setTabCompleter(new RomajiTabCompleter());

        getLogger().info("§aRomajiSwitcher enabled!");
        getLogger().info("§eUse /romaji to toggle romaji conversion");
    }

    @Override
    public void onDisable() {
        getLogger().info("§cRomajiSwitcher disabled!");
    }

    public UserPreferences getPreferences() {
        return preferences;
    }
}
