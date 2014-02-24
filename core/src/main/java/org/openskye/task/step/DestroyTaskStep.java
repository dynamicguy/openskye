package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;
import org.openskye.stores.StoreRegistry;

/**
 * A simple implementation of the destroy task type.
 */
@NoArgsConstructor
public class DestroyTaskStep extends TaskStep {

    @Inject
    private InformationStoreDefinitionDAO informationStoreDefinitionDAO;
    @Getter
    @Setter
    private String objectSetId;
    @Getter
    @Setter
    private InformationStoreDefinition informationStoreDefinition;
    @JsonIgnore
    @Inject
    private StoreRegistry storeRegistry;

    public DestroyTaskStep(String objectSetId, InformationStoreDefinition targetInformationStoreDefinition, Node node) {
        this.objectSetId = objectSetId;
        this.informationStoreDefinition = targetInformationStoreDefinition;
        setNode(node);
    }

    @Override
    public Project getStepProject() {
        return informationStoreDefinition.getProject();
    }

    @Override
    public String getLabel() {
        return "DESTROY";
    }

    @Override
    public void rehydrate() {
        if (informationStoreDefinition.getImplementation() == null) {
            informationStoreDefinition = informationStoreDefinitionDAO.get(informationStoreDefinition.getId()).get();
        }
    }

    @Override
    public void validate() {
        if (objectSetId == null) {
            throw new SkyeException("Task " + task.getId() + " is missing an object set id");
        }
        if (informationStoreDefinition == null) {
            throw new SkyeException("Task " + task.getId() + " is missing an information store definition");
        }
    }

    @Override
    protected TaskStatus doStep() throws Exception {

        Optional<ObjectSet> objectSet;
        if (objectSetId != null) {
            objectSet = omr.getObjectSet(objectSetId);
        } else {
            objectSet = Optional.absent();
        }

        Optional<InformationStore> targetInformationStore = storeRegistry.build(informationStoreDefinition);

        if (targetInformationStore.isPresent()) {
            Iterable<ObjectMetadata> omIterator;
            if (objectSet.isPresent()) {
                omIterator = omr.getObjects(objectSet.get());
            } else {
                omIterator = omr.getObjects(informationStoreDefinition);
            }
            for (ObjectMetadata om : omIterator) {
                for (ArchiveContentBlock acb : om.getArchiveContentBlocks()) {
                    Optional<ArchiveStore> archiveStore = storeRegistry.build(acb.getArchiveStoreInstance());
                    if (archiveStore.isPresent()) {
                        if (getLatestEvent(om).get() != ObjectEvent.DESTROYED) {
                            archiveStore.get().destroy(om);
                            auditObject(om, ObjectEvent.DESTROYED);
                        } else {
                            toLog("Object "+om+" already destroyed");
                        }
                    } else {
                        throw new SkyeException("Unable to build archive store " + archiveStore);
                    }
                }
            }

        } else {
            throw new SkyeException("Unable to build information store from definition " + informationStoreDefinition);
        }

        return TaskStatus.COMPLETED;
    }

}
