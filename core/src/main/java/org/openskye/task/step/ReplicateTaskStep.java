package org.openskye.task.step;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ArchiveStore;
import org.openskye.core.SkyeException;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.Project;
import org.openskye.domain.TaskStatistics;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.ArchiveStoreDefinitionDAO;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.node.NodeManager;
import org.openskye.stores.StoreRegistry;

/**
 * A {@link org.openskye.task.step.TaskStep} that handles a task type of Replication
 */
@NoArgsConstructor
@Slf4j
public class ReplicateTaskStep extends TaskStep {

    @Inject
    private ObjectMetadataRepository omr;
    @Inject
    private StoreRegistry storeRegistry;
    @Inject
    private ArchiveStoreDefinitionDAO archiveStoreDefinitionDAO;
    private ArchiveStoreDefinition archiveStoreDefinition;

    public ArchiveStoreDefinition getArchiveStoreDefinition() {
        return archiveStoreDefinition;
    }

    public Project getProject() {
        return null;
    }

    @Override
    public String getLabel() {
        return "REPLICATE";
    }

    @Override
    public void rehydrate() {
        // When we come back form JSON we have the id
        // but we need the entity

    }

    @Override
    public void validate() {
        if (getArchiveStoreDefinition() == null) {
            throw new SkyeException("Task " + task.getId() + " is missing an archive store definition");
        }
    }

    @Override
    public TaskStatus call() throws Exception {

        log.debug("Starting replication task " + task);
        if (task.getStatistics() == null)
            task.setStatistics(new TaskStatistics());

        Optional<ArchiveStore> optionalArchiveStore = storeRegistry.build(archiveStoreDefinition);
        if (!optionalArchiveStore.isPresent())
            throw new SkyeException("Unable to get archive store for " + archiveStoreDefinition);

        ArchiveStore archiveStore = optionalArchiveStore.get();

        beginTransaction();

        // Get the ACB's that are missing for this node
        for (ArchiveContentBlock acb : omr.getMissingAcbsForNode(NodeManager.getNode(), archiveStoreDefinition)) {
            if (acb.getArchiveStoreDefinitionId().equals(archiveStoreDefinition.getId())) {
                archiveStore.putAcb(acb);
            }
        }

        commitTransaction();
        return TaskStatus.COMPLETED;
    }

}
