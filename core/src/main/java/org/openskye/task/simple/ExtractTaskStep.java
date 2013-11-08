package org.openskye.task.simple;

import com.google.common.base.Optional;
import org.openskye.core.*;
import org.openskye.domain.Task;

/**
 * A simple implementation of the discover task type
 */
public class ExtractTaskStep extends AbstractTaskStep {

    private final Task task;

    public ExtractTaskStep(Task task) {
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
        Optional<ObjectSet> objectSet;
        if (task.getObjectSetId() != null) {
            objectSet = omr.getObjectSet(task.getObjectSetId());
        }
        else{
            objectSet = Optional.absent();
        }

        Optional<InformationStore> targetInformationStore = storeRegistry.build(task.getTargetInformationStoreDefinition());

        if (targetInformationStore.isPresent()) {
            Iterable<ObjectMetadata> objectMetadataIterable;
            if (objectSet.isPresent()) {  //is there an objectSet
                objectMetadataIterable = omr.getObjects(objectSet.get());
            } else {
                //TODO: Add in a way to create an objectSet, either here or in DiscoverTaskStep
                objectMetadataIterable = omr.getObjects(task.getChannel().getInformationStoreDefinition());
            }
            for (ObjectMetadata om : objectMetadataIterable) {
                if (om.getArchiveContentBlocks().size() > 0) {
                    // Lets just get the first ACB
                    ArchiveContentBlock acb = om.getArchiveContentBlocks().get(0);
                    Optional<ArchiveStore> archiveStore = storeRegistry.build(omr.getArchiveStoreDefinition(acb));
                    if (archiveStore.isPresent()) {
                        Optional<SimpleObject> simpleObject = archiveStore.get().getSimpleObject(om);
                        if (simpleObject.isPresent()) {
                            targetInformationStore.get().put(simpleObject.get());
                        } else {
                            throw new SkyeException("Unable to get simple object from archive content block " + acb);
                        }
                    } else {
                        throw new SkyeException("Unable to build the archive store from definition " + omr.getArchiveStoreDefinition(acb));
                    }

                } else {
                    throw new SkyeException("Missing an archive content block for " + om);
                }

            }

        } else {
            throw new SkyeException("Unable to build target information store from definition " + task.getTargetInformationStoreDefinition());
        }
    }

}
