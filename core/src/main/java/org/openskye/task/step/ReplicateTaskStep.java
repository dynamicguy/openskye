package org.openskye.task.step;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.ChannelDAO;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link org.openskye.task.step.TaskStep} that handles a task type of Replication
 */
@NoArgsConstructor
@Slf4j
public class ReplicateTaskStep extends TaskStep {

    @Inject
    private ChannelDAO channelDAO;
    @Getter
    @Setter
    private Channel channel;

    public ReplicateTaskStep(Channel channel) {
        this.channel = channel;
    }

    public Project getProject() {
        return channel.getProject();
    }

    @Override
    public String getLabel() {
        return "ARCHIVE";
    }

    @Override
    public void rehydrate() {
        // When we come back form JSON we have the id
        // but we need the entity
        if ( channel.getInformationStoreDefinition() == null ) {
            setChannel(channelDAO.get(channel.getId()).get());
        }
    }

    @Override
    public void validate() {
        if (getChannel() == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a channel and so can not archive");
        }
    }

    @Override
    public TaskStatus call() throws Exception {

        log.debug("Starting archive task " + task);
        if (task.getStatistics() == null)
            task.setStatistics(new TaskStatistics());

        beginTransaction();

        // Build up the information and archive stores
        log.debug("Starting archive task step on " + channel);
        InformationStore is = buildInformationStore(channel.getInformationStoreDefinition());
        Map<ChannelArchiveStore, ArchiveStoreWriter> channelStoreWriters = new HashMap<>();
        for (ChannelArchiveStore cas : channel.getChannelArchiveStores()) {
            log.debug("Adding archive store writer for " + cas);
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

        commitTransaction();
        return TaskStatus.COMPLETED;
    }

    private Iterable<ObjectMetadata> getObjectMetadataIterator() {
        if (task.getParentTask() == null)
            return omr.getObjects(channel.getInformationStoreDefinition());
        else
            return omr.getObjects(task.getParentTask());
    }
}
