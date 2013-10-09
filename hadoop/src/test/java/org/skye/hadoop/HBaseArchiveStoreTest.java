package org.skye.hadoop;

import org.junit.Ignore;
import org.junit.Test;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.hadoop.stores.HBaseArchiveStore;

public class HBaseArchiveStoreTest {

    /**
     * TODO these tests aren't really functional?
     */
    @Test
    @Ignore
    public void testHBaseConfig() {
        HBaseArchiveStore testStore = new HBaseArchiveStore();
        ArchiveStoreDefinition definition = new ArchiveStoreDefinition();
        definition.getProperties().put(HBaseArchiveStore.HBASE_SITE, "/etc/hbase/conf/hbase-site.xml");
        testStore.initialize(definition);
    }
}


