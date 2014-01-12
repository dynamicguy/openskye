package org.openskye.stores.archive.localfs;

import lombok.extern.slf4j.Slf4j;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.SkyeException;
import org.openskye.domain.Node;
import org.openskye.domain.NodeArchiveStoreInstance;
import org.openskye.domain.NodeRole;
import org.openskye.domain.Project;
import org.openskye.node.NodeManager;
import org.openskye.replicate.Replicator;

/**
 * An implementation of a {@link org.openskye.replicate.Replicator} for the {@link org.openskye.stores.archive.localfs.LocalFSArchiveStore}
 */
@Slf4j
public class LocalFSReplicator implements Replicator {

    private final LocalFSArchiveStore archiveStore;

    public LocalFSReplicator(LocalFSArchiveStore localFSArchiveStore) {
        log.info("Creating replicator for " + localFSArchiveStore);
        this.archiveStore = localFSArchiveStore;
    }

    @Override
    public void replicate(Node targetNode, Project project) {
        // First we need to identify the node that is the primary
        // for this archive store

        log.debug("Replicator starting on node " + targetNode);

        checkNode(targetNode);

        Node primaryNode = getPrimaryNode();

        log.debug("Primary node is " + primaryNode);

        // Find all the ACB's that are on the primary but missing from the secondary

        Iterable<ArchiveContentBlock> acbs = archiveStore.getOmr().getACBsForReplication(primaryNode, NodeManager.getNode(), project);

        // Move the ACB's that are missing from the primary to the secondary
        for (ArchiveContentBlock acb : acbs) {
            log.debug("Replicating ACB " + acb);
            archiveStore.copyACB(acb, primaryNode, NodeManager.getNode());
        }

        log.debug("Replicator complete");

    }

    private void checkNode(Node targetNode) {
        log.debug("Checking target node is " + targetNode + " and we are node " + NodeManager.getNode());
        if (!targetNode.equals(NodeManager.getNode())) {
            throw new SkyeException("You must run replication on the target node");
        }

        for (NodeArchiveStoreInstance nodeInstance : archiveStore.getArchiveStoreInstance().getNodes()) {
            if (targetNode.equals(nodeInstance.getNode())) {
                if (!NodeRole.REPLICA.equals(nodeInstance.getNodeRole()))
                    throw new SkyeException("Replication can not be run on a node that does not have the role REPLICA for the given archive store, this looks like it is the primary");
                return;
            }
        }
        throw new SkyeException("Replication must be run on a node with a role of REPLICA for the given archive store");
    }

    public Node getPrimaryNode() {
        log.info("Archive store instance has " + archiveStore.getArchiveStoreInstance().getNodes().size() + " nodes");
        for (NodeArchiveStoreInstance nodeInstance : archiveStore.getArchiveStoreInstance().getNodes()) {
            if (NodeRole.PRIMARY.equals(nodeInstance.getNodeRole())) {
                log.debug("Identified primary as " + nodeInstance);
                return nodeInstance.getNode();
            }
        }
        throw new SkyeException("No primary node has been defined");
    }
}
