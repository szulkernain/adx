package com.ambrygen.adx.repositories.security;


import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.models.security.VerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUserId(String id);

    void deleteAllByUser(User user);
}
