package com.ambrygen.adx.dto.security;

import com.ambrygen.adx.dto.ADXResponseDTO;

public class SignupResponseDTO extends ADXResponseDTO {
    public SignupResponseDTO(boolean error, String errorMessage, String response) {
        super(error,errorMessage,response);
    }
}
