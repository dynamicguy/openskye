package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * Represents a named tag that can be associated with {@link SimpleObject}
 */
@Embeddable
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "name")
public class Tag {

    @NotNull
    @NotBlank
    private String name;

}
