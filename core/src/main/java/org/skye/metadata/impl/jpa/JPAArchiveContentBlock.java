package org.skye.metadata.impl.jpa;

import lombok.Data;
import org.skye.core.ArchiveContentBlock;
import org.skye.stores.StoreRegistry;

import javax.persistence.Embeddable;
import java.util.UUID;

/**
 * A JPA-Embeddable representation of an {@link ArchiveContentBlock}, complete with
 * methods to translate between the standard {@link ArchiveContentBlock} and a
 * persistent, embeddable class.
 * <p/>
 * Note that this object should either be injected with a {@link StoreRegistry}
 * using either a requestStaticInjection call in a GuiceBerry Module, or it
 * should be assigned directly, before attempting translations
 * between the two types.
 */
@Embeddable
@Data
public class JPAArchiveContentBlock {

    private String id = UUID.randomUUID().toString();
    private String archiveStoreDefinitionId = "";

    public JPAArchiveContentBlock() {
        // Do Nothing
    }

    public JPAArchiveContentBlock(ArchiveContentBlock acb) {
        if (acb.getId() != null)
            this.id = acb.getId();

        this.archiveStoreDefinitionId = acb.getArchiveStoreDefinitionId();
    }

    /**
     * Converts this instance into a {@link ArchiveContentBlock}.
     *
     * @return A copy of this instance as an {@link ArchiveContentBlock}.
     */
    public ArchiveContentBlock toArchiveContentBlock() {
        ArchiveContentBlock acb = new ArchiveContentBlock();

        acb.setId(this.getId());
        acb.setArchiveStoreDefinitionId(this.archiveStoreDefinitionId);

        return acb;
    }
}
