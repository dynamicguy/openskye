package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * A log entry for a {@link org.skye.domain.Task}
 */
@Entity
@Table(name = "TASK_STATISTICS")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStatistics implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(unique = true)
    private String id;
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
