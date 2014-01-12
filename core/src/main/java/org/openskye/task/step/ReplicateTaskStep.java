package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.ArchiveStore;
import org.openskye.core.SkyeException;
import org.openskye.domain.*;
import org.openskye.domain.dao.NodeDAO;
import org.openskye.domain.dao.ProjectDAO;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.replicate.Replicator;
import org.openskye.stores.StoreRegistry;

/**
 * A {@link org.openskye.task.step.TaskStep} that handles a task type of Replication
 */
@NoArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicateTaskStep extends TaskStep {

    @Inject
    private ObjectMetadataRepository omr;
    @Inject
    private StoreRegistry storeRegistry;
    @Inject
    private NodeDAO nodeDAO;
    @Inject
    private ProjectDAO projectDAO;
    @JsonProperty
    @Getter
    @Setter
    private Project project;

    public ReplicateTaskStep(Project project, Node node) {
        setNode(node);
        this.project = project;
    }

    @Override
    public String getLabel() {
        return "REPLICATE";
    }

    @Override
    public void rehydrate() {
        // When we come back form JSON we have the id
        // but we need the entity
        project = projectDAO.get(project.getId()).get();

    }

    @Override
    public void validate() {

    }

    @Override
    public TaskStatus call() throws Exception {

        log.debug("Starting replication task " + task);
        if (task.getStatistics() == null)
            task.setStatistics(new TaskStatistics());

        for (ArchiveStoreDefinition asd : project.getArchiveStores()) {
            log.debug("Replicating archive store definition " + asd);
            Optional<ArchiveStore> optionalArchiveStore = storeRegistry.build(asd.getArchiveStoreInstance());
            if (!optionalArchiveStore.isPresent())
                throw new SkyeException("Unable to get archive store for " + asd);
            ArchiveStore archiveStore = optionalArchiveStore.get();

            Optional<Replicator> replicator = archiveStore.getReplicator();
            if (replicator.isPresent()) {
                log.debug("Beginning replication with  " + replicator);

                beginTransaction();

                replicator.get().replicate(getNode(), getProject());

                commitTransaction();
                log.debug("Ended replication with  " + replicator);

            }
        }

        log.debug("Replication complete");

        return TaskStatus.COMPLETED;
    }

    @Override
    protected Project getStepProject() {
        return getProject();
    }
}
