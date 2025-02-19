package com.ambrygen.adx.services.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.models.security.UserRole;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;
    private String id;
    private String username;
    private String email;
    private User user;
    private String firstName;
    private String lastName;
    private boolean isEnabled;
    private boolean isExpired;
    private boolean isLocked;
    private boolean credentialsExpired;

    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String id, String email, String password,
                           String firstName, String lastName,
                           boolean isEnabled, boolean isExpired, boolean isLocked,
                           boolean credentialsExpired,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = email;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isEnabled = isEnabled;
        this.isExpired = isExpired;
        this.isLocked = isLocked;
        this.credentialsExpired = credentialsExpired;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName().name()))
                .collect(Collectors.toList());
        UserDetailsImpl userDetails = new UserDetailsImpl(
                user.getId(),
                user.getEmailAddress(),
                user.getPassword(),
                user.getFirstName(),
                user.getFamilyName(),
                user.isEnabled(),
                user.isExpired(),
                user.isLocked(),
                user.isCredentialsExpired(),
                authorities);
        userDetails.setUser(user);
        return userDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<UserRole> userRoles = user.getUserRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority(userRole.getRole().getName().name()));
        }

        return authorities;
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

}
