package org.skye.stores.inmemory;

import org.joda.time.DateTime;
import org.skye.core.InformationStore;
import org.skye.core.SimpleObject;

import java.util.Properties;

/**
 * An in-memory {@link InformationStore} that can be used for testing
 */
public class InMemoryInformationStore implements InformationStore {

    public static final String IMPLEMENTATION = "In-memory";

    @Override
    public void initialize(Properties properties) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Properties getMetadata() {
        Properties properties = new Properties();
        properties.setProperty("state", "in-memory");
        return properties;
    }

    @Override
    public String getName() {
        return "In-memory (for testing only)";
    }

    @Override
    public String getUrl() {
        return "mem://.";
    }

    @Override
    public Iterable<SimpleObject> getSince(DateTime dateTime) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<SimpleObject> getAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<SimpleObject> getChildren(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
