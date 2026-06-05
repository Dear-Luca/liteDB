package me.dearluca.liteDB.store;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple in-memory key-value store for handling replicated data.
 */
@Service
public class KeyValueStore {
    private final ConcurrentHashMap<String, StoredValue> store = new ConcurrentHashMap<>();

    /**
     * Puts a key-value pair into the store with a timestamp. If the key already exists, it will only be updated if the new timestamp is greater than or equal to the existing one.
     * @param key the key to store
     * @param value the value to store
     * @param timestamp the timestamp of the operation
     */
    public void putReplica(String key, String value, long timestamp) {
        store.compute(key, (k, existing) -> {
            if (existing == null || timestamp >= existing.timestamp()) {
                return new StoredValue(value, timestamp);
            }

            return existing;
        });
    }

    /**
     * Retrieves the value associated with the given key. Returns null if the key does not exist.
     * @param key the key to retrieve
     * @return the stored value, or null if the key does not exist
     */
    public StoredValue get(String key) {
        return store.get(key);
    }

    /**
     * Deletes the key-value pair associated with the given key.
     * @param key the key to delete
     */
    public void delete(String key) {
        store.remove(key);
    }

    /**
     * Retrieves all key-value pairs in the store.
     * @return a map containing all key-value pairs
     */
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
