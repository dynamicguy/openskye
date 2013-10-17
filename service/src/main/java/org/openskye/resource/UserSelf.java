package org.openskye.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.openskye.domain.User;

/**
 * When the user gets information about the self then they should be
 * able to see the API key for themselves.
 * <p/>
 * This is not typically available on a {@link User}
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSelf {

    protected String id;
    private String email;
    private String name;
    private String apiKey;

    public UserSelf() {
        // No arg constructor
    }

    public UserSelf(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.apiKey = user.getApiKey();
    }

}
