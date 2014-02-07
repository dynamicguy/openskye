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
    @JsonIgnore
    Exception searchException = null;
    @JsonIgnore
    int searchFailures = 0;

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

        for (SimpleObject object : simpleObjects) {

            if (object instanceof ContainerObject) {

                // this is a pure container, such as a directory
                discover(is, is.getChildren(object), task);

            } else if ( isIncludedByFilter(object) ) {

                ObjectMetadata om = object.getObjectMetadata();
                if ( ! omr.isObjectInOMR(om) ) {
                    // this file has either never been discovered for this project, or has
                    // a different checksum than the last time it was found
                    if (object instanceof UnstructuredCompressedObject) {
                        // this is a regular file which is also a container, such as a zip archive
                        omr.put(om);
                        discover(is, ((UnstructuredCompressedObject) object).getObjectsContained(), task);
                    } else {
                        // this is a regular file which is not a container, such as a PDF document
                        task.getStatistics().incrementSimpleObjectsFound();
                        for (AttributeInstance attrInstance : getChannel().getAttributeInstances()) {
                            om.getMetadata().put(attrInstance.getAttributeDefinition().getShortLabel(), attrInstance.getAttributeValue());
                        }
                        if (getChannel().getRetentionPolicy() != null) {
                            om.getMetadata().put(MetadataConstants.RETENTION_POLICY_ID, channel.getRetentionPolicy().getId());
                        }
                        om.setTaskId(task.getId());
                        omr.put(om);
                        auditObject(object, ObjectEvent.DISCOVERED);
                    }
                }

                try {
                    oms.index(om);
                    task.getStatistics().incrementSimpleObjectsProcessed();
                } catch (Exception e) {
                    searchFailures++;
                    if ( searchException == null ) {
                        // save the first exception thrown by OMS
                        searchException = e;
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
            boolean matches = filter.isFiltered(simpleObject.getObjectMetadata());
            if ( matches != filter.isInclude() ) {
                // Exclude the object if:
                // - the filter doesn't match the object, and it's an include filter
                // - OR the filter does match the object, but it's an exclude filter
                return false;
            }
        }
        return true;
    }


}
