package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class KvStoreService<K, V> {
    private ConcurrentHashMap<K , ValueWithTTL<V>> map;
    private PriorityBlockingQueue<Pair<K, ValueWithTTL<V>>> pq;

    public KvStoreService() {
        map = new ConcurrentHashMap<>();
        pq = new PriorityBlockingQueue<>(1000,
                Comparator.comparingLong(a -> a.getValue().getExpiryTimeInMs()));

    }

    public ValueWithTTL<V> getValue(K key) {
        ValueWithTTL<V> val = map.get(key);
        if (null == val || System.currentTimeMillis() > val.getExpiryTimeInMs()) {
            return null;
        }
        return map.get(key);
    }

    public  ValueWithTTL<V> putValue(K key, V value , Long ttl) {
        ValueWithTTL<V> v = new ValueWithTTL<>(value, ttl);
        map.put(key, v);
        return v;
    }

    public   ValueWithTTL<V> deleteValue(K key) {
        ValueWithTTL<V> v = map.get(key);
        if (v != null) {
            v.setExpiryTimeInMs(0L);
            return map.remove(key);
        } else {
            return null;
        }
    }

    public   ValueWithTTL<V> removeKey(K key) {
        return map.remove(key);
    }
    public void add(K key, V value , Long ttl) {
        Pair<K, ValueWithTTL<V>> p = Pair.of(key, new ValueWithTTL<>(value, ttl));
        pq.add(p);
    }
    public  Pair<K, ValueWithTTL<V>> getTop() {
        return pq.peek();
    }

    public  void removeTop() {
        pq.poll();
    }

    public int geTotalElements() {
        return pq.size();
    }


}
