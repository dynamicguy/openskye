package org.skye.hadoop.objects;

import lombok.Data;
import org.skye.core.StructuredObject;
import org.skye.core.structured.ColumnMetadata;
import org.skye.core.structured.Row;

import javax.persistence.*;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "STRUCTURED_OBJECTS", schema = "skye@hbase")
@Data
public class HStructuredObject extends StructuredObject {
    @Id
    @Column(name = "ID")
    private UUID id = UUID.randomUUID();




    @Override
    public List<ColumnMetadata> getColumns() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterator<Row> getRows() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
