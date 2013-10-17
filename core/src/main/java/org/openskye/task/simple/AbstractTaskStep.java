package org.openskye.task.simple;

import com.google.common.base.Optional;
import org.openskye.core.ArchiveStore;
import org.openskye.core.InformationStore;
import org.openskye.core.SkyeException;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
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

}
