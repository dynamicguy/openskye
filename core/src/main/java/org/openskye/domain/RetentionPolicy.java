package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
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
    @Column(unique=true)
    private String name;
    private String description;
    @NotNull
    @NotBlank
    @NaturalId
    private String recordsCode;
    @Min(1)
    private Long retentionPeriod;
    @Min(0)
    private Integer priority = 0;
    @NotNull
    private PeriodType periodType;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEMPLATE_ID")
    private MetadataTemplate metadataTemplate;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "METADATA_CRITERIA",length=4096)
    private String metadataCriteria;
    // If not null, the following attr def overrides the default date used to calculate the retention period
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ATTRIBUTE_DEFINITION_ID")
    private AttributeDefinition triggerDateAttributeDefinition;
}
