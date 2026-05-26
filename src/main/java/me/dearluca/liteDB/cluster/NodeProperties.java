package me.dearluca.liteDB.cluster;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "litedb")
public record NodeProperties(
        String nodeId,
        int port,
        List<Node> nodes
){}
