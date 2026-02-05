package com.github.waras.romajiswitcher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RomajiDictionary
 */
public class RomajiDictionaryTest {

    private Path tempDir;
    private ConversionStats stats;
    private RomajiDictionary dictionary;

    @BeforeEach
    void setUp(@TempDir Path tmpDir) {
        tempDir = tmpDir;
        stats = new ConversionStats(tempDir);
        dictionary = new RomajiDictionary(tempDir, stats);
    }

    @Test
    void testDictionaryInitialization() {
        assertNotNull(dictionary);
        // IPADIC should have loaded entries
        assertTrue(dictionary.size() >= 15);
        System.out.println("Dictionary size: " + dictionary.size());
    }

    @Test
    void testFallbackDictionaryEntries() {
        // Check that IPADIC entries are loaded
        assertTrue(dictionary.contains("sugoi"));  // すごい (凄い)
        assertTrue(dictionary.contains("kawaii")); // かわいい (可愛い)
        assertTrue(dictionary.contains("atarashii")); // あたらしい (新しい)
    }

    @Test
    void testGetBestCandidate() {
        ConversionCandidate candidate = dictionary.getBestCandidate("sugoi");
        
        assertNotNull(candidate);
        assertEquals("凄い", candidate.kanji);
    }

    @Test
    void testGetCandidates() {
        List<ConversionCandidate> candidates = dictionary.getCandidates("arigatou");
        
        assertNotNull(candidates);
        // With the kanji dictionary, there should be at least one candidate
        assertTrue(candidates.size() >= 1);
    }

    @Test
    void testGetCandidatesNonExistent() {
        List<ConversionCandidate> candidates = dictionary.getCandidates("nonexistent");
        
        assertNotNull(candidates);
        assertTrue(candidates.isEmpty());
    }

    @Test
    void testContains() {
        assertTrue(dictionary.contains("arigatou"));
        assertTrue(dictionary.contains("ARIGATOU")); // Case insensitive
        assertFalse(dictionary.contains("nonexistent"));
    }

    @Test
    void testRegisterUserEntry() {
        String romaji = "testword";
        
        assertFalse(dictionary.contains(romaji));
        
        dictionary.registerUserEntry(romaji, "テスト", "てすと");
        
        assertTrue(dictionary.contains(romaji));
        
        ConversionCandidate candidate = dictionary.getBestCandidate(romaji);
        assertNotNull(candidate);
        assertEquals("テスト", candidate.kanji);
    }

    @Test
    void testUserEntryPriority() {
        dictionary.registerUserEntry("sugoi", "スゴイ", "すごい");
        
        ConversionCandidate candidate = dictionary.getBestCandidate("sugoi");
        
        // User entry should be prioritized
        assertEquals("スゴイ", candidate.kanji);
    }

    @Test
    void testAddEntry() {
        List<ConversionCandidate> candidates = Arrays.asList(
            new ConversionCandidate("新規", "しんき", 100)
        );
        
        dictionary.addEntry("shinki", candidates, 0);
        
        assertTrue(dictionary.contains("shinki"));
        ConversionCandidate candidate = dictionary.getBestCandidate("shinki");
        assertEquals("新規", candidate.kanji);
    }

    @Test
    void testUsageRecordingOnGetBestCandidate() {
        // Ensure stats are initially empty
        assertEquals(0, stats.getStatsForRomaji("arigatou").size());
        
        // Get candidate (should record usage)
        dictionary.getBestCandidate("arigatou");
        
        // Check that usage was recorded
        Map<String, ConversionStats.UsageInfo> romajiStats = stats.getStatsForRomaji("arigatou");
        assertTrue(romajiStats.size() > 0);
    }

    @Test
    void testMultipleCandidateScoring() {
        // Register a test entry with multiple uses
        for (int i = 0; i < 5; i++) {
            dictionary.getBestCandidate("sugoi");
        }
        
        // Best candidate should still be selected
        ConversionCandidate best = dictionary.getBestCandidate("sugoi");
        assertNotNull(best);
        assertEquals("凄い", best.kanji);
    }

    @Test
    void testCaseInsensitivity() {
        ConversionCandidate candidate1 = dictionary.getBestCandidate("ARIGATOU");
        ConversionCandidate candidate2 = dictionary.getBestCandidate("arigatou");
        ConversionCandidate candidate3 = dictionary.getBestCandidate("AriGaTou");
        
        assertNotNull(candidate1);
        assertNotNull(candidate2);
        assertNotNull(candidate3);
        
        assertEquals(candidate1.kanji, candidate2.kanji);
        assertEquals(candidate2.kanji, candidate3.kanji);
    }

    @Test
    void testGetAllEntries() {
        Collection<DictionaryEntry> entries = dictionary.getAllEntries();
        
        assertNotNull(entries);
        assertTrue(entries.size() > 0);
    }

    @Test
    void testClearUserEntries() {
        int initialSize = dictionary.size();
        
        dictionary.registerUserEntry("user1", "ユーザー1", "ゆーざー1");
        dictionary.registerUserEntry("user2", "ユーザー2", "ゆーざー2");
        
        assertTrue(dictionary.size() > initialSize);
        
        dictionary.clearUserEntries();
        
        assertEquals(initialSize, dictionary.size());
    }

    @Test
    void testNullHandling() {
        assertNull(dictionary.getBestCandidate(null));
        assertNull(dictionary.getBestCandidate(""));
        assertTrue(dictionary.getCandidates(null).isEmpty());
        assertFalse(dictionary.contains(null));
    }
}
