package com.ambrygen.adx.services.security;

import com.ambrygen.adx.dto.security.PasswordResetRequestDTO;
import com.ambrygen.adx.errors.ForgotPasswordTokenExpiredException;
import com.ambrygen.adx.errors.ResourceNotFoundException;
import com.ambrygen.adx.models.security.PasswordResetToken;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.repositories.security.PasswordResetTokenRepository;
import com.ambrygen.adx.repositories.security.UserRepository;
import com.ambrygen.adx.services.EmailSenderService;
import com.ambrygen.adx.utils.RandomCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    @Value("${security.password.reset.token.timeout:60}")
    private int passwordResetTokenTimeout;

    @Value("${remote.service.tma.webapp.url:http://localhost:4200/}")
    private String tmaWebappUrl;

    @Value("${mail.fromEmailAddress:support@tradingmarketallerts.com}")
    private String fromEmailAddress;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailSenderService emailSenderService;


    public List<PasswordResetToken> getPasswordResetTokensForAUser(User user) {
        return passwordResetTokenRepository.findByUser(user);
    }

    public void sendForgotPasswordRestTokenEmail(String recipientEmailAddress, String token) {
        String url = tmaWebappUrl + "authentication/card/reset-password/" + token;
        String message = "<html><body><h1>Reset my password</h1>" +
                "<p>Enter following verification code in the mobile app to reset your password.</p>" +
                "<p><h2>" + token + "</h2></p>" +
                "</body></html>";

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(fromEmailAddress);
            messageHelper.setTo(recipientEmailAddress);
            messageHelper.setSubject("ADX: Reset your password");
            messageHelper.setText(message, true);
        };
        emailSenderService.sendEmail(messagePreparator);
    }

    @Transactional
    public void resetPassword(PasswordResetRequestDTO passwordResetRequestDTO) {
        PasswordResetToken passwordResetToken = getPasswordResetToken(passwordResetRequestDTO.getToken());
        Timestamp tokenPasswordExpiryDate = passwordResetToken.getExpiryDate();
        Calendar now = Calendar.getInstance();
        if (tokenPasswordExpiryDate.before(now.getTime())) {
            //Throw exception indicating token has expired
            throw new ForgotPasswordTokenExpiredException("Token is expired. Cannot reset password");
        }
        User user = passwordResetToken.getUser();
        user.setPassword(bCryptPasswordEncoder.encode(passwordResetRequestDTO.getPassword()));
        userRepository.save(user);
        //Now delete the password reset token
        passwordResetTokenRepository.deleteByResetToken(passwordResetRequestDTO.getToken());
    }

    public PasswordResetToken getPasswordResetToken(String forgotPasswordToken) {
        Optional<PasswordResetToken> forgotPasswordTokenOptional =
                passwordResetTokenRepository.findByResetToken(forgotPasswordToken);
        if (!forgotPasswordTokenOptional.isPresent()) {
            throw new ResourceNotFoundException(String.format("No such forgot password token found: %s", forgotPasswordToken));
        }
        return forgotPasswordTokenOptional.get();
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

    public String createPasswordResetToken(User user) {
        String resetToken = UUID.randomUUID().toString();
//        String shorterToken = getShorterToken(resetToken);
        String shorterToken = RandomCodeGenerator.generateRandomString(6);
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setResetToken(shorterToken);
        passwordResetToken.setUser(user);
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, passwordResetTokenTimeout);
        java.util.Date hourFromNow = now.getTime();
        passwordResetToken.setExpiryDate(new Timestamp(hourFromNow.getTime()));
        passwordResetTokenRepository.save(passwordResetToken);
        return shorterToken;
    }

    /**
     * Return first 15 characters from this String while removing "-" char
     *
     * @param resetToken
     * @return
     */
//    private String getShorterToken(String resetToken) {
//        String token = resetToken.replaceAll("-", "");
//        return token.substring(0, 6).toUpperCase();
//    }

    public void delete(String resetToken) {
        PasswordResetToken passwordResetToken = getPasswordResetToken(resetToken);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    public void delete(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.delete(passwordResetToken);
    }
}
