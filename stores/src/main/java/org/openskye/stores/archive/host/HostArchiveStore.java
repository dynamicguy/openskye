package org.openskye.stores.archive.host;

import com.google.common.base.Optional;
import com.google.inject.Injector;
import com.jcraft.jsch.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eobjects.metamodel.DataContext;
import org.eobjects.metamodel.DataContextFactory;
import org.openskye.core.*;
import org.openskye.domain.ArchiveStoreInstance;
import org.openskye.domain.Node;
import org.openskye.domain.Task;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.replicate.Replicator;
import org.openskye.stores.information.jdbc.JDBCStructuredObject;
import org.openskye.stores.information.localfs.LocalFileUnstructuredObject;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.eobjects.metamodel.DataContextFactory.createCsvDataContext;

/**
 * An implementation of an {@link ArchiveStore} that simply uses the local
 * filesystem to store archives
 */
@Slf4j
public class HostArchiveStore implements ArchiveStore, QueryableStore {

    public final static String IMPLEMENTATION = "localFS";
    public static final String LOCALFS_PATH = "localFsPath";
    public static final String LOCALFS_TMP_PATH = "localFsTmpPath";
    @Inject
    private ObjectMetadataSearch oms;
    @Getter
    @Inject
    private ObjectMetadataRepository omr;
    private String localPath;
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
        this.archiveStoreInstance = asi;
        this.localPath = archiveStoreInstance.getProperties().get(LOCALFS_PATH);

        this.tmpPath = archiveStoreInstance.getProperties().get(LOCALFS_TMP_PATH);

        if (this.localPath == null)
            this.localPath = "/tmp/" + archiveStoreInstance.getId() + "/archives";
        if (this.tmpPath == null)
            this.tmpPath = "/tmp/" + archiveStoreInstance.getId() + "/tmp";

        HostArchiveStore.log.info("Creating instance of " + this.getName());

