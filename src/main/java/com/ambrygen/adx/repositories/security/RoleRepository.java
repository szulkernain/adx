package com.ambrygen.adx.repositories.security;

import com.ambrygen.adx.models.ERole;
import com.ambrygen.adx.models.security.Role;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByName(ERole name);
}
