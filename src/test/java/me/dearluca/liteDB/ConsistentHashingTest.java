package me.dearluca.liteDB;

import me.dearluca.liteDB.cluster.ConsistentHashing;
import me.dearluca.liteDB.cluster.Node;
import me.dearluca.liteDB.cluster.NodeProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsistentHashingTest {
    private ConsistentHashing hashRing;

    @BeforeEach
    void init() {
        List<Node> nodes = List.of(
                new Node("node1", "localhost", 9090),
                new Node("node2", "localhost", 9091),
                new Node("node3", "localhost", 9092)
        );

        NodeProperties properties =
                new NodeProperties(
                        "node1",
                        9090,
                        nodes
                );
        hashRing = new ConsistentHashing(properties);
    }

    @Test
    void testSameKeyMapToSameNode() {
        Node first = hashRing.getPrimaryNode("user:1");
        Node second = hashRing.getPrimaryNode("user:1");

        assertEquals(first, second);
    }

    @Test
    void testReplicasSize() {
        int replicationFactor = 2;
        List<Node> replicas = hashRing.getReplicaNodes("user:1", replicationFactor);

        assertEquals(2, replicas.size());
    }

    @Test
    void testReplicasUniqueness() {
        int replicationFactor = 3;
        List<Node> replicas = hashRing.getReplicaNodes("user:1", replicationFactor);

        assertEquals(
                replicas.size(),
                replicas.stream().distinct().count()
        );
    }

    @Test
    void testReplicationFactorGreaterThanNodes() {
        int replicationFactor = 10;
        List<Node> replicas =
                hashRing.getReplicaNodes("user:1", replicationFactor);

        assertEquals(3, replicas.size());
    }
}
