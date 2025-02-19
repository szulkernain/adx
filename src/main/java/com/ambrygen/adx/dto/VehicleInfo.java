package com.ambrygen.adx.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class VehicleInfo {
    private String id;
    private String userVehicleId;
    private String vin;
    private int year;
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
    private float displacementCc;
    private float displacementLiter;
    private int engineCylinders;
    private float engineHp;
    private String fuelTypePrimary;
    private String licensePlate;
    private Integer odometerReading;
    private Float purchasePrice;
    private String purchaseDate;
    private String imageFileName;
    private String vehicleName;
}
