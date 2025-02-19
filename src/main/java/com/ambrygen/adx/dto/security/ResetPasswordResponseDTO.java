package com.ambrygen.adx.dto.security;

import com.ambrygen.adx.dto.ADXResponseDTO;

public class ResetPasswordResponseDTO extends ADXResponseDTO {
    public ResetPasswordResponseDTO(boolean error, String errorMessage, String response) {
        super(error,errorMessage,response);
    }
}
