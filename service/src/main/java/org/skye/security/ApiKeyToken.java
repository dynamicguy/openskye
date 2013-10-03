package org.skye.security;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.skye.domain.User;

/**
 * A Skye API key encodes the user id, password and time of creation
 */
public class ApiKeyToken implements AuthenticationToken {

    String key;
    static byte[] b = {0x15,0x23,0x04,0x44,0x51,0x1c,0x3b,0x7c,0x15,0x23,0x04,0x44,0x51,0x1c,0x3b,0x7c}; // encryption key
    static AesCipherService cipherService = new AesCipherService();

    public ApiKeyToken( User user, Long time ) {
        String keyString = user.getEmail() + ":" + time;
        key = cipherService.encrypt(keyString.getBytes(),b).toBase64();
    }

    public ApiKeyToken( User user ) {
        this( user, System.currentTimeMillis()/1000L );
    }

    private String getKeyString() {
        String keyString = new String(cipherService.decrypt(Base64.decode(key),b).getBytes());
        return keyString;
    }

    public ApiKeyToken( String key ) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public boolean isValid() {
        try {
            String keyString = getKeyString();
            long now = System.currentTimeMillis()/1000L;
            long then = Long.parseLong(keyString.replaceAll("^.*:",""));
            return ( then == 0 || now < then + 86400L ); // API key is eternal (time==0) or less than 24 hours old
        } catch( Throwable e ) {
            return false;
        }
    }


    public String getUsername() {
        try {
            return getKeyString().replaceAll(":.*$","");
        } catch ( Throwable e ) {
            return "";
        }
    }

    @Override
    public Object getPrincipal() {
        return getUsername();
    }

    @Override
    public Object getCredentials() {
        return key;
    }
}
