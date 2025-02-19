package com.ambrygen.adx.repositories.security;

import com.ambrygen.adx.models.security.RefreshToken;
import com.ambrygen.adx.models.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    @Override
    Optional<RefreshToken> findById(String id);

    Optional<RefreshToken> findByToken(String token);

    String deleteByUser(User user);
}
