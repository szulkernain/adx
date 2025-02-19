package com.ambrygen.adx.dto.security;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String id;
    private String emailAddress;
    private String firstName;
    private String familyName;
    private boolean enabled;
    private boolean expired;
    private boolean credentialsExpired;
    private boolean locked;
    private List<String> roles;
}
