package com.ambrygen.adx.dto.security;

import com.ambrygen.adx.dto.ADXResponseDTO;

public class ActivateAccountResponseDTO extends ADXResponseDTO {
    public ActivateAccountResponseDTO(boolean error, String errorMessage, String response) {
        super(error,errorMessage,response);
    }
}
