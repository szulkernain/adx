package com.ambrygen.adx.repositories.security;

import com.ambrygen.adx.models.security.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmailAddress(String emailAddress);

    Boolean existsByEmailAddress(String emailAddress);
}
