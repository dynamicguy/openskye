package com.aimtechpartners.skye.platform.stores;

import com.aimtechpartners.skye.platform.DataSet;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Some basic tests around the MySQL store
 */
public class MySqlStoreTest {

    private MySqlInformationStore mySqlStore;

    @BeforeClass
    public void setUp() {
        String jdbcUrl = "jdbc:mysql://address=(protocol=tcp)(host=localhost)(port=3306)(user=root)/confluence";
        mySqlStore = new MySqlInformationStore("test", jdbcUrl, "root", "");
    }

    @Test
    public void getMetadata() {
        // First up lets see if we can see the simple objects
        // that we created in the test case
        mySqlStore.getMetadata();
    }

    @Test
    public void allSimpleObjects() {
        // First up lets see if we can see the simple objects
        // that we created in the test case
        List<DataSet> dataSets = Lists.newArrayList(mySqlStore.getAll());
    }

}
