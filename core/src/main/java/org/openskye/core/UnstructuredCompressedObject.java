package org.openskye.core;

import java.util.List;

/**
 * A class which works with, and can turn objects into, compressed files (currently zip, tar, or tar.gz)
 */
public abstract class UnstructuredCompressedObject extends UnstructuredObject {

    /**
     * Get the objects contained in this compressed file
     *
     * @return The objects in the compressed file
     */
    public abstract List<SimpleObject> getObjectsContained();

    /**
     * Returns the name of the compressed file container
     * @return container name
     */
    public abstract String getCompressedContainer();

}
