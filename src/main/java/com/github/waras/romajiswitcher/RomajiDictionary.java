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
    private final GoogleIMEClient googleIME;
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
        this.googleIME = new GoogleIMEClient();
        
        // Load dictionaries
        loadIPADICDictionary();
        loadUserDictionary();
    }

    /**
     * Load IPADIC base dictionary from resources
     */
    private void loadIPADICDictionary() {
        try {
            // Load IPADIC resource - this is required
            InputStream inputStream = RomajiDictionary.class.getClassLoader()
                    .getResourceAsStream(IPADIC_RESOURCE);
            
            if (inputStream != null) {
                parseIPADICJSON(inputStream);
                System.out.println("IPADIC dictionary loaded: " + dictionary.size() + " entries");
            } else {
                throw new IOException("IPADIC resource not found: " + IPADIC_RESOURCE);
            }
        } catch (Exception e) {
            System.err.println("FATAL: Failed to load IPADIC dictionary: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize IPADIC dictionary", e);
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
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();
            
            String json = jsonContent.toString();
            parseIPADICJsonContent(json);
        } catch (IOException e) {
            System.err.println("Error reading IPADIC JSON: " + e.getMessage());
        }
    }

    /**
     * Parse IPADIC JSON content (simple JSON parsing)
     */
    private void parseIPADICJsonContent(String json) {
        try {
            int loadedCount = 0;
            
            // Simple JSON parsing - look for entry objects
            String[] entries = json.split("\\{");
            for (String entry : entries) {
                if (!entry.contains("\"kanji\"")) continue;
                
                String kanji = extractJsonValue(entry, "kanji");
                String hiragana = extractJsonValue(entry, "hiragana");
                String baseScoreStr = extractJsonValue(entry, "baseScore");
                
                if (!kanji.isEmpty() && !hiragana.isEmpty()) {
                    int baseScore = 100;
                    try {
                        baseScore = Integer.parseInt(baseScoreStr);
                    } catch (NumberFormatException e) {
                        // Use default
                    }
                    
                    // Create romaji from hiragana (for matching with user input)
                    String romaji = hiraganaToRomaji(hiragana).toLowerCase();
                    
                    List<ConversionCandidate> candidates = Arrays.asList(
                        new ConversionCandidate(kanji, hiragana, baseScore)
                    );
                    addEntry(romaji, candidates, 0);
                    loadedCount++;
                }
            }
            
            System.out.println("IPADIC dictionary: " + loadedCount + " entries loaded");
        } catch (Exception e) {
            System.err.println("Error parsing IPADIC JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extract JSON value (simple implementation)
     */
    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        try {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            // Fall through
        }
        return "";
    }

    /**
     * Simple hiragana to romaji converter (basic implementation)
     */
    private String hiraganaToRomaji(String hiragana) {
        // This is a simplified version - for production, use proper kana-to-romaji library
        // For now, we'll use a basic mapping
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("あ", "a"); map.put("い", "i"); map.put("う", "u"); map.put("え", "e"); map.put("お", "o");
        map.put("か", "ka"); map.put("き", "ki"); map.put("く", "ku"); map.put("け", "ke"); map.put("こ", "ko");
        map.put("が", "ga"); map.put("ぎ", "gi"); map.put("ぐ", "gu"); map.put("げ", "ge"); map.put("ご", "go");
        map.put("さ", "sa"); map.put("し", "shi"); map.put("す", "su"); map.put("せ", "se"); map.put("そ", "so");
        map.put("ざ", "za"); map.put("じ", "ji"); map.put("ず", "zu"); map.put("ぜ", "ze"); map.put("ぞ", "zo");
        map.put("た", "ta"); map.put("ち", "chi"); map.put("つ", "tsu"); map.put("て", "te"); map.put("と", "to");
        map.put("だ", "da"); map.put("ぢ", "di"); map.put("づ", "du"); map.put("で", "de"); map.put("ど", "do");
        map.put("な", "na"); map.put("に", "ni"); map.put("ぬ", "nu"); map.put("ね", "ne"); map.put("の", "no");
        map.put("は", "ha"); map.put("ひ", "hi"); map.put("ふ", "fu"); map.put("へ", "he"); map.put("ほ", "ho");
        map.put("ば", "ba"); map.put("び", "bi"); map.put("ぶ", "bu"); map.put("べ", "be"); map.put("ぼ", "bo");
        map.put("ぱ", "pa"); map.put("ぴ", "pi"); map.put("ぷ", "pu"); map.put("ぺ", "pe"); map.put("ぽ", "po");
        map.put("ま", "ma"); map.put("み", "mi"); map.put("む", "mu"); map.put("め", "me"); map.put("も", "mo");
        map.put("や", "ya"); map.put("ゆ", "yu"); map.put("よ", "yo");
        map.put("ら", "ra"); map.put("り", "ri"); map.put("る", "ru"); map.put("れ", "re"); map.put("ろ", "ro");
        map.put("わ", "wa"); map.put("ゐ", "wi"); map.put("ゑ", "we"); map.put("を", "wo"); map.put("ん", "n");
        
        StringBuilder romaji = new StringBuilder();
        for (char c : hiragana.toCharArray()) {
            String ch = String.valueOf(c);
            romaji.append(map.getOrDefault(ch, ch));
        }
        return romaji.toString();
    }

    /**
     * Parse user dictionary JSON format
     */
    private void parseUserDictionaryJSON(String json) {
        // TODO: Implement proper JSON parsing using Gson/Jackson
        // Format: {"entries": [{"romaji": "...", "kanji": "...", "hiragana": "..."}]}
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
     * Uses IPADIC first, then tries Google IME API for better kanji conversion
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
        
        // Try to enhance with Google IME API if available
        if (best != null && best.hiragana != null) {
            String enhancedKanji = googleIME.convert(best.hiragana);
            // If Google IME provided a different result, use it
            if (enhancedKanji != null && !enhancedKanji.equals(best.hiragana)) {
                best = new ConversionCandidate(enhancedKanji, best.hiragana, best.baseScore);
            }
        }
        
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
