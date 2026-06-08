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

/**
 * Controller for managing the key-value store.
 */
@RestController
@RequestMapping("/kv")
public class KVStoreController {
    private final KeyValueStore store;
    private final ConsistentHashing hashRing;
    private final NodeProperties nodeProperties;
    private final NodeClient replicationClient;

    /**
     * Constructor for KVStoreController. 
     * @param store the key-value store instance to manage.
     * @param hashRing the consistent hashing ring for node management.
     * @param nodeProperties the properties of the current node.
     * @param replicationClient the client for replicating data to other nodes.
     */
    public KVStoreController(KeyValueStore store, ConsistentHashing hashRing, NodeProperties nodeProperties, NodeClient replicationClient) {
        this.store = store;
        this.hashRing = hashRing;
        this.nodeProperties = nodeProperties;
        this.replicationClient = replicationClient;
    }

    /**
     * Retrieves all key-value pairs from the store.
     * @return a map containing all key-value pairs.
     */
    @GetMapping
    public Map<String, StoredValue> getKVStore() {
        return store.getAll();
    }

    /**
     * Inserts or updates a key-value pair in the store.
     * @param key the key for the entry.
     * @param value the value for the entry.
     * @return a response entity indicating the outcome of the operation.
     */
    @PutMapping("/{key}")
    public ResponseEntity<Void> put(
            @PathVariable String key,
            @RequestBody String value
    ) {
        long timestamp = System.currentTimeMillis();
        if (!nodeProperties.replicationEnabled()) {
            store.put(key, value, timestamp);
            return ResponseEntity.ok().build();
        }
        for (Node node: hashRing.getReplicaNodes(key, nodeProperties.replicationFactor())) {
            if (node.id().equals(nodeProperties.nodeId())) {
                store.put(key, value, timestamp);
            } else {
                replicationClient.replicatePut(node, key, value, timestamp);
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves a value for a given key from the store.
     * @param key the key for the entry.
     * @return a response entity containing the value or a 404 Not Found error.
     */
    @GetMapping("/{key}")
    public ResponseEntity<StoredValue> get(
            @PathVariable String key
    ) {
        var replicas = hashRing.getReplicaNodes(key, nodeProperties.replicationFactor());
        StoredValue local = store.get(key);
        System.out.println("ID " + nodeProperties.nodeId());
        if (local != null) {
            return ResponseEntity.ok(local);
        }
        for (Node node : replicas) {
            if (node.id().equals(nodeProperties.nodeId())) {
                continue;
            }
            StoredValue remote = replicationClient.getValue(node, key);
            if (remote != null) {
                return ResponseEntity.ok(remote);
            }
        }
        System.out.println("Value not found");
        return ResponseEntity.notFound().build();
    }

    /**
     * Deletes a key-value pair from the store.
     * @param key the key for the entry to delete.
     * @return a response entity indicating the outcome of the operation.
     */
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
