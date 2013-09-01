package org.skye.task.inmemory;

import org.skye.core.*;
import org.skye.domain.ChannelArchiveStore;
import org.skye.domain.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link TaskStep} that handles a task type of Archive
 */
public class ArchiveTaskStep extends AbstractTaskStep {

    private final Task task;
    private Map<ChannelArchiveStore, ArchiveStoreWriter> channelStoreWriters = new HashMap<>();

    public ArchiveTaskStep(Task task) {
        this.task = task;
    }

    @Override
    public void validate() {
        if (task.getChannel() == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a channel and so can not archive");
        }
    }

    @Override
    public void start() {

        // Build up the information and archive stores

        InformationStore is = buildInformationStore(task.getChannel().getDomainInformationStore());
        for (ChannelArchiveStore cas : task.getChannel().getChannelArchiveStores()) {
            channelStoreWriters.put(cas, buildArchiveStore(cas.getDomainArchiveStore()).getWriter(task));
        }

        // Based on the fact that we have done discovery then we will
        // look for all SimpleObject's that from the OMR

        for (ObjectMetadata objectMetadata : omr.getObjects(task.getChannel().getDomainInformationStore())) {
            objectMetadata.setIngested(true);
            objectMetadata.setTaskId(task.getId());
            try {
                SimpleObject simpleObject = is.materialize(objectMetadata);
                for (ChannelArchiveStore cas : task.getChannel().getChannelArchiveStores()) {
                    task.getStatistics().incrementSimpleObjectsIngested();
                    channelStoreWriters.get(cas).put(simpleObject);
                }
            } catch (InvalidSimpleObjectException e) {
                throw new SkyeException("Unable to materialize object " + objectMetadata, e);
            }

        }
    }

}
