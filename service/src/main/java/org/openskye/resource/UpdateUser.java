package org.openskye.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * This is the update representation of a user
 * <p/>
 * Note that it includes the old and new password which is why it is a different
 * entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUser {

    protected String id;
    private String email;
    private String name;
    private String apiKey;
    private String oldPassword;
    private String newPassword;

}
