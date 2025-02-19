package com.ambrygen.adx.models.security;

import com.ambrygen.adx.models.Auditable;
import com.ambrygen.adx.utils.RandomCodeGenerator;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;

@Table(name = "verification_tokens")
@Data
@Entity
public class VerificationToken extends Auditable {
    private static final int EXPIRATION = 60 * 24;

    @Column(name = "token")
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public VerificationToken() {
        super();

        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
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


    public VerificationToken(final User user) {
        super();
        this.token = RandomCodeGenerator.generateRandomString(6);
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    /**
     * Return first 15 characters from this String while removing "-" char
     *
     * @param resetToken
     * @return
     */
    private String getShorterToken(String resetToken) {
        String token = resetToken.replaceAll("-", "");
        return token.substring(0, 16);
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

}
