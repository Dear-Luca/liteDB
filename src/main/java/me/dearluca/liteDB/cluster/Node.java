package me.dearluca.liteDB.cluster;

import java.util.Objects;

/**
 * Represents a node in the cluster.
 */
public final class Node {
    private final String id;
    private final String host;
    private final int port;
    private NodeStatus nodeStatus = NodeStatus.UP;

    /**
     * @param id   the unique identifier of the node.
     * @param host the hostname or IP address of the node.
     * @param port the port number on which the node is listening for gRPC requests.
     */
    public Node(
            String id,
            String host,
            int port
    ) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public String id() {
        return id;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public NodeStatus getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Node) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.host, that.host) &&
                this.port == that.port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, host, port);
    }

    @Override
    public String toString() {
        return "Node[" +
                "id=" + id + ", " +
                "host=" + host + ", " +
                "port=" + port + ']';
    }
}
