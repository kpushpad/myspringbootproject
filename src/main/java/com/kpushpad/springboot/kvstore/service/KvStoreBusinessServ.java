package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.common.FileService;
import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import jakarta.annotation.PostConstruct;
import org.hibernate.annotations.Synchronize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KvStoreBusinessServ<K,V> {

    private final KvStoreService<K,V> kvStoreService;
    private final FileService fileService;
    private final CacheAofUtil cacheAofUtil;

    @Autowired
    public KvStoreBusinessServ(KvStoreService<K,V> kvStoreService, FileService fileService,
                               CacheAofUtil cacheAofUtil) {
        this.kvStoreService = kvStoreService;
        this.fileService = fileService;
        this.cacheAofUtil = cacheAofUtil;
    }
    public  boolean put(K key, V value, Long ttl) {
        long newTtl;
        try {
            if (null != ttl) {
                newTtl = System.currentTimeMillis() + ttl*1000;
            } else {
                newTtl = Long.MAX_VALUE;
            }
            synchronized(this) {
                ValueWithTTL<V> v = kvStoreService.putValue(key, value, newTtl);
                kvStoreService.addToQueue(key, value, newTtl);
            }
            fileService.writeLine(cacheAofUtil.getAofFilePath(),
                    cacheAofUtil.getPutCmd(key.toString(), value.toString() ,
                            String.valueOf(newTtl)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public V get(K key) {
        return kvStoreService.getValue(key) != null ?
                kvStoreService.getValue(key).getKey() : null;
    }

    public V  delete(K key) throws IOException {
        fileService.writeLine(cacheAofUtil.getAofFilePath(),
                cacheAofUtil.getDelCmd(key.toString()));
        return kvStoreService.deleteValue(key) != null ?
                kvStoreService.deleteValue(key).getKey() : null;
    }

    public Integer fillData(Map<K, ValueWithTTL<V>> map) {
        kvStoreService.setKeyMap(map);
        map.clear();
        return kvStoreService.getSize();
    }

    public void getAllKeys() {
        Iterator<Map.Entry<K, ValueWithTTL<V>>> itr = kvStoreService.getItr();
        while (itr.hasNext()) {
            Map.Entry<K, ValueWithTTL<V>> entry = itr.next();
            String key = entry.getKey().toString();
            String value = entry.getValue().getKey().toString();
            String ttl = entry.getValue().getExpiryTimeInMs().toString();
            System.out.println(
                    Arrays.asList(key, value , ttl).stream().collect(Collectors.joining(" ")));
        }
    }
}
