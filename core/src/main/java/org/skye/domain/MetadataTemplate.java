package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "METADATA_TEMPLATE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetadataTemplate implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(unique = true)
    private String id;
    private String name;
    private String description;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Permission> fieldDefinitions = new ArrayList<>();

}
