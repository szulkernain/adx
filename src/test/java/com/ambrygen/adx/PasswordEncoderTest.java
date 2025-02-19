package com.ambrygen.adx;

import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    @Test
    public  void testPasswordEncoding() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("Hello"));

        Assert.isTrue(passwordEncoder.matches("Hello", "$2a$10$ggTxZLMZuEHCDuvkEVH9lelpU.GHDE.HiA49sHblTh1fk7HgxV7qu"));
    }
}
