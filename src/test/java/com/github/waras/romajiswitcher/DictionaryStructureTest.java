package com.github.waras.romajiswitcher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the new dictionary structure
 */
public class DictionaryStructureTest {

    private Path tempDir;

    @BeforeEach
    void setUp(@TempDir Path tmpDir) {
        tempDir = tmpDir;
    }

    @Test
    void testConversionCandidateCreation() {
        ConversionCandidate candidate = new ConversionCandidate("有難う", "ありがとう", 100);
        
        assertNotNull(candidate);
        assertEquals("有難う", candidate.kanji);
        assertEquals("ありがとう", candidate.hiragana);
        assertEquals(100, candidate.baseScore);
        assertEquals("有難う", candidate.getBestRepresentation());
    }

    @Test
    void testConversionCandidatePreferKanji() {
        ConversionCandidate candidate1 = new ConversionCandidate("漢字", "かんじ", 100);
        assertEquals("漢字", candidate1.getBestRepresentation());

        ConversionCandidate candidate2 = new ConversionCandidate("", "かんじ", 100);
        assertEquals("かんじ", candidate2.getBestRepresentation());
    }

    @Test
    void testConversionCandidateScoreCalculation() {
        ConversionCandidate candidate = new ConversionCandidate("有難う", "ありがとう", 100);
        
        // System entry (userPriority = 0)
        double scoreSystem = candidate.calculateScore(0, null);
        assertEquals(10000, scoreSystem); // 100 * 100 + 0 (no stats)

        // User entry (userPriority = 1)
        double scoreUser = candidate.calculateScore(1, null);
        assertEquals(10000, scoreUser); // 10000 + 0 (no stats)
    }

    @Test
    void testDictionaryEntryCreation() {
        List<ConversionCandidate> candidates = Arrays.asList(
            new ConversionCandidate("有難う", "ありがとう", 100),
            new ConversionCandidate("ありがとう", "ありがとう", 50)
        );
        
        DictionaryEntry entry = new DictionaryEntry("arigatou", candidates, 0);
        
        assertNotNull(entry);
        assertEquals("arigatou", entry.romaji);
        assertEquals(2, entry.candidates.size());
        assertEquals(0, entry.userPriority);
    }

    @Test
    void testDictionaryEntryGetBestCandidate() {
        List<ConversionCandidate> candidates = Arrays.asList(
            new ConversionCandidate("有難う", "ありがとう", 100),
            new ConversionCandidate("ありがとう", "ありがとう", 50)
        );
        
        DictionaryEntry entry = new DictionaryEntry("arigatou", candidates, 0);
        ConversionCandidate best = entry.getBestCandidate(null);
        
        assertNotNull(best);
        assertEquals("有難う", best.kanji); // Higher baseScore
    }

    @Test
    void testDictionaryEntryAddCandidate() {
        DictionaryEntry entry = new DictionaryEntry("arigatou", new ArrayList<>(), 0);
        
        ConversionCandidate cand1 = new ConversionCandidate("有難う", "ありがとう", 100);
        entry.addCandidate(cand1);
        
        assertEquals(1, entry.candidates.size());
        
        // Add another
        ConversionCandidate cand2 = new ConversionCandidate("ありがとう", "ありがとう", 50);
        entry.addCandidate(cand2);
        
        assertEquals(2, entry.candidates.size());
        
        // Update existing (should not duplicate)
        ConversionCandidate cand1Updated = new ConversionCandidate("有難う", "ありがとうございます", 120);
        entry.addCandidate(cand1Updated);
        
        assertEquals(2, entry.candidates.size());
    }

    @Test
    void testConversionStatsRecordUsage() {
        ConversionStats stats = new ConversionStats(tempDir);
        
        stats.recordUsage("arigatou", "有難う");
        stats.recordUsage("arigatou", "有難う");
        stats.recordUsage("arigatou", "ありがとう");
        
        Map<String, ConversionStats.UsageInfo> romajiStats = stats.getStatsForRomaji("arigatou");
        
        assertEquals(2, romajiStats.size());
        assertEquals(2, romajiStats.get("有難う").count);
        assertEquals(1, romajiStats.get("ありがとう").count);
    }

    @Test
    void testConversionStatsUsageScore() {
        ConversionStats stats = new ConversionStats(tempDir);
        
        stats.recordUsage("arigatou", "有難う");
        stats.recordUsage("arigatou", "有難う");
        stats.recordUsage("arigatou", "有難う");
        stats.recordUsage("arigatou", "有難う");
        
        double score = stats.getUsageScore("arigatou", "有難う");
        
        // log2(4 + 1) × 10 ≈ log2(5) × 10 ≈ 2.32 × 10 ≈ 23.2
        assertTrue(score > 20 && score < 25);
    }

    @Test
    void testConversionStatsClear() {
        ConversionStats stats = new ConversionStats(tempDir);
        
        stats.recordUsage("arigatou", "有難う");
        assertEquals(1, stats.getStatsForRomaji("arigatou").size());
        
        stats.clear();
        assertEquals(0, stats.getStatsForRomaji("arigatou").size());
    }

    @Test
    void testUserEntryPriorityScore() {
        ConversionCandidate candidate = new ConversionCandidate("ユーザー", "ゆーざー", 10);
        
        // System entry (userPriority = 0): 10 * 100 = 1000
        double scoreSystem = candidate.calculateScore(0, null);
        assertEquals(1000, scoreSystem);

        // User entry (userPriority = 1): 10000
        double scoreUser = candidate.calculateScore(1, null);
        assertEquals(10000, scoreUser);
        
        // User entry always wins
        assertTrue(scoreUser > scoreSystem);
    }
}
