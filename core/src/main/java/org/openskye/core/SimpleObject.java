package org.openskye.core;

import lombok.Data;

/**
 * A base abstract type to represent a simple object within the enterprise.
 * <p/>
 * A simple object is basically a handle to the object within Skye,  and links to the {@link org.openskye.core.ObjectMetadata}.
 * <p/>
 * You should always use the shared interfaces that are derived from a simple object such as:
 * <p/>
 * {@link org.openskye.core.StructuredObject} and {@link org.openskye.core.UnstructuredObject}
 * <p/>
 * These expose how you can interact with the content of the simple object.
 * <p/>
 * Note that the implementation of the subclass of the simple object is always tied to the {@link org.openskye.core.InformationStore}
 * or {@link org.openskye.core.ArchiveStore} from which you received the {@link org.openskye.core.SimpleObject}
 */
@Data
public abstract class SimpleObject {

    private ObjectMetadata objectMetadata = new ObjectMetadata();

}
