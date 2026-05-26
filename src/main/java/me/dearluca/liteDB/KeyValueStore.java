package me.dearluca.liteDB;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class KeyValueStore {
    private final ConcurrentHashMap<String, StoredValue> store = new ConcurrentHashMap<>();

    public void put(String key, String value) {
        store.put(key, new StoredValue(value, System.currentTimeMillis()));
    }

    public StoredValue get(String key) {
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
