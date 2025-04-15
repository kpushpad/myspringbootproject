package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KvStoreBusinessServ<K,V> {

    private final KvStoreService<K,V> kvStoreService;

    @Autowired
    public KvStoreBusinessServ(KvStoreService<K,V> kvStoreService) {
        this.kvStoreService = kvStoreService;
    }
    public  boolean put(K key, V value, Long ttl) {
        long newTtl;
        try {
            if (null != ttl) {
                newTtl = System.currentTimeMillis() + ttl*1000;
            } else {
                newTtl = Long.MAX_VALUE;
            }
            ValueWithTTL<V> v= kvStoreService.putValue(key, value, newTtl);
            kvStoreService.add(key, value, newTtl);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public V get(K key) {
        return kvStoreService.getValue(key) != null ?
                kvStoreService.getValue(key).getKey() : null;
    }

    public V  delete(K key) {
        return kvStoreService.deleteValue(key) != null ?
                kvStoreService.deleteValue(key).getKey() : null;
    }
}
