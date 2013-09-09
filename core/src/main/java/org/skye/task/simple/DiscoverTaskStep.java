package org.skye.task.simple;

import org.skye.core.*;
import org.skye.domain.AttributeInstance;
import org.skye.domain.ChannelFilterDefinition;
import org.skye.domain.Task;
import org.skye.filters.ChannelFilter;
import org.skye.filters.ChannelFilterFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of the discover task type
 */
public class DiscoverTaskStep extends AbstractTaskStep {

    private final Task task;
    private List<ChannelFilter> filters = new ArrayList<>();

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

        for (ChannelFilterDefinition cfd : task.getChannel().getChannelFilters()) {
            filters.add(ChannelFilterFactory.getInstance(cfd));
        }

    }

    private void discover(InformationStore is, Iterable<SimpleObject> simpleObjects, Task task) {
        for (SimpleObject simpleObject : simpleObjects) {

            if (simpleObject instanceof ContainerObject)
                discover(is, is.getChildren(simpleObject), task);
            else {
                if (isIncludedByFilter(simpleObject)) {
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

    /**
     * Helper method that looks at the {@link org.skye.domain.ChannelFilterDefinition} for the
     * {@link org.skye.domain.Channel} to ensure that the simple object should be picked up
     *
     * @param simpleObject simple object to check
     * @return true if the simple object should be included
     */
    private boolean isIncludedByFilter(SimpleObject simpleObject) {
        for (ChannelFilter filter : filters) {
            if (filter.isFiltered(simpleObject.getObjectMetadata())) {
                if (filter.isInclude())
                    return true;
                if (!filter.isInclude()) {
                    return false;
                }
            }
        }
        return true;
    }

}
