package org.skye.metadata.impl.jpa;

import javax.inject.Inject;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.google.common.base.Optional;
import lombok.Data;

import org.skye.core.ArchiveContentBlock;
import org.skye.core.SkyeException;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.core.ArchiveStore;
import org.skye.stores.StoreRegistry;

/**
 * A JPA-Embeddable representation of an {@link ArchiveContentBlock}, complete with
 * methods to translate between the standard {@link ArchiveContentBlock} and a
 * persistent, embeddable class.
 *
 * Note that this object should either be injected with a {@link StoreRegistry}
 * using either a requestStaticInjection call in a GuiceBerry Module, or it
 * should be assigned directly, before attempting translations
 * between the two types.
 */
@Embeddable
@Data
public class JPAArchiveContentBlock
{
    @Inject
    @Transient
    public static StoreRegistry storeRegistry;

    private String id;

    /**
     * The {@link ArchiveStoreDefinition} is used instead of the
     * {@link ArchiveStore} because only the definition can be persisted.
     * Conversion is necessary between the definition and the store as
     * blocks are converted.
     */
    private ArchiveStoreDefinition archiveStoreDefinition;

    /**
     * Converts the persistable JPAArchiveContentBlock into a
     * {@link ArchiveContentBlock}.  Note that this method requires that
     * the storeRegistry static field be set to a valid {@link StoreRegistry},
     * either through static injection or by directly setting it.
     *
     * @return An {@link ArchiveContentBlock} translation of this instance.
     *
     * @throws SkyeException Indicates that either the storeRegistry field is
     * invalid or that the {@link ArchiveStore} for the
     * {@link ArchiveContentBlock} can't be created, perhaps due to an invalid
     * {@link ArchiveStoreDefinition}.
     */
    public ArchiveContentBlock toArchiveContentBlock()
    {
        ArchiveContentBlock acb = new ArchiveContentBlock();
        Optional<ArchiveStore> archiveStore = null;

        acb.setId(this.getId());

        if(this.getArchiveStoreDefinition() != null)
        {
            if(storeRegistry == null)
            {
                throw new SkyeException("The StoreRegistry is null.");
            }

            archiveStore = storeRegistry.build(this.getArchiveStoreDefinition());

            if(!archiveStore.isPresent())
            {
                throw new SkyeException("The ArchiveStore could not be built.");
            }

            acb.setArchiveStoreDefinitionId(archiveStore.get().getArchiveStoreDefinition().get().getId());
        }
        else
            acb.setArchiveStoreDefinitionId(null);

        return acb;
    }
}
