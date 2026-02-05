package com.github.waras.romajiswitcher;

/**
 * Represents a single conversion candidate for a romanized word.
 * Contains multiple representations and scoring information.
 */
public class ConversionCandidate {
    public final String kanji;
    public final String hiragana;
    public final int baseScore;

    /**
     * @param kanji The kanji representation (e.g., "有難う")
     * @param hiragana The hiragana representation (e.g., "ありがとう")
     * @param baseScore Base priority score from IPADIC (higher = more common)
     */
    public ConversionCandidate(String kanji, String hiragana, int baseScore) {
        this.kanji = kanji;
        this.hiragana = hiragana;
        this.baseScore = baseScore;
    }

    /**
     * Calculate final score for this candidate based on user priority and usage statistics
     * 
     * Score calculation:
     * - User-registered entries: 10000 + usage_score
     * - System entries: (baseScore × 100) + usage_score
     * - usage_score = log2(count + 1) × 10
     */
    public double calculateScore(int userPriority, ConversionStats stats) {
        double usageScore = 0;
        
        if (stats != null) {
            usageScore = stats.getUsageScore(this);
        }

        if (userPriority > 0) {
            return 10000 + usageScore;
        } else {
            return (baseScore * 100) + usageScore;
        }
    }

    /**
     * Get the best representation (prefer kanji if available, otherwise hiragana)
     */
    public String getBestRepresentation() {
        return kanji != null && !kanji.isEmpty() ? kanji : hiragana;
    }

    @Override
    public String toString() {
        return String.format("ConversionCandidate{kanji='%s', hiragana='%s', baseScore=%d}", 
            kanji, hiragana, baseScore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversionCandidate)) return false;
        ConversionCandidate that = (ConversionCandidate) o;
        return kanji.equals(that.kanji);
    }

    @Override
    public int hashCode() {
        return kanji.hashCode();
    }
}
