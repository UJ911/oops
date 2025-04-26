package com.medicineordering.prescription;

public enum PrescriptionStatus {
    PENDING("Prescription is pending verification"),
    VERIFIED("Prescription has been verified"),
    EXPIRED("Prescription has expired"),
    USED("Prescription has been used"),
    CANCELLED("Prescription has been cancelled");

    private final String description;

    PrescriptionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 