package com.medicineordering.exception;

public class PaymentFailedException extends PharmacyException {
    public PaymentFailedException(String message) {
        super(message);
    }

    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
} 