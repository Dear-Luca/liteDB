package me.dearluca.liteDB.cluster;

import java.util.*;

public class ConsistentHashing {

    private final SortedMap<Long, Node> ring = new TreeMap<>();

    public ConsistentHashing(NodeProperties nodeProperties) {
        for (Node node : nodeProperties.nodes()) {
            ring.put(hash(node.id()), node);
        }
    }

    public Node getPrimaryNode(String key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("Hash ring is empty");
        }

        var tailMap = ring.tailMap(hash(key));
        long node = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        return ring.get(node);
    }

    public List<Node> getReplicaNodes(String key, int replicationFactor) {
        if (replicationFactor <= 0) {
            throw new IllegalArgumentException("Replication factor must be positive");
        }
        if (ring.isEmpty()) {
            throw new IllegalStateException("Hash ring is empty");
        }
        int targetReplicas = Math.min(replicationFactor, ring.size());

        List<Node> replicas = new ArrayList<>();
        Set<Node> seen = new HashSet<>();

        var tailMap = ring.tailMap(hash(key));

        addNodes(tailMap, replicas, seen, targetReplicas);

        if (replicas.size() < targetReplicas) {
            addNodes(ring, replicas, seen, targetReplicas);
        }

        return replicas;
    }

    private void addNodes(
            SortedMap<Long, Node> source,
            List<Node> replicas,
            Set<Node> seen,
            int targetReplicas
    ) {
        for (var entry : source.entrySet()) {
            Node node = entry.getValue();

            if (seen.add(node)) {
                replicas.add(node);
            }

            if (replicas.size() == targetReplicas) {
                return;
            }
        }
    }

    private long hash(String key) {
        return Integer.toUnsignedLong(key.hashCode());
    }
}
