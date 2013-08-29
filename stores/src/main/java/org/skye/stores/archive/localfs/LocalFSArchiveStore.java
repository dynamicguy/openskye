package org.skye.stores.archive.localfs;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.omg.CORBA.portable.InputStream;
import org.skye.core.ArchiveStore;
import org.skye.core.ArchiveStoreWriter;
import org.skye.core.SimpleObject;
import org.skye.core.SkyeException;
import org.skye.domain.DomainArchiveStore;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.ObjectMetadataSearch;
import org.skye.stores.information.jdbc.JDBCStructuredObject;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * An implementation of an {@link ArchiveStore} that simply uses the local
 * filesystem to store archives
 */
@Slf4j
public class LocalFSArchiveStore implements ArchiveStore {

    public final static String IMPLEMENTATION = "localFS";
    public static final String LOCALFS_PATH = "localFsPath";
    private DomainArchiveStore domainArchiveStore;
    @Inject
    private ObjectMetadataRepository omr;
    @Inject
    private ObjectMetadataSearch oms;
    private String localPath;
    @Inject
    private Injector injector;

    @Override
    public void initialize(DomainArchiveStore das) {
        this.domainArchiveStore = das;
        this.localPath = das.getArchiveStoreInstance().getProperties().get(LOCALFS_PATH);

        log.info("Creating instance of " + this.getName());

        try {
            FileUtils.forceMkdir(new File(this.localPath));
        } catch (IOException e) {
            log.error("A problem occurred while trying to create path " + this.localPath);
            throw new SkyeException("Unable to create path for local filesystem archive store", e);
        }
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
        // We need to make sure we inject everything we need into the writer
        // since the abstract writer has some injection
        LocalFSArchiveWriter writer = new LocalFSArchiveWriter(task, this);
        injector.injectMembers(writer);
        return writer;
    }

    @Override
    public InputStream getStream(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getLocalPath() {
        return localPath;
    }
}
