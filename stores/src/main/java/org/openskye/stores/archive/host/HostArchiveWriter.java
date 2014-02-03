package org.openskye.stores.archive.host;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
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
import org.openskye.node.NodeManager;
import org.openskye.stores.FileSystemCompressedObject;
import org.openskye.stores.archive.AbstractArchiveStoreWriter;
import org.openskye.stores.information.jdbc.JDBCStructuredObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An implementation of an {@link ArchiveStoreWriter} for the {@link HostArchiveStore}
 */
@Slf4j
public class HostArchiveWriter extends AbstractArchiveStoreWriter {
    public final String TMP_DECOMPRESSION_PATH;
    private final Task task;
    private final HostArchiveStore hostArchiveStore;


    public HostArchiveWriter(Task task, HostArchiveStore hostArchiveStore) {
        this.hostArchiveStore = hostArchiveStore;
        this.task = task;
        TMP_DECOMPRESSION_PATH = hostArchiveStore.getFilePath() + "/tmpArchive/";

    }

    @Override
    public SimpleObject put(SimpleObject simpleObject) {
        ArchiveContentBlock acb;

        //check if the archive store already has this simple object in an ACB and if object duplication is allowed on the project
        if (isObjectArchived(simpleObject) && simpleObject.getObjectMetadata().getProject().isDuplicationAllowed()) {
            //This archive store has this object already, so write to the existing ACB with this object in this store
            acb = simpleObject.getObjectMetadata().getArchiveContentBlock(hostArchiveStore.getArchiveStoreInstance()).get();
        } else {

            acb = new ArchiveContentBlock();
            // We need to link this ACB to the Node we are currently running on
            acb.setNodes(new ArrayList());
            if (NodeManager.hasNode()) {
                acb.getNodes().add(NodeManager.getNode());
            }
            acb.setObjectMetadataReferences(new ArrayList());
            acb.getObjectMetadataReferences().add(simpleObject.getObjectMetadata());
            acb.setArchiveStoreInstance(hostArchiveStore.getArchiveStoreInstance());
            acb.setProject(simpleObject.getObjectMetadata().getProject());
            acb.setChecksum(simpleObject.getObjectMetadata().getChecksum());
            acb.setOriginalSize(simpleObject.getObjectMetadata().getOriginalSize());

            // We need to push the ACB to the OMR so that we are able to have a
            // UUID on the ACB
            acb = getOmr().put(acb);

            simpleObject.getObjectMetadata().getArchiveContentBlocks().add(acb);
        }

        if (simpleObject instanceof JDBCStructuredObject) {
            // we need to store the whole table as a CSV
            final File tempStoragePath = hostArchiveStore.getTempACBPath(acb, true);
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
            UnstructuredObject unstructuredObject = null;
            final File tempStoragePath = hostArchiveStore.getTempACBPath(acb, true);

            // we can just store this as a file
            if (simpleObject.getObjectMetadata().getImplementation().equals(FileSystemCompressedObject.class.getName())) { //is it a compressed object?
                unstructuredObject = (FileSystemCompressedObject) simpleObject;
                decompress((FileSystemCompressedObject) unstructuredObject);
                try {
                    FileUtils.copyInputStreamToFile(unstructuredObject.getInputStream(), tempStoragePath);

                } catch (IOException e) {
                    throw new SkyeException("An I/O exception occurred while trying to write unstructured data for simple object " + simpleObject.getObjectMetadata().getId() + " to " + hostArchiveStore.getFilePath(), e);
                } catch (MissingObjectException e) {
                    throw new SkyeException("Simple object missing from information store?", e);
                }
            } else {
                unstructuredObject = (UnstructuredObject) simpleObject;

                if (isInCompressedObject(unstructuredObject)) {
                    String path = unstructuredObject.getObjectMetadata().getPath();
                    String[] pathSplit = path.split("/");
                    String fileName = pathSplit[pathSplit.length - 1];
                    String container = pathSplit[pathSplit.length - 2];
                    unstructuredObject.getObjectMetadata().setPath(TMP_DECOMPRESSION_PATH + container + "/" + fileName);
                }
                try {
                    FileUtils.copyInputStreamToFile(unstructuredObject.getInputStream(), tempStoragePath);

                } catch (IOException e) {
                    throw new SkyeException("An I/O exception occurred while trying to write unstructured data for simple object " + simpleObject.getObjectMetadata().getId() + " to " + hostArchiveStore.getFilePath(), e);
                } catch (MissingObjectException e) {
                    throw new SkyeException("Simple object missing from information store?", e);
                }

            }

            // Post process the stored object to handle the filters
            postProcess(acb, tempStoragePath, unstructuredObject);


        } else {
            throw new SkyeException("Archive store " + hostArchiveStore.getName() + " does not support simple object " + simpleObject);
        }
        return simpleObject;

    }

