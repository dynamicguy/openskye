package org.skye.task.simple;

import com.google.common.base.Optional;
import org.skye.core.*;
import org.skye.domain.Task;
import org.skye.filters.ChannelFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of the destroy task type
 */
public class DestroyTaskStep extends AbstractTaskStep {

    private final Task task;
    private List<ChannelFilter> filters = new ArrayList<>();

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
                        acb.getArchiveStore().destroy(om);
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
