package com.ambrygen.adx.dto.security;

import lombok.Data;

@Data
public class PasswordResetRequestDTO {
    private String token;
    private String password;
}
