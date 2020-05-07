package de.tesis.dynaware.grapheditor.demo;

import de.tesis.dynaware.grapheditor.model.GNode;

public class NodeGeometryWrapper {
    private final GNode node;

    public NodeGeometryWrapper(GNode node) {
        this.node = node;
    }

    public GNode getNode() {
        return node;
    }

    public String render() {
        return String.format("Width: %.2f%nHeight: %.2f%nx-coordinate: %.2f%ny-coordinate: %.2f%n",
                 node.getWidth(), node.getHeight(), node.getX(), node.getY());
    }
}
