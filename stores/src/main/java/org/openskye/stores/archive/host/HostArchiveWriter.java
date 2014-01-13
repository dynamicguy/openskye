package org.openskye.stores.archive.host;

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
import java.util.List;

/**
 * An implementation of an {@link ArchiveStoreWriter} for the {@link HostArchiveStore}
 */
@Slf4j
public class HostArchiveWriter extends AbstractArchiveStoreWriter {
    private final Task task;
    private final HostArchiveStore localFilesystemArchiveStore;

    public HostArchiveWriter(Task task, HostArchiveStore localFilesystemArchiveStore) {
        this.localFilesystemArchiveStore = localFilesystemArchiveStore;
        this.task = task;
    }

    @Override
    public SimpleObject put(SimpleObject simpleObject) {
        //check if the archive store already has this simple object in an ACB and if object duplication is allowed on the project
        if (isObjectArchived(simpleObject)) {
            //This archive store has this object already
            List<ArchiveContentBlock> objectACBs = simpleObject.getObjectMetadata().getArchiveContentBlocks();
            objectACBs.add(simpleObject.getObjectMetadata().getArchiveContentBlock(localFilesystemArchiveStore.getArchiveStoreInstance().getId()).get());
            simpleObject.getObjectMetadata().setArchiveContentBlocks(objectACBs);
        } else {

            ArchiveContentBlock acb = new ArchiveContentBlock();

            ObjectMetadata om = simpleObject.getObjectMetadata();
            acb.setArchiveStoreInstanceId(this.localFilesystemArchiveStore.getArchiveStoreInstance().getId());

            if (simpleObject instanceof JDBCStructuredObject) {
                // we need to store the whole table as a CSV
                final File tempStoragePath = localFilesystemArchiveStore.getTempACBPath(acb, true);
                final JDBCStructuredObject structuredObject = (JDBCStructuredObject) simpleObject;
                if (HostArchiveWriter.log.isDebugEnabled())
                    HostArchiveWriter.log.debug("Writing temp structured object to " + tempStoragePath.getAbsolutePath());
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
                final File tempStoragePath = localFilesystemArchiveStore.getTempACBPath(acb, true);

                try {
                    FileUtils.copyInputStreamToFile(unstructuredObject.getInputStream(), tempStoragePath);
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
        }
        return simpleObject;
    }

    private void postProcess(ArchiveContentBlock acb, File tempStoragePath, SimpleObject simpleObject) {
        try {
            simpleObject.getObjectMetadata().setOriginalSize(tempStoragePath.length());
            File targetPath = localFilesystemArchiveStore.getAcbPath(acb, true);
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

    @Override
    public boolean isObjectArchived(SimpleObject simpleObject) {
        return simpleObject.getObjectMetadata().getArchiveContentBlock(localFilesystemArchiveStore.getArchiveStoreInstance().getId()).isPresent() && !simpleObject.getObjectMetadata().getProject().isDuplicationAllowed();
    }
}
