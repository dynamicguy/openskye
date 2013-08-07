package org.skye.security;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service("skyeAuthenticationManager")
public class SkyeAuthenticationManager implements AuthenticationManager {

    //DUMMY values for now
    private Multimap<String,GrantedAuthority> privs = ArrayListMultimap.create();

    @PostConstruct
    public void init() {
        //create dummy users with privs
        privs.put("read", new SimpleGrantedAuthority("read-only"));

        privs.put("admin", new SimpleGrantedAuthority("admin"));
        privs.put("admin", new SimpleGrantedAuthority("read-only"));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String user = String.valueOf(authentication.getName());
        String password = String.valueOf(authentication.getCredentials());

        if (!privs.containsKey(user) || !"test".equals(password)) {
            throw new BadCredentialsException("Access denied.");
        }

        //return authentication token + set roles in context
        Authentication auth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(), privs.get(user));

        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }

    public void authenticateBackgroundTask(Class<?> source, String...requiredPrivileges) {
        Authentication auth = null;
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(String privilege : requiredPrivileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        auth = new UsernamePasswordAuthenticationToken(source.getName(),
                "background", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
