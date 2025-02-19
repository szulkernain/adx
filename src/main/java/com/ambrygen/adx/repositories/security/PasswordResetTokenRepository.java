package com.ambrygen.adx.repositories.security;

import com.ambrygen.adx.models.security.PasswordResetToken;
import com.ambrygen.adx.models.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByResetToken(String sessionToken);

    List<PasswordResetToken> findByUser(User user);

    Long deleteByResetToken(String resetToken);
}
