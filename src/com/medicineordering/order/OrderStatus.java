package com.medicineordering.order;

public enum OrderStatus {
    PENDING("Order is pending"),
    PROCESSING("Order is being processed"),
    SHIPPED("Order has been shipped"),
    DELIVERED("Order has been delivered"),
    CANCELLED("Order has been cancelled"),
    PAYMENT_FAILED("Payment failed for order"),
    ERROR("Error processing order");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 