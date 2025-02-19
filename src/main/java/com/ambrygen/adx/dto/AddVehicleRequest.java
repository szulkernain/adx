package com.ambrygen.adx.dto;

import lombok.Data;

@Data
public class AddVehicleRequest {
    private String vehicleId;
    private String userId;
    private String vin;
    private String year;
    private String make;
    private String model;
    private String bodyClass;
    private String plantCity;
    private String plantCountry;
    private String manufacturer;
    private String trim;
    private String series;
    private String vehicleType;
    private String engineConfiguration;
    private String fuelTypePrimary;
    private String displacementCC;
    private String displacementLiter;
    private String engineCylinders;
    private String engineHP;
    private String licensePlate;
    private String purchaseDate;
    private String odometerReading;
    private String purchasePrice;
}
