package me.dearluca.liteDB.grpc;

import io.grpc.stub.StreamObserver;
import me.dearluca.liteDB.store.KeyValueStore;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class NodeService extends NodeServiceGrpc.NodeServiceImplBase {
    private final KeyValueStore store;

    public NodeService(KeyValueStore store) {
        this.store = store;
    }

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
