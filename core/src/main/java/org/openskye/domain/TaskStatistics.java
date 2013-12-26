package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * A log entry for a {@link org.openskye.domain.Task}
 */
@Entity
@Table(name = "TASK_STATISTICS")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
public class TaskStatistics implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @OneToOne (fetch = FetchType.EAGER)
    private Task task;
    private long simpleObjectsProcessed;
    private long simpleObjectsFound;

    public void incrementSimpleObjectsProcessed() {
        simpleObjectsProcessed++;
    }

    public void incrementSimpleObjectsFound() {
        simpleObjectsFound++;
    }

    public void addSimpleObjectsProcessed(long n) {
        simpleObjectsProcessed += n;
    }

    public void addSimpleObjectsFound(long n) {
        simpleObjectsFound += n;
    }
}
