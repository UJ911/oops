package com.medicineordering.payment;

import com.medicineordering.exception.PaymentFailedException;
import com.medicineordering.exception.PharmacyException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.UUID; // For better transaction IDs

// Placeholder for TransactionLog entry type
class TransactionLogEntry {
    String transactionId;
    String orderId;
    BigDecimal amount;
    String paymentMethod; // Added
    String status; // e.g., PENDING, SUCCESS, FAILED
    Date timestamp;
    // ... other details
}

public class PaymentGateway implements PaymentProcessor {
    // Properties from plan
    private String gatewayId;
    private List<String> supportedMethods; // e.g., ["CreditCard", "DebitCard", "NetBanking"]
    private Map<String, TransactionLogEntry> transactionLog;
    private TransactionValidator transactionValidator; // Optional validator from nested interface
    
    // Constructor
    public PaymentGateway(String gatewayId, List<String> supportedMethods) {
        this.gatewayId = gatewayId;
        this.supportedMethods = (supportedMethods != null) ? new ArrayList<>(supportedMethods) : new ArrayList<>();
        this.transactionLog = new HashMap<>();
        // Initialize a default validator or allow setting it via setter/constructor
        this.transactionValidator = new DefaultTransactionValidator();
    }
    
    // Default validator implementation
    private static class DefaultTransactionValidator implements TransactionValidator {
        @Override
        public boolean validate(Object transactionDetails) {
            // Example: Check if details are a non-empty String
            boolean valid = transactionDetails instanceof String && !((String)transactionDetails).trim().isEmpty();
            System.out.println("Validating transaction details... Result: " + valid);
            return valid;
        }
    }
    
    // Implementing PaymentProcessor methods
    @Override
    public boolean processPayment(String orderId, BigDecimal amount, String paymentMethod) throws PaymentFailedException, PharmacyException {
        System.out.println("Processing payment of " + amount + " for Order " + orderId + " via Gateway " + gatewayId + " using method: " + paymentMethod);

        if (orderId == null || amount == null || paymentMethod == null) {
            throw new PharmacyException("Order ID, amount, and payment method cannot be null");
        }
        if (!supportedMethods.contains(paymentMethod)) {
            throw new PharmacyException("Unsupported payment method: " + paymentMethod + " by Gateway " + gatewayId);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PharmacyException("Payment amount must be positive");
        }
        if (!transactionValidator.validate(paymentMethod)) { // Basic validation example
             throw new PharmacyException("Invalid payment details/method provided.");
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString();
        logTransaction(transactionId, orderId, amount, paymentMethod, "PENDING");

        // Simulate external gateway call
        boolean success = simulatePaymentProcessing();

        if (success) {
             logTransaction(transactionId, orderId, amount, paymentMethod, "SUCCESS");
             System.out.println("Payment Successful (TXN: " + transactionId + ")");
             return true;
        } else {
             logTransaction(transactionId, orderId, amount, paymentMethod, "FAILED");
             System.out.println("Payment Failed (TXN: " + transactionId + ")");
             throw new PaymentFailedException("Payment gateway failed to process transaction " + transactionId);
        }
    }
    
    @Override
    public boolean verifyTransaction(String transactionId) throws PharmacyException {
        System.out.println("Verifying transaction " + transactionId + " via Gateway " + gatewayId);
        if (transactionId == null) {
            throw new PharmacyException("Transaction ID cannot be null");
        }
        TransactionLogEntry entry = transactionLog.get(transactionId);
        if (entry == null) {
             throw new PharmacyException("Transaction not found: " + transactionId);
        }
        // Consider re-checking with gateway API in real system
        return "SUCCESS".equals(entry.status);
    }
    
    @Override
    public String generateReceipt(String transactionId) throws PharmacyException {
        System.out.println("Generating receipt for transaction " + transactionId + " via Gateway " + gatewayId);
         if (transactionId == null) {
            throw new PharmacyException("Transaction ID cannot be null");
        }
        TransactionLogEntry entry = transactionLog.get(transactionId);
        if (entry == null) {
             throw new PharmacyException("Transaction not found: " + transactionId);
        }
        if (!"SUCCESS".equals(entry.status)) {
            return "Receipt generation failed: Transaction " + transactionId + " did not succeed.";
        }

        return String.format(
                "--- Payment Receipt ---\n" +
                "Transaction ID: %s\n" +
                "Order ID: %s\n" +
                "Amount: %.2f\n" +
                "Method: %s\n" +
                "Status: %s\n" +
                "Date: %s\n" +
                "Gateway: %s",
                entry.transactionId,
                entry.orderId,
                entry.amount,
                entry.paymentMethod,
                entry.status,
                entry.timestamp,
                gatewayId);
    }
    
    // Helper method for logging (updates existing entry if status changes)
    private void logTransaction(String transactionId, String orderId, BigDecimal amount, String paymentMethod, String status) {
        TransactionLogEntry entry = transactionLog.getOrDefault(transactionId, new TransactionLogEntry());
        entry.transactionId = transactionId;
        entry.orderId = orderId;
        entry.amount = amount;
        entry.paymentMethod = paymentMethod;
        entry.status = status;
        entry.timestamp = new Date(); // Update timestamp on status change
        transactionLog.put(transactionId, entry);
        System.out.println("Logged transaction: " + transactionId + " Status: " + status);
    }
    
    private boolean simulatePaymentProcessing() {
        // Simulate a 90% success rate
        return Math.random() > 0.1;
    }
    
    // Getters and potentially setters
    public String getGatewayId() { return gatewayId; }
    public List<String> getSupportedMethods() { return new ArrayList<>(supportedMethods); }
    public Map<String, TransactionLogEntry> getTransactionLog() { return new HashMap<>(transactionLog); } // Return a copy
    public void setTransactionValidator(TransactionValidator validator) { this.transactionValidator = validator; }
} 