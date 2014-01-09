package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Representation of a filter on a {@link Channel}. A filter can be defined and used to limit the simple objects that
 * will pass through a {@link Channel}
 */
@Entity
@Table(name = "CHANNEL_FILTER_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
public class ChannelFilterDefinition implements Identifiable {
    /**
     * The id for this filter
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    /**
     * The type of filter this is. For example, a regex filter.
     */
    private String implementation;
    /**
     * A brief description of this filter
     */
    private String description;
    /**
     * The String that defines this filter. For example, a regex string.
     */
    private String definition;
    /**
     * The <code>Channel</code> this filter is attached to.
     */
    @ManyToOne
    @JsonBackReference("channelFilters")
    private Channel channel;
    /**
     * Determines if the filter is inclusive.  If this is true then we will include based on the filter.  If it is
     * false we will exclude based on the filter.
     */
    private boolean include = true;
}
