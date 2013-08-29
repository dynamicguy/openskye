package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Relationship between {@link User} and {@link Role}
 */
@Entity
@Table(name = "USER_ROLE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRole {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Role role;

    @Override
    public String toString(){
        return "User = "+user + " Role = "+role;
    }
}
