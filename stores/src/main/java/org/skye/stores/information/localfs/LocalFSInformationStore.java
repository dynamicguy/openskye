package org.skye.stores.information.localfs;

import org.joda.time.DateTime;
import org.skye.core.*;
import org.skye.domain.DomainInformationStore;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A local filesystem information store
 * <p/>
 * Can be used to pull from a local filesystem and return a set of {@link org.skye.core.UnstructuredObject}
 * instances
 */
public class LocalFSInformationStore implements InformationStore {

    public final static String IMPLEMENTATION = "localFS";
    private static final String FILE_PATH = "filePath";
    private DomainInformationStore domainInformationStore;

    @Override
    public void initialize(DomainInformationStore dis) {
        this.domainInformationStore = dis;
    }

    @Override
    public Properties getMetadata() {
        return new Properties();
    }

    @Override
    public String getName() {
        return domainInformationStore.getProperties().get(FILE_PATH);
    }

    @Override
    public String getUrl() {
        return "file://" + domainInformationStore.getProperties().get(FILE_PATH);
    }

    @Override
    public Iterable<SimpleObject> getSince(DateTime dateTime) {
        throw new UnsupportedOperationException("Unable to perform change track by time");
    }

    @Override
    public Iterable<SimpleObject> getRoot() {

        try (DirectoryStream<Path> ds =
                     Files.newDirectoryStream(getFileSystem())) {

            // TODO is this going to be a problem on a large filesystem?
            List<SimpleObject> all = new ArrayList<>();
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    ContainerObject container = new ContainerObject();
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setPath(p.toAbsolutePath().toString());
                    container.setObjectMetadata(metadata);
                    all.add(container);
                } else {
                    UnstructuredObject unstructObj = new UnstructuredObject();
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setPath(p.toAbsolutePath().toString());
                    all.add(unstructObj);
                }
            }
            return all;
        } catch (IOException e) {
            throw new SkyeException("Unable to list files on filesystem", e);
        }
    }

    @Override
    public Iterable<SimpleObject> getChildren(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<SimpleObject> getRelated(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);
    }

    @Override
    public SimpleObject materialize(ObjectMetadata objectMetadata) throws InvalidSimpleObjectException {
        UnstructuredObject unstructObj = new UnstructuredObject();
        ObjectMetadata metadata = new ObjectMetadata();
        unstructObj.setObjectMetadata(metadata);
        return unstructObj;
    }

    public Path getFileSystem() {
        return FileSystems.getDefault().getPath(domainInformationStore.getProperties().get(FILE_PATH));
    }
}