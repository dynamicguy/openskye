package org.skye.stores.archive.localfs;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eobjects.metamodel.DataContextFactory;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateScript;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.insert.RowInsertionBuilder;
import org.eobjects.metamodel.schema.Table;
import org.skye.core.*;
import org.skye.core.structured.Row;
import org.skye.domain.Task;
import org.skye.stores.archive.AbstractArchiveStoreWriter;
import org.skye.stores.information.jdbc.JDBCStructuredObject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * An implementation of an {@link ArchiveStoreWriter} for the {@link LocalFilesystemArchiveStore}
 */
@Slf4j
public class LocalFilesystemArchiveWriter extends AbstractArchiveStoreWriter {
    private final Task task;
    private final LocalFilesystemArchiveStore localFilesystemArchiveStore;

    public LocalFilesystemArchiveWriter(Task task, LocalFilesystemArchiveStore localFilesystemArchiveStore) {
        this.localFilesystemArchiveStore = localFilesystemArchiveStore;
        this.task = task;
    }

    @Override
    public void put(final SimpleObject simpleObject) {
        ArchiveContentBlock acb = new ArchiveContentBlock();
        if (simpleObject instanceof JDBCStructuredObject) {
            // we need to store the whole table as a CSV
            UpdateableDataContext dataContext = DataContextFactory.createCsvDataContext(getSimpleObjectPath(simpleObject));
            dataContext.executeUpdate(new UpdateScript() {
                public void run(UpdateCallback callback) {

                    // Create the table in a file representing the Archive Content Block
                    JDBCStructuredObject structuredObject = (JDBCStructuredObject) simpleObject;
                    Table table = callback.createTable(structuredObject.getTable().getSchema(), structuredObject.getTable().getName())
                            .execute();

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

        } else if (simpleObject instanceof UnstructuredObject) {
            // we can just store this as a file
            UnstructuredObject unstructuredObject = (UnstructuredObject) simpleObject;
            try {
                FileUtils.copyInputStreamToFile(unstructuredObject.getContent(), getSimpleObjectPath(simpleObject));
            } catch (IOException e) {
                throw new SkyeException("An I/O exception occured while trying to write unstructured data for simple object " + simpleObject.getId() + " to " + localFilesystemArchiveStore.getLocalPath(), e);
            }
            localFilesystemArchiveStore.getLocalPath();
        } else {
            throw new SkyeException("Archive store " + localFilesystemArchiveStore.getName() + " does not support simple object " + simpleObject);
        }

        simpleObject.setArchiveContentBlocks(ImmutableList.of(acb));
        updateMetadata(simpleObject);
    }

    private File getSimpleObjectPath(SimpleObject simpleObject) {
        return new File(localFilesystemArchiveStore.getLocalPath() + "/" + simpleObject.getId());
    }

    @Override
    public void close() {
        // nothing to do
    }
}
