package com.github.waras.romajiswitcher;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * RomajiSwitcher - A Paper plugin that converts romaji to Japanese in chat
 */
public class RomajiSwitcher extends JavaPlugin {

    private UserPreferences preferences;
    private RomajiDictionary dictionary;
    private ConversionStats stats;

    @Override
    public void onEnable() {
        // Ensure data folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.preferences = new UserPreferences(getDataFolder());
        
        // Initialize new dictionary-based system
        try {
            this.stats = new ConversionStats(getDataFolder().toPath());
            this.dictionary = new RomajiDictionary(getDataFolder().toPath(), stats);
            
            getLogger().info("§aNew dictionary system initialized (" + dictionary.size() + " entries)");
        } catch (Exception e) {
            getLogger().warning("§cFailed to initialize dictionary system, using legacy mode");
            e.printStackTrace();
            this.dictionary = null;
            this.stats = null;
        }

        // Register listeners with new system if available
        if (dictionary != null && stats != null) {
            getServer().getPluginManager().registerEvents(new ChatListener(preferences, dictionary, stats), this);
        } else {
            getServer().getPluginManager().registerEvents(new ChatListener(preferences), this);
        }

        // Register commands
        RomajiCommand romajiCommand = new RomajiCommand(preferences);
        getCommand("romaji").setExecutor(romajiCommand);
        getCommand("romaji").setTabCompleter(new RomajiTabCompleter());

        getLogger().info("§aRomajiSwitcher enabled!");
        getLogger().info("§eUse /romaji to toggle romaji conversion");
    }

    @Override
    public void onDisable() {
        // Save statistics before shutdown
        if (stats != null) {
            stats.save();
        }
        
        getLogger().info("§cRomajiSwitcher disabled!");
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public RomajiDictionary getDictionary() {
        return dictionary;
    }

    public ConversionStats getStats() {
        return stats;
    }
}
