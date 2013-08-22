package org.skye.security;


import org.apache.shiro.authc.AuthenticationToken;

/**
 * SkyeAuthenticationToken
 *
 * An AuthenticationToken class specific to Skye (currently just a username-password authentication,
 * hashing and API credentials to be added later)
 */
public class SkyeAuthenticationToken implements AuthenticationToken {

    private String user;
    private String password;

    public SkyeAuthenticationToken(){
        user="";
        password = "";
    }

    public SkyeAuthenticationToken(String newUser, String newPassword){
        user = newUser;
        password = newPassword;
    }

    //retrieve the password
    @Override
    public String getCredentials() {
        return password;
    }

    //retrieve username
    @Override
    public String getPrincipal() {
        return user;
    }

}
