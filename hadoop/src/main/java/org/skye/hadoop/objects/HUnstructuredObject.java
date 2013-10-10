package org.skye.hadoop.objects;

import lombok.Data;
import org.skye.core.MissingObjectException;
import org.skye.core.UnstructuredObject;

import javax.persistence.*;
import java.io.InputStream;
import java.util.UUID;

@Entity
@Table(name = "UNSTRUCTURED_OBJECTS", schema = "skye@hbase")
@Data
public class HUnstructuredObject extends UnstructuredObject {

    @Id
    @Column(name = "ID")
    private UUID id = UUID.randomUUID();

    @Override
    public InputStream getContent() throws MissingObjectException {

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
