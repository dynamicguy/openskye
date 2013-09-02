package org.skye.task.inmemory;

import org.skye.core.*;
import org.skye.domain.AttributeInstance;
import org.skye.domain.Task;

/**
 * A simple implementation of the discover task type
 */
public class DiscoverTaskStep extends AbstractTaskStep {

    private final Task task;

    public DiscoverTaskStep(Task task) {
        this.task = task;
    }

    @Override
    public void validate() {
        if (task.getChannel() == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a channel and so can not discover");
        }
    }

    @Override
    public void start() {
        InformationStore is = buildInformationStore(task.getChannel().getDomainInformationStore());
        discover(is, is.getRoot(), task);
    }

    private void discover(InformationStore is, Iterable<SimpleObject> simpleObjects, Task task) {
        for (SimpleObject simpleObject : simpleObjects) {
            if (simpleObject instanceof ContainerObject)
                discover(is, is.getChildren(simpleObject), task);
            else {
                this.task.getStatistics().incrementSimpleObjectsDiscovered();

                ObjectMetadata om = simpleObject.getObjectMetadata();
                for (AttributeInstance attrInstance : task.getChannel().getAttributeInstances()) {
                    om.getMetadata().put(attrInstance.getAttributeDefinition().getShortLabel(), attrInstance.getAttributeValue());
                }
                om.setTaskId(task.getId());
                om.setProject(task.getProject());
                omr.put(om);
            }

        }
    }

}
