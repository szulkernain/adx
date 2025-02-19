package com.ambrygen.adx.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_service_logs")
@Data
public class VehicleServiceLog extends Auditable<String> {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_vehicle_id", nullable = false)
    @JsonIgnore
    private UserVehicle userVehicle;
    //See the comment for attribute purchaseDate in UserVehicle.java - to know why serviceDate is a String.
    @Column
    private String serviceDate;
    @Column
    private String serviceType;
    @Column
    private String serviceSummary;
    @Column
    private int odometerReading;
    @Column
    private float serviceCost;
    @Column
    private String serviceNotes;
    @Column
    private String serviceProvider;

    @Column
    private String serviceProviderCity;

    @Column
    private String serviceProviderProvince;
    @Column
    private String serviceProviderCountry;
    @Column
    private float gallons;
    @Transient
    private String make;
    @Transient
    private String model;
}
