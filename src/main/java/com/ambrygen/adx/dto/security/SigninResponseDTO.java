package com.ambrygen.adx.dto.security;

import com.ambrygen.adx.dto.ADXResponseDTO;

public class SigninResponseDTO extends ADXResponseDTO {
    public SigninResponseDTO(boolean error, String errorMessage, JwtResponse response) {
        super(error,errorMessage,response);
    }
}
