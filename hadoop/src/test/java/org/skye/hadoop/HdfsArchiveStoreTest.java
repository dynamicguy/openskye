package org.skye.hadoop;

import org.junit.Ignore;
import org.junit.Test;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.hadoop.stores.HdfsArchiveStore;

public class HdfsArchiveStoreTest {

    /**
     * TODO these tests aren't really functional?
     */
    @Test
    @Ignore
    public void testHdfsConfig() {
        HdfsArchiveStore testStore = new HdfsArchiveStore();
        ArchiveStoreDefinition definition = new ArchiveStoreDefinition();
        definition.getProperties().put(HdfsArchiveStore.HDFS_CONFIG, "/home/atcmostafavi/hbase-conf");
        testStore.initialize(definition);
    }
}