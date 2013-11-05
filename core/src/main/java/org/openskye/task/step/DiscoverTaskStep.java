package org.openskye.task.step;

import org.openskye.core.*;
import org.openskye.domain.AttributeInstance;
import org.openskye.domain.ChannelFilterDefinition;
import org.openskye.domain.Task;
import org.openskye.filters.ChannelFilter;
import org.openskye.filters.ChannelFilterFactory;

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
        InformationStore is = buildInformationStore(task.getChannel().getInformationStoreDefinition());

        for (ChannelFilterDefinition cfd : task.getChannel().getChannelFilters()) {
            filters.add(ChannelFilterFactory.getInstance(cfd));
        }

        discover(is, is.getRoot(), task);
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
     * Helper method that looks at the {@link org.openskye.domain.ChannelFilterDefinition} for the
     * {@link org.openskye.domain.Channel} to ensure that the simple object should be picked up
     *
     * @param simpleObject simple object to check
     * @return true if the simple object should be included
     */
    private boolean isIncludedByFilter(SimpleObject simpleObject) {
        for (ChannelFilter filter : filters) {
            if (filter.isFiltered(simpleObject.getObjectMetadata())) {
                return filter.isInclude();
            } else {
                return false;
            }
        }
        return true;
    }

}
