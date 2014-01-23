package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.filters.ChannelFilter;
import org.openskye.filters.ChannelFilterFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of the discover task type
 */
@NoArgsConstructor
public class DiscoverTaskStep extends TaskStep {

    @Inject
    private ChannelDAO channelDAO;
    @Getter
    @Setter
    private Channel channel;
    @JsonIgnore
    private List<ChannelFilter> filters = new ArrayList<>();

    public DiscoverTaskStep(Channel channel, Node node) {
        this.channel = channel;
        setNode(node);
    }

    @Override
    public Project getStepProject() {
        return channel.getProject();
    }

    @Override
    public String getLabel() {
        return "DISCOVER";
    }

    @Override
    public void rehydrate() {
        if (channel.getInformationStoreDefinition() == null) {
            channel = channelDAO.get(channel.getId()).get();
        }
    }

    @Override
    public void validate() {
        if (channel == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a channel and so can not discover");
        }
    }

    @Override
    protected TaskStatus doStep() throws Exception {

        InformationStore is = buildInformationStore(channel.getInformationStoreDefinition());

        for (ChannelFilterDefinition cfd : channel.getChannelFilters()) {
            filters.add(ChannelFilterFactory.getInstance(cfd));
        }

        discover(is, is.getRoot(), task);

        return TaskStatus.COMPLETED;
    }

    private void discover(InformationStore is, Iterable<SimpleObject> simpleObjects, Task task) {

        if (task.getStatistics() == null)
            task.setStatistics(new TaskStatistics());

        for (SimpleObject simpleObject : simpleObjects) {
            if (!omr.isObjectInOMR(simpleObject.getObjectMetadata()) || task.getProject().isDuplicationAllowed()) {  //is the object in the OMR already?
                if (simpleObject instanceof ContainerObject)
                    discover(is, is.getChildren(simpleObject), task);
                else if (simpleObject instanceof UnstructuredCompressedObject) {
                    omr.put(simpleObject.getObjectMetadata());
                    discover(is, ((UnstructuredCompressedObject) simpleObject).getObjectsContained(), task);
                } else {
                    if (isIncludedByFilter(simpleObject)) {
                        task.getStatistics().incrementSimpleObjectsFound();

                        ObjectMetadata om = simpleObject.getObjectMetadata();
                        for (AttributeInstance attrInstance : getChannel().getAttributeInstances()) {
                            om.getMetadata().put(attrInstance.getAttributeDefinition().getShortLabel(), attrInstance.getAttributeValue());
                        }
                        if (getChannel().getRetentionPolicy() != null) {
                            om.getMetadata().put(MetadataConstants.RETENTION_POLICY_ID, channel.getRetentionPolicy().getId());
                        }
                        om.setTaskId(task.getId());
                        omr.put(om);
                        oms.index(om);
                        auditObject(simpleObject, ObjectEvent.DISCOVERED);
                    }
                }
            }

        }
    }

    /**
     * Helper method that looks at the {@link org.openskye.domain.ChannelFilterDefinition} for the {@link
     * org.openskye.domain.Channel} to ensure that the simple object should be picked up
     *
     * @param simpleObject simple object to check
     *
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
