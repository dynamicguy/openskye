package org.openskye.task.step;

import com.google.common.base.Optional;
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

    @Inject
    protected StoreRegistry storeRegistry;
    @Inject
    protected ObjectMetadataRepository omr;

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

    public static TaskStep fromTask(Task task) {
        switch (task.getTaskType()) {
            case ARCHIVE:
                return new ArchiveTaskStep(task);
            case DISCOVER:
                return new DiscoverTaskStep(task);
            case EXTRACT:
                return new ExtractTaskStep(task);
            case DESTROY:
                return new DestroyTaskStep(task);
            default:
                throw new SkyeException("Unsupported task type " + task.getTaskType());
        }
    }

}
