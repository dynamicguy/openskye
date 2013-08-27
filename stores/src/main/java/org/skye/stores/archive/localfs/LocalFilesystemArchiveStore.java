package org.skye.stores.archive.localfs;

import org.omg.CORBA.portable.InputStream;
import org.skye.core.ArchiveStore;
import org.skye.core.ArchiveStoreWriter;
import org.skye.core.SimpleObject;
import org.skye.domain.DomainArchiveStore;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.ObjectMetadataSearch;
import org.skye.stores.information.jdbc.JDBCStructuredObject;

import javax.inject.Inject;

/**
 * An implementation of an {@link ArchiveStore} that simply uses the local
 * filesystem to store archives
 */
public class LocalFilesystemArchiveStore implements ArchiveStore {

    public final static String IMPLEMENTATION = "localFS";
    public static final String LOCALFS_PATH = "localFsPath";
    private DomainArchiveStore domainArchiveStore;
    @Inject
    private ObjectMetadataRepository omr;
    @Inject
    private ObjectMetadataSearch oms;
    private String localPath;

    @Override
    public void initialize(DomainArchiveStore das) {
        this.domainArchiveStore = das;
        this.localPath = das.getProperties().get(LOCALFS_PATH);
    }

    @Override
    public String getName() {
        return domainArchiveStore.getName() + " (Local filesystem)";
    }

    @Override
    public String getUrl() {
        return "localFS://" + domainArchiveStore.getId();
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
    public ArchiveStoreWriter getWriter(Task task) {
        return new LocalFilesystemArchiveWriter(task, this);
    }

    @Override
    public InputStream getStream(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getLocalPath() {
        return localPath;
    }
}
