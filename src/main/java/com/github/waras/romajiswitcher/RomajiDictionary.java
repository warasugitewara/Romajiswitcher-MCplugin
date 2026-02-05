package com.github.waras.romajiswitcher;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Manages the romanization dictionary with support for:
 * - IPADIC base dictionary (system entries)
 * - User-registered entries (higher priority)
 * - Multiple conversion candidates per romaji
 * - Dynamic scoring based on usage statistics
 */
public class RomajiDictionary {
    private final Map<String, DictionaryEntry> dictionary;
    private final ConversionStats stats;
    private final Path dictionaryPath;
    private static final String USER_DICT_FILENAME = "user-dictionary.json";
    private static final String IPADIC_RESOURCE = "ipadic-subset.json";
    
    /**
     * @param pluginDataFolder Path to the plugin data folder
     * @param stats ConversionStats instance for learning
     */
    public RomajiDictionary(Path pluginDataFolder, ConversionStats stats) {
        this.dictionary = new ConcurrentHashMap<>();
        this.stats = stats;
        this.dictionaryPath = pluginDataFolder.resolve(USER_DICT_FILENAME);
        
        // Load dictionaries
        loadIPADICDictionary();
        loadUserDictionary();
    }

    /**
     * Load IPADIC base dictionary from resources
     */
    private void loadIPADICDictionary() {
        try {
            // Try to load IPADIC resource if available
            InputStream inputStream = RomajiDictionary.class.getClassLoader()
                    .getResourceAsStream(IPADIC_RESOURCE);
            
            if (inputStream != null) {
                parseIPADICJSON(inputStream);
                System.out.println("IPADIC dictionary loaded: " + dictionary.size() + " entries");
            } else {
                System.out.println("No IPADIC resource found, using fallback dictionary");
                loadFallbackDictionary();
            }
        } catch (Exception e) {
            System.err.println("Failed to load IPADIC dictionary: " + e.getMessage());
            e.printStackTrace();
            loadFallbackDictionary();
        }
    }

    /**
     * Load user-registered dictionary from file
     */
    private void loadUserDictionary() {
        if (!Files.exists(dictionaryPath)) {
            return;
        }

        try {
            String content = new String(Files.readAllBytes(dictionaryPath), "UTF-8");
            parseUserDictionaryJSON(content);
            System.out.println("User dictionary loaded");
        } catch (Exception e) {
            System.err.println("Failed to load user dictionary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parse IPADIC JSON format
     */
    private void parseIPADICJSON(InputStream input) {
        // TODO: Implement proper JSON parsing using Gson/Jackson
        // For now, this is a placeholder that can be extended
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                // Simple parsing - will be replaced with proper JSON library
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading IPADIC: " + e.getMessage());
        }
    }

    /**
     * Parse user dictionary JSON format
     */
    private void parseUserDictionaryJSON(String json) {
        // TODO: Implement proper JSON parsing using Gson/Jackson
        // Format: {"entries": [{"romaji": "...", "kanji": "...", "hiragana": "..."}]}
    }

    /**
     * Load fallback dictionary with minimal common entries
     */
    private void loadFallbackDictionary() {
        // Minimal fallback entries for testing and basic functionality
        // Users should register their own entries via /romaji dictionary
        
        addEntry("arigatou", 
            Arrays.asList(
                new ConversionCandidate("ありがとう", "ありがとう", 100)
            ), 0);
        
        addEntry("konnichiwa",
            Arrays.asList(
                new ConversionCandidate("こんにちは", "こんにちは", 100)
            ), 0);
        
        addEntry("sayounara",
            Arrays.asList(
                new ConversionCandidate("さようなら", "さようなら", 100)
            ), 0);
        
        System.out.println("Fallback dictionary loaded: " + dictionary.size() + " entries");
    }

    /**
     * Add an entry to the dictionary
     */
    public void addEntry(String romaji, List<ConversionCandidate> candidates, int userPriority) {
        if (romaji == null || candidates == null || candidates.isEmpty()) {
            return;
        }

        String key = romaji.toLowerCase();
        DictionaryEntry entry = new DictionaryEntry(key, candidates, userPriority);
        dictionary.put(key, entry);
    }

    /**
     * Register a user-defined conversion
     */
    public void registerUserEntry(String romaji, String kanji, String hiragana) {
        if (romaji == null || kanji == null) {
            return;
        }

        String key = romaji.toLowerCase();
        DictionaryEntry existing = dictionary.get(key);

        if (existing != null) {
            // Update existing entry
            ConversionCandidate newCandidate = new ConversionCandidate(kanji, hiragana, 1000);
            existing.addCandidate(newCandidate);
        } else {
            // Create new user entry
            ConversionCandidate candidate = new ConversionCandidate(kanji, hiragana, 1000);
            addEntry(romaji, Arrays.asList(candidate), 1);
        }

        // Save to file
        saveUserDictionary();
    }

    /**
     * Get the best conversion candidate for a romaji input
     */
    public ConversionCandidate getBestCandidate(String romaji) {
        if (romaji == null || romaji.isEmpty()) {
            return null;
        }

        String key = romaji.toLowerCase();
        DictionaryEntry entry = dictionary.get(key);
        
        if (entry == null) {
            return null;
        }

        ConversionCandidate best = entry.getBestCandidate(stats);
        
        // Record usage
        if (best != null) {
            stats.recordUsage(romaji, best.kanji);
        }
        
        return best;
    }

    /**
     * Get all candidates for a romaji input (for debugging/advanced features)
     */
    public List<ConversionCandidate> getCandidates(String romaji) {
        if (romaji == null || romaji.isEmpty()) {
            return Collections.emptyList();
        }

        String key = romaji.toLowerCase();
        DictionaryEntry entry = dictionary.get(key);
        
        if (entry == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(entry.candidates);
    }

    /**
     * Check if a romaji entry exists
     */
    public boolean contains(String romaji) {
        return romaji != null && dictionary.containsKey(romaji.toLowerCase());
    }

    /**
     * Get dictionary size
     */
    public int size() {
        return dictionary.size();
    }

    /**
     * Save user dictionary to file
     */
    public void saveUserDictionary() {
        try {
            Files.createDirectories(dictionaryPath.getParent());
            
            // Filter user entries only
            StringBuilder json = new StringBuilder();
            json.append("{\"entries\": [");
            
            boolean first = true;
            for (DictionaryEntry entry : dictionary.values()) {
                if (entry.userPriority > 0) {
                    if (!first) json.append(",");
                    first = false;
                    
                    json.append("{\"romaji\": \"").append(entry.romaji).append("\", ")
                        .append("\"candidates\": [");
                    
                    boolean firstCand = true;
                    for (ConversionCandidate cand : entry.candidates) {
                        if (!firstCand) json.append(",");
                        firstCand = false;
                        
                        json.append("{\"kanji\": \"").append(cand.kanji).append("\", ")
                            .append("\"hiragana\": \"").append(cand.hiragana).append("\"}");
                    }
                    
                    json.append("]}");
                }
            }
            
            json.append("]}");
            Files.write(dictionaryPath, json.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            System.err.println("Failed to save user dictionary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get all dictionary entries (for debugging)
     */
    public Collection<DictionaryEntry> getAllEntries() {
        return Collections.unmodifiableCollection(dictionary.values());
    }

    /**
     * Clear all user-registered entries
     */
    public void clearUserEntries() {
        dictionary.entrySet().removeIf(entry -> entry.getValue().userPriority > 0);
        saveUserDictionary();
    }
}
