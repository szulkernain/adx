package com.ambrygen.adx.dto;


import lombok.Data;

@Data
public class ADXResponseDTO<T> {
    protected boolean error;
    protected String reason;
    protected T response;

    public ADXResponseDTO(boolean error, String reason, T response) {
        this.error = error;
        this.reason = reason;
        this.response  = response;
    }
}
