package org.openskye.core;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: atcmostafavi
 * Date: 12/4/13
 * Time: 9:44 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class UnstructuredCompressedObject extends UnstructuredObject {

    public abstract List<SimpleObject> getObjectsContained();

    public abstract UnstructuredCompressedObject compress(SimpleObject so, CompressionType type);
}
