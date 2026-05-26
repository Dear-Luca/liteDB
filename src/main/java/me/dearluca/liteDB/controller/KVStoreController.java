package me.dearluca.liteDB.controller;

import me.dearluca.liteDB.store.KeyValueStore;
import me.dearluca.liteDB.store.StoredValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class KVStoreController {
    private final KeyValueStore store;

    public KVStoreController(KeyValueStore store) {
        this.store = store;
    }

    @GetMapping("/kv")
    public Map<String, StoredValue> getKVStore() {
        return store.getAll();
    }

    @PutMapping("/kv/{key}")
    public ResponseEntity<Void> put(
            @PathVariable String key,
            @RequestBody String value
    ) {
        store.put(key, value);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/kv/{key}")
    public ResponseEntity<StoredValue> get(
            @PathVariable String key
    ) {
        StoredValue value = store.get(key);

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(value);
    }

    @DeleteMapping("/kv/{key}")
    public ResponseEntity<Void> delete(
            @PathVariable String key
    ) {
        if (store.get(key) == null) {
            return ResponseEntity.notFound().build();
        }
        store.delete(key);
        return ResponseEntity.ok().build();
    }
}
