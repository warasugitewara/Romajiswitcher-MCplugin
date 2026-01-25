package com.github.waras.romajiswitcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;

/**
 * Manages user preferences for romaji conversion with persistent storage
 */
public class UserPreferences {
    private final Map<UUID, Boolean> userSettings = new HashMap<>();
    private final File configFile;
    private final Gson gson;

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
            Map<String, Boolean> data = gson.fromJson(reader,
                    new TypeToken<Map<String, Boolean>>() {}.getType());
            if (data != null) {
                data.forEach((uuidStr, enabled) -> {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        userSettings.put(uuid, enabled);
                    } catch (IllegalArgumentException e) {
                        // Invalid UUID, skip
                    }
                });
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
                // Convert UUID to String for JSON serialization
                Map<String, Boolean> data = new HashMap<>();
                userSettings.forEach((uuid, enabled) ->
                    data.put(uuid.toString(), enabled)
                );
                gson.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save user settings: " + e.getMessage());
        }
    }

    public void setEnabled(UUID playerId, boolean enabled) {
        userSettings.put(playerId, enabled);
        saveSettings();
    }

    public boolean isEnabled(UUID playerId) {
        // Default to true if not set
        return userSettings.getOrDefault(playerId, true);
    }

    public void toggleEnabled(UUID playerId) {
        boolean current = isEnabled(playerId);
        setEnabled(playerId, !current);
    }
}
