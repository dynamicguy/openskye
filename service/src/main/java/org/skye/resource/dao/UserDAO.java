package org.skye.resource.dao;

import com.google.common.base.Optional;
import org.hibernate.criterion.Restrictions;
import org.skye.domain.User;

import java.util.List;

/**
 * DAO for the {@link org.skye.domain.User}
 */
public class UserDAO extends AbstractPaginatingDAO<User> {

    public Optional<User> findByEmail(String email) {
        List<User> user = criteria().add(Restrictions.eq("email", email)).setMaxResults(1).list();
        if (user.size() == 0) {
            return Optional.absent();
        } else {
            return Optional.of(user.get(0));
        }
    }

}
