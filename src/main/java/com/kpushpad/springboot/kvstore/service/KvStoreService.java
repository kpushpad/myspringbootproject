package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.model.CacheEntry;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class KvStoreService<K, V> {
    private ConcurrentHashMap<K , CacheEntry<K,V>> map;
    private PriorityBlockingQueue<CacheEntry<K,V>> pq;

    public KvStoreService() {
        this.map = new ConcurrentHashMap<>();
        pq = new PriorityBlockingQueue<>(1000,
                Comparator.comparingLong(e -> e.getValue().getExpiryTimeInMs()));
    }

    public V get(K key) {
        CacheEntry<K,V> val = map.get(key);
        if (null == val || System.currentTimeMillis() > val.getValue().getExpiryTimeInMs()) {
            return null;
        }
        return val.getValue().getKey();
    }

    public void put(CacheEntry<K, V> cacheEntry) {
        map.put(cacheEntry.getKey(), cacheEntry);
    }

    public V delete(K key) {
        CacheEntry<K, V> v = map.get(key);
        if (v != null) {
           v.getValue().setExpiryTimeInMs(0L);
           return v.getValue().getKey();
        }
        return null;
    }

    public void removeKey(K key) {map.remove(key); }
    public Integer getSize() {return map.size();}
    public void setKeyMap(Map<K, CacheEntry<K,V>>  map) {
        this.map = new ConcurrentHashMap<>(map);
    }
    public Map<K, CacheEntry<K,V>> getKeyMap() {
        return new HashMap<>(map);
    }

    public void add(CacheEntry<K,V> entry) {pq.add(entry);}
    public CacheEntry<K, V> getTop() {return pq.peek();}
    public  void removeTop() { pq.poll();}

    public Iterator<Map.Entry<K, CacheEntry<K, V>>> getItr() {
        return  map.entrySet().iterator();
    }
}
