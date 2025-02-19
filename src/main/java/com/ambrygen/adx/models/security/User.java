package com.ambrygen.adx.models.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ambrygen.adx.models.Auditable;
import com.ambrygen.adx.models.UserVehicle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter

@NoArgsConstructor
public class User extends Auditable<String> {
    @Column
    private String emailAddress;

    @Column
    private String firstName;

    @Column(name  = "last_name")
    private String familyName;

    @Column
    @JsonIgnore
    private String password;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "is_expired")
    private boolean isExpired;

    @Column(name = "is_locked")
    private boolean isLocked;

    @Column(name = "credentials_expired")
    private boolean credentialsExpired;

    @OneToMany(mappedBy = "user")
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


    @OneToMany(mappedBy = "user")
    private Set<UserVehicle> userVehicles = new HashSet<UserVehicle>();

    public Set<UserVehicle> getUserVehicles() {
        return userVehicles;
    }

    public void setUserVehicles(Set<UserVehicle> userVehicles) {
        this.userVehicles = userVehicles;
    }

    public void addUserVehicle(UserVehicle userVehicle) {
        this.userVehicles.add(userVehicle);
    }

    public User(String emailAddress, String firstName, String lastName, String password) {
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.familyName = lastName;
        this.password = password;
    }
}
