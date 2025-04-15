package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class CacheCleanUpService<K,V> {

    private final KvStoreService<K,V> kvStoreService;
    private final int maxCount;

    @Autowired
    public CacheCleanUpService(KvStoreService<K, V> kvStoreService,
                               @Value("${kvstore.evict.max.count}") String count) {
        this.kvStoreService = kvStoreService;
        this.maxCount = Integer.parseInt(count);
    }

    public void cleanUpExpiredCacheEntry() {
        Pair<K, ValueWithTTL<V>> value = kvStoreService.getTop();
        long currentTime = System.currentTimeMillis();
        int count = 0;

        while(currentTime > value.getValue().getExpiryTimeInMs() &&
        count < maxCount) {
            /*
              Remove from Queue
             */
            kvStoreService.removeTop();
            System.out.println("Deleting key : " + value.getKey());
            /*
              Remove from Map
             */
            kvStoreService.removeKey(value.getKey());
            value = kvStoreService.getTop();
            currentTime = System.currentTimeMillis();
            count++;
        }
        System.out.println("Current size : " + kvStoreService.geTotalElements());
    }
}
