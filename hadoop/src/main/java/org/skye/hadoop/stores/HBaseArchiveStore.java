package org.skye.hadoop.stores;

import com.google.common.base.Optional;
import com.impetus.client.hbase.HBaseClient;
import com.impetus.client.hbase.admin.HBaseDataHandler;
import com.impetus.kundera.KunderaPersistence;
import com.impetus.kundera.metadata.KunderaMetadataManager;
import com.impetus.kundera.metadata.model.EntityMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.skye.core.*;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of an {@link ArchiveStore} that uses Apache HBase to store the {@link org.skye.core.ArchiveContentBlock}s
 */

@Slf4j
public class HBaseArchiveStore implements ArchiveStore {

    public static final String IMPLEMENTATION = "hbase";
    public static final String HBASE_SITE = "hbase";
    private ArchiveStoreDefinition archiveStoreDefinition;
    @Inject
    private ObjectMetadataRepository omr;
    private HBaseConfiguration hBaseConfiguration = new HBaseConfiguration();
    private EntityManager hBaseEntityManager;

    @Override
    public void initialize(ArchiveStoreDefinition das) {
        this.archiveStoreDefinition = das;
        this.hBaseConfiguration.addResource(das.getProperties().get(HBASE_SITE));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hbase");
        this.hBaseEntityManager = emf.createEntityManager();

    }

    @Override
    public String getName() {
        return archiveStoreDefinition.getName() + " (HBase)";
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
        return implementation.equals(IMPLEMENTATION);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArchiveStoreWriter getWriter(Task task) {
        return null;
    }

    @Override
    public Optional<InputStream> getStream(ObjectMetadata objectMetadata) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<SimpleObject> getSimpleObject(ObjectMetadata metadata) {

        SimpleObject result=hBaseEntityManager.find(SimpleObject.class, metadata.getId());
        if(result==null){
            return Optional.absent();
        }
        else{
            return Optional.of(result);
        }
    }

    @Override
    public Iterable<ObjectStreamFilter> getFilters() {
        return new ArrayList<>();
    }

    @Override
    public Optional<ArchiveStoreDefinition> getArchiveStoreDefinition() {
        if(this.archiveStoreDefinition==null){
            return Optional.absent();
        }
        else{
            return Optional.of(this.archiveStoreDefinition);
        }
    }

    @Override
    public void destroy(ObjectMetadata om) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
