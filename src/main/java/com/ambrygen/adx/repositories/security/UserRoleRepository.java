package com.ambrygen.adx.repositories.security;

import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.models.security.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    List<UserRole> findByUser(User user);
}
