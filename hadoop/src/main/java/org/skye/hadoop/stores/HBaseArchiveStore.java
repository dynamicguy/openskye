package org.skye.hadoop.stores;

import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.skye.core.*;
import org.skye.core.structured.ColumnMetadata;
import org.skye.core.structured.Row;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.Task;
import org.skye.hadoop.metadata.HBaseObjectMetadataRepository;
import org.skye.hadoop.objects.HStructuredObject;
import org.skye.hadoop.objects.HUnstructuredObject;
import org.skye.metadata.ObjectMetadataRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.impetus.kundera.*;

/**
 * An implementation of an {@link ArchiveStore} that uses Apache HBase to store the {@link org.skye.core.ArchiveContentBlock}s
 */

@Slf4j
public class HBaseArchiveStore implements ArchiveStore, ArchiveStoreWriter {

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
        Path filepath = new Path(das.getProperties().get(HBASE_SITE));
        this.hBaseConfiguration.addResource(filepath);
        this.omr = new HBaseObjectMetadataRepository();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hbase");
        this.hBaseEntityManager = emf.createEntityManager();

    }

    @Override
    public String getName() {
        return archiveStoreDefinition.getName() + " (HBase)";
    }

    @Override
    public String getUrl() {
        return hBaseConfiguration.get("hbase.rootdir");
    }

    @Override
    public boolean isSupported(SimpleObject so) {
        boolean supported = false;
        if(so.getObjectMetadata().getImplementation().equals(HStructuredObject.class.getCanonicalName())||so.getObjectMetadata().getImplementation().equals(HUnstructuredObject.class.getCanonicalName())){
            supported=true;
        }
        return supported;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArchiveStoreWriter getWriter(Task task) {
        return this;
    }

    @Override
    public Optional<InputStream> getStream(ObjectMetadata objectMetadata) {

        return null;
    }

    @Override
    public Optional<SimpleObject> getSimpleObject(ObjectMetadata metadata) {
        SimpleObject result;
        if(metadata.getImplementation().equals(StructuredObject.class.getCanonicalName())){
            result = hBaseEntityManager.find(StructuredObject.class, metadata.getId());
        }
        else if(metadata.getImplementation().equals(UnstructuredObject.class.getCanonicalName())){
            result = hBaseEntityManager.find(UnstructuredObject.class, metadata.getId());
        }

        else {
            log.debug("Simple object type not supported! " + metadata);
            return Optional.absent();
        }

        if (result == null) {
            return Optional.absent();
        } else {
            return Optional.of(result);
        }
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
        hBaseEntityManager.remove(getSimpleObject(om));
    }

    @Override
    public SimpleObject put(SimpleObject simpleObject) {
        if(simpleObject.getObjectMetadata().getImplementation().equals(StructuredObject.class.getCanonicalName())){
            try {
                StructuredObject structuredObject = (StructuredObject)simpleObject;
                HBaseAdmin admin = new HBaseAdmin(hBaseConfiguration);
                HTableDescriptor desc = new HTableDescriptor(simpleObject.getObjectMetadata().getId());

                HColumnDescriptor meta = new HColumnDescriptor("Column Metadata");
                HColumnDescriptor cols = new HColumnDescriptor("Columns");
                desc.addFamily(meta);
                desc.addFamily(cols);
                admin.createTable(desc);
                admin.disableTable(desc.getName());
                List<ColumnMetadata> columnMetadataList = structuredObject.getColumns();
                Iterator<Row> rows = structuredObject.getRows();
                Put p = new Put();
                //add column metadata for this object
                for(ColumnMetadata c : columnMetadataList){
                    p.add(meta.getName(), Bytes.toBytes("Name"), c.getName().getBytes());
                    p.add(meta.getName(), Bytes.toBytes("Native Type"), c.getNativeType().getBytes());
                    p.add(meta.getName(), Bytes.toBytes("Size"), Bytes.toBytes(c.getSize()));
                    p.add(meta.getName(), Bytes.toBytes("Remarks"), c.getRemarks().getBytes());
                }
                //store rows
                while(rows.hasNext()){
                    Row r = rows.next();
                    Object[] vals = r.getValues();
                    for(Object v : vals){
                        for(ColumnMetadata c : columnMetadataList){

                            p.add(cols.getName(), c.getName().getBytes(), Bytes.toBytes(v.hashCode()));
                        }
                    }
                }

            } catch (MasterNotRunningException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ZooKeeperConnectionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        else if(simpleObject.getObjectMetadata().getImplementation().equals(UnstructuredObject.class.getCanonicalName())){
            HUnstructuredObject unstructuredObject = (HUnstructuredObject)simpleObject;
            hBaseEntityManager.persist(unstructuredObject);
        }
        else{
            log.error("Simple object type not supported");
        }
        return simpleObject;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() {
        hBaseEntityManager.close();
    }
}
