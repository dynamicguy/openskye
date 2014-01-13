package org.openskye.stores.archive.host;

import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.SkyeException;
import org.openskye.domain.Node;
import org.openskye.domain.NodeArchiveStoreInstance;
import org.openskye.domain.NodeRole;
import org.openskye.domain.Project;
import org.openskye.node.NodeManager;
import org.openskye.replicate.Replicator;

/**
 * An implementation of a {@link org.openskye.replicate.Replicator} for the {@link HostArchiveStore}
 */
public class HostReplicator implements Replicator {

    private final HostArchiveStore archiveStore;

    public HostReplicator(HostArchiveStore localFSArchiveStore) {
        this.archiveStore = localFSArchiveStore;
    }

    @Override
    public void replicate(Node secondary, Project project) {
        // First we need to identify the node that is the primary
        // for this archive store

        Node primaryNode = getPrimaryNode();


        // Find all the ACB's that are on the primary but missing from the secondary
        Iterable<ArchiveContentBlock> acbs = archiveStore.getOmr().getACBsForReplication(primaryNode, NodeManager.getNode(), project);

        // Move the ACB's that are missing from the primary to the secondary
        for (ArchiveContentBlock acb : acbs) {
            archiveStore.copyACB(acb, primaryNode, NodeManager.getNode());
        }

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
