package me.dearluca.liteDB;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class KeyValueStore {
    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    public void put(String key, String value) {
        store.put(key, value);
    }

    public String get(String key) {
        return store.get(key);
    }

    public void delete(String key) {
        store.remove(key);
    }

    @Override
    public String toString() {
        return store.toString();
    }
}
