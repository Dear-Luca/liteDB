package me.dearluca.liteDB.cluster;

import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Implements consistent hashing to distribute keys across nodes in the cluster.
 */
@Service
public class ConsistentHashing {

    private final SortedMap<Long, Node> ring = new TreeMap<>();

    /**
     * Initializes the consistent hashing ring with the given nodes.
     * @param nodeProperties the properties containing the list of nodes in the cluster.
     */
    public ConsistentHashing(NodeProperties nodeProperties) {
        for (Node node : nodeProperties.nodes()) {
            ring.put(hash(node.getId()), node);
        }
    }

    /**
     * Returns an unmodifiable view of the hash ring.
     * @return a map representing the hash ring, where keys are hash values and values are nodes.
     */
    public Map<Long, Node> getRing() {
        return Map.copyOf(ring);
    }

    /**
     * Given a key, returns the primary node responsible for that key based on the consistent hashing algorithm.
     * @param key the key for which to find the primary node.
     * @return the primary node responsible for the given key.
     */
    public Node getPrimaryNode(String key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("Hash ring is empty");
        }

        var tailMap = ring.tailMap(hash(key));
        long node = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        return ring.get(node);
    }

    /**
     * Given a key and a replication factor, returns a list of replica nodes responsible for that key based on the consistent hashing algorithm.
     * @param key the key for which to find replica nodes.
     * @param replicationFactor the number of replica nodes to return.
     * @return a list of replica nodes responsible for the given key.
     */
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

    /**
     * Adds nodes to the replica list, ensuring no duplicates.
     * @param source the source map of nodes.
     * @param replicas the list to which nodes will be added.
     * @param seen the set of already seen nodes.
     * @param targetReplicas the number of replica nodes to find.
     */
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

    /**
     * Computes the hash of a given key.
     * @param key the key for which to compute the hash.
     * @return the hash value of the key.
     */
    private long hash(String key) {
        try {
            String hashAlgorithm = "SHA-256";
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
            byte[] bytes = digest.digest(key.getBytes(StandardCharsets.UTF_8));

            return ByteBuffer.wrap(bytes).getLong() & Long.MAX_VALUE;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not compute hash", e);
        }
    }
}
