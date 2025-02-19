package com.ambrygen.adx.models;

public interface ExpenseByServiceTypeAndServiceDate {
    String getId();
    Float getGasCost();
    Float getOilChangeCost();
    Float getBrakesCost();
    Float getTiresCost();
    Float getOtherCost();
    Float getLightsCost();
    Float getModsCost();
    String getServiceDate();
}
