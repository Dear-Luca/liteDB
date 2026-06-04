package me.dearluca.liteDB.controller;

import me.dearluca.liteDB.cluster.ConsistentHashing;
import me.dearluca.liteDB.cluster.Node;
import me.dearluca.liteDB.cluster.NodeProperties;
import me.dearluca.liteDB.grpc.NodeClient;
import me.dearluca.liteDB.store.KeyValueStore;
import me.dearluca.liteDB.store.StoredValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/kv")
public class KVStoreController {
    private final KeyValueStore store;
    private final ConsistentHashing hashRing;
    private final NodeProperties nodeProperties;
    private final NodeClient replicationClient;

    public KVStoreController(KeyValueStore store, ConsistentHashing hashRing, NodeProperties nodeProperties, NodeClient replicationClient) {
        this.store = store;
        this.hashRing = hashRing;
        this.nodeProperties = nodeProperties;
        this.replicationClient = replicationClient;
    }

    @GetMapping
    public Map<String, StoredValue> getKVStore() {
        return store.getAll();
    }

    @PutMapping("/{key}")
    public ResponseEntity<Void> put(
            @PathVariable String key,
            @RequestBody String value
    ) {
        long timestamp = System.currentTimeMillis();
        for (Node node: hashRing.getReplicaNodes(key, 2)) {
            if (node.id().equals(nodeProperties.nodeId())) {
                store.putReplica(key, value, timestamp);
            } else {
                replicationClient.replicatePut(node, key, value, timestamp);
            }
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{key}")
    public ResponseEntity<StoredValue> get(
            @PathVariable String key
    ) {
        StoredValue value = store.get(key);

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(value);
    }

    @DeleteMapping("/{key}")
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
