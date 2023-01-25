package com.example.pigeon.security;

import com.example.pigeon.model.Privilege;
import com.example.pigeon.model.Role;
import com.example.pigeon.model.User;
import com.example.pigeon.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("userDetailsService")
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    private LoginAttemptService loginAttemptService;
    private HttpServletRequest request;

    public CustomUserDetailsService() { super(); }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {

        final String ip = getClientIP();
        if(loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("blocked");
        }

        try {
            final User user = userRepository.findByEmail(email);
            if(user == null) {
                throw new UsernameNotFoundException("No user found with username: " + email);
            }

            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                    user.getPassword(),
                    user.isEnabled(),
                    true,
                    true,
                    true,
                    getAuthorities(user.getRoles()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {

        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(final Collection<Role> roles) {

        final List<String> privileges = new ArrayList<>();
        final List<Privilege> collection = new ArrayList<>();
        for(final Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for(final Privilege item : collection) {
            privileges.add(item.getName());
        }

        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {

        final List<GrantedAuthority> authorities = new ArrayList<>();
        for(final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    private String getClientIP() {

        final String xfHeader = request.getHeader("X-Forwarded-For");
        if(xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
