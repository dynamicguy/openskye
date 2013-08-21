package org.skye.task.inmemory;

import com.google.common.base.Optional;
import org.skye.core.ArchiveStore;
import org.skye.core.InformationStore;
import org.skye.core.SkyeException;
import org.skye.domain.DomainArchiveStore;
import org.skye.domain.DomainInformationStore;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.stores.StoreRegistry;

import javax.inject.Inject;

/**
 * An abstract base for the {@link TaskStep}
 */
public abstract class AbstractTaskStep implements TaskStep {

    @Inject
    protected StoreRegistry storeRegistry;
    @Inject
    protected ObjectMetadataRepository omr;

    protected InformationStore buildInformationStore(DomainInformationStore dis) {
        Optional<InformationStore> is = storeRegistry.build(dis);
        if (!is.isPresent())
            throw new SkyeException("Unable to build information store");
        return is.get();
    }

    protected ArchiveStore buildArchiveStore(DomainArchiveStore domainArchiveStore) {
        Optional<ArchiveStore> as = storeRegistry.build(domainArchiveStore);
        if (!as.isPresent())
            throw new SkyeException("Unable to build archive store");
        return as.get();
    }

}