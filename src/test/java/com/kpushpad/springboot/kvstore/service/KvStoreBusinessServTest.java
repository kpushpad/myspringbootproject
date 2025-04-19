package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.model.CacheEntry;
import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Nested
class KvStoreBusinessServTest {

    @Mock
    private KvStoreService<String, String> kvStoreService;

    @Mock
    private CacheAOFLogService cacheAOFLogService;

    private KvStoreBusinessServ<String, String> kvStoreBusinessServ;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kvStoreBusinessServ = new KvStoreBusinessServ<>(kvStoreService, cacheAOFLogService);
    }

    @Test
    void testGet() {
        String key = "testKey";
        String expectedValue = "testValue";
        when(kvStoreService.get(key)).thenReturn(expectedValue);

        String result = kvStoreBusinessServ.get(key);

        assertEquals(expectedValue, result);
        verify(kvStoreService).get(key);
    }

    @Test
    void testPutSuccess() throws IOException {
        String key = "testKey";
        String value = "testValue";
        Long ttl = 1000L;

        boolean result = kvStoreBusinessServ.put(key, value, ttl);

        assertTrue(result);
        verify(kvStoreService).put(any());
        verify(kvStoreService).add(any());
        verify(cacheAOFLogService).recordPutCommand(any());
    }

    @Test
    void testPutFailure() {
        String key = "testKey";
        String value = "testValue";
        Long ttl = 1000L;

        doThrow(new RuntimeException("Test exception")).when(kvStoreService).put(any());

        boolean result = kvStoreBusinessServ.put(key, value, ttl);

        assertFalse(result);
    }

    @Test
    void testDelete() throws IOException {
        String key = "testKey";
        String expectedValue = "testValue";
        when(kvStoreService.delete(key)).thenReturn(expectedValue);

        String result = kvStoreBusinessServ.delete(key);

        assertEquals(expectedValue, result);
        verify(kvStoreService).delete(key);
        verify(cacheAOFLogService).recordDelCommand(any());
    }

    @Test
    void testFillData() {
        Map<String, CacheEntry<String, String>> testMap = new HashMap<>();
        CacheEntry<String, String> entry1 = new CacheEntry<>("key1",
                new ValueWithTTL<>("value1", System.currentTimeMillis() + 1000));
        CacheEntry<String, String> entry2 = new CacheEntry<>("key2",
                new ValueWithTTL<>("value2", System.currentTimeMillis() - 1000));

        testMap.put("key1", entry1);
        testMap.put("key2", entry2);

        when(kvStoreService.getSize()).thenReturn(1);

        Integer result = kvStoreBusinessServ.fillData(testMap);

        verify(kvStoreService).setKeyMap(testMap);
        verify(kvStoreService).removeKey("key2");
        verify(kvStoreService).add(entry1);
        assertEquals(1, result);
    }

    @Test
    void testGetAllKeys() {
        // Note: This test would depend on the actual implementation of getAllKeys method
        // which seems to be incomplete in the provided code snippet
    }
}

