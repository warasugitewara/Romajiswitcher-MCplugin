package com.github.waras.romajiswitcher;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client for Google CGI API for Japanese Input (仮名漢字変換)
 * Converts hiragana to kanji with caching support
 */
public class GoogleIMEClient {
    private static final String API_URL = "http://www.google.com/transliterate";
    private static final String LANG_PAIR = "ja-Hira|ja";
    private static final int TIMEOUT_MS = 3000;
    
    private Map<String, List<String>> cache;
    private boolean enabled;

    public GoogleIMEClient() {
        this.cache = new ConcurrentHashMap<>();
        this.enabled = true;
    }

    /**
     * Convert hiragana to kanji using Google IME API
     * @param hiragana Input hiragana string
     * @return Best kanji conversion or original hiragana if API fails
     */
    public String convert(String hiragana) {
        if (!enabled || hiragana == null || hiragana.isEmpty()) {
            return hiragana;
        }

        // Check cache first
        if (cache.containsKey(hiragana)) {
            List<String> candidates = cache.get(hiragana);
            return candidates.isEmpty() ? hiragana : candidates.get(0);
        }

        try {
            List<String> candidates = fetchFromAPI(hiragana);
            cache.put(hiragana, candidates);
            return candidates.isEmpty() ? hiragana : candidates.get(0);
        } catch (Exception e) {
            System.err.println("Google IME API error: " + e.getMessage());
            cache.put(hiragana, new ArrayList<>()); // Cache the failure
            return hiragana;
        }
    }

    /**
     * Get all conversion candidates for hiragana
     * @param hiragana Input hiragana string
     * @return List of conversion candidates
     */
    public List<String> getCandidates(String hiragana) {
        if (!enabled || hiragana == null || hiragana.isEmpty()) {
            return new ArrayList<>();
        }

        if (cache.containsKey(hiragana)) {
            return new ArrayList<>(cache.get(hiragana));
        }

        try {
            List<String> candidates = fetchFromAPI(hiragana);
            cache.put(hiragana, candidates);
            return candidates;
        } catch (Exception e) {
            System.err.println("Google IME API error: " + e.getMessage());
            cache.put(hiragana, new ArrayList<>());
            return new ArrayList<>();
        }
    }

    /**
     * Fetch conversion candidates from Google IME API
     */
    private List<String> fetchFromAPI(String hiragana) throws IOException {
        String encodedText = URLEncoder.encode(hiragana, StandardCharsets.UTF_8);
        String url = API_URL + "?langpair=" + LANG_PAIR + "&text=" + encodedText;

        try {
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("HTTP " + responseCode);
            }

            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return parseResponse(response.toString());
        } catch (java.net.SocketTimeoutException e) {
            throw new IOException("API timeout: " + e.getMessage());
        }
    }

    /**
     * Parse JSON response from Google IME API
     * Expected format: [["hiragana", ["candidate1", "candidate2", ...]]]
     */
    private List<String> parseResponse(String json) {
        List<String> candidates = new ArrayList<>();
        
        try {
            // Simple JSON parsing - extract candidates from the array
            // Format: [["input",["candidate1","candidate2",...]]]
            int startIdx = json.indexOf("[[");
            if (startIdx == -1) return candidates;
            
            int innerStart = json.indexOf("[\"", startIdx + 2);
            if (innerStart == -1) return candidates;
            
            int endIdx = json.indexOf("]]", innerStart);
            if (endIdx == -1) return candidates;
            
            String candidateStr = json.substring(innerStart + 1, endIdx);
            
            // Parse candidate strings - split by comma but respect quoted strings
            String[] parts = candidateStr.split("\",\"");
            for (String part : parts) {
                String clean = part.replaceAll("^[\"\\[]", "").replaceAll("[\"\\]]$", "").trim();
                if (!clean.isEmpty() && !clean.equals("[")) {
                    candidates.add(clean);
                }
            }
        } catch (Exception e) {
            System.err.println("JSON parse error: " + e.getMessage());
        }
        
        return candidates;
    }

    /**
     * Disable API calls (for testing or error recovery)
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * Enable API calls
     */
    public void enable() {
        this.enabled = true;
    }

    /**
     * Clear the cache
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Get cache size (for testing)
     */
    public int getCacheSize() {
        return cache.size();
    }
}
