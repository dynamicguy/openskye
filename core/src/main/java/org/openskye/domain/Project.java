package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * A project
 */
@Entity
@Table(name = "PROJECT")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"informationStores", "archiveStores", "channels", "tasks"})
public class Project implements Identifiable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    @JoinColumn(name = "DOMAIN_ID")
    private Domain domain;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    @JsonIgnore
    private List<InformationStoreDefinition> informationStores = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    @JsonIgnore
    private List<ArchiveStoreDefinition> archiveStores = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    @JsonIgnore
    private List<Channel> channels = new ArrayList<>();
    @NotNull
    @NotBlank
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();
    private boolean active = true;
    private boolean duplicationAllowed=false;
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    @JsonIgnore
    private List<ProjectUser> projectUsers = new ArrayList<>();


}
