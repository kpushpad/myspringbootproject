package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.model.CacheEntry;
import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class KvStoreBusinessServ<K,V> {

    private final KvStoreService<K,V> kvStoreService;
    private final CacheAOFLogService cacheAOFLogService;

    @Autowired
    public KvStoreBusinessServ(KvStoreService<K,V> kvStoreService, CacheAOFLogService cacheAOFLogService) {
        this.kvStoreService = kvStoreService;
        this.cacheAOFLogService = cacheAOFLogService;
    }

    public V get(K key) {return kvStoreService.get(key);}

    public  boolean put(K key, V value, Long ttl) {
        try {
            long newTtl = getExpiryTime(ttl);
            CacheEntry<K,V> cacheEntry = new CacheEntry<>(key, new ValueWithTTL<>(value, newTtl));
            synchronized(this) {
                kvStoreService.put(cacheEntry);
                kvStoreService.add(cacheEntry);
                cacheAOFLogService.recordPutCommand(cacheEntry);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public V  delete(K key) throws IOException {
        synchronized (this) {
            V v = kvStoreService.delete(key);
            cacheAOFLogService.recordDelCommand(new CacheEntry<>(key, new ValueWithTTL<>(v, 0L)));
            return v;
        }
    }

    public Integer fillData(Map<K, CacheEntry<K, V>> map) {
        kvStoreService.setKeyMap(map);
        map.forEach((key, value) -> {
            if (System.currentTimeMillis() > value.getValue().getExpiryTimeInMs()) {
                kvStoreService.removeKey(key);
            } else {
                kvStoreService.add(value);
            }
        });
        map.clear();
        return kvStoreService.getSize();
    }

    public List<K> getAllKeys() {
        Iterator<Map.Entry<K, CacheEntry<K, V>>> itr = kvStoreService.getItr();
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(itr, Spliterator.ORDERED), false)
                .filter(k -> k.getValue().getValue().getExpiryTimeInMs() > System.currentTimeMillis())
                .map(Map.Entry::getKey) // extract only keys
                .collect(Collectors.toList());
    }

    public boolean removeTopExpiredValues() {
        CacheEntry<K, V> value = kvStoreService.getTop();
        if (value != null && value.getValue().getExpiryTimeInMs() < System.currentTimeMillis()) {
            kvStoreService.removeTop();
            kvStoreService.removeKey(value.getKey());
            return true;
        }
        return false;
    }
    public Integer getTotalKeysCount() {
        return kvStoreService.getSize();
    }

    private Long getExpiryTime(Long ttl) {
        if (null != ttl && !ttl.equals(Long.MAX_VALUE)) {
            return System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(ttl);
        } else {
            return Long.MAX_VALUE;
        }
    }
}