        try {
            FileUtils.forceMkdir(new File(this.localPath));
        } catch (IOException e) {
            HostArchiveStore.log.error("A problem occurred while trying to create path " + this.localPath);
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
            if (metadata.getArchiveContentBlock(this.getArchiveStoreInstance().getId()).isPresent()) {
                InputStream is = new FileInputStream(getAcbPath(metadata.getArchiveContentBlock(getArchiveStoreInstance().getId()).get(), false));
                return Optional.of(is);
            } else return Optional.absent();
        } catch (FileNotFoundException e) {
            throw new SkyeException("ACB references storage, but unable to find archive file?");
        }
    }

    @Override
    public Optional<SimpleObject> materialize(ObjectMetadata metadata) {
        try {
            Class<?> impl = Class.forName(metadata.getImplementation());

            HostArchiveStore.log.debug("Looking for implementation " + impl);
            if (metadata.getArchiveContentBlock(getArchiveStoreInstance().getId()).isPresent()) { //is there an ACB?
                if (isObjectArchived(metadata.getArchiveContentBlock(getArchiveStoreInstance().getId()).get(), metadata)) { //is the object currently archived?
                    if (impl.getSuperclass().equals(StructuredObject.class)) { //is the object structured?
                        if (metadata.getImplementation().equals(JDBCStructuredObject.class.getCanonicalName())) {
                            HostArchiveStore.log.debug("Found an structured object, returning");
                            File tableFile = new File(getTempPath() + UUID.randomUUID().toString() + "/" + metadata.getPath().substring(metadata.getPath().lastIndexOf("/")) + ".csv");
                            mkParentDir(tableFile);
                            tableFile.deleteOnExit();
                            IOUtils.copy(new FileInputStream(getAcbPath(metadata.getArchiveContentBlock(getArchiveStoreInstance().getId()).get(), false)), new FileOutputStream(tableFile));
                            DataContext dataContext = createCsvDataContext(tableFile);
                            // We need to make sure that the table name is correct

                            HostArchiveStore.log.debug("Loaded structured object with table " + dataContext.getDefaultSchema().getTableNames()[0]);

                            SimpleObject simpleObject = new JDBCStructuredObject(dataContext);
                            simpleObject.setObjectMetadata(metadata);
                            return Optional.of(simpleObject);
                        } else {
                            HostArchiveStore.log.debug("Found a structured object, but implementation not known?");
                            return Optional.absent();
                        }
                    } else if (impl.getSuperclass().equals(UnstructuredObject.class)) {  //its unstructured
                        HostArchiveStore.log.debug("Found an unstructured object, returning");
                        SimpleObject simpleObject = new LocalFileUnstructuredObject();
                        simpleObject.setObjectMetadata(metadata);
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
    public Iterable<ObjectStreamFilter> getFilters() {
        return new ArrayList<>();
        //return ImmutableList.of((ObjectStreamFilter) new ZipCompressionFilter());
    }

    @Override
    public void destroy(ObjectMetadata om) {
        if (om.getArchiveContentBlock(getArchiveStoreInstance().getId()).isPresent()) {
            getAcbPath(om.getArchiveContentBlock(getArchiveStoreInstance().getId()).get(), false).delete();
        }
    }

    @Override
    public Optional<Replicator> getReplicator() {
        Replicator replicator = new HostReplicator(this);
        return Optional.of(replicator);
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getTempPath() {
        return tmpPath;
    }

    public File getAcbPath(ArchiveContentBlock acb, boolean isNew) {
        String fileName = getLocalPath() + "/" + acb.getId() + "/" + acb.getId() + ".csv";
        File simpleObjectDir = new File(fileName);
        HostArchiveStore.log.info("Storing object with ACB [" + getLocalPath() + "/" + acb.getId() + "/" + acb.getId() + "]");

        if (isNew) {
            mkParentDir(simpleObjectDir);
        } else if (!simpleObjectDir.exists()) {
            throw new SkyeException("Unable to find simple object for acb " + acb);
        }
        return simpleObjectDir;
    }

    public File getTempACBPath(ArchiveContentBlock acb, boolean isNew) {
        String fileName = getTempPath() + "/" + acb.getId() + "/" + acb.getId() + ".csv";
        File simpleObjectDir = new File(fileName);
        HostArchiveStore.log.info("Storing temp object with ACB [" + getTempPath() + "/" + acb.getId() + "/" + acb.getId() + "]");

        if (isNew) {
            mkParentDir(simpleObjectDir);

        } else if (!simpleObjectDir.exists()) {
            throw new SkyeException("Unable to find simple object for acb " + acb);
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
            DataContext dc = ((JDBCStructuredObject) obj).getDataContext();
            dataContexts.add(dc);
        }

        // Build a composite context
        DataContext compositeDataContext = DataContextFactory.createCompositeDataContext(dataContexts);
        return new QueryResultStructuredObject(compositeDataContext.executeQuery(query));
    }

    public boolean isObjectArchived(ArchiveContentBlock acb, ObjectMetadata om) {
        return getAcbPath(acb, false).exists();
    }

    /**
     * An ACB copy from the primary to another node,  used in the replication
     *
     * @param acb         The ACB you wish to copy
     * @param primaryNode The node we are copying from
     * @param node        The node we are copying to (should we were we are)
     */
    protected void copyACB(ArchiveContentBlock acb, Node primaryNode, Node node) {

        // Get the paths

        String sourceAcbPath = getAcbPath(acb, false).getPath();
        String targetAcbPath = getAcbPath(acb, false).getPath();

        JSch jsch = new JSch();

        try {
            Session session = jsch.getSession(primaryNode.getServiceAccount(), primaryNode.getHostname(), 22);

            // exec 'scp -f rfile' remotely
            String command = "scp -f " + sourceAcbPath;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] buf = new byte[1024];

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        // error
                        break;
                    }
                    if (buf[0] == ' ') break;
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }

                String file = null;
                for (int i = 0; ; i++) {
                    in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }

                //System.out.println("filesize="+filesize+", file="+file);

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();

                // read a content of lfile
                FileOutputStream fos = new FileOutputStream(targetAcbPath);
                int foo;
                while (true) {
                    if (buf.length < filesize) foo = buf.length;
                    else foo = (int) filesize;
                    foo = in.read(buf, 0, foo);
                    if (foo < 0) {
                        // error
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize -= foo;
                    if (filesize == 0L) break;
                }
                fos.close();

                if (checkAck(in) != 0) {
                    System.exit(0);
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }

            session.disconnect();
        } catch (JSchException e) {
            throw new SkyeException("Unable to connect to " + node.getHostname() + " as " + node.getServiceAccount(), e);
        } catch (FileNotFoundException e) {
            throw new SkyeException("Unable to find ACB " + acb + " on " + node.getHostname(), e);
        } catch (IOException e) {
            throw new SkyeException("Unable to copy to find ACB " + acb + " from " + node.getHostname(), e);
        }
    }

    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }
}
