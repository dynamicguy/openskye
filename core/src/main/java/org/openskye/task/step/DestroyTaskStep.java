package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;
import org.openskye.stores.StoreRegistry;

/**
 * A simple implementation of the destroy task type
 */
@NoArgsConstructor
public class DestroyTaskStep extends TaskStep {

    @Inject
    private InformationStoreDefinitionDAO informationStoreDefinitionDAO;
    @Getter
    @Setter
    private String objectSetId;
    @Getter
    @Setter
    private InformationStoreDefinition targetInformationStoreDefinition;
    @JsonIgnore
    @Inject
    private StoreRegistry storeRegistry;
    @Getter
    @Setter
    private Node node;

    public DestroyTaskStep(String objectSetId, InformationStoreDefinition targetInformationStoreDefinition, Node node) {
        this.objectSetId = objectSetId;
        this.targetInformationStoreDefinition = targetInformationStoreDefinition;
        this.node = node;
    }

    public Project getProject() {
        return targetInformationStoreDefinition.getProject();
    }

    @Override
    public String getLabel() {
        return "DESTROY";
    }

    @Override
    public void rehydrate() {
        if (targetInformationStoreDefinition.getImplementation() == null) {
            targetInformationStoreDefinition = informationStoreDefinitionDAO.get(targetInformationStoreDefinition.getId()).get();
        }
    }

    @Override
    public void validate() {
        if (objectSetId == null) {
            throw new SkyeException("Task " + task.getId() + " is missing an object set id");
        }
        if (targetInformationStoreDefinition == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a target information store definition");
        }
    }

    @Override
    public TaskStatus call() throws Exception {
        beginTransaction();

        Optional<ObjectSet> objectSet;
        if (objectSetId != null) {
            objectSet = omr.getObjectSet(objectSetId);
        } else {
            objectSet = Optional.absent();
        }

        Optional<InformationStore> targetInformationStore = storeRegistry.build(targetInformationStoreDefinition);

        if (targetInformationStore.isPresent()) {
            Iterable<ObjectMetadata> omIterator;
            if (objectSet.isPresent()) {
                omIterator = omr.getObjects(objectSet.get());
            } else {
                omIterator = omr.getObjects(targetInformationStoreDefinition);
            }
            for (ObjectMetadata om : omIterator) {
                for (ArchiveContentBlock acb : om.getArchiveContentBlocks()) {
                    // TODO do we need to check if this ACB is in use by another
                    // object metadata
                    ArchiveStoreInstance asi = omr.getArchiveStoreInstance(acb);
                    Optional<ArchiveStore> archiveStore = storeRegistry.build(asi);
                    if (archiveStore.isPresent()) {
                        archiveStore.get().destroy(om);
                    } else {
                        throw new SkyeException("Unable to build archive store " + archiveStore);
                    }
                }
            }

        } else {
            throw new SkyeException("Unable to build target information store from definition " + targetInformationStoreDefinition);
        }

        commitTransaction();
        return TaskStatus.COMPLETED;
    }

}
