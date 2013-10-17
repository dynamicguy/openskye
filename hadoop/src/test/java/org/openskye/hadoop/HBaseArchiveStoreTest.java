package org.openskye.hadoop;

import org.junit.Ignore;
import org.junit.Test;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.hadoop.stores.HBaseArchiveStore;

public class HBaseArchiveStoreTest {

    /**
     * TODO these tests need to be able to run on a build server?
     */
    @Test
    @Ignore
    public void testHBaseConfig() {
        HBaseArchiveStore testStore = new HBaseArchiveStore();
        ArchiveStoreDefinition definition = new ArchiveStoreDefinition();
        definition.getProperties().put(HBaseArchiveStore.HBASE_SITE, "/etc/hbase/conf/hbase-site.xml");
        testStore.initialize(definition);
        System.out.println(testStore.getUrl());
    }
}


