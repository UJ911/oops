package com.medicineordering.payment;

import java.math.BigDecimal; // Use BigDecimal for currency
import com.medicineordering.exception.PaymentFailedException;
import com.medicineordering.exception.PharmacyException;

public interface PaymentProcessor {

    // Nested Interface from plan
    interface TransactionValidator {
        boolean validate(Object transactionDetails); // Placeholder for transaction details type
    }

    // Methods from plan - Use BigDecimal for amount
    boolean processPayment(String orderId, BigDecimal amount, String paymentDetails) throws PaymentFailedException, PharmacyException;
    
    boolean verifyTransaction(String transactionId) throws PharmacyException;
    
    String generateReceipt(String transactionId) throws PharmacyException;

    // Potentially add the validator as a method parameter or have implementers manage it
    // void setTransactionValidator(TransactionValidator validator);
} 