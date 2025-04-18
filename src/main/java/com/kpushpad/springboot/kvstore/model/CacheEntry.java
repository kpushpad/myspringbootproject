package com.kpushpad.springboot.kvstore.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CacheEntry<K, V> implements Serializable {
    private K key;
    private ValueWithTTL<V> value;

    public CacheEntry() {} // Only for De JACKSON DeSerialization

    public CacheEntry(K key, ValueWithTTL<V> value) {
        this.key = key;
        this.value = value;
    }
}
