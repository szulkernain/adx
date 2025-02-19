package com.ambrygen.adx.services.security;

import com.ambrygen.adx.models.security.RefreshToken;
import com.ambrygen.adx.repositories.security.RefreshTokenRepository;
import com.ambrygen.adx.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;


    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    /**
     * Replace UUID.randomUUID() with following method
     * <p>
     * import java.security.SecureRandom;
     * import java.util.Base64;
     * <p>
     * public class SecureRandomString {
     * private static final SecureRandom random = new SecureRandom();
     * private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
     * <p>
     * public static String generate() {
     * byte[] buffer = new byte[20];
     * random.nextBytes(buffer);
     * return encoder.encodeToString(buffer);
     * }
     * }
     **/


    public RefreshToken createRefreshToken(String userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(calculateExpiryDate(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        Calendar cal = Calendar.getInstance();

        if ((token.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            refreshTokenRepository.delete(token);
            String.format("Refresh token %s was expired. Please make a new signin request", token.getToken());
        }
        return token;
    }

    @Transactional
    public String deleteByUserId(String userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    private Date calculateExpiryDate(final Long expiryTimeInMilliseconds) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MILLISECOND, expiryTimeInMilliseconds.intValue());
        return new Date(cal.getTime().getTime());
    }
}
