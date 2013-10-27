package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * Represents a retention policy
 */
@Entity
@Table(name = "RETENTION_POLICY")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "RetentionPolicyGenerator")
@EqualsAndHashCode(of = "id")
public class RetentionPolicy implements Identifiable {
    @Id
    @GeneratedValue(generator = "RetentionPolicyGenerator")
    @Column(unique = true, length = 36)
    private String id;
    private String name;
    private String description;
    private String recordsCode;
    private Long retentionPeriod;
    private PeriodType periodType;
    private boolean onHold = false;

}
