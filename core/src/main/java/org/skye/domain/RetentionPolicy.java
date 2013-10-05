package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * Represents a retention policy
 */
@Entity
@Table(name = "RETENTION_POLICY")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RetentionPolicy implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(unique = true)
    private String id;
    private String name;
    private String description;
    private String recordsCode;
    private Long retentionPeriod;
    private PeriodType periodType;
    private boolean onHold = false;

}
