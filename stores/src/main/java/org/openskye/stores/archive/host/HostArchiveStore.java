package org.openskye.stores.archive.host;

import com.google.common.base.Optional;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.changes.ChangeSet;
import org.apache.commons.compress.changes.ChangeSetPerformer;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eobjects.metamodel.DataContext;
import org.eobjects.metamodel.DataContextFactory;
import org.openskye.core.*;
import org.openskye.domain.ArchiveStoreInstance;
import org.openskye.domain.Task;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.replicate.Replicator;
import org.openskye.stores.information.jdbc.JDBCStructuredObject;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.eobjects.metamodel.DataContextFactory.createCsvDataContext;

/**
 * An implementation of an {@link ArchiveStore} that simply uses the local filesystem to store archives
 */
@Slf4j
public class HostArchiveStore implements ArchiveStore, QueryableStore {

    public final static String IMPLEMENTATION = "host";
    public static final String FILE_PATH = "filePath";
    public static final String TMP_PATH = "tmpPath";
    @Inject
    private ObjectMetadataSearch oms;
    @Getter
    @Inject
    private ObjectMetadataRepository omr;
    private String filePath;
    @Inject
    private Injector injector;
    private String tmpPath;
    private ArchiveStoreInstance archiveStoreInstance;

    @Override
    public ArchiveStoreInstance getArchiveStoreInstance() {
        return archiveStoreInstance;
    }

    @Override
    public void initialize(ArchiveStoreInstance asi) {
        String s = File.separator;
        this.archiveStoreInstance = asi;
        this.filePath = archiveStoreInstance.getProperties().get(FILE_PATH);

        this.tmpPath = archiveStoreInstance.getProperties().get(TMP_PATH);

        if (this.filePath == null)
            this.filePath = s + "tmp" + s + archiveStoreInstance.getId() + s + "archives";
        if (this.tmpPath == null)
            this.tmpPath = s + "tmp" + s + archiveStoreInstance.getId() + s + "tmp";

        HostArchiveStore.log.info("Creating instance of " + this.getName());

        try {
            FileUtils.forceMkdir(new File(this.filePath));
        } catch (IOException e) {
            HostArchiveStore.log.error("A problem occurred while trying to create path " + this.filePath);
            throw new SkyeException("Unable to create path for local filesystem archive store", e);
        }
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
        return "localFS://" + archiveStoreInstance.getId();
    }

