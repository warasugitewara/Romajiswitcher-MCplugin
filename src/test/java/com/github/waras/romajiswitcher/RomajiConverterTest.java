package com.github.waras.romajiswitcher;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RomajiConverterTest {

    @Test
    public void testBasicConversion() {
        RomajiConverter.ConversionResult result = RomajiConverter.convert("aiueo");
        assertNotNull(result);
        assertFalse(result.japanese.isEmpty());
        assertEquals("あいうえお(aiueo)", result.getFormattedText());
    }

    @Test
    public void testSingleA() {
        RomajiConverter.ConversionResult result = RomajiConverter.convert("a");
        assertNotNull(result);
        assertTrue(result.japanese.contains("あ"));
    }

    @Test
    public void testKonnichiwa() {
        RomajiConverter.ConversionResult result = RomajiConverter.convert("konnichiwa");
        assertNotNull(result);
        assertFalse(result.japanese.isEmpty());
    }

    @Test
    public void testEmptyString() {
        RomajiConverter.ConversionResult result = RomajiConverter.convert("");
        assertEquals("", result.japanese);
    }

    @Test
    public void testMixedText() {
        RomajiConverter.ConversionResult result = RomajiConverter.convert("hello world");
        assertNotNull(result);
    }

    @Test
    public void testNParticle() {
        RomajiConverter.ConversionResult result = RomajiConverter.convertWord("san");
        assertEquals("さん", result.japanese);
    }

    @Test
    public void testStandaloneN() {
        RomajiConverter.ConversionResult result = RomajiConverter.convertWord("n");
        assertEquals("ん", result.japanese);
    }

    @Test
    public void testContainsFullWidthJapaneseWithKanji() {
        assertTrue(RomajiConverter.containsFullWidthJapanese("俺今日valoするからゲームできん"));
    }

    @Test
    public void testContainsFullWidthJapaneseWithHiragana() {
        assertTrue(RomajiConverter.containsFullWidthJapanese("これはひらがなです"));
    }

    @Test
    public void testContainsFullWidthJapaneseWithKatakana() {
        assertTrue(RomajiConverter.containsFullWidthJapanese("これはカタカナです"));
    }

    @Test
    public void testDoesNotContainFullWidthJapanese() {
        assertFalse(RomajiConverter.containsFullWidthJapanese("hello world"));
        assertFalse(RomajiConverter.containsFullWidthJapanese("romaji text"));
    }

    @Test
    public void testContainsFullWidthJapaneseMixed() {
        assertTrue(RomajiConverter.containsFullWidthJapanese("mix 日本語 and romaji"));
    }
}

