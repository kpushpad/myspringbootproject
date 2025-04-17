package com.kpushpad.springboot.kvstore.model;

import com.kpushpad.springboot.kvstore.serdser.SerialzDSer;

import java.io.Serializable;

public class ValueWithTTL<V> implements Serializable {

    private V key;
    private Long expiryTimeInMs;

    public ValueWithTTL(){

    }
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

    public void setKey(V key) { this.key = key;}
}
