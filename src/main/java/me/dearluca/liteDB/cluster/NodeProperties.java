package me.dearluca.liteDB.cluster;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Configuration properties for the cluster nodes.
 *
 * @param nodeId             the unique identifier of the node.
 * @param grpcPort           the port number on which the node is listening for gRPC requests.
 * @param replicationFactor  the replication factor of the key in the cluster
 * @param replicationEnabled true if replication is enabled false otherwise
 * @param nodes              the list of nodes in the cluster.
 */
@ConfigurationProperties(prefix = "litedb")
public record NodeProperties(
        String nodeId,
        int grpcPort,
        int replicationFactor,
        boolean replicationEnabled,
        List<Node> nodes
){}
