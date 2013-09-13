package org.skye.stores.information.localfs;

import org.skye.core.MissingObjectException;
import org.skye.core.UnstructuredObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * An implementation of an {@link UnstructuredObject} that can be used
 * for local files
 */
public class LocalFileUnstructuredObject extends UnstructuredObject {

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
