package com.ambrygen.adx.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle extends Auditable<String> {
    @Column
    private String vin;
    @Column
    private String make;
    @Column
    private String model;
    @Column
    private int year;
    @Column
    private String bodyClass;
    @Column
    private String manufacturer;
    @Column
    private String plantCity;
    @Column
    private String plantCountry;
    @Column
    private String trim;
    @Column
    private String series;
    @Column
    private String vehicleType;
    @Column
    private String engineConfiguration;
    @Column
    private String fuelTypePrimary;
    @Column
    private float displacementCc;
    @Column
    private float displacementLiter;
    @Column
    private int engineCylinders;
    @Column
    private float engineHp;
    @Transient
    private String vehicleName;
}
