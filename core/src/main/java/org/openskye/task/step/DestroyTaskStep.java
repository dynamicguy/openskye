package org.openskye.task.step;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.openskye.core.*;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.Task;
import org.openskye.stores.StoreRegistry;

/**
 * A simple implementation of the destroy task type
 */
public class DestroyTaskStep extends AbstractTaskStep {

    private final Task task;
    @Inject
    private StoreRegistry storeRegistry;

    public DestroyTaskStep(Task task) {
        this.task = task;
    }

    @Override
    public void validate() {
        if (task.getObjectSetId() == null) {
            throw new SkyeException("Task " + task.getId() + " is missing an object set id");
        }
        if (task.getTargetInformationStoreDefinition() == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a target information store definition");
        }
    }

    @Override
    public void start() {
        Optional<ObjectSet> objectSet = omr.getObjectSet(task.getObjectSetId());

        Optional<InformationStore> targetInformationStore = storeRegistry.build(task.getTargetInformationStoreDefinition());

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
                throw new SkyeException("Unable to find object set with id " + task.getObjectSetId());
            }
        } else {
            throw new SkyeException("Unable to build target information store from definition " + task.getTargetInformationStoreDefinition());
        }
    }

}
