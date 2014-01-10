package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Stores information on Holds. A hold is a saved query that denotes objects that should not be destroyed (regardless of
 * that object's {@link RetentionPolicy}.
 */
@Entity
@Table(name = "HOLD")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
public class Hold implements Identifiable {
    /**
     * The id number for this <code>Hold</code>
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    /**
     * The name for this <code>Hold</code>
     */
    private String name;
    /**
     * A brief description of this <code>Hold</code>
     */
    private String description;
    /**
     * The query that this <code>Hold</code> is based on. Objects that fit this query are not eligible for destruction
     * until this <code>Hold</code> is removed.
     */
    @NotNull
    @NotBlank
    private String query;

}
