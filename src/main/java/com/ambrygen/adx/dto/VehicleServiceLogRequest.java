package com.ambrygen.adx.dto;

import lombok.Data;

@Data
public class VehicleServiceLogRequest {
    private String id;
    private String serviceDate;
    private String odometerReading;
    private String serviceCost;
    private String serviceType;
    private String gallons;
    private String serviceProvider;
    private String serviceSummary;
    private String serviceNotes;
}
