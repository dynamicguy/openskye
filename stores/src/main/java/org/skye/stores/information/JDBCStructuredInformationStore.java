package org.skye.stores.information;

import lombok.extern.slf4j.Slf4j;
import org.eobjects.metamodel.DataContext;
import org.eobjects.metamodel.DataContextFactory;
import org.eobjects.metamodel.schema.Column;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.joda.time.DateTime;
import org.skye.core.*;
import org.skye.core.structured.ColumnMetadata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A JDBC based structured information store
 * <p/>
 * This type of information store can be used with most JDBC databases and provide access to the tables
 * within the database as a set of {@link org.skye.core.StructuredObject}
 */
@Slf4j
public class JDBCStructuredInformationStore implements InformationStore {

    public final static String IMPLEMENTATION = "jdbcStructured";
    public static final String DRIVER_CLASS = "driverClass";
    public static final String DB_URL = "dbUrl";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    private Properties properties;

    @Override
    public void initialize(Properties properties) {
        this.properties = properties;
    }

    private Connection getConnection() {
        try {
            Class.forName(properties.getProperty(DRIVER_CLASS));

            //STEP 3: Open a connection
            log.info("Connecting to database...");
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SkyeException("Unable to find driver class", e);
        } catch (SQLException e) {
            throw new SkyeException("Unable to connect to database due to SQL exception", e);
        }
    }

    @Override
    public Properties getMetadata() {
        Properties dbProperties = new Properties();
        try {
            Connection connection = getConnection();
            dbProperties.setProperty("databaseMajorVersion", String.valueOf(connection.getMetaData().getDatabaseMajorVersion()));
            dbProperties.setProperty("databaseMinorVersion", String.valueOf(connection.getMetaData().getDatabaseMinorVersion()));
            dbProperties.setProperty("databaseProductName", String.valueOf(connection.getMetaData().getDatabaseProductName()));
            dbProperties.setProperty("databaseProductName", String.valueOf(connection.getMetaData().getDatabaseProductVersion()));

            return dbProperties;
        } catch (SQLException e) {
            throw new SkyeException("Unable to gather database metadata", e);
        }
    }

    @Override
    public String getName() {
        return properties.getProperty(InformationStore.NAME);
    }

    @Override
    public String getUrl() {
        return properties.getProperty(DB_URL);
    }

    @Override
    public Iterable<SimpleObject> getSince(DateTime dateTime) {
        throw new UnsupportedOperationException("Unable to perform change track by time");
    }

    @Override
    public Iterable<SimpleObject> getAll() {
        DataContext dataContext = DataContextFactory.createJdbcDataContext(getConnection());
        List<SimpleObject> schemaObjects = new ArrayList<>();
        for (Schema schema : dataContext.getSchemas()) {
            ContainerObject container = new ContainerObject();
            container.setPath(schema.getName());
            container.setId(schema.getQualifiedLabel());
            schemaObjects.add(container);
        }
        return schemaObjects;
    }

    @Override
    public Iterable<SimpleObject> getChildren(SimpleObject simpleObject) {
        if (simpleObject instanceof ContainerObject) {
            DataContext dataContext = DataContextFactory.createJdbcDataContext(getConnection());
            Schema schema = dataContext.getSchemaByName(simpleObject.getId());
            if (schema != null) {
                List<SimpleObject> tableObjects = new ArrayList<>();
                for (Table table : schema.getTables()) {
                    StructuredObject structuredObject = new StructuredObject();
                    structuredObject.setPath(schema.getName() + "/" + table.getName());
                    structuredObject.setId(table.getQualifiedLabel());
                    List<ColumnMetadata> colMetas = new ArrayList<>();
                    for (Column column : table.getColumns()) {
                        // need to work out what we need in a column?
                        ColumnMetadata colMeta = new ColumnMetadata();
                        colMeta.setName(column.getName());
                        colMeta.setSize(column.getColumnSize());
                        colMeta.setNativeType(column.getNativeType());
                        colMeta.setRemarks(column.getRemarks());
                        colMetas.add(colMeta);
                    }
                    structuredObject.setColumns(colMetas);
                    tableObjects.add(structuredObject);
                }
                return tableObjects;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public Iterable<SimpleObject> getRelated(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);
    }
}
