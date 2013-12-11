package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Represents a retention policy
 */
@Entity
@Table(name = "RETENTION_POLICY")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
public class RetentionPolicy implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @NotBlank
    private String recordsCode;
    @Min(1)
    private Long retentionPeriod;
    @NotNull
    private PeriodType periodType;
    private boolean onHold = false;

}
