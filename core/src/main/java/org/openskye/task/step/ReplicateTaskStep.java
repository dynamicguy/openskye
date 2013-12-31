package org.openskye.task.step;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.ArchiveStore;
import org.openskye.core.SkyeException;
import org.openskye.domain.*;
import org.openskye.domain.dao.ArchiveStoreDefinitionDAO;
import org.openskye.domain.dao.NodeDAO;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.replicate.Replicator;
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
    private NodeDAO nodeDAO;
    @Getter
    @Setter
    private Node node;
    @Getter
    @Setter
    private Project project;
    @Inject
    private ArchiveStoreDefinitionDAO archiveStoreDefinitionDAO;
    private ArchiveStoreDefinition archiveStoreDefinition;

    public ReplicateTaskStep(Project project, Node node) {
        this.node = node;
        this.project = project;
    }

    public ArchiveStoreDefinition getArchiveStoreDefinition() {
        return archiveStoreDefinition;
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

        Optional<ArchiveStore> optionalArchiveStore = storeRegistry.build(archiveStoreDefinition.getArchiveStoreInstance());
        if (!optionalArchiveStore.isPresent())
            throw new SkyeException("Unable to get archive store for " + archiveStoreDefinition);

        ArchiveStore archiveStore = optionalArchiveStore.get();

        Optional<Replicator> replicator = archiveStore.getReplicator();

        if (replicator.isPresent()) {
            beginTransaction();

            replicator.get().replicate(node, getProject());

            commitTransaction();
            return TaskStatus.COMPLETED;
        } else {
            return TaskStatus.FAILED;
        }
    }

    @Override
    protected Project getStepProject() {
        return getProject();
    }
}
