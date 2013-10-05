package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Representation of a filter on a {@link Channel},  a filter can be defined and
 * used to limit the simple objects that will pass through a {@link Channel}
 */
@Entity
@Table(name = "CHANNEL_FILTER")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelFilterDefinition implements Identifiable
{
    @Id
    @GeneratedValue(generator = "uuid")
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
