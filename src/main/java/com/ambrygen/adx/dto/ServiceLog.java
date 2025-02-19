package com.ambrygen.adx.dto;

public interface ServiceLog {
    String getId();

    String getUserVehicleId();

    String getServiceDate();

    String getServiceType();

    String getServiceSummary();

    int getOdometerReading();

    float getServiceCost();

    String getServiceNotes();

    String getServiceProvider();

    String getServiceProviderCity();

    String getServiceProviderProvince();

    String getServiceProviderCountry();

    float getGallons();

    String getMake();

    String getModel();
}
