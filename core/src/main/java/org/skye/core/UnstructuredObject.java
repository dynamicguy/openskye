package org.skye.core;

import java.io.InputStream;

/**
 * An <code>UnstructuredObject</code> is the metadata representing an unstructured object (ie. a file)
 */
public class UnstructuredObject extends SimpleObject {

    public InputStream getContent() {
        return null;
    }

}
