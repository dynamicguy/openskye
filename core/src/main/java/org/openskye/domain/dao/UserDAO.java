package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.openskye.domain.*;

import javax.persistence.criteria.*;
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

    public boolean isPermitted(String userId, String permission)
    {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Permission> permissionRoot = criteria.from(Permission.class);
        ListJoin<Permission, RolePermission> rolePermissionJoin = permissionRoot.joinList("rolePermissions");
        Join<RolePermission, Role> roleJoin = rolePermissionJoin.join("role");
        Join<Role, UserRole> userRoleJoin = roleJoin.join("id");
        Join<UserRole, User> userJoin = userRoleJoin.join("user");

        criteria.select(builder.count(permissionRoot));
        criteria.where(builder.equal(userJoin.get("id"), userId))
                .where(builder.equal(permissionRoot.get("permission"), permission));

        return (getEntityManagerProvider().get().createQuery(criteria).getSingleResult() > 0);
    }
}
