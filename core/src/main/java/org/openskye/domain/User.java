package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
@EqualsAndHashCode(of = "id")
public class User implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @NotNull
    @NotBlank
    @NaturalId
    @Email
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

    public boolean isPassword(String passwordToTest) {
        return BCrypt.hashpw(passwordToTest, BCrypt.gensalt()).equals(password);
    }
}
