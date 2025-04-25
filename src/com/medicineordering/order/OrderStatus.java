package com.medicineordering.order;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    PAYMENT_FAILED, // Added status for payment failure
    ERROR // General error status
} 