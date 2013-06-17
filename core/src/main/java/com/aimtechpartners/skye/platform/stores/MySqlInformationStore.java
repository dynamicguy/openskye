package com.aimtechpartners.skye.platform.stores;

import com.aimtechpartners.skye.platform.*;
import com.aimtechpartners.skye.platform.util.JdbcCloser;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of an {@link InformationStore} for the MySQL
 * database
 */
public class MySqlInformationStore implements InformationStore<DataSet> {

    private final static Logger LOGGER = LoggerFactory.getLogger(MySqlInformationStore.class);
    private final String name;
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public MySqlInformationStore(String name, String jdbcUrl, String username, String password) {
        this.name = name;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;

        LOGGER.debug("Created MySQL information store");
    }

    @Override
    public Map<String, String> getMetadata() {
        Connection conn = null;
        Map<String, String> metadata = new HashMap();
        try {
            conn = DriverManager.getConnection(jdbcUrl,
                    username, password);

            DatabaseMetaData dbmd = conn.getMetaData();
            metadata.put(MetadataConstants.PRODUCT_NAME, dbmd.getDatabaseProductName());
            metadata.put(MetadataConstants.PRODUCT_VERSION, dbmd.getDatabaseProductVersion());
            metadata.put(MetadataConstants.DRIVER_VERSION, dbmd.getDriverVersion());

            LOGGER.info("MySQL information store has " + metadata);
            return metadata;
        } catch (Exception e) {
            throw new SkyePlatformException("Exception while getting all datasets from MySQL information store", e);
        } finally {
            JdbcCloser.closeConn(conn);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUrl() {
        return jdbcUrl;
    }

    @Override
    public Iterable<DataSet> getSince(DateTime dateTime) {
        return Lists.newArrayList();
    }

    @Override
    public Iterable<DataSet> getAll() {
        Connection conn = null;
        ResultSet rs = null;
        List<DataSet> dataSets = Lists.newArrayList();
        try {
            conn = DriverManager.getConnection(jdbcUrl,
                    username, password);
            rs = conn.getMetaData().getTables(null, null, "%", null);
            while (rs.next()) {
                String tableName = rs.getString(3);

                DataSet newDataset = new DataSet();
                newDataset.setPath(tableName);
                newDataset.setRowCount(getRowCount(conn, tableName));
                newDataset.setDataSetType(DataSetType.TABLE);

                dataSets.add(newDataset);
                LOGGER.debug("Data set " + newDataset);
            }


            return dataSets;

        } catch (Exception e) {
            throw new SkyePlatformException("Exception while getting all datasets from MySQL information store", e);
        } finally {
            JdbcCloser.closeRs(rs);
            JdbcCloser.closeConn(conn);
        }
    }

    //
    // A little helper to get row counts for a table on a connection
    //
    private long getRowCount(Connection conn, String tableName) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM " + tableName);

        while (rs.next())
            return rs.getInt(1);

        return 0;
    }

    @Override
    public InputStream getStream(DataSet simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
