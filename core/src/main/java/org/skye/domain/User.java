package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The representation of a user
 */
@Entity
@Table(name = "USER")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private String username;
    private String email;
    private String name;
    private String passwordHash;
    @Transient
    @JsonIgnore
    private String password;
    @ManyToOne
    private Domain domain;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<UserRole> userRoles = new ArrayList<>();

    @PrePersist
    public void encryptPassword() {
        if (password != null) {
            setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        } else {
            setPasswordHash(BCrypt.hashpw("changeme", BCrypt.gensalt()));
        }
    }



}
