package org.skye.metadata.impl.jpa;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashSet;
import javax.inject.Inject;

import com.google.common.base.Optional;
import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Rule;
import org.junit.Test;

import org.skye.core.*;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.ArchiveStoreInstance;
import org.skye.stores.StoreRegistry;
import org.skye.stores.inmemory.InMemoryArchiveStore;
import org.skye.task.TaskManager;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * This class performs unit testing for the jpa package of classes, including
 * {@link JPAArchiveContentBlock}, {@link JPAObjectMetadata}, and the
 * {@link JPAObjectMetadataRepository}.
 */
public class JPAObjectMetadataTest
{
    @Rule
    public final GuiceBerryRule guiceBerryRule = new GuiceBerryRule(JPATestModule.class);

    @Inject
    public TaskManager taskManager;

    @Inject
    public StoreRegistry storeRegistry;

    @Test
    public void testConversionToArchiveContentBlock() throws Exception
    {
        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        ArchiveStoreDefinition asd = new ArchiveStoreDefinition();
        Optional<ArchiveStore> archiveStore = null;
        JPAArchiveContentBlock jpaAcb = new JPAArchiveContentBlock();
        ArchiveContentBlock acb = null;
        final String id = "012345";
        String acbStoreId = "";

        asi.setImplementation(InMemoryArchiveStore.IMPLEMENTATION);
        asd.setArchiveStoreInstance(asi);

        jpaAcb.setArchiveStoreDefinition(asd);
        jpaAcb.setId(id);

        acb = jpaAcb.ToArchiveContentBlock();

        assertThat("The ArchiveContentBlock has a valid ArchiveStoreDefinition.", acb.getArchiveStore().getArchiveStoreDefinition().isPresent());

        acbStoreId = acb.getArchiveStore().getArchiveStoreDefinition().get().getId();

        assertThat("The ArchiveContentBlocks have the same id", acb.getId(), is(equalTo(id)));
        assertThat("The ArchiveContentBlocks have the same archive store.", acbStoreId, is(equalTo(jpaAcb.getArchiveStoreDefinition().getId())));

        return;
    }

    @Test
    public void testConversionToJPAArchiveContentBlock() throws Exception
    {
        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        ArchiveStoreDefinition asd = new ArchiveStoreDefinition();
        Optional<ArchiveStore> archiveStore = null;
        JPAArchiveContentBlock jpaAcb = null;
        ArchiveContentBlock acb = new ArchiveContentBlock();
        final String id = "012345";
        String acbStoreId = "";

        asi.setImplementation(InMemoryArchiveStore.IMPLEMENTATION);
        asd.setArchiveStoreInstance(asi);
        archiveStore = storeRegistry.build(asd);

        assertThat("An in-memory archive store can be created.", archiveStore.isPresent());

        acb.setArchiveStore(archiveStore.get());
        acb.setId(id);

        assertThat("The ArchiveContentBlock has a valid ArchiveStoreDefinition.", acb.getArchiveStore().getArchiveStoreDefinition().isPresent());

        acbStoreId = acb.getArchiveStore().getArchiveStoreDefinition().get().getId();

        jpaAcb = new JPAArchiveContentBlock(acb);

        assertThat("The ArchiveContentBlocks have the same id", jpaAcb.getId(), is(equalTo(id)));
        assertThat("The ArchiveContentBlocks have the same archive store", jpaAcb.getArchiveStoreDefinition().getId(), is(equalTo(acbStoreId)));

        return;
    }
}
