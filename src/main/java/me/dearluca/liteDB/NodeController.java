package me.dearluca.liteDB;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeController {
    private final NodeProperties nodeProperties;

    public NodeController(NodeProperties nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    @GetMapping("/node/info")
    public NodeProperties info() {
        return nodeProperties;
    }
}
