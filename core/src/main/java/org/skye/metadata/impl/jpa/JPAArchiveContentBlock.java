package org.skye.metadata.impl.jpa;

import javax.persistence.Embeddable;
import lombok.Data;

import org.skye.core.ArchiveContentBlock;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.core.ArchiveStore;
import org.skye.stores.StoreRegistry;

/**
 * A JPA-Embeddable representation of an {@link ArchiveContentBlock}, complete with
 * methods to translate between the standard {@link ArchiveContentBlock} and a
 * persistent, embeddable class.
 */
@Embeddable
@Data
public class JPAArchiveContentBlock
{
    private String id;

    /**
     * The {@link ArchiveStoreDefinition} is used instead of the
     * {@link ArchiveStore} because only the definition can be persisted.
     * Conversion is necessary between the definition and the store as
     * blocks are converted.
     */
    private ArchiveStoreDefinition archiveStoreDefinition;

    public JPAArchiveContentBlock()
    {
        this.id = "";
        this.archiveStoreDefinition = null;

        return;
    }

    public JPAArchiveContentBlock(ArchiveContentBlock acb)
    {
        this.id = acb.getId();
        this.archiveStoreDefinition = acb.getArchiveStore().getArchiveStoreDefinition().get();

        return;
    }

    public ArchiveContentBlock ToArchiveContentBlock()
    {
        ArchiveContentBlock acb = new ArchiveContentBlock();
        ArchiveStore archiveStore = null;
        StoreRegistry storeRegistry = null;

        if(this.archivestoredefinition != null)
        {
            storeRegistry = new StoreRegistry();
            archiveStore = storeRegistry.build(this.archiveStoreDefinition).get();
        }

        acb.setId(this.id);
        acb.setArchiveStore(archiveStore);

        return acb;
    }
}
