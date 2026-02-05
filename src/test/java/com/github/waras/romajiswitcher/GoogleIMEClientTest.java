package com.github.waras.romajiswitcher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GoogleIMEClient
 */
public class GoogleIMEClientTest {
    
    private GoogleIMEClient client;
    
    @BeforeEach
    void setUp() {
        client = new GoogleIMEClient();
    }
    
    @Test
    void testCacheWorks() {
        String hiragana = "ありがとう";
        
        // First call - API
        String result1 = client.convert(hiragana);
        assertNotNull(result1);
        int cacheSize1 = client.getCacheSize();
        
        // Second call - should use cache
        String result2 = client.convert(hiragana);
        assertNotNull(result2);
        int cacheSize2 = client.getCacheSize();
        
        // Cache size should be the same
        assertEquals(cacheSize1, cacheSize2);
    }
    
    @Test
    void testDisable() {
        client.disable();
        String result = client.convert("ありがとう");
        
        // When disabled, should return original
        assertEquals("ありがとう", result);
    }
    
    @Test
    void testNullHandling() {
        assertNull(client.convert(null));
        assertTrue(client.getCandidates(null).isEmpty());
    }
    
    @Test
    void testClearCache() {
        client.convert("ありがとう");
        assertTrue(client.getCacheSize() > 0);
        
        client.clearCache();
        assertEquals(0, client.getCacheSize());
    }
}
