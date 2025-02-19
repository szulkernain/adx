package com.ambrygen.adx.models.security;

import com.ambrygen.adx.models.Auditable;
import com.ambrygen.adx.models.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role extends Auditable<String> {
    @Enumerated(EnumType.STRING)
    @Column
    private ERole name;

    @Column
    private String description;

    public Role(ERole name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = new HashSet<UserRole>();


    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
    }
}
