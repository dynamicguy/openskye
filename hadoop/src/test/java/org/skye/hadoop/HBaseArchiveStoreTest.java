package org.skye.hadoop;

import org.junit.Test;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.hadoop.stores.HBaseArchiveStore;

public class HBaseArchiveStoreTest {

    @Test
    public void testHBaseConfig(){
        HBaseArchiveStore testStore = new HBaseArchiveStore();
        ArchiveStoreDefinition definition = new ArchiveStoreDefinition();
        definition.getProperties().put(HBaseArchiveStore.HBASE_SITE, "/home/atcmostafavi/hbase-conf/hbase-site.xml");
        testStore.initialize(definition);
    }
}
