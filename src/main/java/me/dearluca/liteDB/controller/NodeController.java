package me.dearluca.liteDB.controller;

import me.dearluca.liteDB.cluster.ConsistentHashing;
import me.dearluca.liteDB.cluster.Node;
import me.dearluca.liteDB.cluster.NodeProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller for node-related operations.
 */
@RestController
@RequestMapping("/node")
public class NodeController {
    private final NodeProperties nodeProperties;
    private final ConsistentHashing hashRing;

    /**
     * Constructor for NodeController.
     * @param nodeProperties the properties of the current node.
     */
    public NodeController(NodeProperties nodeProperties) {
        this.nodeProperties = nodeProperties;
        this.hashRing = new ConsistentHashing(nodeProperties);
    }

    /**
     * Get the list of nodes.
     * @return the list of the nodes.
     */
    @GetMapping("/nodes")
    public ResponseEntity<List<Node>> nodes() {
        return ResponseEntity.ok(nodeProperties.nodes());
    }

    /**
     * Get the current hash ring information.
     * @return a map of node IDs to Node objects representing the hash ring.
     */
    @GetMapping("/ring")
    public ResponseEntity<Map<Long, Node>> ring(){
        return ResponseEntity.ok(hashRing.getRing());
    }

    /**
     * Get the properties of the current node.
     * @return the properties of the current node.
     */
    @GetMapping("/info")
    public ResponseEntity<NodeProperties> info() {
        return ResponseEntity.ok(this.nodeProperties);
    }

    /**
     * Get the primary node for a given key.
     * @param key the key for which to find the primary node.
     * @return the primary node for the given key.
     */
    @GetMapping("/primary/{key}")
    public ResponseEntity<Node> primary(
            @PathVariable String key
    ) {
        var res = hashRing.getPrimaryNode(key);
        return ResponseEntity.ok(res);
    }

    /**
     * Get the replica nodes for a given key.
     * @param key the key for which to find the replica nodes.
     * @param replicationFactor the number of replica nodes to retrieve.
     * @return a list of replica nodes for the given key.
     */
    @GetMapping("/replicas/{key}/{replicationFactor}")
    public ResponseEntity<List<Node>> replicas(
            @PathVariable String key,
            @PathVariable int replicationFactor
    ) {
        var res = hashRing.getReplicaNodes(key, replicationFactor);
        return ResponseEntity.ok(res);
    }

}
