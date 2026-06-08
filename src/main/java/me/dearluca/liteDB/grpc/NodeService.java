package me.dearluca.liteDB.grpc;

import io.grpc.stub.StreamObserver;
import me.dearluca.liteDB.cluster.NodeProperties;
import me.dearluca.liteDB.controller.KVStoreController;
import me.dearluca.liteDB.store.KeyValueStore;
import me.dearluca.liteDB.store.StoredValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;

/**
 * A gRPC service for handling node-to-node communication.
 */
@GrpcService
public class NodeService extends NodeServiceGrpc.NodeServiceImplBase {
    private final KeyValueStore store;
    private final NodeProperties nodeProperties;
    private static final Logger log = LoggerFactory.getLogger(NodeService.class);


    /**
     * Creates a new NodeService with the given KeyValueStore.
     * @param store the KeyValueStore to use for storing replicated data
     */
    public NodeService(KeyValueStore store, NodeProperties nodeProperties) {
        this.store = store;
        this.nodeProperties = nodeProperties;
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
        log.info("[GRPC]: Node {} received PUT for key={}", nodeProperties.nodeId(), request.getKey());
        store.put(
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

    /**
     * Get value operation to the local store.
     * @param request the request containing the key
     * @param responseObserver the observer for sending the response
     */
    @Override
    public void getValue(
            GetValueRequest request,
            StreamObserver<GetValueResponse> responseObserver
    ) {
        log.info("[GRPC]: Node {} received GET for key={}", nodeProperties.nodeId(), request.getKey());
        StoredValue storedValue = store.get(request.getKey());
        if (storedValue == null) {
            responseObserver.onNext(
                    GetValueResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage("Value not found")
                            .build()
            );
            responseObserver.onCompleted();
            return;
        }

        responseObserver.onNext(
                GetValueResponse.newBuilder()
                        .setSuccess(true)
                        .setValue(storedValue.value())
                        .setTimestamp(storedValue.timestamp())
                        .setMessage("Value found")
                        .build()
        );

        responseObserver.onCompleted();
    }

    @Override
    public void replicateDelete(DeleteKeyRequest request, StreamObserver<DeleteKeyResponse> responseObserver) {
        log.info("[GRPC]: Node {} received DELETE for key={}", nodeProperties.nodeId(), request.getKey());
        if (!store.delete(request.getKey())){
            responseObserver.onNext(
                    DeleteKeyResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage("Delete failed")
                            .build()
            );
            responseObserver.onCompleted();
            return;
        }

        responseObserver.onNext(
            DeleteKeyResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Delete completed")
                .build()
        );
        responseObserver.onCompleted();
    }
}
