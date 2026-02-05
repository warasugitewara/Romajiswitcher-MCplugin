package com.github.waras.romajiswitcher;

import java.util.*;

/**
 * Represents a single entry in the romanization dictionary.
 * Each entry can have multiple conversion candidates with different priorities.
 */
public class DictionaryEntry {
    public final String romaji;
    public final List<ConversionCandidate> candidates;
    public final int userPriority;

    /**
     * @param romaji The romanized input (lowercase)
     * @param candidates List of possible conversion candidates
     * @param userPriority Priority for user-registered entries (0 = system entry, > 0 = user entry)
     */
    public DictionaryEntry(String romaji, List<ConversionCandidate> candidates, int userPriority) {
        this.romaji = romaji.toLowerCase();
        this.candidates = candidates != null ? new ArrayList<>(candidates) : new ArrayList<>();
        this.userPriority = userPriority;
    }

    /**
     * Get the best candidate based on current scores
     */
    public ConversionCandidate getBestCandidate(ConversionStats stats) {
        if (candidates.isEmpty()) {
            return null;
        }
        
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        // Sort by score (highest first)
        ConversionCandidate best = candidates.get(0);
        double bestScore = best.calculateScore(this.userPriority, stats);

        for (ConversionCandidate candidate : candidates) {
            double score = candidate.calculateScore(this.userPriority, stats);
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        return best;
    }

    /**
     * Add a new candidate or update existing one
     */
    public void addCandidate(ConversionCandidate candidate) {
        // Remove if already exists
        candidates.removeIf(c -> c.kanji.equals(candidate.kanji));
        candidates.add(candidate);
    }

    @Override
    public String toString() {
        return String.format("DictionaryEntry{romaji='%s', candidates=%d, userPriority=%d}", 
            romaji, candidates.size(), userPriority);
    }
}
