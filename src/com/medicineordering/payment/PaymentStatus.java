package com.medicineordering.payment;

public enum PaymentStatus {
    PENDING("Payment is pending"),
    PROCESSING("Payment is being processed"),
    PAID("Payment completed successfully"),
    FAILED("Payment failed"),
    REFUNDED("Payment has been refunded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 