    private boolean isInCompressedObject(SimpleObject simpleObject) {
        String path = simpleObject.getObjectMetadata().getPath();
        String[] suffixes = new String[]{".zip", ".tar", ".tar.gz"};
        for (String suffix : suffixes) {
            if (path.contains(suffix)) {
                return true;
            }
        }
        return false;
    }

    private void postProcess(ArchiveContentBlock acb, File tempStoragePath, SimpleObject simpleObject) {
        try {
            if (simpleObject != null) {
                simpleObject.getObjectMetadata().setOriginalSize(tempStoragePath.length());
                File targetPath = hostArchiveStore.getAcbPath(acb, true);
                FileUtils.copyInputStreamToFile(processFilters(hostArchiveStore.getFilters(), new FileInputStream(tempStoragePath)), targetPath);
                FileInputStream fis = new FileInputStream(targetPath);
                simpleObject.getObjectMetadata().setChecksum(DigestUtils.md5Hex(fis));
                simpleObject.getObjectMetadata().setIngested(DateTime.now());
                simpleObject.getObjectMetadata().setMimeType("text/csv");
                simpleObject.getObjectMetadata().setArchiveSize(targetPath.length());
                tempStoragePath.delete();
                //clean up the temporary decompression folder and the files within once they're archived
                File decompressionPath = new File(TMP_DECOMPRESSION_PATH);
                if (simpleObject.getObjectMetadata().getPath().contains(TMP_DECOMPRESSION_PATH) && decompressionPath.exists()) {
                    File extractedFile = new File(simpleObject.getObjectMetadata().getPath());
                    extractedFile.delete();
                    File[] currentFiles = decompressionPath.listFiles();
                    for (File f : currentFiles) {
                        if (f.isDirectory()) {
                            if (f.listFiles().length == 0) {
                                f.delete();
                            }
                        }
                    }
                    if (decompressionPath.listFiles().length == 0) {
                        decompressionPath.delete();
                    }
                }
            }

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
        return simpleObject.getObjectMetadata().getArchiveContentBlock(hostArchiveStore.getArchiveStoreInstance()).isPresent();
    }

    @Override
    public UnstructuredCompressedObject compress(SimpleObject so) {
        UnstructuredCompressedObject compressedObject = new FileSystemCompressedObject();
        ObjectMetadata om = so.getObjectMetadata();
        if (so instanceof StructuredObject) {
            //TODO: How (or are we) going to to compress individual structured objects?
        } else if (so instanceof UnstructuredObject) {
            try {
                OutputStream out = new FileOutputStream(so.getObjectMetadata().getPath() + ".tar");
                ArchiveOutputStream outputStream = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.TAR, out);
                ArchiveEntry entry = new TarArchiveEntry(so.getObjectMetadata().getPath());
                outputStream.putArchiveEntry(entry);
                IOUtils.copy(((UnstructuredObject) so).getInputStream(), outputStream);
                outputStream.closeArchiveEntry();
                outputStream.close();
                om.setPath(so.getObjectMetadata().getPath() + ".tar");
                compressedObject.setObjectMetadata(om);
            } catch (ArchiveException e) {
                throw new SkyeException("Skye Exception", e);
            } catch (MissingObjectException e) {
                throw new SkyeException("Skye Exception", e);
            } catch (IOException e) {
                throw new SkyeException("Skye Exception", e);
            }
        }

        return compressedObject;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UnstructuredCompressedObject compress(ArchiveContentBlock acb) {
        return null;
    }

    @Override
    public List<SimpleObject> decompress(UnstructuredCompressedObject compressedObject) {
        log.info("Decompressing compressed object: " + compressedObject.getObjectMetadata());
        try {
            ArchiveInputStream stream = (ArchiveInputStream) compressedObject.getInputStream();
            ArchiveEntry currentEntry = stream.getNextEntry();
            List<SimpleObject> objects = compressedObject.getObjectsContained();

            for (SimpleObject so : objects) {
                String path = so.getObjectMetadata().getPath();
                so.getObjectMetadata().setPath(createDecompressionPath(path));
            }
            while (currentEntry != null) {
                byte[] entryContent = new byte[(int) currentEntry.getSize()];
                stream.read(entryContent);
                File destFile = new File(TMP_DECOMPRESSION_PATH + compressedObject.getCompressedContainer() + "/" + currentEntry.getName());
                FileUtils.copyInputStreamToFile(new ByteArrayInputStream(entryContent), destFile);
                currentEntry = stream.getNextEntry();
            }
            return objects;
        } catch (MissingObjectException e) {
            throw new SkyeException("Object not found", e);
        } catch (IOException e) {
            throw new SkyeException("Cannot open stream", e);
        }

    }

    public String createDecompressionPath(String path) {
        String[] pathSplit = path.split("/");
        String fileName = pathSplit[pathSplit.length - 1];
        String container = pathSplit[pathSplit.length - 2];
        return TMP_DECOMPRESSION_PATH + container + "/" + fileName;
    }

}
