package com.ambrygen.adx.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VINDecodeRequest {
    private String vin;
    private int year;
}
