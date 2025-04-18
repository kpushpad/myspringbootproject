package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheCleanExpKeysService<K,V> {

    private final KvStoreBusinessServ<K,V> kvStoreBusinessServ;
    private final int maxCount;

    @Autowired
    public CacheCleanExpKeysService(KvStoreBusinessServ<K, V> kvStoreBusinessServ,
                                    @Value("${kvstore.evict.max.count}") String count) {
        this.kvStoreBusinessServ = kvStoreBusinessServ;
        this.maxCount = Integer.parseInt(count);
    }

    public void cleanUpExpiredCacheEntry() throws Exception {
        int count = 0;
        while (count < maxCount && kvStoreBusinessServ.removeTopExpiredValues()) {
            count++;
        }
        log.debug("Total deleted keys : {}", count);
        log.debug("Total Remaining keys : {}", kvStoreBusinessServ.getTotalKeysCount());
    }
}
