package com.ambrygen.adx.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserVehicleData {
    private String licensePlate;
    private Integer odometerReading;
    private String purchaseDate;
    private Float purchasePrice;
    private String imageFileName;
    private String userVehicleId;
}
