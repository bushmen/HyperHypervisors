package pl.edu.agh.student.hyperhypervisors.session;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.agh.student.hyperhypervisors.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.neo4j.domain.UserRole;

import java.util.Collection;
import java.util.Collections;

public class UserSessionDetails implements UserDetails {

    private final User user;

    public UserSessionDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<UserRole> roles = user.getRoles();
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}
