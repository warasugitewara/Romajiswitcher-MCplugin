package com.github.waras.romajiswitcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import java.io.*;
import java.util.*;

/**
 * Manages user preferences for romaji conversion with persistent storage
 * Stores: enabled status and color preferences
 */
public class UserPreferences {
    private final Map<UUID, UserSettings> userSettings = new HashMap<>();
    private final File configFile;
    private final Gson gson;

    public static class UserSettings {
        public boolean enabled = true;
        public String japaneseColor = "white";      // Default color for Japanese text
        public String romajiColor = "gray";         // Default color for Romaji text (parentheses)

        public UserSettings() {}

        public UserSettings(boolean enabled, String japaneseColor, String romajiColor) {
            this.enabled = enabled;
            this.japaneseColor = japaneseColor;
            this.romajiColor = romajiColor;
        }
    }

    public UserPreferences(File pluginDataFolder) {
        this.configFile = new File(pluginDataFolder, "user_settings.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadSettings();
    }

    /**
     * Load settings from file
     */
    private void loadSettings() {
        if (!configFile.exists()) {
            return;
        }

        try (Reader reader = new FileReader(configFile)) {
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            if (data != null) {
                for (String uuidStr : data.keySet()) {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        JsonElement element = data.get(uuidStr);
                        UserSettings settings = gson.fromJson(element, UserSettings.class);
                        userSettings.put(uuid, settings);
                    } catch (IllegalArgumentException e) {
                        // Invalid UUID, skip
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load user settings: " + e.getMessage());
        }
    }

    /**
     * Save settings to file
     */
    private void saveSettings() {
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            try (Writer writer = new FileWriter(configFile)) {
                JsonObject data = new JsonObject();
                for (Map.Entry<UUID, UserSettings> entry : userSettings.entrySet()) {
                    data.add(entry.getKey().toString(), gson.toJsonTree(entry.getValue()));
                }
                gson.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save user settings: " + e.getMessage());
        }
    }

    private UserSettings getOrCreateSettings(UUID playerId) {
        return userSettings.computeIfAbsent(playerId, k -> new UserSettings());
    }

    public void setEnabled(UUID playerId, boolean enabled) {
        getOrCreateSettings(playerId).enabled = enabled;
        saveSettings();
    }

    public boolean isEnabled(UUID playerId) {
        return getOrCreateSettings(playerId).enabled;
    }

    public void toggleEnabled(UUID playerId) {
        boolean current = isEnabled(playerId);
        setEnabled(playerId, !current);
    }

    /**
     * Set color preferences for a player
     * @param playerId Player UUID
     * @param japaneseColor Color for Japanese text
     * @param romajiColor Color for Romaji text (in parentheses)
     */
    public void setColors(UUID playerId, String japaneseColor, String romajiColor) {
        UserSettings settings = getOrCreateSettings(playerId);
        
        // Validate colors
        if (ColorManager.isValidColor(japaneseColor)) {
            settings.japaneseColor = japaneseColor;
        }
        if (ColorManager.isValidColor(romajiColor)) {
            settings.romajiColor = romajiColor;
        }
        
        saveSettings();
    }

    /**
     * Get color preferences for a player
     * @return Array: [japaneseColor, romajiColor]
     */
    public String[] getColors(UUID playerId) {
        UserSettings settings = getOrCreateSettings(playerId);
        return new String[] { settings.japaneseColor, settings.romajiColor };
    }

    /**
     * Get color preference (single)
     */
    public String getJapaneseColor(UUID playerId) {
        return getOrCreateSettings(playerId).japaneseColor;
    }

    public String getRomajiColor(UUID playerId) {
        return getOrCreateSettings(playerId).romajiColor;
    }
}
