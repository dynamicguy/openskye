package org.openskye.stores;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.joda.time.DateTime;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SimpleObject;
import org.openskye.core.SkyeException;
import org.openskye.stores.information.localfs.LocalFileUnstructuredObject;
import org.openskye.stores.util.MimeTypeUtil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * An implementation of a {@link SimpleObject} that can be used
 * for compressed files (e.g. zip, tar)
 */
public class CompressedObject extends SimpleObject {

    public List<SimpleObject> getObjectsContained(){
        try {
            List<SimpleObject> objects = new ArrayList<>();
            ArchiveInputStream stream = new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(new FileInputStream(this.getObjectMetadata().getPath())));
            ArchiveEntry currentEntry = stream.getNextEntry();
            while(currentEntry!=null){
                ObjectMetadata om = this.getObjectMetadata();
                om.setPath(this.getObjectMetadata().getPath()+"/"+currentEntry.getName());
                om.setOriginalSize(currentEntry.getSize());
                om.setContainer(currentEntry.isDirectory());
                om.setLastModified(new DateTime(currentEntry.getLastModifiedDate()));
                om.setMimeType(MimeTypeUtil.getContentType(readBytes(currentEntry, stream)));
                SimpleObject so = new LocalFileUnstructuredObject();
                so.setObjectMetadata(om);
                objects.add(so);
            }
            return objects;
        } catch (Exception e) {
            throw new SkyeException("Skye Exception", e);
        }

    }

    public byte[] readBytes(ArchiveEntry entry, ArchiveInputStream stream){
        byte[] content = new byte[(int)entry.getSize()];
        for(int offset=0;offset<content.length;offset++){
            try {
               stream.read(content, offset, content.length-offset);
            } catch (IOException e) {
                throw new SkyeException("Skye Exception", e);
            }
        }
        return content;
    }

}
