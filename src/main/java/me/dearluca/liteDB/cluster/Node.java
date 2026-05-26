package me.dearluca.liteDB.cluster;

public record Node(
        String id,
        String host,
        int port
) {}
