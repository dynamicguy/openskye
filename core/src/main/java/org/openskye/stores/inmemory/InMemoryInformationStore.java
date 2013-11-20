package org.openskye.stores.inmemory;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.openskye.core.InformationStore;
import org.openskye.core.InvalidSimpleObjectException;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SimpleObject;
import org.openskye.domain.InformationStoreDefinition;

import java.util.*;

/**
 * An in-memory {@link InformationStore} that can be used for testing
 */
public class InMemoryInformationStore implements InformationStore {

    public static final String IMPLEMENTATION = "In-memory";
    private Map<ObjectMetadata, SimpleObject> store = new HashMap<>();
    private List<SimpleObject> root = new ArrayList<>();
    private InformationStoreDefinition informationStoreDefinition;

    @Override
    public void initialize(InformationStoreDefinition dis) {
        this.informationStoreDefinition = dis;

        return;
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
    public String getImplementation() {
        return IMPLEMENTATION;
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
    public Iterable<SimpleObject> getRoot() {
        return root;
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

    @Override
    public SimpleObject materialize(ObjectMetadata objectMetadata) throws InvalidSimpleObjectException {
        return store.get(objectMetadata);
    }

    @Override
    public Optional<InformationStoreDefinition> getInformationStoreDefinition() {
        if (this.informationStoreDefinition == null)
            return Optional.absent();

        return Optional.of(this.informationStoreDefinition);
    }

    @Override
    public void put(SimpleObject simpleObject) {
        root.add(simpleObject);
    }
}
