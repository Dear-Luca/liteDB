package me.dearluca.liteDB.cluster;

/**
 * Represents a node in the cluster.
 * @param id the unique identifier of the node.
 * @param host the hostname or IP address of the node.
 * @param port the port number on which the node is listening for gRPC requests.
 */
public record Node(
        String id,
        String host,
        int port
) {}
