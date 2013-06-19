package com.aimtechpartners.skye.stores;

import com.aimtechpartners.skye.platform.DataSet;
import com.aimtechpartners.skye.platform.InformationStore;
import com.aimtechpartners.skye.platform.MetadataConstants;
import com.aimtechpartners.skye.platform.SkyePlatformException;
import com.aimtechpartners.skye.stores.util.JdbcCloser;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * An implementation of an {@link InformationStore} for the MySQL
 * database
 */
public class MySqlInformationStore extends AbstractJdbcInformationStore {

    private final static Logger LOGGER = LoggerFactory.getLogger(MySqlInformationStore.class);

    @Override
    public Iterable<DataSet> getSince(DateTime dateTime) {
        return Lists.newArrayList();
    }
}
