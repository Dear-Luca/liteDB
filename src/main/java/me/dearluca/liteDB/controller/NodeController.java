package me.dearluca.liteDB.controller;

import me.dearluca.liteDB.cluster.NodeProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/node")
public class NodeController {
    private final NodeProperties nodeProperties;

    public NodeController(NodeProperties nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    @GetMapping("/info")
    public ResponseEntity<NodeProperties> info() {
        return ResponseEntity.ok(this.nodeProperties);
    }
}
