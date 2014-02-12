package org.openskye.task.step;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.domain.dao.TaskDAO;

import java.util.*;

/**
 * A {@link TaskStep} that handles a task type of Archive
 */
@NoArgsConstructor
@Slf4j
public class ArchiveTaskStep extends TaskStep {
    @Inject
    private ChannelDAO channelDAO;
    @Inject
    private TaskDAO taskDAO;
    @Getter
    @Setter
    private Channel channel;

    public ArchiveTaskStep(Channel channel, Node node) {
        this.channel = channel;
        setNode(node);
    }

    @Override
    public Project getStepProject() {
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
        if (channel.getInformationStoreDefinition() == null) {
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
    protected TaskStatus doStep() throws Exception {

        log.debug("Starting archive task " + task);
        if (task.getStatistics() == null)
            task.setStatistics(new TaskStatistics());

        // Build up the information and archive stores
        log.debug("Starting archive task step on " + channel);
        InformationStore is = buildInformationStore(channel.getInformationStoreDefinition());
        Map<ChannelArchiveStore, ArchiveStoreWriter> channelStoreWriters = new HashMap<>();
        for (ChannelArchiveStore cas : channel.getChannelArchiveStores()) {
            log.debug("Adding archive store writer for " + cas);
            channelStoreWriters.put(cas, buildArchiveStore(cas.getArchiveStoreDefinition().getArchiveStoreInstance()).getWriter(task));
        }

        // Based on the fact that we have done discovery then we will
        // look for all SimpleObject's that from the OMR
        for (ObjectMetadata objectMetadata : getObjectMetadataIterator()) {
            objectMetadata.setTaskId(task.getId());
            try {
                SimpleObject simpleObject = is.materialize(objectMetadata);
                task.getStatistics().incrementSimpleObjectsProcessed();
                for (ChannelArchiveStore cas : channel.getChannelArchiveStores()) {
                    channelStoreWriters.get(cas).put(simpleObject);
                    auditObject(simpleObject, ObjectEvent.ARCHIVED);
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
        // Find all objects discovered through the specified channel that have not yet been archived

        List<ObjectMetadata> objects = new ArrayList<>();
        for ( ObjectMetadata om : omr.getObjects(channel.getInformationStoreDefinition()) ) {
            if ( readyToArchive(om) ) {
                objects.add(om);
            }
            if ( (Runtime.getRuntime().totalMemory() * 100) / Runtime.getRuntime().maxMemory() > 90 ) {
                // Don't have enough memory to keep building this huge object list .. run another archive task later
                break;
            }
        }
        Collections.sort(objects, new Comparator<ObjectMetadata>() { //orders based on implementation
            @Override
            public int compare(ObjectMetadata o1, ObjectMetadata o2) {
                if (o1.getImplementation().equals(o2.getImplementation())) {
                    return 0;
                } else try {
                    if (Class.forName(o1.getImplementation()).getSuperclass().equals(UnstructuredCompressedObject.class)) {
                        return -1;
                    }

                } catch (Exception e) {
                    throw new SkyeException("Skye Exception", e);
                }
                return 1;
            }
        });

        return objects;
    }

    private boolean readyToArchive(ObjectMetadata om) {
        String omStoreId = om.getInformationStoreId();
        InformationStoreDefinition channelStore = channel.getInformationStoreDefinition();
        if ( omStoreId == null || channelStore == null || ! omStoreId.equals(channelStore.getId()) ) {
            return false;
        }
        // Make sure there are no ACBs for this object, before calling it 'ready to archive'
        List<ArchiveContentBlock> acbList = om.getArchiveContentBlocks();
        return ( acbList == null || acbList.size() == 0 );
    }
}
