package org.skye.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;



/**
 * A class to test the effectiveness of SkyeRealm
 */
public class SkyeRealmTest {

    public static void main(String[] args){
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-mock.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);



    }
}
