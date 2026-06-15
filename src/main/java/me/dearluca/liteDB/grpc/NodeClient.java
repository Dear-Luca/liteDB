package me.dearluca.liteDB.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import me.dearluca.liteDB.cluster.Node;
import me.dearluca.liteDB.cluster.NodeProperties;
import me.dearluca.liteDB.store.StoredValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * A client for communicating with other nodes in the cluster.
 */
@Service
public class NodeClient {
    private static final Logger log = LoggerFactory.getLogger(NodeClient.class);
    private final NodeProperties nodeProperties;

    public NodeClient(NodeProperties nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    /**
     * Replicates a put operation to another node.
     * @param targetNode the node to replicate to
     * @param key the key to replicate
     * @param value the value to replicate
     * @param timestamp the timestamp of the operation
     */
    public void replicatePut(Node targetNode, String key, String value, long timestamp) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(targetNode.getHost(), targetNode.getPort())
                .usePlaintext()
                .build();

        try {
            NodeServiceGrpc.NodeServiceBlockingStub stub =
                    NodeServiceGrpc.newBlockingStub(channel);

            PutRequest request = PutRequest.newBuilder()
                    .setKey(key)
                    .setValue(value)
                    .setTimestamp(timestamp)
                    .build();

            PutResponse response = stub.replicatePut(request);

            if (!response.getSuccess()) {
                log.error("[GRPC] PUT: {}", response.getMessage());
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
                .forAddress(targetNode.getHost(), targetNode.getPort())
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
                log.error("[GRPC] GET: {}", response.getMessage());
                return null;
            }

            return new StoredValue(response.getValue(), response.getTimestamp());

        } finally {
            channel.shutdown();
        }
    }

    public boolean replicateDelete(Node targetNode, String key) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(targetNode.getHost(), targetNode.getPort())
                .usePlaintext()
                .build();
        try {
            NodeServiceGrpc.NodeServiceBlockingStub stub =
                    NodeServiceGrpc.newBlockingStub(channel);

            DeleteKeyRequest request = DeleteKeyRequest.newBuilder()
                    .setKey(key)
                    .build();

            DeleteKeyResponse response = stub.replicateDelete(request);

            if (!response.getSuccess()) {
                log.error("[GRPC] DELETE: {}", response.getMessage());
                return false;
            }

        } finally {
            channel.shutdown();
        }
        return true;
    }

    public boolean heartbeat(Node targetNode) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(targetNode.getHost(), targetNode.getPort())
                .usePlaintext()
                .build();
        try {
            NodeServiceGrpc.NodeServiceBlockingStub stub =
                    NodeServiceGrpc.newBlockingStub(channel);
            HeartbeatRequest request = HeartbeatRequest.newBuilder()
                    .setNodeId(nodeProperties.nodeId())
                    .build();
            HeartbeatResponse response = stub.heartbeat(request);
            return response.getIsAlive();
        } catch (Exception e) {
            return false;
        }
        finally {
            channel.shutdown();
        }
    }
}
