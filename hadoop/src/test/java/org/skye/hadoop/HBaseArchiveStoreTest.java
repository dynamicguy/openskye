package org.skye.hadoop;

import org.junit.Test;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.hadoop.stores.HBaseArchiveStore;

import javax.inject.Inject;
import static org.hamcrest.MatcherAssert.assertThat;

public class HBaseArchiveStoreTest {

    @Test
    public void testHBaseConfig(){
        HBaseArchiveStore testStore = null;
        ArchiveStoreDefinition definition = new ArchiveStoreDefinition();

        definition.getProperties().put(HBaseArchiveStore.HBASE_CONFIG_PATH, "/hbase-site.xml");
        definition.getProperties().put(HBaseArchiveStore.HBASE_POOL_SIZE, "1000");

        testStore.initialize(definition);
    }
}
