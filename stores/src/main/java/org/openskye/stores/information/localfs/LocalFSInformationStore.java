package org.openskye.stores.information.localfs;

import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
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
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.stores.FileSystemCompressedObject;
import org.openskye.stores.information.jdbc.JDBCStructuredObject;
import org.openskye.stores.util.MimeTypeUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * A local filesystem information store
 * <p/>
 * Can be used to pull from a local filesystem and return a set of {@link org.openskye.core.UnstructuredObject}
 * instances
 */
@Slf4j
public class LocalFSInformationStore implements InformationStore {

    public final static String IMPLEMENTATION = "localFS";
    public final static String FILE_PATH = "filePath";
    private InformationStoreDefinition informationStoreDefinition;

    @Override
    public void initialize(InformationStoreDefinition dis) {
        this.informationStoreDefinition = dis;
    }

    @Override
    public Properties getMetadata() {
        return new Properties();
    }

    @Override
    public String getName() {
        return "Local filesystem";
    }

    @Override
    public String getImplementation() {
        return IMPLEMENTATION;
    }

    @Override
    public String getUrl() {
        return "file://" + informationStoreDefinition.getProperties().get(FILE_PATH);
    }

    @Override
    public Iterable<SimpleObject> getSince(DateTime dateTime) {
        throw new UnsupportedOperationException("Unable to perform change track by time");
    }

    @Override
    public Iterable<SimpleObject> getRoot() {
        return buildObjectsForPath(informationStoreDefinition.getProperties().get(FILE_PATH));
    }

    private Iterable<SimpleObject> buildObjectsForPath(String path) {
        log.debug("Scanning path " + path + " for objects");
        try (DirectoryStream<Path> ds =
                     Files.newDirectoryStream(Paths.get(path))) {

            List<SimpleObject> all = new ArrayList<>();
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    ContainerObject container = new ContainerObject();
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setImplementation(ContainerObject.class.getCanonicalName());
                    metadata.setPath(p.toAbsolutePath().toString());
                    metadata.setInformationStoreId(this.getInformationStoreDefinition().get().getId());
                    container.setObjectMetadata(metadata);
                    if (log.isDebugEnabled())
                        log.debug("Found directory " + metadata);
                    all.add(container);
                } else if (Files.isRegularFile(p)) {
                    if (isCompressedFile(p)) {
                        UnstructuredCompressedObject compressedObj = new FileSystemCompressedObject();
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setImplementation(FileSystemCompressedObject.class.getCanonicalName());
                        metadata.setPath(p.toAbsolutePath().toString());
                        metadata.setProject(informationStoreDefinition.getProject());
                        metadata.setOriginalSize(FileUtils.sizeOf(p.toFile()));
                        metadata.setLastModified(new DateTime(p.toFile().lastModified()));
                        metadata.setMimeType(MimeTypeUtil.getContentType(p));
                        metadata.setInformationStoreId(informationStoreDefinition.getId());
                        compressedObj.setObjectMetadata(metadata);
                        all.add(compressedObj);
                    } else {
                        UnstructuredObject unstructObj = new LocalFileUnstructuredObject();
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setImplementation(LocalFileUnstructuredObject.class.getCanonicalName());
                        metadata.setPath(p.toAbsolutePath().toString());
                        metadata.setProject(informationStoreDefinition.getProject());
                        metadata.setOriginalSize(FileUtils.sizeOf(p.toFile()));
                        metadata.setLastModified(new DateTime(p.toFile().lastModified()));
                        metadata.setMimeType(MimeTypeUtil.getContentType(p));
                        metadata.setInformationStoreId(informationStoreDefinition.getId());
                        unstructObj.setObjectMetadata(metadata);

                        if (log.isDebugEnabled())
                            log.debug("Found file " + metadata);
                        all.add(unstructObj);
                    }
                }
            }
            return all;
        } catch (IOException e) {
            throw new SkyeException("Unable to list files on filesystem", e);
        }
    }

    @Override
    public Iterable<SimpleObject> getChildren(SimpleObject simpleObject) {
        return buildObjectsForPath(simpleObject.getObjectMetadata().getPath());
    }

    @Override
    public Iterable<SimpleObject> getRelated(SimpleObject simpleObject) {
        return new ArrayList<>();
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);
    }

    @Override
    public SimpleObject materialize(ObjectMetadata objectMetadata) throws InvalidSimpleObjectException {
        SimpleObject unstructObj = null;
        try {
            unstructObj = (SimpleObject) Class.forName(objectMetadata.getImplementation()).newInstance();
        } catch (Exception e) {
            throw new SkyeException("Cannot materialize object from LocalFSInformationStore:", e);
        }
        unstructObj.setObjectMetadata(objectMetadata);
        return unstructObj;
    }

    @Override
    public Optional<InformationStoreDefinition> getInformationStoreDefinition() {
        if (this.informationStoreDefinition == null)
            return Optional.absent();

        return Optional.of(this.informationStoreDefinition);
    }

    @Override
    public void put(SimpleObject simpleObject) {
        if (!isObjectInStore(simpleObject)) {

            File targetFile = new File(this.informationStoreDefinition.getProperties().get(FILE_PATH) + "/" + simpleObject.getObjectMetadata().getPath());
            targetFile.setWritable(true);
            targetFile.mkdirs();
            targetFile.delete();

            if (simpleObject instanceof UnstructuredObject) {
                UnstructuredObject unstructuredObject = (UnstructuredObject) simpleObject;
                try {
                    FileUtils.copyInputStreamToFile(unstructuredObject.getInputStream(), targetFile);
                } catch (Exception e) {
                    throw new SkyeException("Unable to write input stream for " + unstructuredObject + " to local file system information store", e);
                }
            } else if (simpleObject instanceof JDBCStructuredObject) {
                // we need to store the whole table as a CSV
                final JDBCStructuredObject structuredObject = (JDBCStructuredObject) simpleObject;
                if (log.isDebugEnabled())
                    log.debug("Writing temp structured object to " + targetFile.getAbsolutePath());
                final UpdateableDataContext dataContext = DataContextFactory.createCsvDataContext(targetFile);
                dataContext.executeUpdate(new UpdateScript() {
                    public void run(UpdateCallback callback) {

                        // Create the table in a file representing the Archive Content Block

                        TableCreationBuilder tableCreator = callback.createTable(dataContext.getDefaultSchema(), structuredObject.getTable().getName());

                        for (Column column : structuredObject.getTable().getColumns()) {
                            tableCreator.withColumn(column.getName()).ofType(column.getType());
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
            } else {
                throw new SkyeException("Local filesystem information store does not support " + simpleObject + " for put");
            }
        }
    }

    @Override
    public boolean isObjectInStore(SimpleObject simpleObject) {
        File targetFile = new File(this.informationStoreDefinition.getProperties().get(FILE_PATH) + "/" + simpleObject.getObjectMetadata().getPath());
        return targetFile.exists();
    }

    public boolean isCompressedFile(Path p) {
        File f = p.toFile();
        String[] suffixes = new String[]{".zip", ".tar", ".tar.gz"};
        SuffixFileFilter filter = new SuffixFileFilter(suffixes);
        return filter.accept(f);
    }

}
