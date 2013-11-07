package org.openskye.stores.information.cifs;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.openskye.core.MissingObjectException;
import org.openskye.core.UnstructuredObject;

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
            InputStream is = new SmbFileInputStream(smbFile);
            return is;
        } catch (Exception e) {
            throw new MissingObjectException();
        }
    }


}
