package com.github.waras.romajiswitcher;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RomajiConverterTest {

    @Test
    public void testBasicConversion() {
        String result = RomajiConverter.convert("aiueo");
        // Output to debug
        System.err.println("Test result for 'aiueo': " + result);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testSingleA() {
        String result = RomajiConverter.convert("a");
        assertNotNull(result);
        assertTrue(result.contains("あ"));
    }

    @Test
    public void testKonnichiwa() {
        String result = RomajiConverter.convert("konnichiwa");
        assertNotNull(result);
        assertNotEquals("", result);
    }

    @Test
    public void testContainsRomaji() {
        assertTrue(RomajiConverter.containsRomaji("aiueo"));
        assertTrue(RomajiConverter.containsRomaji("a"));
    }

    @Test
    public void testEmptyString() {
        String result = RomajiConverter.convert("");
        assertEquals("", result);
    }

    @Test
    public void testMixedText() {
        String result = RomajiConverter.convert("hello world");
        assertNotNull(result);
    }
}


