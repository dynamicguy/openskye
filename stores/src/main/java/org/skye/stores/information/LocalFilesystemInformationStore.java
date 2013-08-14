package org.skye.stores.information;

import org.joda.time.DateTime;
import org.skye.core.*;

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
public class LocalFilesystemInformationStore implements InformationStore {

    private static final String FILE_PATH = "filePath";
    private Properties properties;
    private Path fileSystem;

    @Override
    public void initialize(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Properties getMetadata() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return properties.getProperty(FILE_PATH);
    }

    @Override
    public String getUrl() {
        return "file://" + properties.getProperty(FILE_PATH);
    }

    @Override
    public Iterable<SimpleObject> getSince(DateTime dateTime) {
        throw new UnsupportedOperationException("Unable to perform change track by time");
    }

    @Override
    public Iterable<SimpleObject> getAll() {

        try (DirectoryStream<Path> ds =
                     Files.newDirectoryStream(getFileSystem())) {
            List<SimpleObject> all = new ArrayList<>();
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    ContainerObject container = new ContainerObject();
                    container.setPath(p.toAbsolutePath().toString());
                    all.add(container);
                } else {
                    UnstructuredObject unstructObj = new UnstructuredObject();
                    unstructObj.setPath(p.toAbsolutePath().toString());
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

    public Path getFileSystem() {
        return FileSystems.getDefault().getPath(properties.getProperty(FILE_PATH));
    }
}
