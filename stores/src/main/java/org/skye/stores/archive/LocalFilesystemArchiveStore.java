package org.skye.stores.archive;

import org.omg.CORBA.portable.InputStream;
import org.skye.core.ArchiveStore;
import org.skye.core.SimpleObject;
import org.skye.core.SkyeException;
import org.skye.domain.DomainArchiveStore;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.ObjectMetadataSearch;
import org.skye.stores.information.JDBCStructuredObject;

import javax.inject.Inject;

/**
 * An implementation of an {@link ArchiveStore} that simply uses the local
 * filesystem to store archives
 */
public class LocalFilesystemArchiveStore implements ArchiveStore {

    public final static String IMPLEMENTATION = "localFS";
    private DomainArchiveStore domainArchiveStore;
    @Inject
    private ObjectMetadataRepository omr;
    @Inject
    private ObjectMetadataSearch oms;

    @Override
    public void initialize(DomainArchiveStore das) {
        this.domainArchiveStore = das;
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getUrl() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSupported(SimpleObject so) {
        return so instanceof JDBCStructuredObject;
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);
    }

    @Override
    public void put(SimpleObject simpleObject) {
        if (!isSupported(simpleObject)) {
            throw new SkyeException("Unsupported simple object type " + simpleObject.getClass().getName());
        }

        // Lets try and put it
        if (simpleObject instanceof JDBCStructuredObject) {
            putJDBCStructuredObject((JDBCStructuredObject) simpleObject);
        }

        oms.index(simpleObject);
        omr.put(simpleObject);
    }

    private void putJDBCStructuredObject(JDBCStructuredObject simpleObject) {
    }

    @Override
    public InputStream getStream(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
