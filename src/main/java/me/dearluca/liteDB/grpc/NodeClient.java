package me.dearluca.liteDB.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import me.dearluca.liteDB.cluster.Node;
import me.dearluca.liteDB.store.StoredValue;
import org.springframework.stereotype.Service;

/**
 * A client for communicating with other nodes in the cluster.
 */
@Service
public class NodeClient {
    /**
     * Replicates a put operation to another node.
     * @param targetNode the node to replicate to
     * @param key the key to replicate
     * @param value the value to replicate
     * @param timestamp the timestamp of the operation
     */
    public void replicatePut(Node targetNode, String key, String value, long timestamp) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(targetNode.host(), targetNode.port())
                .usePlaintext()
                .build();

        try {
            NodeServiceGrpc.NodeServiceBlockingStub stub =
                    NodeServiceGrpc.newBlockingStub(channel);

            ReplicatePutRequest request = ReplicatePutRequest.newBuilder()
                    .setKey(key)
                    .setValue(value)
                    .setTimestamp(timestamp)
                    .build();

            ReplicateResponse response = stub.replicatePut(request);

            if (!response.getSuccess()) {
                throw new RuntimeException("Replication failed: " + response.getMessage());
            }

        } finally {
            channel.shutdown();
        }
    }

    /**
     * Get value operation from a target Node with the given key.
     * @param targetNode the node where the get operation is executed
     * @param key the key from which to obtain the value
     * @return the value of the given key or throw an exception
     */
    public StoredValue getValue(Node targetNode, String key) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(targetNode.host(), targetNode.port())
                .usePlaintext()
                .build();
        try {
            NodeServiceGrpc.NodeServiceBlockingStub stub =
                    NodeServiceGrpc.newBlockingStub(channel);

            GetValueRequest request = GetValueRequest.newBuilder()
                    .setKey(key)
                    .build();

            GetValueResponse response = stub.getValue(request);

            if (!response.getSuccess()) {
                throw new RuntimeException("Get value failed: " + response.getMessage());
            }

            return new StoredValue(response.getValue(), response.getTimestamp());

        } finally {
            channel.shutdown();
        }
    }
}
