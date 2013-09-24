package org.skye.stores.information.cifs;

import jcifs.smb.SmbFile;
import org.skye.core.MissingObjectException;
import org.skye.core.UnstructuredObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * An implementation of an {@link UnstructuredObject} that can be used
 * for local files
 */
public class CifsUnstructuredObject extends UnstructuredObject {

    public SmbFile smbFile;

    @Override
    public InputStream getContent() throws MissingObjectException {
        try {
            InputStream is = new FileInputStream(getObjectMetadata().getPath());
            return is;
        } catch (FileNotFoundException e) {
            throw new MissingObjectException();
        }
    }
}
