package org.openskye.task.step;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openskye.core.*;
import org.openskye.domain.Channel;
import org.openskye.domain.ChannelArchiveStore;
import org.openskye.domain.TaskStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link TaskStep} that handles a task type of Archive
 */
@NoArgsConstructor
public class ArchiveTaskStep extends TaskStep {
    @Getter
    @Setter
    private Channel channel;

    public ArchiveTaskStep(Channel channel) {
        this.channel = channel;
        this.projectId = channel.getProject().getId();
    }

    @Override
    public String getLabel() {
        return "ARCHIVE";
    }

    @Override
    public void validate() {
        if (getChannel() == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a channel and so can not archive");
        }
    }

    @Override
    public TaskStatus call() throws Exception {

        // Build up the information and archive stores
        InformationStore is = buildInformationStore(channel.getInformationStoreDefinition());
        Map<ChannelArchiveStore, ArchiveStoreWriter> channelStoreWriters = new HashMap<>();
        for (ChannelArchiveStore cas : channel.getChannelArchiveStores()) {
            channelStoreWriters.put(cas, buildArchiveStore(cas.getArchiveStoreDefinition()).getWriter(task));
        }

        // Based on the fact that we have done discovery then we will
        // look for all SimpleObject's that from the OMR
        for (ObjectMetadata objectMetadata : getObjectMetadataIterator()) {
            objectMetadata.setTaskId(task.getId());
            try {
                SimpleObject simpleObject = is.materialize(objectMetadata);
                task.getStatistics().incrementSimpleObjectsIngested();
                for (ChannelArchiveStore cas : channel.getChannelArchiveStores()) {
                    channelStoreWriters.get(cas).put(simpleObject);
                }
                // After we have ingested we need to update the
                // object metadata again
                omr.put(simpleObject.getObjectMetadata());
            } catch (InvalidSimpleObjectException e) {
                throw new SkyeException("Unable to materialize object " + objectMetadata, e);
            }
        }

        return TaskStatus.COMPLETED;
    }

    private Iterable<ObjectMetadata> getObjectMetadataIterator() {
        if (task.getParentTask() == null)
            return omr.getObjects(channel.getInformationStoreDefinition());
        else
            return omr.getObjects(task.getParentTask());
    }
}
