package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The representation of an information store owned by a {@link Domain}
 */
@Entity
@Table(name = "DOMAIN_INFORMATION_STORE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InformationStoreDefinition {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String id;
    private String name;
    @ManyToOne
    private Domain domain;
    // The name of the {@link InformationStore} implementation
    private String implementation;
    @Transient
    private Map<String, String> properties = new HashMap<>();

}
