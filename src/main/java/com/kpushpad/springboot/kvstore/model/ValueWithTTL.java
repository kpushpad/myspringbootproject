package com.kpushpad.springboot.kvstore.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ValueWithTTL<V> implements Serializable {

    private V key;
    private Long expiryTimeInMs;

    public ValueWithTTL(){
    }
    public ValueWithTTL(V item, Long expiryTimeInMs) {
        this.key = item;
        this.expiryTimeInMs = expiryTimeInMs;
    }
}
