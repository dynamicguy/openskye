package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * A log entry for a {@link org.skye.domain.Task}
 */
@Entity
@Table(name = "TASK_STATISTICS")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStatistics implements Identifiable
{
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private Task task;
    private long simpleObjectsIngested;
    private long simpleObjectsDiscovered;

    public void incrementSimpleObjectsIngested() {
        simpleObjectsIngested++;
    }

    public void incrementSimpleObjectsDiscovered() {
        simpleObjectsDiscovered++;
    }
}
