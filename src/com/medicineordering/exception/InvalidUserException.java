package com.medicineordering.exception;

// Base exception already exists (PharmacyException.java)

// Specific exception classes
public class InvalidUserException extends PharmacyException {
    public InvalidUserException(String message) {
        super(message);
    }
} 