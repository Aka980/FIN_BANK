package com.example.demo.entity;

public enum LoanType {

    CAR_LOAN(9.0),
    BIKE_LOAN(11.5),
    COMMERCIAL_VEHICLE_LOAN(10.5),
    USED_VEHICLE_LOAN(13.0);

    private final double roi;

    LoanType(double roi) {
        this.roi = roi;
    }

    public double getRoi() {
        return roi;
    }
}
