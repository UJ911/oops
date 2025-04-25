package com.medicineordering.exception;

public class PharmacyException extends Exception {
    public PharmacyException(String message) {
        super(message);
    }
    
    public PharmacyException(String message, Throwable cause) {
        super(message, cause);
    }
}

class PrescriptionVerificationException extends PharmacyException {
    public PrescriptionVerificationException(String message) {
        super(message);
    }
    
    public PrescriptionVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}

class PaymentFailedException extends PharmacyException {
    public PaymentFailedException(String message) {
        super(message);
    }
    
    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

class InvalidUserException extends PharmacyException {
    public InvalidUserException(String message) {
        super(message);
    }
    
    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

class MedicineOutOfStockException extends PharmacyException {
    public MedicineOutOfStockException(String message) {
        super(message);
    }
    
    public MedicineOutOfStockException(String message, Throwable cause) {
        super(message, cause);
    }
} 