package com.doomageddon.model.security;

import com.doomageddon.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

public class UserDetailsImpl implements UserDetails {

    private Long userId;
    private String name;
    private boolean enable;
    private String password;
    private List<GrantedAuthority> roles;

    public UserDetailsImpl(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.roles = getUserRoles(user);
        this.enable = user.getIsEnabled();
        this.password = user.getPassword();
    }

    private List<GrantedAuthority> getUserRoles(User user) {
        return singletonList(new SimpleGrantedAuthority(user.getRole().toString()));
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
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
        return enable;
    }
}
