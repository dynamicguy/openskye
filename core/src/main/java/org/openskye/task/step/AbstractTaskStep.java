package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import lombok.Getter;
import lombok.Setter;
import org.openskye.core.ArchiveStore;
import org.openskye.core.InformationStore;
import org.openskye.core.SkyeException;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Task;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.stores.StoreRegistry;

import javax.inject.Inject;

/**
 * An abstract base for the {@link TaskStep}
 */
public abstract class AbstractTaskStep implements TaskStep {

    @JsonIgnore
    @Getter
    @Setter
    Task task;
    @JsonIgnore
    @Inject
    protected StoreRegistry storeRegistry;
    @JsonIgnore
    @Inject
    protected ObjectMetadataRepository omr;

    public Task toTask() {
        // Create a new Task object from this step
        task = new Task();
        task.setStep(this);
        return task;
    }

    protected InformationStore buildInformationStore(InformationStoreDefinition dis) {
        Optional<InformationStore> is = storeRegistry.build(dis);
        if (!is.isPresent())
            throw new SkyeException("Unable to build information store");
        return is.get();
    }

    protected ArchiveStore buildArchiveStore(ArchiveStoreDefinition archiveStoreDefinition) {
        Optional<ArchiveStore> as = storeRegistry.build(archiveStoreDefinition);
        if (!as.isPresent())
            throw new SkyeException("Unable to build archive store");
        return as.get();
    }

}
