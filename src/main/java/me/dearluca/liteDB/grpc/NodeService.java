package me.dearluca.liteDB.grpc;

import io.grpc.stub.StreamObserver;
import me.dearluca.liteDB.store.KeyValueStore;
import org.springframework.grpc.server.service.GrpcService;

/**
 * A gRPC service for handling node-to-node communication.
 */
@GrpcService
public class NodeService extends NodeServiceGrpc.NodeServiceImplBase {
    private final KeyValueStore store;

    /**
     * Creates a new NodeService with the given KeyValueStore.
     * @param store the KeyValueStore to use for storing replicated data
     */
    public NodeService(KeyValueStore store) {
        this.store = store;
    }

    /**
     * Replicates a put operation to the local store.
     * @param request the request containing the key, value, and timestamp
     * @param responseObserver the observer for sending the response
     */
    @Override
    public void replicatePut(
            ReplicatePutRequest request,
            StreamObserver<ReplicateResponse> responseObserver
    ) {
        store.putReplica(
                request.getKey(),
                request.getValue(),
                request.getTimestamp()
        );

        ReplicateResponse response = ReplicateResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Replica stored")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
