package me.dearluca.liteDB.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import me.dearluca.liteDB.cluster.Node;
import org.springframework.stereotype.Service;

@Service
public class NodeClient {
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
}
