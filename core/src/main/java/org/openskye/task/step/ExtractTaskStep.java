package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openskye.core.*;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Task;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;

import javax.inject.Inject;

/**
 * A simple implementation of the discover task type
 */
@NoArgsConstructor
public class ExtractTaskStep extends AbstractTaskStep {
    @Getter
    @Setter
    private String objectSetId;
    @Getter
    @Setter
    private InformationStoreDefinition targetInformationStoreDefinition;

    public ExtractTaskStep(String objectSetId,InformationStoreDefinition targetInformationStoreDefinition) {
        this.objectSetId = objectSetId;
        this.targetInformationStoreDefinition = targetInformationStoreDefinition;
        this.projectId = targetInformationStoreDefinition.getProject().getId();
    }

    @Override
    public String getLabel() {
        return "EXTRACT";
    }

    @Override
    public void validate() {
        if (objectSetId == null) {
            throw new SkyeException("Task " + task.getId() + " is missing an object set id");
        }
        if (targetInformationStoreDefinition == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a target information store definition");
        }
    }

    @Override
    public TaskStatus call() throws Exception {
        Optional<ObjectSet> objectSet = omr.getObjectSet(objectSetId);
        Optional<InformationStore> targetInformationStore = storeRegistry.build(targetInformationStoreDefinition);

        if (targetInformationStore.isPresent()) {
            if (objectSet.isPresent()) {
                for (ObjectMetadata om : omr.getObjects(objectSet.get())) {
                    if (om.getArchiveContentBlocks().size() > 0) {
                        // Lets just get the first ACB
                        ArchiveContentBlock acb = om.getArchiveContentBlocks().get(0);
                        Optional<ArchiveStore> archiveStore = storeRegistry.build(omr.getArchiveStoreDefinition(acb));
                        if (archiveStore.isPresent()) {
                            Optional<SimpleObject> simpleObject = archiveStore.get().getSimpleObject(om);
                            if (simpleObject.isPresent()) {
                                targetInformationStore.get().put(simpleObject.get());
                            } else {
                                throw new SkyeException("Unable to get simple object from archive content block " + acb);
                            }
                        } else {
                            throw new SkyeException("Unable to build the archive store from definition " + omr.getArchiveStoreDefinition(acb));
                        }

                    } else {
                        throw new SkyeException("Missing an archive content block for " + om);
                    }

                }
            } else {
                throw new SkyeException("Unable to find object set with id " + objectSetId);
            }
        } else {
            throw new SkyeException("Unable to build target information store from definition " + targetInformationStoreDefinition.getId());
        }

        return TaskStatus.COMPLETED;
    }

}
