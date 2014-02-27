package org.openskye.task.step;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;

/**
 * A simple implementation of the discover task type
 */
@NoArgsConstructor
@Slf4j
public class ExtractTaskStep extends TaskStep {

    @Inject
    private InformationStoreDefinitionDAO informationStoreDefinitionDAO;
    @Inject
    private ChannelDAO channelDAO;
    // An extract can either be aimed at an object set, or all the objects ever ingested from
    // a selected channel
    @Getter
    @Setter
    private String objectSetId;
    @Getter
    @Setter
    private Channel channel;
    @Getter
    @Setter
    private InformationStoreDefinition targetInformationStoreDefinition;

    public ExtractTaskStep(String objectSetId, InformationStoreDefinition targetInformationStoreDefinition, Node node) {
        this.objectSetId = objectSetId;
        this.channel = null;
        this.targetInformationStoreDefinition = targetInformationStoreDefinition;
        setNode(node);
    }

    public ExtractTaskStep(Channel channel, InformationStoreDefinition targetInformationStoreDefinition, Node node) {
        this.objectSetId = null;
        this.channel = channel;
        this.targetInformationStoreDefinition = targetInformationStoreDefinition;
        setNode(node);
    }

    @Override
    public Project getStepProject() {
        return targetInformationStoreDefinition.getProject();
    }

    @Override
    public String getLabel() {
        return "EXTRACT";
    }

    @Override
    public void rehydrate() {
        if (channel != null && channel.getInformationStoreDefinition() == null) {
            channel = channelDAO.get(channel.getId()).get();
        }
        if (targetInformationStoreDefinition.getImplementation() == null) {
            targetInformationStoreDefinition = informationStoreDefinitionDAO.get(targetInformationStoreDefinition.getId()).get();
        }
    }

    @Override
    public void validate() {
        if (objectSetId == null && channel == null) {
            throw new SkyeException("Task " + task.getId() + " is missing both a channel and an object set id");
        }
        if (targetInformationStoreDefinition == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a target information store definition");
        }
    }

    @Override
    protected TaskStatus doStep() throws Exception {

        Optional<InformationStore> targetInformationStore = storeRegistry.build(targetInformationStoreDefinition);
        if (!targetInformationStore.isPresent()) {
            throw new SkyeException("Unable to build target information store from definition " + targetInformationStoreDefinition);
        }

        Iterable<ObjectMetadata> objectMetadataIterable;
        if (objectSetId != null) {
            Optional<ObjectSet> objectSet = omr.getObjectSet(objectSetId);
            if (objectSet.isPresent()) {
                objectMetadataIterable = omr.getObjects(objectSet.get());
            } else {
                throw new SkyeException("Object set " + objectSetId + " not found");
            }
        } else if (channel != null) {
            InformationStoreDefinition isd = channel.getInformationStoreDefinition();
            if (isd != null) {
                objectMetadataIterable = omr.getObjects(channel.getInformationStoreDefinition());
            } else {
                throw new SkyeException("Channel " + channel.getId() + " has no information store definition");
            }
        } else {
            throw new SkyeException("No valid object set or channel provided");
        }

        for (ObjectMetadata om : objectMetadataIterable) {
            if (!getLatestEvent(om).get().equals(ObjectEvent.DESTROYED)) {
                if (hasEvent(om, ObjectEvent.ARCHIVED)) {
                    // It should only attempt the extract if there is an associated ACB
                    if (om.getArchiveContentBlocks().size() > 0) {
                        // Lets just get the first ACB
                        ArchiveContentBlock acb = om.getArchiveContentBlocks().get(0);
                        Optional<ArchiveStore> archiveStore = storeRegistry.build(acb.getArchiveStoreInstance());
                        if (archiveStore.isPresent()) {
                            Optional<SimpleObject> simpleObject = archiveStore.get().materialize(om);
                            if (simpleObject.isPresent()) {
                                targetInformationStore.get().put(simpleObject.get());
                            }
                        } else {
                            throw new SkyeException("Unable to build the archive store from definition " + acb.getArchiveStoreInstance());
                        }

                    } else {
                        log.warn("File Skipped. Missing an archive content block for " + om);
                        //TODO add a message to show that a certain # of files were skipped
                    }
                } else {
                    log.warn("File Skipped. OM hasn't been Archived yet for " + om);
                    //TODO add a message to show that a certain # of files were skipped
                }
            }
        }

        return TaskStatus.COMPLETED;
    }

}
