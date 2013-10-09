package org.skye.security;

import com.google.common.base.Optional;
import org.apache.shiro.authc.AuthenticationToken;
import org.skye.domain.User;
import org.skye.domain.dao.UserDAO;

import javax.inject.Inject;


/**
 * A Skye API key encodes the user id, password and time of creation
 */
public class ApiKeyToken implements AuthenticationToken {

    String key;

    @Inject
    private UserDAO userDao;

    public ApiKeyToken(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public Object getPrincipal() {
        Optional<User> user = userDao.findByApiKey(key);
        if (user.isPresent()) {
            return user.get();
        } else {
            return "";
        }
    }

    @Override
    public Object getCredentials() {
        return key;
    }
}
