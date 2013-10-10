package org.skye.hadoop.stores;

import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.skye.core.*;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.Task;
import org.skye.hadoop.objects.HStructuredObject;
import org.skye.hadoop.objects.HUnstructuredObject;
import org.skye.stores.information.localfs.LocalFileUnstructuredObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * An implementation of an {@link ArchiveStore} that uses HDFS to store the {@link org.skye.core.ArchiveContentBlock}s
 */
@Slf4j
public class HdfsArchiveStore implements ArchiveStore {

    public static final String HDFS_CONFIG = "hdfs";
    public static final String IMPLEMENTATION = "hdfs";
    private Configuration hdfsConfig;
    private FileSystem hdfsFileSystem;
    private ArchiveStoreDefinition archiveStoreDefinition;

    @Override
    public void initialize(ArchiveStoreDefinition das) {
        this.archiveStoreDefinition = das;
        String path = das.getProperties().get(HDFS_CONFIG);
        hdfsConfig = new Configuration();
        try {
            hdfsFileSystem = FileSystem.get(hdfsConfig);
        } catch (IOException e) {
            log.error("HDFS config file not found");
            throw new SkyeException("HDFS config file not found", e);
        }

    }

    @Override
    public String getName() {
        return archiveStoreDefinition.getName() + " (HDFS)";
    }

    @Override
    public String getUrl() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSupported(SimpleObject so) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);   //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArchiveStoreWriter getWriter(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<InputStream> getStream(ObjectMetadata metadata) {
        try {
            if (metadata.getArchiveContentBlock(this.getArchiveStoreDefinition().get().getId()).isPresent()) {
                InputStream is = hdfsFileSystem.open(hdfsFileSystem.getWorkingDirectory());
                return Optional.of(is);
            } else return Optional.absent();
        } catch (IOException e) {
            throw new SkyeException("ACB references storage, but unable to find archive file?");
        }
    }

    @Override
    public Optional<SimpleObject> getSimpleObject(ObjectMetadata metadata) {
        if (this.getStream(metadata).isPresent()) {
            if (metadata.getImplementation().equals(HStructuredObject.class.getCanonicalName())) {
                if (metadata.getArchiveContentBlock(this.getArchiveStoreDefinition().get().getId()).isPresent()) {

                } else {
                    log.debug("Unable to find ACB for archive store " + this.getArchiveStoreDefinition());
                    return Optional.absent();
                }
            } else if (metadata.getImplementation().equals(HUnstructuredObject.class.getCanonicalName())) {
                if (metadata.getArchiveContentBlock(this.getArchiveStoreDefinition().get().getId()).isPresent()) {
                    SimpleObject obj = new LocalFileUnstructuredObject();
                    obj.setObjectMetadata(metadata);
                    return Optional.of(obj);
                } else {
                    log.debug("Unable to find ACB for archive store " + this.getArchiveStoreDefinition());
                    return Optional.absent();
                }
            } else {
                throw new SkyeException("Object type not supported");
            }

        } else {
            throw new SkyeException("Input stream not present");
        }
       return null;
    }

    @Override
    public Iterable<ObjectStreamFilter> getFilters() {
        return new ArrayList<>();
    }

    @Override
    public Optional<ArchiveStoreDefinition> getArchiveStoreDefinition() {
        if (this.archiveStoreDefinition == null) {
            return Optional.absent();
        } else {
            return Optional.of(this.archiveStoreDefinition);
        }
    }

    @Override
    public void destroy(ObjectMetadata om) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
