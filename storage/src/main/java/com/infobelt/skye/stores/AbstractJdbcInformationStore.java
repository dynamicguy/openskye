package com.infobelt.skye.stores;

import com.google.common.collect.Lists;
import com.infobelt.skye.platform.*;
import com.infobelt.skye.stores.util.JdbcCloser;
import com.infobelt.skye.stores.util.JdbcProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;
import java.util.Properties;

/**
 * A common base for an {@link InformationStore} based on JDBC.
 * <p/>
 * Note that this type of abstract information store must use a {@link DataSet}
 */
public abstract class AbstractJdbcInformationStore implements InformationStore<DataSet> {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractJdbcInformationStore.class);
    protected JdbcProperties storeProperties;

    @Override
    public void initialize(Properties properties) {
        storeProperties = new JdbcProperties(properties);
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

    protected Connection getJdbcConnection() throws SQLException {
        return DriverManager.getConnection(storeProperties.getJdbcUrl(),
                storeProperties.getUsername(), storeProperties.getPassword());
    }

    @Override
    public Iterable<DataSet> getAll() {
        Connection conn = null;
        ResultSet rs = null;
        List<DataSet> dataSets = Lists.newArrayList();
        try {
            conn = getJdbcConnection();
            String[] types = {"TABLE"};
            rs = conn.getMetaData().getTables(null, null, "%", types);
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

    public Properties getMetadata() {
        Connection conn = null;
        Properties metadata = new Properties();
        try {
            conn = getJdbcConnection();
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
        return storeProperties.getName();
    }

    @Override
    public String getUrl() {
        return storeProperties.getJdbcUrl();
    }
}
