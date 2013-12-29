package org.openskye.stores.archive.localfs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.eobjects.metamodel.DataContextFactory;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateScript;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.create.TableCreationBuilder;
import org.eobjects.metamodel.insert.RowInsertionBuilder;
import org.eobjects.metamodel.schema.Column;
import org.eobjects.metamodel.schema.Table;
import org.joda.time.DateTime;
import org.openskye.core.*;
import org.openskye.core.structured.Row;
import org.openskye.domain.Task;
import org.openskye.stores.archive.AbstractArchiveStoreWriter;
import org.openskye.stores.information.jdbc.JDBCStructuredObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * An implementation of an {@link ArchiveStoreWriter} for the {@link LocalFSArchiveStore}
 */
@Slf4j
public class LocalFSArchiveWriter extends AbstractArchiveStoreWriter {
    private final Task task;
    private final LocalFSArchiveStore localFilesystemArchiveStore;

    public LocalFSArchiveWriter(Task task, LocalFSArchiveStore localFilesystemArchiveStore) {
        this.localFilesystemArchiveStore = localFilesystemArchiveStore;
        this.task = task;
    }

    @Override
    public SimpleObject put(SimpleObject simpleObject) {

        // We need to work out if we already have an ACB in this archive store that
        // has the same content,  and if we do then we need to make sure that we just point
        // to that and de-dupe the storage

        ArchiveContentBlock acb = new ArchiveContentBlock();

        ObjectMetadata om = simpleObject.getObjectMetadata();
        acb.setArchiveStoreInstanceId(this.localFilesystemArchiveStore.getArchiveStoreInstance().getId());

        if (simpleObject instanceof JDBCStructuredObject) {
            // we need to store the whole table as a CSV
            final File tempStoragePath = localFilesystemArchiveStore.getTempSimpleObjectPath(acb, om, true);
            final JDBCStructuredObject structuredObject = (JDBCStructuredObject) simpleObject;
            if (log.isDebugEnabled())
                log.debug("Writing temp structured object to " + tempStoragePath.getAbsolutePath());
            final UpdateableDataContext dataContext = DataContextFactory.createCsvDataContext(tempStoragePath);
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

            // Post process the stored object to handle the filters
            postProcess(acb, tempStoragePath, simpleObject);

        } else if (simpleObject instanceof UnstructuredObject) {
            // we can just store this as a file
            UnstructuredObject unstructuredObject = (UnstructuredObject) simpleObject;
            final File tempStoragePath = localFilesystemArchiveStore.getTempSimpleObjectPath(acb, om, true);

            try {
                FileUtils.copyInputStreamToFile(unstructuredObject.getContent(), tempStoragePath);
            } catch (IOException e) {
                throw new SkyeException("An I/O exception occurred while trying to write unstructured data for simple object " + simpleObject.getObjectMetadata().getId() + " to " + localFilesystemArchiveStore.getLocalPath(), e);
            } catch (MissingObjectException e) {
                throw new SkyeException("Simple object missing from information store?", e);
            }

            // Post process the stored object to handle the filters
            postProcess(acb, tempStoragePath, simpleObject);

        } else {
            throw new SkyeException("Archive store " + localFilesystemArchiveStore.getName() + " does not support simple object " + simpleObject);
        }

        simpleObject.getObjectMetadata().getArchiveContentBlocks().add(acb);
        updateMetadata(simpleObject);
        return simpleObject;
    }

    private void postProcess(ArchiveContentBlock acb, File tempStoragePath, SimpleObject simpleObject) {
        try {
            simpleObject.getObjectMetadata().setOriginalSize(tempStoragePath.length());
            File targetPath = localFilesystemArchiveStore.getSimpleObjectPath(acb, simpleObject.getObjectMetadata(), true);
            FileUtils.copyInputStreamToFile(processFilters(localFilesystemArchiveStore.getFilters(), new FileInputStream(tempStoragePath)), targetPath);

            FileInputStream fis = new FileInputStream(targetPath);
            simpleObject.getObjectMetadata().setChecksum(DigestUtils.md5Hex(fis));
            simpleObject.getObjectMetadata().setIngested(DateTime.now());
            simpleObject.getObjectMetadata().setMimeType("text/csv");
            simpleObject.getObjectMetadata().setArchiveSize(targetPath.length());
            tempStoragePath.delete();
        } catch (FileNotFoundException e) {
            throw new SkyeException("Unable to process filters since we can't find the archived file?");
        } catch (IOException e) {
            throw new SkyeException("IO exception while trying to write output of filters to final archive location", e);
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
