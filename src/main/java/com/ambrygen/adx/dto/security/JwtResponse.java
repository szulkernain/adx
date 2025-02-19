package com.ambrygen.adx.dto.security;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private String emailAddress;
    private String firstName;
    private String familyName;
    private String lastName;
    private String refreshToken;
    private List<String> roles;

    public JwtResponse(String accessToken, String refreshToken, String id, String email,
                       String firstName, String lastName, List<String> roles) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.emailAddress = email;
        this.roles = roles;
        this.firstName = firstName;
        this.familyName = lastName;
        this.lastName = lastName;
    }
}
