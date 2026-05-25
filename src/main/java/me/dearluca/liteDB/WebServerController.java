package me.dearluca.liteDB;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebServerController {
    private final KeyValueStore store;

    public WebServerController(KeyValueStore store) {
        this.store = store;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello world";
    }

    @GetMapping("/kv")
    public String getKVStore() {
        return store.toString();
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
    public String get(
            @PathVariable String key
    ) {
        return store.get(key);
    }

}
