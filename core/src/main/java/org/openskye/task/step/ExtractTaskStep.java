package org.openskye.task.step;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;

/**
 * A simple implementation of the discover task type
 */
@NoArgsConstructor
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
    private Node node;
    @Getter
    @Setter
    private InformationStoreDefinition targetInformationStoreDefinition;

    public ExtractTaskStep(String objectSetId, InformationStoreDefinition targetInformationStoreDefinition, Node node) {
        this.objectSetId = objectSetId;
        this.channel = null;
        this.targetInformationStoreDefinition = targetInformationStoreDefinition;
        this.node = node;
    }

    public ExtractTaskStep(Channel channel, InformationStoreDefinition targetInformationStoreDefinition, Node node) {
        this.objectSetId = null;
        this.channel = channel;
        this.targetInformationStoreDefinition = targetInformationStoreDefinition;
        this.node = node;
    }

    @Override
    public Project getProject() {
        return targetInformationStoreDefinition.getProject();
    }

    @Override
    public String getLabel() {
        return "EXTRACT";
    }

    @Override
    public void rehydrate() {
        if (channel.getInformationStoreDefinition() == null) {
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
    public TaskStatus call() throws Exception {
        Optional<ObjectSet> objectSet;
        if (objectSetId != null) {
            objectSet = omr.getObjectSet(objectSetId);
        } else {
            objectSet = Optional.absent();
        }

        Optional<InformationStore> targetInformationStore = storeRegistry.build(targetInformationStoreDefinition);

        if (targetInformationStore.isPresent()) {
            Iterable<ObjectMetadata> objectMetadataIterable;
            if (objectSet.isPresent()) {
                objectMetadataIterable = omr.getObjects(objectSet.get());
            } else {
                objectMetadataIterable = omr.getObjects(channel.getInformationStoreDefinition());
            }
            for (ObjectMetadata om : objectMetadataIterable) {
                if (om.getArchiveContentBlocks().size() > 0) {
                    // Lets just get the first ACB
                    ArchiveContentBlock acb = om.getArchiveContentBlocks().get(0);
                    Optional<ArchiveStore> archiveStore = storeRegistry.build(omr.getArchiveStoreInstance(acb));
                    if (archiveStore.isPresent()) {
                        Optional<SimpleObject> simpleObject = archiveStore.get().getSimpleObject(om);
                        if (simpleObject.isPresent()) {
                            targetInformationStore.get().put(simpleObject.get());
                        }
                    } else {
                        throw new SkyeException("Unable to build the archive store from definition " + omr.getArchiveStoreInstance(acb));
                    }

                } else {
                    throw new SkyeException("Missing an archive content block for " + om);
                }

            }

        } else {
            throw new SkyeException("Unable to build target information store from definition " + targetInformationStoreDefinition);
        }

        return TaskStatus.COMPLETED;
    }

}
