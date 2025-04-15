package com.kpushpad.springboot.kvstore.model;

public class CacheEntry<K , V> {
    private final K key;
    private final ValueWithTTL<V> value;

    public CacheEntry(K key, ValueWithTTL<V> value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public ValueWithTTL<V> getValue() {
        return value;
    }
}
