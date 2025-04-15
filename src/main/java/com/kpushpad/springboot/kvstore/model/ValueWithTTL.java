package com.kpushpad.springboot.kvstore.model;

public class ValueWithTTL<V>{
    private V key;
    private Long expiryTimeInMs;

    public ValueWithTTL(V item, Long expiryTimeInMs) {
        this.key = item;
        this.expiryTimeInMs = expiryTimeInMs;
    }

    public V getKey() {
        return key;
    }

    public Long getExpiryTimeInMs() {
        return expiryTimeInMs;
    }

    public void setExpiryTimeInMs(Long expiryTimeInMs) {
        this.expiryTimeInMs = expiryTimeInMs;
    }
}
