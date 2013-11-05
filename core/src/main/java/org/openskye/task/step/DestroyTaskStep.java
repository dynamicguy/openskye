package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.openskye.core.*;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Task;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;
import org.openskye.stores.StoreRegistry;

/**
 * A simple implementation of the destroy task type
 */
public class DestroyTaskStep extends AbstractTaskStep {
    @Getter
    @Setter
    private String objectSetId;
    @Getter
    @Setter
    private InformationStoreDefinition targetInformationStoreDefinition;

    @JsonIgnore
    @Inject
    private StoreRegistry storeRegistry;

    public DestroyTaskStep(String objectSetId,InformationStoreDefinition targetInformationStoreDefinition) {
        this.objectSetId = objectSetId;
        this.targetInformationStoreDefinition = targetInformationStoreDefinition;
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
    public void start() {
        Optional<ObjectSet> objectSet = omr.getObjectSet(objectSetId);
        Optional<InformationStore> targetInformationStore = storeRegistry.build(targetInformationStoreDefinition);

        if (targetInformationStore.isPresent()) {
            if (objectSet.isPresent()) {
                for (ObjectMetadata om : omr.getObjects(objectSet.get())) {
                    for (ArchiveContentBlock acb : om.getArchiveContentBlocks()) {
                        // TODO do we need to check if this ACB is in use by another
                        // object metadata
                        ArchiveStoreDefinition asd = omr.getArchiveStoreDefinition(acb);
                        Optional<ArchiveStore> archiveStore = storeRegistry.build(asd);
                        if (archiveStore.isPresent()) {
                            archiveStore.get().destroy(om);
                        } else {
                            throw new SkyeException("Unable to build archive store " + archiveStore);
                        }
                    }
                }
            } else {
                throw new SkyeException("Unable to find object set with id " + objectSetId);
            }
        } else {
            throw new SkyeException("Unable to build target information store from definition " + targetInformationStoreDefinition.getId());
        }
    }

}
