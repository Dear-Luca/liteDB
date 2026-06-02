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

@RestController
@RequestMapping("/node")
public class NodeController {
    private final NodeProperties nodeProperties;
    private final ConsistentHashing hashRing;

    public NodeController(NodeProperties nodeProperties) {
        this.nodeProperties = nodeProperties;
        this.hashRing = new ConsistentHashing(nodeProperties);
    }

    @GetMapping("/ring")
    public ResponseEntity<Map<Long, Node>> ring(){
        return ResponseEntity.ok(hashRing.getRing());
    }

    @GetMapping("/info")
    public ResponseEntity<NodeProperties> info() {
        return ResponseEntity.ok(this.nodeProperties);
    }

    @GetMapping("/primary/{key}")
    public ResponseEntity<Node> primary(
            @PathVariable String key
    ) {
        var res = hashRing.getPrimaryNode(key);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/replicas/{key}/{replicationFactor}")
    public ResponseEntity<List<Node>> replicas(
            @PathVariable String key,
            @PathVariable int replicationFactor
    ) {
        var res = hashRing.getReplicaNodes(key, replicationFactor);
        return ResponseEntity.ok(res);
    }

}
