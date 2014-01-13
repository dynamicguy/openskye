package org.openskye.hadoop.stores;

import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Bytes;
import org.eobjects.metamodel.DataContextFactory;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateScript;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.create.TableCreationBuilder;
import org.eobjects.metamodel.insert.RowInsertionBuilder;
import org.eobjects.metamodel.schema.Column;
import org.eobjects.metamodel.schema.Table;
import org.openskye.core.*;
import org.openskye.core.structured.Row;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.Task;
import org.openskye.hadoop.objects.HStructuredObject;
import org.openskye.hadoop.objects.HUnstructuredObject;
import org.openskye.stores.information.jdbc.JDBCStructuredObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import static org.eobjects.metamodel.DataContextFactory.createCsvDataContext;

/**
 * An implementation of an {@link ArchiveStore} that uses HDFS to store the {@link org.openskye.core.ArchiveContentBlock}s
 */
@Slf4j
public class HdfsArchiveStore implements ArchiveStore, ArchiveStoreWriter {

    public static final String HDFS_CONFIG = "hdfs";
    public static final String IMPLEMENTATION = "hdfs";
    private Configuration hdfsConfig;
    private FileSystem hdfsFileSystem;
    private ArchiveStoreDefinition archiveStoreDefinition;

    @Override
    public void initialize(ArchiveStoreDefinition das) {
        this.archiveStoreDefinition = das;
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
    public String getImplementation() {
        return IMPLEMENTATION;
    }

    @Override
    public String getUrl() {
        return hdfsConfig.get("fs.defaultFS");
    }

    @Override
    public boolean isSupported(SimpleObject so) {
        boolean supported = false;
        if (so.getObjectMetadata().getImplementation().equals(HStructuredObject.class.getCanonicalName()) || so.getObjectMetadata().getImplementation().equals(HUnstructuredObject.class.getCanonicalName())) {
            supported = true;
        }
        return supported;
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);   //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArchiveStoreWriter getWriter(Task task) {

        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<InputStream> getStream(ObjectMetadata metadata) {
        try {
            if (metadata.getArchiveContentBlock(this.getArchiveStoreDefinition().get().getId()).isPresent()) {
                ArchiveContentBlock acb = metadata.getArchiveContentBlock(this.getArchiveStoreDefinition().get().getId()).get();
                InputStream is = hdfsFileSystem.open(new Path(hdfsFileSystem.getWorkingDirectory().toString() + acb.getId() + "/" + metadata.getPath() + ".csv"));
                return Optional.of(is);
            } else return Optional.absent();
        } catch (IOException e) {
            throw new SkyeException("ACB references storage, but unable to find archive file?");
        }
    }

    @Override
    public Optional<SimpleObject> materialize(ObjectMetadata metadata) {
        if (this.getStream(metadata).isPresent()) {
            if (metadata.getImplementation().equals(HStructuredObject.class.getCanonicalName())) {
                if (metadata.getArchiveContentBlock(this.getArchiveStoreDefinition().get().getId()).isPresent()) {
                    if (metadata.getArchiveContentBlock(this.getArchiveStoreDefinition().get().getId()).isPresent()) {
                        UpdateableDataContext dataContext = createCsvDataContext(new File(hdfsFileSystem.getWorkingDirectory().toString() + "/" + metadata.getArchiveContentBlock(this.getArchiveStoreDefinition().get().getId() + "/" + metadata.getPath())));
                        SimpleObject simpleObject = new JDBCStructuredObject(dataContext);
                        return Optional.of(simpleObject);
                    } else {
                        log.debug("Unable to find ACB for archive store " + this.getArchiveStoreDefinition());
                        return Optional.absent();
                    }
                } else {
                    log.debug("Unable to find ACB for archive store " + this.getArchiveStoreDefinition());
                    return Optional.absent();
                }
            } else if (metadata.getImplementation().equals(HUnstructuredObject.class.getCanonicalName())) {
                if (metadata.getArchiveContentBlock(this.getArchiveStoreDefinition().get().getId()).isPresent()) {
                    SimpleObject obj = new HUnstructuredObject();
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
        try {
            hdfsFileSystem.delete(new Path(om.getPath()), false);
        } catch (IOException e) {
            log.error("File not found, maybe already deleted");
            throw new SkyeException("File not found", e);
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SimpleObject put(SimpleObject simpleObject) {
        ArchiveContentBlock acb = new ArchiveContentBlock();
        ObjectMetadata om = simpleObject.getObjectMetadata();
        acb.setArchiveStoreDefinitionId(this.getArchiveStoreDefinition().get().getId());

        if (simpleObject instanceof JDBCStructuredObject) {
            final JDBCStructuredObject structuredObject = (JDBCStructuredObject) simpleObject;
            try {
                hdfsFileSystem.mkdirs(new Path(hdfsFileSystem.getWorkingDirectory().toString() + "/" + acb.getId() + "/" + om.getPath()));
                final File f = new File(hdfsFileSystem.getWorkingDirectory().toString() + "/" + acb.getId() + "/" + om.getPath() + ".csv");

                final UpdateableDataContext dataContext = DataContextFactory.createCsvDataContext(f);
                dataContext.executeUpdate(new UpdateScript() {
                    public void run(UpdateCallback callback) {

                        // Create the table in a file representing the Archive Content Block
                        TableCreationBuilder tableCreator = callback.createTable(dataContext.getDefaultSchema(), structuredObject.getTable().getName());

                        for (Column column : structuredObject.getTable().getColumns()) {
                            tableCreator.withColumn(column.getName()).ofType(column.getType()).ofSize(column.getColumnSize());
                        }

                        Table table = tableCreator.execute();
                        Iterator<Row> rows = structuredObject.getRows();
                        while (rows.hasNext()) {
                            Row row = rows.next();
                            RowInsertionBuilder insert = callback.insertInto(table);
                            int pos = 0;
                            for (String name : structuredObject.getTable().getColumnNames()) {
                                insert.value(name, row.getValues()[pos]);
                                pos++;
                            }
                            insert.execute();
                        }
                    }

                });

            } catch (IOException e) {
                log.error("File not found");
                throw new SkyeException("File not found", e);
            }
        } else if (simpleObject instanceof UnstructuredObject) {
            try {
                FSDataOutputStream in = hdfsFileSystem.create(new Path(hdfsFileSystem.getWorkingDirectory().toString() + "/" + acb.getId() + "/" + om.getPath() + ".txt"));
                in.write(Bytes.toBytes(String.valueOf(((UnstructuredObject) simpleObject).getInputStream())));
            } catch (IOException e) {
                log.error("File not found");
                throw new SkyeException("File not found", e);
            } catch (MissingObjectException e) {
                log.error("Object is missing");
                throw new SkyeException("Object is missing", e);
            }
        } else {
            log.error("Simple object type not supported");
        }
        return simpleObject;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() {
        try {
            hdfsFileSystem.close();
        } catch (IOException e) {
            log.error("Connection already closed");
            throw new SkyeException("Connection already closed", e);
        }
    }
}


