package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * Relationship between {@link User} and {@link Role}
 */
@Entity
@Table(name = "USER_ROLE")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRole implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(unique = true)
    private String id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Role role;

}
