package org.skye.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.util.Factory;
import org.junit.Test;


/**
 * A class to test the effectiveness of SkyeRealm
 */
public class SkyeRealmTest {

    @Test
    public void initializesSkyeRealm(){
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-mock.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        for (Realm realm : ((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms()) {
            System.out.println(realm.getName());
        }

    }
}