    @Override
    public boolean isSupported(SimpleObject so) {
        return so instanceof JDBCStructuredObject;
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);
    }

    @Override
    public ArchiveStoreWriter getWriter(Task task) {
        // We need to make sure we inject everything we need into the writer
        // since the abstract writer has some injection
        HostArchiveWriter writer = new HostArchiveWriter(task, this);
        injector.injectMembers(writer);
        return writer;
    }

    @Override
    public Optional<InputStream> getStream(ObjectMetadata metadata) {
        try {
            if (metadata.getArchiveContentBlock(this.getArchiveStoreInstance()).isPresent()) {
                File acbBucketFile = new File(getFilePath() + "/" + getBucket(metadata.getArchiveContentBlock(this.getArchiveStoreInstance()).get()));
                InputStream is = null;
                for (File f : acbBucketFile.listFiles()) {
                    if (f.getPath().contains(".tar.gz")) {
                        TarArchiveInputStream archiveStream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(f))));
                        ArchiveEntry entry = archiveStream.getNextEntry();
                        while (entry != null) {
                            if (metadata.getPath().contains(entry.getName())) {
                                byte[] entryContent = new byte[(int) entry.getSize()];
                                archiveStream.read(entryContent);
                                is = new ByteArrayInputStream(entryContent);
                                break;
                            } else {
                                entry = archiveStream.getNextTarEntry();
                            }
                        }

                    } else {
                        is = new FileInputStream(getAcbPath(metadata.getArchiveContentBlock(getArchiveStoreInstance()).get(), false));
                    }
                    return Optional.of(is);
                }
            } else return Optional.absent();
        } catch (FileNotFoundException e) {
            throw new SkyeException("ACB references storage, but unable to find archive file?");
        } catch (IOException e) {
            throw new SkyeException("Skye Exception", e);
        }
        return Optional.absent();
    }

    @Override
    public Optional<SimpleObject> materialize(ObjectMetadata metadata) {
        try {
            Class<?> impl = Class.forName(metadata.getImplementation());

            HostArchiveStore.log.debug("Looking for implementation " + impl);
            if (metadata.getArchiveContentBlock(getArchiveStoreInstance()).isPresent()) { //is there an ACB?
                if (isObjectArchived(metadata.getArchiveContentBlock(getArchiveStoreInstance()).get(), metadata)) { //is the object currently archived?
                    if (impl.getSuperclass().equals(StructuredObject.class)) { //is the object structured?
                        if (metadata.getImplementation().equals(JDBCStructuredObject.class.getCanonicalName())) {
                            HostArchiveStore.log.debug("Found an structured object, returning");
                            File tableFile = new File(getTempPath() + UUID.randomUUID().toString() + "/" + metadata.getPath().substring(metadata.getPath().lastIndexOf("/")) + ".csv");
                            mkParentDir(tableFile);
                            tableFile.deleteOnExit();
                            IOUtils.copy(new FileInputStream(getAcbPath(metadata.getArchiveContentBlock(getArchiveStoreInstance()).get(), false)), new FileOutputStream(tableFile));
                            DataContext dataContext = createCsvDataContext(tableFile);
                            // We need to make sure that the table name is correct

                            HostArchiveStore.log.debug("Loaded structured object with table " + dataContext.getDefaultSchema().getTableNames()[0]);

                            SimpleObject simpleObject = new HostArchiveStructuredObject(dataContext);
                            simpleObject.setObjectMetadata(metadata);
                            return Optional.of(simpleObject);
                        } else {
                            HostArchiveStore.log.debug("Found a structured object, but implementation not known?");
                            return Optional.absent();
                        }
                    } else if (impl.getSuperclass().equals(UnstructuredObject.class)) {  //its unstructured
                        HostArchiveStore.log.debug("Found an unstructured object, returning");
                        HostArchiveUnstructuredObject archivedObject = new HostArchiveUnstructuredObject();
                        archivedObject.setObjectMetadata(metadata);
                        archivedObject.setArchiveStore(this);
                        SimpleObject simpleObject = archivedObject;
                        return Optional.of(simpleObject);
                    } else {
                        HostArchiveStore.log.debug("Simple object type not supported!");
                        return Optional.absent();
                    }
                } else {
                    HostArchiveStore.log.debug("Unable to find ACB for " + metadata + ", maybe it hasn't been archived?");
                    return Optional.absent();
                }
            } else {
                HostArchiveStore.log.debug("Unable to find ACB for " + metadata + " for archive store " + getArchiveStoreInstance());
                return Optional.absent();
            }
        } catch (Exception e) {
            throw new SkyeException("Unable to create object for metadata " + metadata, e);
        }

    }

    @Override
    public boolean verify(ArchiveContentBlock acb) {
        try {
            File targetPath = getAcbPath(acb, false);
            FileInputStream fis = new FileInputStream(targetPath);
            String newChecksum = DigestUtils.md5Hex(fis);
            fis.close();
            if (acb.getChecksum().equals(newChecksum)) {
                return true;
            } else {
                log.error("Checksum mismatch for ACB " + acb.getId());
                return false;
            }
        } catch (FileNotFoundException fe) {
            log.error("File not found for ACB " + acb.getId());
            return false;
        } catch (IOException ie) {
            throw new SkyeException("Unable to verify ACB", ie);
        }
    }

    @Override
    public Iterable<ObjectStreamFilter> getFilters() {
        return new ArrayList<>();
        //return ImmutableList.of((ObjectStreamFilter) new ZipCompressionFilter());
    }

    @Override
    public void destroy(ObjectMetadata om) {
        if (om.getArchiveContentBlock(getArchiveStoreInstance()).isPresent()) {
            ArchiveContentBlock acb = om.getArchiveContentBlock(getArchiveStoreInstance()).get();
            try {
                TarArchiveInputStream archiveInputStream = new TarArchiveInputStream(new FileInputStream(getAcbPath(acb, false)));
                TarArchiveOutputStream archiveOut = new TarArchiveOutputStream(new FileOutputStream(getAcbPath(acb, false)));
                TarArchiveEntry currentEntry = archiveInputStream.getNextTarEntry();
                InputStream is;
                ChangeSet changes = new ChangeSet();
                while (currentEntry != null) { //go through the ACB tar
                    byte[] entryContent = new byte[(int) currentEntry.getSize()];

                    if (om.getPath().contains(currentEntry.getName())) {
                        log.debug("Rewriting data on object "+om);
                        for (int i = 0; i <= 6; i++) {
                            changes.add(currentEntry, new ByteArrayInputStream(StringUtils.leftPad("", entryContent.length, (char) Math.random()).getBytes()));
                        }
                        log.debug("Rewrite complete.");
                    } else {
                        archiveInputStream.read(entryContent);
                        is = new ByteArrayInputStream(entryContent);
                        changes.add(currentEntry, is);
                    }
                    currentEntry = archiveInputStream.getNextTarEntry();

                }


                if (acb.getObjectMetadataReferences().size() <= 1) {
                    //delete the container if there are no other objects using it
                    log.debug("Deleting empty ACB "+acb);
                    getAcbPath(acb, false).delete();
                } else {
                    changes.delete(om.getPath());
                    ChangeSetPerformer performer = new ChangeSetPerformer(changes);
                    performer.perform(archiveInputStream, archiveOut);
                    //remove entry
                }
            } catch (Exception e) {
                throw new SkyeException("ACB not found", e);
            }
        }
    }

    @Override
    public Optional<Replicator> getReplicator() {
        Replicator replicator = new HostReplicator(this);
        return Optional.of(replicator);
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTempPath() {
        return tmpPath;
    }

    public File getAcbPath(ArchiveContentBlock acb, boolean isNew) {
        String s = File.separator;
        HostArchiveStore.log.debug("Getting path for " + acb);
        // Lets create rough buckets so we don't end up with everything in one directory
        String fileName = getFilePath() + s + getBucket(acb) + s + acb.getId();
        File acbFile = new File(fileName);

        if (isNew) {
            HostArchiveStore.log.debug("Storing object with ACB [" + fileName + "]");
            mkParentDir(acbFile);
        } else {
            if (!acbFile.exists()) {  //file doesn't exist where its supposed to?
                acbFile = new File(fileName + ".tar.gz"); //does the compressed version exist?
            }
        }
        return acbFile;
    }

    private String getBucket(ArchiveContentBlock acb) {
        String bucketName = acb.getId().substring(0, 10);
        return bucketName;
    }

    public File getTempACBPath(ArchiveContentBlock acb, boolean isNew) {
        String s = File.separator;
        HostArchiveStore.log.debug("Getting temp path for " + acb);
        // Lets create rough buckets so we don't end up with everything in one directory
        String fileName = getTempPath() + s + getBucket(acb) + s + acb.getId();
        File simpleObjectDir = new File(fileName);
        HostArchiveStore.log.debug("Storing temp object with ACB [" + fileName + "]");

        if (isNew) {
            mkParentDir(simpleObjectDir);
        }
        return simpleObjectDir;
    }

    private void mkParentDir(File file) {
        if (file.exists())
            throw new SkyeException("File already exists? " + file.getPath());
        file.mkdirs();
        file.delete();
    }

    @Override
    public StructuredObject executeQuery(QueryContext context, String query) {
        // First we need to get all the objects back so we can build the context
        List<StructuredObject> contextObjects = context.resolveObjects(this);
        List<DataContext> dataContexts = new ArrayList<>();
        for (StructuredObject obj : contextObjects) {
            DataContext dc = ((HostArchiveStructuredObject) obj).getDataContext();
            dataContexts.add(dc);
        }

        // Build a composite context
        DataContext compositeDataContext = DataContextFactory.createCompositeDataContext(dataContexts);
        return new QueryResultStructuredObject(compositeDataContext.executeQuery(query));
    }

    public boolean isObjectArchived(ArchiveContentBlock acb, ObjectMetadata om) {
        return getAcbPath(acb, false).exists();
    }

}
