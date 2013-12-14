package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROJECT_USER")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectUser implements Identifiable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    @ManyToMany
    @JsonIgnore
    @JoinTable(
        name = "PROJECT_USER_ROLES", joinColumns = {
        @JoinColumn(
                name = "roleId",
                referencedColumnName = "id"
        )
    },
    inverseJoinColumns = {
        @JoinColumn(
                name = "projectUserId",
                referencedColumnName = "id"
        )
    }
    )
    private List<Role> projectUserRoles = new ArrayList<>();

    public void addRole(Role r){
        projectUserRoles.add(r);
    }



}
