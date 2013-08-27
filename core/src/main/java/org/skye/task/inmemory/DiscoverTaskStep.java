package org.skye.task.inmemory;

import org.skye.core.ContainerObject;
import org.skye.core.InformationStore;
import org.skye.core.SimpleObject;
import org.skye.core.SkyeException;
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
        discover(is, is.getRoot());
    }

    private void discover(InformationStore is, Iterable<SimpleObject> simpleObjects) {
        for (SimpleObject simpleObject : simpleObjects) {
            if (simpleObject instanceof ContainerObject)
                discover(is, is.getChildren(simpleObject));
            else {
                task.getStatistics().incrementSimpleObjectsDiscovered();
                omr.put(simpleObject);
            }

        }
    }

}
