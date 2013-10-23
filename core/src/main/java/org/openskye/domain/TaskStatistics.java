package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * A log entry for a {@link org.openskye.domain.Task}
 */
@Entity
@Table(name = "TASK_STATISTICS")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "TaskStatisticsGenerator")
public class TaskStatistics implements Identifiable {
    @Id
    @GeneratedValue(generator = "TaskStatisticsGenerator")
    @Column(unique = true, length = 36)
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
