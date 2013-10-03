package org.skye.hadoop;

import org.junit.Test;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.hadoop.stores.HdfsArchiveStore;

public class HdfsArchiveStoreTest {

    @Test
    public void testHdfsConfig() {
        HdfsArchiveStore testStore = new HdfsArchiveStore();
        ArchiveStoreDefinition definition = new ArchiveStoreDefinition();
        definition.getProperties().put(HdfsArchiveStore.HDFS_CONFIG, "/home/atcmostafavi/hbase-conf");
        definition.getProperties().put(HdfsArchiveStore.HDFS_FS_SITE, "hdfs://ec2-54-200-72-73.us-west-2.compute.amazonaws.com:8020");
        testStore.initialize(definition);
    }
}