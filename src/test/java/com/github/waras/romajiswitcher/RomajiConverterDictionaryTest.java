package com.github.waras.romajiswitcher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the new dictionary-based conversion system
 */
public class RomajiConverterDictionaryTest {

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
    void testConvertWordWithDictionaryBasic() {
        RomajiConverter.ConversionResult result = 
            RomajiConverter.convertWordWithDictionary("arigatou", dictionary, stats);

        assertNotNull(result);
        assertNotNull(result.japanese);
        assertFalse(result.japanese.isEmpty());
        assertEquals("arigatou", result.originalRomaji.toLowerCase());
    }

    @Test
    void testConvertWordWithDictionaryKanji() {
        RomajiConverter.ConversionResult result = 
            RomajiConverter.convertWordWithDictionary("arigatou", dictionary, stats);

        // Should prefer kanji representation
        assertTrue(result.japanese.contains("有難う") || result.japanese.contains("ありがとう"));
    }

    @Test
    void testConvertWordWithDictionaryNonExistent() {
        RomajiConverter.ConversionResult result = 
            RomajiConverter.convertWordWithDictionary("nonexistentword123", dictionary, stats);

        assertNotNull(result);
        // Should fallback to original system
    }

    @Test
    void testConvertWordWithDictionaryNullDictionary() {
        // Should fallback to original convertWord
        RomajiConverter.ConversionResult result = 
            RomajiConverter.convertWordWithDictionary("arigatou", null, null);

        assertNotNull(result);
        assertNotNull(result.japanese);
    }

    @Test
    void testConvertWithDictionaryFullSentence() {
        RomajiConverter.ConversionResult result = 
            RomajiConverter.convertWithDictionary("arigatou sugoi", dictionary, stats);

        assertNotNull(result);
        assertNotNull(result.japanese);
        assertFalse(result.japanese.isEmpty());
    }

    @Test
    void testConvertWithDictionaryPreservesSpaces() {
        RomajiConverter.ConversionResult result = 
            RomajiConverter.convertWithDictionary("hello world", dictionary, stats);

        assertNotNull(result);
        assertTrue(result.japanese.contains(" "));
    }

    @Test
    void testConvertWithDictionaryMixedWords() {
        RomajiConverter.ConversionResult result = 
            RomajiConverter.convertWithDictionary("konnichiwa kawaii", dictionary, stats);

        assertNotNull(result);
        assertFalse(result.japanese.isEmpty());
    }

    @Test
    void testConvertWordCaseInsensitivity() {
        RomajiConverter.ConversionResult result1 = 
            RomajiConverter.convertWordWithDictionary("ARIGATOU", dictionary, stats);
        RomajiConverter.ConversionResult result2 = 
            RomajiConverter.convertWordWithDictionary("arigatou", dictionary, stats);

        assertNotNull(result1);
        assertNotNull(result2);
        // Both should produce equivalent results (though case may differ in original)
        assertEquals(result1.japanese, result2.japanese);
    }

    @Test
    void testUsageRecordingWithDictionary() {
        // Ensure stats are initially empty for sugoi
        assertEquals(0, stats.getStatsForRomaji("sugoi").size());
        
        // Get candidate (should record usage)
        RomajiConverter.convertWordWithDictionary("sugoi", dictionary, stats);
        
        // Check that usage was recorded
        Map<String, ConversionStats.UsageInfo> romajiStats = stats.getStatsForRomaji("sugoi");
        assertTrue(romajiStats.size() > 0);
    }

    @Test
    void testConvertWithUserDictionary() {
        // Register a user entry
        dictionary.registerUserEntry("tesuto", "テスト", "てすと");

        // Convert using the user entry
        RomajiConverter.ConversionResult result = 
            RomajiConverter.convertWordWithDictionary("tesuto", dictionary, stats);

        assertNotNull(result);
        assertEquals("テスト", result.japanese);
    }

    @Test
    void testEmptyStringHandling() {
        RomajiConverter.ConversionResult result = 
            RomajiConverter.convertWordWithDictionary("", dictionary, stats);

        assertNotNull(result);
        assertEquals("", result.japanese);
        assertEquals("", result.originalRomaji);
    }

    @Test
    void testNullInputHandling() {
        RomajiConverter.ConversionResult result = 
            RomajiConverter.convertWordWithDictionary(null, dictionary, stats);

        assertNotNull(result);
        assertEquals("", result.japanese);
        assertEquals("", result.originalRomaji);
    }

    @Test
    void testBackwardCompatibilityWithOriginalConvert() {
        // Ensure original convert method still works
        RomajiConverter.ConversionResult originalResult = 
            RomajiConverter.convert("arigatou");
        RomajiConverter.ConversionResult dictionaryResult = 
            RomajiConverter.convertWithDictionary("arigatou", dictionary, stats);

        assertNotNull(originalResult);
        assertNotNull(dictionaryResult);

        // Both should produce valid Japanese text
        assertFalse(originalResult.japanese.isEmpty());
        assertFalse(dictionaryResult.japanese.isEmpty());
    }
}
