package org.skye.metadata.impl.jpa;

import com.google.common.base.Optional;
import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Rule;
import org.junit.Test;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.ArchiveStore;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.ArchiveStoreInstance;
import org.skye.stores.StoreRegistry;
import org.skye.stores.inmemory.InMemoryArchiveStore;
import org.skye.task.TaskManager;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * This class performs unit testing for the jpa package of classes, including
 * {@link JPAArchiveContentBlock}, {@link JPAObjectMetadata}, and the
 * {@link JPAObjectMetadataRepository}.
 */
public class JPAObjectMetadataTest {
    @Rule
    public final GuiceBerryRule guiceBerryRule = new GuiceBerryRule(JPATestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    public StoreRegistry storeRegistry;

}
