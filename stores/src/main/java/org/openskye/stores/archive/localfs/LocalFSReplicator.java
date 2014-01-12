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
        this.archiveStore = localFSArchiveStore;
    }

    @Override
    public void replicate(Node secondary, Project project) {
        // First we need to identify the node that is the primary
        // for this archive store

        log.debug("Replicator starting ");


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

    public Node getPrimaryNode() {
        for (NodeArchiveStoreInstance nodeInstance : archiveStore.getArchiveStoreInstance().getNodes()) {
            if (NodeRole.PRIMARY.equals(nodeInstance.getNodeRole())) {
                return nodeInstance.getNode();
            }
        }
        throw new SkyeException("No primary node has been defined");
    }
}
