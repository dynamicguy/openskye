package org.openskye.stores.inmemory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.MissingObjectException;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SkyeException;
import org.openskye.core.UnstructuredObject;
import org.openskye.domain.Project;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * An implementation of an {@link UnstructuredObject} that can be used
 * for testing.  Its content is a fixed string.
 */
@Slf4j
public class InMemoryUnstructuredObject extends UnstructuredObject {
    @Getter
    @Setter
    private String content = "This is an unstructured object test.";

    public InMemoryUnstructuredObject(Project project, String id, String path, String content) {
        this.content = content;
        ObjectMetadata om = new ObjectMetadata();
        om.setId(id);
        om.setImplementation(this.getClass().getName());
        om.setPath(path);
        om.setProject(project);
        this.setObjectMetadata(om);
    }

    @Override
    public InputStream getInputStream() throws MissingObjectException {
        try {
            return new ByteArrayInputStream(content.getBytes(Charset.defaultCharset()));
        } catch(Exception e) {
            throw new SkyeException("Unable to convert string to input stream",e);
        }
    }
}
