package org.openskye.hadoop.objects;

import lombok.Data;
import org.openskye.core.UnstructuredObject;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Entity
@Table(name = "UNSTRUCTURED_OBJECTS", schema = "openskye@hbase")
@Data
public class HUnstructuredObject extends UnstructuredObject {

    @Id
    @Column(name = "ID")
    private String id = getObjectMetadata().getId();

    @Column(name="Raw Content")
    private byte[] content;


    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(content);
    }
}
