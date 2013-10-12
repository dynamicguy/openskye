package org.skye.hadoop.objects;

import com.mongodb.Bytes;
import lombok.Data;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.hbase.util.*;
import org.skye.core.MissingObjectException;
import org.skye.core.UnstructuredObject;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Entity
@Table(name = "UNSTRUCTURED_OBJECTS", schema = "skye@hbase")
@Data
public class HUnstructuredObject extends UnstructuredObject {

    @Id
    @Column(name = "ID")
    private String id = getObjectMetadata().getId();

    @Column(name="Raw Content")
    private byte[] content;


    @Override
    public InputStream getContent() {
        return null;
    }
}
