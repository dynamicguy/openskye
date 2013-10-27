package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.persistence.annotations.UuidGenerator;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The representation of a user
 */
@Entity
@Table(name = "USER")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "UserGenerator")
@EqualsAndHashCode(of = "id")
public class User implements Identifiable {
    @Id
    @GeneratedValue(generator = "UserGenerator")
    @Column(unique = true, length = 36)
    private String id;
    private String email;
    private String name;
    @JsonIgnore
    private String passwordHash;
    @JsonIgnore
    private String apiKey;
    @Transient
    @JsonIgnore
    private String password;
    @ManyToOne
    private Domain domain;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private List<UserRole> userRoles = new ArrayList<>();

    @PrePersist
    public void setKeys() {
        encryptPassword();
    }

    public void encryptPassword() {
        if (password != null) {
            setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        } else {
            // Generate a UUID as a password
            setPasswordHash(BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt()));
        }
    }
}
