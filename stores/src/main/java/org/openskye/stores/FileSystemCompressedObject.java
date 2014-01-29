package org.openskye.stores;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.joda.time.DateTime;
import org.openskye.core.*;
import org.openskye.stores.information.localfs.LocalFileUnstructuredObject;
import org.openskye.stores.util.MimeTypeUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * An implementation of a {@link SimpleObject} that can be used for compressed files (e.g. zip, tar)
 */
public class FileSystemCompressedObject extends UnstructuredCompressedObject {

    public List<SimpleObject> getObjectsContained() {
        try {
            List<SimpleObject> objects = new ArrayList<>();
            ArchiveInputStream stream = new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(new FileInputStream(this.getObjectMetadata().getPath())));
            ArchiveEntry currentEntry = stream.getNextEntry();
            ObjectMetadata om = this.getObjectMetadata();
            String originalPath = this.getObjectMetadata().getPath();
            while (currentEntry != null) {
                ObjectMetadata newOm = new ObjectMetadata();
                newOm.setPath(originalPath + "/" + currentEntry.getName());
                newOm.setOriginalSize(currentEntry.getSize());
                newOm.setContainer(currentEntry.isDirectory());
                newOm.setLastModified(new DateTime(currentEntry.getLastModifiedDate()));
                newOm.setMimeType(MimeTypeUtil.getContentType(readBytes(currentEntry, stream)));
                newOm.setTaskId(om.getTaskId());
                newOm.setCreated(om.getCreated());
                newOm.setIngested(om.getIngested());
                newOm.setInformationStoreId(om.getInformationStoreId());
                newOm.setProject(om.getProject());
                newOm.setImplementation(LocalFileUnstructuredObject.class.getCanonicalName());
                SimpleObject so = new LocalFileUnstructuredObject();
                so.setObjectMetadata(newOm);
                objects.add(so);
                currentEntry = stream.getNextEntry();
            }
            return objects;
        } catch (Exception e) {
            throw new SkyeException("Skye Exception", e);
        }

    }

    @Override
    public String getCompressedContainer() {
        String[] pathSplit = getObjectMetadata().getPath().split("/");
        return pathSplit[pathSplit.length-1];  //To change body of implemented methods use File | Settings | File Templates.
    }


    public byte[] readBytes(ArchiveEntry entry, ArchiveInputStream stream) {
        byte[] content = new byte[(int) entry.getSize()];
        for (int offset = 0; offset < content.length; offset++) {
            try {
                stream.read(content, offset, content.length - offset);
            } catch (IOException e) {
                throw new SkyeException("Skye Exception", e);
            }
        }
        return content;
    }

    @Override
    public InputStream getInputStream() throws MissingObjectException {
        ArchiveInputStream stream = null;
        try {
            stream = new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(new FileInputStream(this.getObjectMetadata().getPath())));
        } catch (ArchiveException e) {
            throw new SkyeException("Cannot create ArchiveStream", e);
        } catch (FileNotFoundException e) {
            throw new SkyeException("Path not found", e);
        }

        return stream;
    }
}
