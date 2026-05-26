package me.dearluca.liteDB.store;

import org.springframework.stereotype.Service;

import java.util.Map;
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

    public Map<String, StoredValue> getAll() {
        return Map.copyOf(store);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KeyValueStore {\n");
        store.forEach((key, value) ->
                sb.append("  ")
                        .append(key)
                        .append(" -> ")
                        .append(value)
                        .append("\n")
        );
        sb.append("}");
        return sb.toString();
    }
}
