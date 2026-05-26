package me.dearluca.liteDB;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "litedb")
public record NodeProperties(
        String host,
        int port,
        List<Node> nodes
){}
