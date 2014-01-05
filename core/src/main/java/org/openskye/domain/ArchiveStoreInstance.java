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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An instance of an {@link org.openskye.core.ArchiveStore} definition
 */
@Entity
@Table(name = "ARCHIVE_STORE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
@ToString(exclude = "properties")
public class ArchiveStoreInstance implements Identifiable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @NotNull
    @NotBlank
    @Column(unique=true)
    private String name;
    // The name of the {@link ArchiveStore} implementation
    @NotNull
    @NotBlank
    private String implementation;
    @ElementCollection
    @MapKeyColumn(name = "NAME")
    @Column(name = "VALUE")
    @CollectionTable(name = "ARCHIVE_STORE_PROPERTIES", joinColumns = @JoinColumn(name = "ARCHIVE_STORE_ID"))
    private Map<String, String> properties = new HashMap<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "archiveStoreInstance")
    @JsonIgnore
    private List<NodeArchiveStoreInstance> nodes = new ArrayList<>();

}
