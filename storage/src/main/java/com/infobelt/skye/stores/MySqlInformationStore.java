package com.infobelt.skye.stores;

import com.google.common.collect.Lists;
import com.infobelt.skye.platform.DataSet;
import com.infobelt.skye.platform.InformationStore;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

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
