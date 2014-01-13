package org.openskye.stores.information.localfs;

import lombok.extern.slf4j.Slf4j;
import org.openskye.core.MissingObjectException;
import org.openskye.core.UnstructuredObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * An implementation of an {@link UnstructuredObject} that can be used
 * for local files
 */
@Slf4j
public class LocalFileUnstructuredObject extends UnstructuredObject {

    @Override
    public InputStream getInputStream() throws MissingObjectException {
        try {
            log.info("Getting content on " + getObjectMetadata());
            InputStream is = new FileInputStream(getObjectMetadata().getPath());
            return is;
        } catch (FileNotFoundException e) {
            throw new MissingObjectException();
        }

    }
}
