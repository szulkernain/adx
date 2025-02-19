package com.ambrygen.adx.dto;

import lombok.Data;

@Data
public class UpdateVehicleRequest {
    private String id;
    private String licensePlate;
    private String purchaseDate;
    private String odometerReading;
    private String purchasePrice;
}
