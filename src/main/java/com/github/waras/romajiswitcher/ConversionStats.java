package com.github.waras.romajiswitcher;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages conversion usage statistics and learning.
 * Records how often each conversion is used and calculates scores accordingly.
 */
public class ConversionStats {
    private static final String STATS_FILENAME = "conversion-stats.json";
    
    /**
     * stats.get(romaji).get(kanji) → { count: long, lastUsedTime: long }
     */
    private final Map<String, Map<String, UsageInfo>> stats;
    private final Path statsPath;
    private static final long SAVE_INTERVAL_MS = 5 * 60 * 1000; // 5 minutes
    private long lastSaveTime;

    public static class UsageInfo {
        public long count;
        public long lastUsedTime;

        public UsageInfo(long count, long lastUsedTime) {
            this.count = count;
            this.lastUsedTime = lastUsedTime;
        }

        @Override
        public String toString() {
            return String.format("{count=%d, lastUsedTime=%d}", count, lastUsedTime);
        }
    }

    /**
     * @param pluginDataFolder Path to the plugin data folder
     */
    public ConversionStats(Path pluginDataFolder) {
        this.statsPath = pluginDataFolder.resolve(STATS_FILENAME);
        this.stats = new HashMap<>();
        this.lastSaveTime = System.currentTimeMillis();
        load();
    }

    /**
     * Record usage of a specific conversion
     */
    public synchronized void recordUsage(String romaji, String kanji) {
        if (romaji == null || kanji == null) {
            return;
        }

        String key = romaji.toLowerCase();
        stats.computeIfAbsent(key, k -> new HashMap<>())
            .compute(kanji, (k, v) -> {
                if (v == null) {
                    return new UsageInfo(1, System.currentTimeMillis());
                } else {
                    return new UsageInfo(v.count + 1, System.currentTimeMillis());
                }
            });

        // Save if enough time has passed
        if (System.currentTimeMillis() - lastSaveTime >= SAVE_INTERVAL_MS) {
            save();
            lastSaveTime = System.currentTimeMillis();
        }
    }

    /**
     * Get usage score for a specific candidate
     * Formula: log2(count + 1) × 10
     */
    public double getUsageScore(ConversionCandidate candidate) {
        if (candidate == null) {
            return 0;
        }

        // Note: romaji key is determined by RomajiDictionary context
        // This method only calculates from usage count
        return 0; // Will be called with romaji context in practice
    }

    /**
     * Get usage score for a specific romaji/kanji combination
     */
    public double getUsageScore(String romaji, String kanji) {
        if (romaji == null || kanji == null) {
            return 0;
        }

        String key = romaji.toLowerCase();
        Map<String, UsageInfo> candidates = stats.get(key);
        if (candidates == null) {
            return 0;
        }

        UsageInfo info = candidates.get(kanji);
        if (info == null) {
            return 0;
        }

        // log2(count + 1) × 10
        return Math.log(info.count + 1) / Math.log(2) * 10;
    }

    /**
     * Get all usage statistics
     */
    public Map<String, Map<String, UsageInfo>> getAllStats() {
        return Collections.unmodifiableMap(stats);
    }

    /**
     * Load statistics from file
     */
    public synchronized void load() {
        if (!Files.exists(statsPath)) {
            return;
        }

        try {
            String content = new String(Files.readAllBytes(statsPath), "UTF-8");
            parseJSON(content);
        } catch (Exception e) {
            System.err.println("Failed to load conversion stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save statistics to file
     */
    public synchronized void save() {
        try {
            Files.createDirectories(statsPath.getParent());
            String json = toJSON();
            Files.write(statsPath, json.getBytes("UTF-8"));
        } catch (Exception e) {
            System.err.println("Failed to save conversion stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parse JSON format (simple implementation)
     */
    private void parseJSON(String json) {
        // Simple JSON parsing for stats
        // Format: {"romaji": {"kanji": {"count": 5, "lastUsedTime": 1234567890}}}
        // TODO: Use a proper JSON library (Gson, Jackson)
        // For now, this is a placeholder
    }

    /**
     * Convert to JSON format
     */
    private String toJSON() {
        // Simple JSON generation
        // TODO: Use a proper JSON library (Gson, Jackson)
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        boolean firstRomaji = true;
        for (Map.Entry<String, Map<String, UsageInfo>> entry : stats.entrySet()) {
            if (!firstRomaji) sb.append(",");
            firstRomaji = false;
            
            sb.append("\"").append(entry.getKey()).append("\": {");
            
            boolean firstCandidate = true;
            for (Map.Entry<String, UsageInfo> candEntry : entry.getValue().entrySet()) {
                if (!firstCandidate) sb.append(",");
                firstCandidate = false;
                
                sb.append("\"").append(candEntry.getKey()).append("\": {")
                    .append("\"count\": ").append(candEntry.getValue().count)
                    .append(", \"lastUsedTime\": ").append(candEntry.getValue().lastUsedTime)
                    .append("}");
            }
            
            sb.append("}");
        }
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * Clear all statistics
     */
    public synchronized void clear() {
        stats.clear();
        save();
    }

    /**
     * Get statistics for a specific romaji
     */
    public Map<String, UsageInfo> getStatsForRomaji(String romaji) {
        String key = romaji.toLowerCase();
        Map<String, UsageInfo> result = stats.get(key);
        return result != null ? Collections.unmodifiableMap(result) : Collections.emptyMap();
    }
}
