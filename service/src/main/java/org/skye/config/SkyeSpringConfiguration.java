package org.skye.config;

import org.skye.SkyeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.access.vote.RoleVoter;

/**
 * Main Spring Configuration
 */
@Configuration
@ImportResource("classpath:skye-security.xml")
@ComponentScan(basePackageClasses = SkyeService.class)
public class SkyeSpringConfiguration {

    @Bean
    public RoleVoter getRoleVoter() {
        RoleVoter roleVoter = new RoleVoter();
        roleVoter.setRolePrefix("ROLE_");
        return roleVoter;
    }
}
