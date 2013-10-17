package org.openskye.hadoop.objects;

import lombok.Data;
import org.openskye.core.StructuredObject;
import org.openskye.core.structured.ColumnMetadata;
import org.openskye.core.structured.Row;

import javax.persistence.*;
import java.util.Iterator;
import java.util.List;

@Entity
@Table(name = "STRUCTURED_OBJECTS", schema = "openskye@hbase")
@Data
public class HStructuredObject extends StructuredObject {
    @Id
    @Column(name = "ID")
    private String id=this.getObjectMetadata().getId();

    @Override
    public List<ColumnMetadata> getColumns() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterator<Row> getRows() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
