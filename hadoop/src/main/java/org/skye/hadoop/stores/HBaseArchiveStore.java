package org.skye.hadoop.stores;

import com.google.common.base.Optional;
import com.impetus.client.hbase.admin.HBaseDataHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.skye.core.*;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.Task;
import org.skye.hadoop.metadata.HBaseObjectMetadataRepository;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.ObjectMetadataSearch;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;

/**
 * An implementation of an {@link ArchiveStore} that uses Apache HBase to store the {@link org.skye.core.ArchiveContentBlock}s
 */

@Slf4j
public class HBaseArchiveStore implements ArchiveStore {

    public static final String HBASE_SITE = "hbase-site";

    private ArchiveStoreDefinition archiveStoreDefinition;
    @Inject
    private ObjectMetadataRepository omr = new HBaseObjectMetadataRepository();
    private HBaseConfiguration configuration;
    private HBaseDataHandler dataHandler;

    @Override
    public void initialize(ArchiveStoreDefinition das) {
        this.archiveStoreDefinition = das;
        log.info("Creating an instance of " + this.getName());
        try{
            this.configuration.addResource(das.getArchiveStoreInstance().getProperties().get((HBASE_SITE)));
        } catch (IOException e){
            log.error("A problem occurred while trying to access hbase config file at " + HBASE_SITE);
            throw new SkyeException("Unable to create path for local filesystem archive store", e);
        }
        this.configuration.create();

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
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isImplementing(String implementation) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArchiveStoreWriter getWriter(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public java.io.InputStream getStream(ObjectMetadata objectMetadata) {
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
}
