package com.ambrygen.adx.services.security;


import com.ambrygen.adx.errors.TokenRefreshException;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.models.security.VerificationToken;
import com.ambrygen.adx.repositories.security.UserRepository;
import com.ambrygen.adx.repositories.security.VerificationTokenRepository;
import com.ambrygen.adx.services.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Optional;

@Service("verificationTokenService")
@RequiredArgsConstructor
public class VerificationTokenService {
    @Value("${remote.service.tma.webapp.url:http://localhost:4200/}")
    private String tmaWebappUrl;

    @Value("${mail.fromEmailAddress:support@tradingmarketallerts.com}")
    private String fromEmailAddress;

    private final EmailSenderService emailSenderService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    public User verfiyTokenAndEnableUser(VerificationToken verificationToken) throws TokenRefreshException {
//        Calendar cal = Calendar.getInstance();
//        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
//            throw new TokenRefreshException(String.format("Verification token %s is not valid anymore.", verificationToken.getToken()));
//        }
        User user = verificationToken.getUser();
        user.setEnabled(true);
        return userRepository.save(user);
    }

    //Deletes all verification token for the given user id
    @Transactional
    public void deleteAllByUser(User user) {
        verificationTokenRepository.deleteAllByUser(user);
    }

    public String createVerificationToken(User user) {
        VerificationToken verificationCode = new VerificationToken(user);
        String token =  verificationCode.getToken();
        verificationTokenRepository.save(verificationCode);
        return token;
    }

    public void sendVerificationCodeEmail(String emailAddress, String verificationCode) {
        String message = "<html><body>" +
                "<p>Your verification code is <b>" + verificationCode + "</b></p>" +
                "<p>Enter this verification code in the mobile app to activate your account.</p></body></html>";

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(fromEmailAddress);
            messageHelper.setTo(emailAddress);
            messageHelper.setSubject("ADX: Activate Your Account");
            messageHelper.setText(message, true);
        };
        emailSenderService.sendEmail(messagePreparator);
    }

    public String sendVerificationEmail(User user) {

        String token = createVerificationToken(user);

        String message = "<html><body><h1>Welcome to ADX!</h1><p>You can now start using ADX.</p>" +
                "<p>Your verification code is <b>" + token + "</b></p>" +
                "<p>ADX is still in beta development, so there will likely be bugs, and there " +
                "will definitely be lots of ways to improve it. We welcome any feedback at adx-support@ambrygenetics.com</p>" +
                "Enjoy!</br/>- ADX Team at Ambry Genetics</body></html>";

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(fromEmailAddress);
            messageHelper.setTo(user.getEmailAddress());
            messageHelper.setSubject("ADX: Verify your email address");
            messageHelper.setText(message, true);
        };


        emailSenderService.sendEmail(messagePreparator);

        return token;
    }
}
