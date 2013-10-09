package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * Representation of a filter on a {@link Channel},  a filter can be defined and
 * used to limit the simple objects that will pass through a {@link Channel}
 */
@Entity
@Table(name = "CHANNEL_FILTER_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "ChannelFilterDefinitionGenerator")
public class ChannelFilterDefinition implements Identifiable {
    @Id
    @GeneratedValue(generator = "ChannelFilterDefinitionGenerator")
    @Column(unique = true)
    private String id;
    private String implementation;
    private String description;
    private String definition;
    @ManyToOne
    private Channel channel;
    // Determines if the filter is to include,  if this is true then we will include
    // based on the filter.  If it is false we will exclude based on the filter.
    private boolean include = true;
}
