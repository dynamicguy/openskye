package org.openskye.stores.information.jdbc;

import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.eobjects.metamodel.DataContext;
import org.eobjects.metamodel.DataContextFactory;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.joda.time.DateTime;
import org.openskye.core.*;
import org.openskye.domain.InformationStoreDefinition;

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
 * within the database as a set of {@link org.openskye.core.StructuredObject}
 */
@Slf4j
public class JDBCStructuredInformationStore implements InformationStore {

    public final static String IMPLEMENTATION = "jdbcStructured";
    public static final String DRIVER_CLASS = "driverClass";
    public static final String DB_URL = "dbUrl";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    private InformationStoreDefinition informationStoreDefinition;
    private UpdateableDataContext dataContext;

    protected Connection getConnection() {
        try {
            Class.forName(informationStoreDefinition.getProperties().get(DRIVER_CLASS));

            log.info("Connecting to database...");
            return DriverManager.getConnection(informationStoreDefinition.getProperties().get(DB_URL), informationStoreDefinition.getProperties().get(USER), informationStoreDefinition.getProperties().get(PASSWORD));
        } catch (ClassNotFoundException e) {
            throw new SkyeException("Unable to find driver class", e);
        } catch (SQLException e) {
            throw new SkyeException("Unable to connect to database due to exception on URL " + informationStoreDefinition.getProperties().get(DB_URL), e);
        }
    }

    @Override
    public void initialize(InformationStoreDefinition dis) {
        this.informationStoreDefinition = dis;
    }

    @Override
    public Properties getMetadata() {
        Properties dbProperties = new Properties();
        try {
            Connection connection = getConnection();
            dbProperties.setProperty("databaseMajorVersion", String.valueOf(connection.getMetaData().getDatabaseMajorVersion()));
            dbProperties.setProperty("databaseMinorVersion", String.valueOf(connection.getMetaData().getDatabaseMinorVersion()));
            dbProperties.setProperty("databaseProductName", String.valueOf(connection.getMetaData().getDatabaseProductName()));
            dbProperties.setProperty("databaseProductVersion", String.valueOf(connection.getMetaData().getDatabaseProductVersion()));

            return dbProperties;
        } catch (SQLException e) {
            throw new SkyeException("Unable to gather database metadata", e);
        }
    }

    @Override
    public String getName() {
        return informationStoreDefinition.getName();
    }

    @Override
    public String getUrl() {
        return informationStoreDefinition.getProperties().get(DB_URL);
    }

    @Override
    public Iterable<SimpleObject> getSince(DateTime dateTime) {
        throw new UnsupportedOperationException("Unable to perform change track by time");
    }

    @Override
    public Iterable<SimpleObject> getRoot() {
        DataContext dataContext = getDataContext();
        List<SimpleObject> schemaObjects = new ArrayList<>();
        for (Schema schema : dataContext.getSchemas()) {
            ContainerObject container = new ContainerObject();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setPath(schema.getName());
            metadata.setImplementation(JDBCStructuredObject.class.getCanonicalName());
            metadata.setInformationStoreId(this.getInformationStoreDefinition().get().getId());
            container.setObjectMetadata(metadata);
            schemaObjects.add(container);
        }
        return schemaObjects;
    }

    @Override
    public Iterable<SimpleObject> getChildren(SimpleObject simpleObject) {
        if (simpleObject instanceof ContainerObject) {
            DataContext dataContext = getDataContext();
            Schema schema = dataContext.getSchemaByName(simpleObject.getObjectMetadata().getPath());
            if (schema != null) {
                List<SimpleObject> tableObjects = new ArrayList<>();
                for (Table table : schema.getTables()) {
                    JDBCStructuredObject structuredObject = new JDBCStructuredObject(dataContext, table);
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setImplementation(JDBCStructuredObject.class.getCanonicalName());
                    metadata.setPath(schema.getName() + "/" + table.getName());
                    metadata.setInformationStoreId(this.getInformationStoreDefinition().get().getId());
                    structuredObject.setObjectMetadata(metadata);
                    tableObjects.add(structuredObject);
                }
                return tableObjects;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public Iterable<SimpleObject> getRelated(SimpleObject simpleObject) {
        return new ArrayList<>();
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);
    }

    @Override
    public SimpleObject materialize(ObjectMetadata objectMetadata) throws InvalidSimpleObjectException {
        if (!objectMetadata.getImplementation().equals(JDBCStructuredObject.class.getCanonicalName())) {
            throw new InvalidSimpleObjectException();
        }
        DataContext dataContext = getDataContext();
        Schema schema = dataContext.getSchemaByName(objectMetadata.getPath().split("/")[0]);
        Table table = schema.getTableByName(objectMetadata.getPath().split("/")[1]);
        JDBCStructuredObject structuredObject = new JDBCStructuredObject(dataContext, table);
        structuredObject.setObjectMetadata(objectMetadata);
        return structuredObject;
    }

    @Override
    public Optional<InformationStoreDefinition> getInformationStoreDefinition() {
        if (this.informationStoreDefinition == null)
            return Optional.absent();

        return Optional.of(this.informationStoreDefinition);
    }

    @Override
    public void put(SimpleObject simpleObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public UpdateableDataContext getDataContext() {
        if (this.dataContext == null)
            this.dataContext = DataContextFactory.createJdbcDataContext(getConnection());
        return this.dataContext;
    }
}
