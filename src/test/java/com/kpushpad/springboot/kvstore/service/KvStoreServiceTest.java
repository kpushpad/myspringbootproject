package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.kpushpad.springboot.kvstore.model.CacheEntry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class KvStoreServiceTest {

    private KvStoreService<String, String> kvStoreService;
    private CacheEntry<String, String> testEntry1;
    private CacheEntry<String, String> testEntry2;

    @BeforeEach
    void setUp() {
        kvStoreService = new KvStoreService<>();
        testEntry1 = new CacheEntry<>("key1",
                new ValueWithTTL<>("value1", System.currentTimeMillis() + 1000));
        testEntry2 = new CacheEntry<>("key2",
                new ValueWithTTL<>("value2", System.currentTimeMillis() + 2000));
    }

    @Test
    void testPutAndGet() {

        CacheEntry<String, String> entry =
                new CacheEntry<>("key1", new ValueWithTTL<>(
                        "value1", System.currentTimeMillis() + 1000));
        kvStoreService.put(entry);

        String retrievedValue = kvStoreService.get("key1");
        assertEquals("value1", retrievedValue);
    }

    @Test
    void testGetExpiredEntry() {
        CacheEntry<String, String> entry =
                new CacheEntry<>("key2",  new ValueWithTTL<>("value2",
                        System.currentTimeMillis() - 1000));
        kvStoreService.put(entry);

        String retrievedValue = kvStoreService.get("key2");
        assertNull(retrievedValue);
    }

    @Test
    void testDelete() {
        CacheEntry<String, String> entry =
                new CacheEntry<>("key3",  new ValueWithTTL<>("value3",
                        System.currentTimeMillis() + 1000));
        kvStoreService.put(entry);

        String deletedValue = kvStoreService.delete("key3");
        assertEquals("value3", deletedValue);

        String retrievedValue = kvStoreService.get("key3");
        assertNull(retrievedValue);
    }

    @Test
    void testRemoveKey() {
        CacheEntry<String, String> entry =
                new CacheEntry<>("key4",  new ValueWithTTL<>("value4",
                        System.currentTimeMillis() + 1000));
        kvStoreService.put(entry);

        kvStoreService.removeKey("key4");

        String retrievedValue = kvStoreService.get("key4");
        assertNull(retrievedValue);
    }

    @Test
    void testGetSize() {
        CacheEntry<String, String> entry1 =
                new CacheEntry<>("key5", new ValueWithTTL<>("value5",
                        System.currentTimeMillis() + 1000));
        CacheEntry<String, String> entry2 =
                new CacheEntry<>("key6", new ValueWithTTL<>("value6",
                        System.currentTimeMillis() + 1000));

        kvStoreService.put(entry1);
        kvStoreService.put(entry2);

        assertEquals(2, kvStoreService.getSize());
    }

    @Test
    void testSetAndGetKeyMap() {
        CacheEntry<String, String> entry1 =
                new CacheEntry<>("key7", new ValueWithTTL<>("value7",
                        System.currentTimeMillis() + 1000));
        CacheEntry<String, String> entry2 =
                new CacheEntry<>("key8", new ValueWithTTL<>("value8",
                        System.currentTimeMillis() + 1000));

        Map<String, CacheEntry<String, String>> testMap = new HashMap<>();
        testMap.put(entry1.getKey(), entry1);
        testMap.put(entry2.getKey(), entry2);

        kvStoreService.setKeyMap(testMap);

        Map<String, CacheEntry<String, String>> retrievedMap = kvStoreService.getKeyMap();
        assertEquals(2, retrievedMap.size());
        assertTrue(retrievedMap.containsKey("key7"));
        assertTrue(retrievedMap.containsKey("key8"));
    }

    @Test
    void testGetItr() {
        // Add entries to the map
        kvStoreService.put(testEntry1);
        kvStoreService.put(testEntry2);

        // Get iterator and verify
        Iterator<Map.Entry<String, CacheEntry<String, String>>> iterator = kvStoreService.getItr();

        assertNotNull(iterator);
        assertTrue(iterator.hasNext());

        // Verify iterator contents
        int count = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, CacheEntry<String, String>> entry = iterator.next();
            assertTrue(entry.getKey().equals("key1") || entry.getKey().equals("key2"));
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void testAdd() {
        // Test adding an entry to the priority queue
        kvStoreService.add(testEntry1);
        assertEquals(testEntry1, kvStoreService.getTop());

        // Test adding multiple entries
        kvStoreService.add(testEntry2);
        assertEquals(testEntry1, kvStoreService.getTop());
    }

    @Test
    void testGetTop() {
        // Test getting top element from empty queue
        assertNull(kvStoreService.getTop());

        // Add entries and verify top element
        kvStoreService.add(testEntry1);
        kvStoreService.add(testEntry2);

        // Top should be the entry with earlier expiry time
        assertEquals(testEntry1, kvStoreService.getTop());
    }

    @Test
    void testRemoveTop() {
        // Test removing from empty queue
        kvStoreService.removeTop(); // Should not throw exception

        // Add entries and remove top
        kvStoreService.add(testEntry1);
        kvStoreService.add(testEntry2);

        kvStoreService.removeTop();
        assertEquals(testEntry2, kvStoreService.getTop());
    }

}