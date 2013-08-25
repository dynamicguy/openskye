package org.skye.task.inmemory;

import org.skye.core.ArchiveStore;
import org.skye.core.InformationStore;
import org.skye.core.SimpleObject;
import org.skye.core.SkyeException;
import org.skye.domain.ChannelArchiveStore;
import org.skye.domain.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link TaskStep} that handles a task type of Archive
 */
public class ArchiveTaskStep extends AbstractTaskStep {

    private final Task task;
    private Map<ChannelArchiveStore, ArchiveStore> channelStoreMap = new HashMap<>();

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
            channelStoreMap.put(cas, buildArchiveStore(cas.getDomainArchiveStore()));
        }

        // Based on the fact that we have done discovery then we will
        // look for all SimpleObject's that from the OMR

        for (SimpleObject simpleObject : omr.getSimpleObjects(task.getChannel().getDomainInformationStore())) {
            simpleObject.setIngested(true);
            simpleObject.setTaskId(task.getId());
            for (ChannelArchiveStore cas : task.getChannel().getChannelArchiveStores()) {
                channelStoreMap.get(cas).put(simpleObject);
            }
        }
    }

}
