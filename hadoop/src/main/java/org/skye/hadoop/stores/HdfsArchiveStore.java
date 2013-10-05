package org.skye.hadoop.stores;

import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.skye.core.*;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

/**
 * An implementation of an {@link ArchiveStore} that uses HDFS to store the {@link org.skye.core.ArchiveContentBlock}s
 */
@Slf4j
public class HdfsArchiveStore implements ArchiveStore {

    public static final String HDFS_CONFIG = "hdfs";
    public static final String HDFS_FS_SITE = "fs.defaultFS";
    public static final String IMPLEMENTATION = "hdfs";
    private Configuration hdfsConfig;
    private FileSystem hdfsFileSystem;
    private ArchiveStoreDefinition archiveStoreDefinition;


    @Override
    public void initialize(ArchiveStoreDefinition das) {
        this.archiveStoreDefinition = das;
        String path = das.getProperties().get(HDFS_CONFIG);
        Path hdfsSite = new Path(path + "/hdfs-site.xml");
        Path corePath = new Path(path + "/core-site.xml");
        hdfsConfig = new Configuration();
        hdfsConfig.addResource(corePath.toString());
        hdfsConfig.addResource(hdfsSite.toString());
        if (!hdfsConfig.get("fs.defaultFS").equals(das.getProperties().get(HDFS_FS_SITE))) {
            hdfsConfig.set("fs.defaultFS", das.getProperties().get(HDFS_FS_SITE));
            hdfsConfig.set("hadoop.security.group.mapping", "org.apache.hadoop.security.ShellBasedUnixGroupsMapping");
        }
        try {
            hdfsFileSystem = FileSystem.get(URI.create(das.getProperties().get(HDFS_FS_SITE)), hdfsConfig);
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<SimpleObject> getSimpleObject(ObjectMetadata metadata) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
