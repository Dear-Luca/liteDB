package me.dearluca.liteDB.grpc;

import me.dearluca.liteDB.cluster.Node;
import me.dearluca.liteDB.cluster.NodeProperties;
import me.dearluca.liteDB.controller.KVStoreController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HeartbeatScheduler {
    private static final int HEARTBEAT_RATE = 2000;
    private final NodeProperties nodeProperties;
    private final NodeClient replicationClient;
    private static final Logger log = LoggerFactory.getLogger(HeartbeatScheduler.class);


    public HeartbeatScheduler(NodeProperties nodeProperties, NodeClient replicationClient) {
        this.nodeProperties = nodeProperties;
        this.replicationClient = replicationClient;
    }

    @Scheduled(fixedRate = HEARTBEAT_RATE)
    public void heartbeat() {
        for (Node node: nodeProperties.nodes()) {
            if (node.id().equals(nodeProperties.nodeId())) {
                continue;
            }
            log.info("[GRPC] HEARTBEAT to {}", node.id());
            boolean alive = replicationClient.heartbeat(node);
            log.info("[GRPC] HEARTBEAT result node={} alive={}", node.id(), alive);
        }
    }
}
