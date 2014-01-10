package org.openskye.node;

import org.openskye.core.SkyeException;
import org.openskye.domain.Node;

/**
 * The Node Manager is a simple mechanism that allows us to
 * find out which node we are running on.
 * <p/>
 * The premisse is that each JVM instance is running on a single node
 */
public class NodeManager {

    private static Node node;

    public static Node getNode() {
        if (node == null)
            throw new SkyeException("Node could not be identified");
        else
            return node;
    }

    public static void setNode(Node newNode) {
        node = newNode;
    }
}
