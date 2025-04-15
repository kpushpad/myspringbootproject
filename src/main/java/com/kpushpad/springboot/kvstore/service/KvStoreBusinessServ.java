package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.common.FileService;
import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KvStoreBusinessServ<K,V> {

    private final KvStoreService<K,V> kvStoreService;
    private final FileService fileService;
    private final CacheAofCmdUtil cacheAofCmdUtil;



    @Autowired
    public KvStoreBusinessServ(KvStoreService<K,V> kvStoreService, FileService fileService,
                               CacheAofCmdUtil cacheAofCmdUtil) {
        this.kvStoreService = kvStoreService;
        this.fileService = fileService;
        this.cacheAofCmdUtil = cacheAofCmdUtil;
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
            fileService.writeLine(cacheAofCmdUtil.getAofFilePath(),
                    cacheAofCmdUtil.getPutCmd(key.toString(), value.toString() ,
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
        fileService.writeLine(cacheAofCmdUtil.getAofFilePath(),
                cacheAofCmdUtil.getDelCmd(key.toString()));
        return kvStoreService.deleteValue(key) != null ?
                kvStoreService.deleteValue(key).getKey() : null;
    }

    @PostConstruct
    public void init() throws IOException {
        // Open file AOF log file
        fileService.openForWriting(cacheAofCmdUtil.getAofFilePath(), true);
    }
}
