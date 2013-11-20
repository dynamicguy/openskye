package org.openskye.stores.information.cifs;

import com.google.common.base.Optional;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.openskye.core.*;
import org.openskye.domain.InformationStoreDefinition;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A local filesystem information store
 * <p/>
 * Can be used to pull from a local filesystem and return a set of {@link org.openskye.core.UnstructuredObject}
 * instances
 */
@Slf4j
public class CifsInformationStore implements InformationStore {

    public final static String IMPLEMENTATION = "CIFS";
    public final static String DOMAIN = "domain";
    public final static String USER = "user";
    public final static String PASSWORD = "password";
    public final static String HOST = "host";
    public final static String PORT = "port";
    public final static String SHARE = "share";
    public final static String FILE_PATH = "filePath";
    private InformationStoreDefinition informationStoreDefinition;
    private NtlmPasswordAuthentication credential;

    @Override
    public void initialize(InformationStoreDefinition dis) {
        this.informationStoreDefinition = dis;
        this.credential = new NtlmPasswordAuthentication(
                informationStoreDefinition.getProperties().get(DOMAIN),
                informationStoreDefinition.getProperties().get(USER),
                informationStoreDefinition.getProperties().get(PASSWORD)
        );
    }

    @Override
    public Properties getMetadata() {
        return new Properties();
    }

    @Override
    public String getName() {
        return "CIFS mountable filesystem";
    }

    @Override
    public String getImplementation() {
        return IMPLEMENTATION;
    }

    @Override
    public String getUrl() {
        String domain = informationStoreDefinition.getProperties().get(DOMAIN);
        String user = informationStoreDefinition.getProperties().get(USER);
        String host = informationStoreDefinition.getProperties().get(HOST);
        String port = informationStoreDefinition.getProperties().get(PORT);
        String share = informationStoreDefinition.getProperties().get(SHARE);
        String filePath = informationStoreDefinition.getProperties().get(FILE_PATH);
        return String.format("smb://%s;%s@%s:%s/%s/%s", domain, user, host, port, share, filePath);
    }

    @Override
    public Iterable<SimpleObject> getSince(DateTime dateTime) {
        throw new UnsupportedOperationException("Unable to perform change track by time");
    }

    @Override
    public Iterable<SimpleObject> getRoot() {
        try {
            String host = informationStoreDefinition.getProperties().get(HOST);
            String port = informationStoreDefinition.getProperties().get(PORT);
            String share = informationStoreDefinition.getProperties().get(SHARE);
            CifsContainerObject root = new CifsContainerObject();
            root.smbFile = new SmbFile(String.format("smb://%s:%s/%s/", host, port, share), credential);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setImplementation(CifsContainerObject.class.getCanonicalName());
            metadata.setPath(root.smbFile.getPath());
            metadata.setInformationStoreId(this.getInformationStoreDefinition().get().getId());
            root.setObjectMetadata(metadata);
            return getChildren(root);
        } catch (Exception e) {
            throw new SkyeException(e.getLocalizedMessage());
        }
    }

    @Override
    public Iterable<SimpleObject> getChildren(SimpleObject simpleObject) {
        if (simpleObject instanceof CifsContainerObject) {
            try {
                SmbFile file = ((CifsContainerObject) simpleObject).smbFile;
                SmbFile[] list = file.listFiles();
                List<SimpleObject> all = new ArrayList<>();
                for (SmbFile child : list) {
                    if (child.isDirectory()) {
                        CifsContainerObject container = new CifsContainerObject();
                        container.smbFile = child;
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setImplementation(CifsContainerObject.class.getCanonicalName());
                        metadata.setPath(child.getCanonicalPath());
                        metadata.setInformationStoreId(this.getInformationStoreDefinition().get().getId());
                        container.setObjectMetadata(metadata);
                        all.add(container);
                    } else if (child.isFile()) {
                        CifsUnstructuredObject unstructObj = new CifsUnstructuredObject();
                        unstructObj.smbFile = child;
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setImplementation(CifsUnstructuredObject.class.getCanonicalName());
                        metadata.setPath(child.getCanonicalPath());
                        metadata.setInformationStoreId(this.getInformationStoreDefinition().get().getId());
                        unstructObj.setObjectMetadata(metadata);
                        all.add(unstructObj);
                    }
                }
                return all;
            } catch (IOException e) {
                throw new SkyeException("Unable to list files on filesystem", e);
            }
        } else {
            return new ArrayList<>();
        }
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
        CifsUnstructuredObject unstructObj = new CifsUnstructuredObject();
        try {
            unstructObj.smbFile = new SmbFile(objectMetadata.getPath(), credential);
        } catch (MalformedURLException e) {
            throw new InvalidSimpleObjectException();
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
        throw new SkyeException("CIFS file system does not support put");
    }
}
