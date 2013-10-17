package org.openskye.core;

import java.io.InputStream;

/**
 * An <code>UnstructuredObject</code> is the metadata representing an unstructured object (ie. a file)
 */
public abstract class UnstructuredObject extends SimpleObject {

    public abstract InputStream getContent() throws MissingObjectException;

}
