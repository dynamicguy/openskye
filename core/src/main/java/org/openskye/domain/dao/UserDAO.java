package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.openskye.domain.Role;
import org.openskye.domain.User;
import org.openskye.domain.UserRole;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * DAO for the {@link org.openskye.domain.User}
 */
public class UserDAO extends AbstractPaginatingDAO<User> {

    public Optional<User> findByEmail(String email) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> userRoot = criteria.from(User.class);
        criteria.select(userRoot);
        criteria.where(builder.equal(userRoot.get("email"), email));
        List<User> users = currentEntityManager().createQuery(criteria).getResultList();
        if (users.size() == 0) {
            return Optional.absent();
        } else {
            return Optional.of(users.get(0));
        }
    }

    public Optional<User> findByApiKey(String key) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> userRoot = criteria.from(User.class);
        criteria.select(userRoot);
        criteria.where(builder.equal(userRoot.get("apiKey"), key));
        List<User> users = currentEntityManager().createQuery(criteria).getResultList();
        if (users.size() == 0) {
            return Optional.absent();
        } else {
            return Optional.of(users.get(0));
        }
    }

    public void setNewUserRole(User newUser){
        CriteriaQuery<Role> criteriaQuery = getCriteriaBuilder().createQuery(Role.class);
        Root<Role> roleRoot = criteriaQuery.from(Role.class);
        criteriaQuery.select(roleRoot);
        criteriaQuery.where(getCriteriaBuilder().equal(roleRoot.get("name"),"readonly"));
        Role reader = currentEntityManager().createQuery(criteriaQuery).getSingleResult();
        UserRole userRole = new UserRole();
        userRole.setUser(newUser);
        userRole.setRole(reader);
        newUser.getUserRoles().add(userRole);
        update(newUser.getId(),newUser);
    }
}
