package org.skye.stores.archive.localfs;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.skye.core.*;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.ObjectMetadataSearch;
import org.skye.stores.archive.filters.ZipCompressionFilter;
import org.skye.stores.information.jdbc.JDBCStructuredObject;

import javax.inject.Inject;
import java.io.*;

/**
 * An implementation of an {@link ArchiveStore} that simply uses the local
 * filesystem to store archives
 */
@Slf4j
public class LocalFSArchiveStore implements ArchiveStore {

    public final static String IMPLEMENTATION = "localFS";
    public static final String LOCALFS_PATH = "localFsPath";
    public static final String LOCALFS_TMP_PATH = "localFsTmpPath";
    private ArchiveStoreDefinition archiveStoreDefinition;
    @Inject
    private ObjectMetadataRepository omr;
    @Inject
    private ObjectMetadataSearch oms;
    private String localPath;
    @Inject
    private Injector injector;
    private String tmpPath;

    @Override
    public void initialize(ArchiveStoreDefinition das) {
        this.archiveStoreDefinition = das;
        this.localPath = das.getArchiveStoreInstance().getProperties().get(LOCALFS_PATH);

        this.tmpPath = das.getArchiveStoreInstance().getProperties().get(LOCALFS_TMP_PATH);

        if (this.localPath == null)
            this.localPath = "/tmp/" + das.getId() + "/archives";
        if (this.tmpPath == null)
            this.tmpPath = "/tmp/" + das.getId() + "/tmp";

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
        return archiveStoreDefinition.getName() + " (Local filesystem)";
    }

    @Override
    public String getUrl() {
        return "localFS://" + archiveStoreDefinition.getId();
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
    public Optional<InputStream> getStream(ObjectMetadata metadata) {
        try {
            if (metadata.getArchiveContentBlock(this).isPresent()) {
                InputStream is = new FileInputStream(getSimpleObjectPath(metadata.getArchiveContentBlock(this).get()));
                return Optional.of(is);
            } else return Optional.absent();
        } catch (FileNotFoundException e) {
            throw new SkyeException("ACB references storage, but unable to find archive file?");
        }
    }

    @Override
    public Optional<SimpleObject> getSimpleObject(ObjectMetadata metadata) {

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectStreamFilter> getFilters() {
        return ImmutableList.of((ObjectStreamFilter) new ZipCompressionFilter());
    }

    @Override
    public Optional<ArchiveStoreDefinition> getArchiveStoreDefinition() {
        if (this.archiveStoreDefinition == null)
            return Optional.absent();

        return Optional.of(this.archiveStoreDefinition);
    }

    @Override
    public void destroy(ObjectMetadata om) {
        if (om.getArchiveContentBlock(this).isPresent()) {
            getSimpleObjectPath(om.getArchiveContentBlock(this).get()).delete();

        }
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getTempPath() {
        return tmpPath;
    }

    public File getSimpleObjectPath(ArchiveContentBlock acb) {
        File simpleObjectDir = new File(getLocalPath() + "/" + acb.getId());
        log.info("Storing object with ACB [" + getLocalPath() + "/" + acb.getId() + "]");

        if (simpleObjectDir.exists())
            throw new SkyeException("Simple object already archived? " + acb);
        simpleObjectDir.mkdirs();
        simpleObjectDir.delete();
        return simpleObjectDir;
    }

    public File getTempSimpleObjectPath(ArchiveContentBlock acb) {
        File simpleObjectDir = new File(getTempPath() + "/" + acb.getId());
        log.info("Storing temp object with ACB [" + getTempPath() + "/" + acb.getId() + "]");

        if (simpleObjectDir.exists())
            throw new SkyeException("Simple object already in temp? " + acb);
        simpleObjectDir.mkdirs();
        simpleObjectDir.delete();
        return simpleObjectDir;
    }
}
