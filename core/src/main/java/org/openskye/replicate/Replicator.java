package org.openskye.replicate;

import org.openskye.domain.Node;
import org.openskye.domain.Project;

/**
 * A replicator is a simple process that can be provided by an {@link org.openskye.core.ArchiveStore},
 * when used the replicator will simply ensure that all ACB's from the primary node are also on the
 * secondary node.
 * <p/>
 * You only provide the node you are wanting to replicate to,  the primary is defined by the
 * Node relationships to the ArchiveStoreInstance
 */
public interface Replicator {

    void replicate(Node secondary, Project project);
}
