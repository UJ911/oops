package com.medicineordering;

import com.medicineordering.user.*;
import com.medicineordering.inventory.*;
import com.medicineordering.order.*;
import com.medicineordering.prescription.*;
import com.medicineordering.payment.*;
import com.medicineordering.notification.*;
import com.medicineordering.security.*;
import com.medicineordering.health.*;
import com.medicineordering.exception.*; // Import all exceptions

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap; // For notification params
import java.util.Map; // For notification params
import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        System.out.println("--- Online Medicine Ordering System Simulation ---");

        // --- Initialization ---
        System.out.println("\n--- Initializing System Components ---");
        // Define HealthRecordManager (assuming HealthRecord class exists)
        HealthRecordManager healthManager = new HealthRecordManager(); 
        SecurityManager securityManager = new SecurityManager();
        NotificationService notificationService = new NotificationService();
        List<String> supportedPayMethods = new ArrayList<>();
        supportedPayMethods.add("CreditCard");
        supportedPayMethods.add("DebitCard");
        PaymentGateway paymentGateway = new PaymentGateway("GW1", supportedPayMethods);

        // --- User Management ---
        System.out.println("\n--- User Registration & Login ---");
        Customer customer = new Customer("CUST001", "Alice Wonderland", "alice@example.com", "password123", "123-456-7890");
        customer.register();
        boolean customerLogin = customer.login("alice@example.com", "password123");
        System.out.println("Customer Login Successful: " + customerLogin);
        if (customerLogin) {
             securityManager.logActivity(customer.getUserId(), "CUSTOMER_LOGIN_SUCCESS");
             // Send welcome notification
             Map<String, String> params = new HashMap<>();
             params.put("name", customer.getName());
             notificationService.sendNotificationFromTemplate(customer.getUserId(), "WELCOME_EMAIL", params);
        } else {
             securityManager.logActivity(customer.getEmail(), "CUSTOMER_LOGIN_FAILED");
             System.out.println("Customer login failed!");
             return; // Stop simulation if login fails
        }
       
        Doctor doctor = new Doctor("DOC001", "Bob The Healer", "bob@clinic.com", "docpass456", "987-654-3210", "LIC987", "General Medicine", "Mon-Fri 9am-5pm");
        doctor.register();
        boolean doctorLogin = doctor.login("bob@clinic.com", "docpass456");
        System.out.println("Doctor Login Successful: " + doctorLogin);
         if (doctorLogin) {
            securityManager.logActivity(doctor.getUserId(), "DOCTOR_LOGIN_SUCCESS");
        } else {
             securityManager.logActivity(doctor.getEmail(), "DOCTOR_LOGIN_FAILED");
             System.out.println("Doctor login failed!");
             // Continue simulation even if doctor login fails?
        }

        // --- Inventory ---
        System.out.println("\n--- Medicine Inventory ---");
        Medicine med1 = new Medicine("MED01", "Paracetamol", "Pain reliever", new BigDecimal("5.00"), false, 100);
        Medicine med2 = new Medicine("MED02", "Amoxicillin", "Antibiotic", new BigDecimal("15.50"), true, 50);
        System.out.println("Medicine Available: " + med1.getDetails());
        System.out.println("Medicine Available: " + med2.getDetails());

        // --- Prescription Flow ---
        System.out.println("\n--- Prescription Workflow ---");
        List<Medicine> prescribedMeds = new ArrayList<>();
        prescribedMeds.add(med2);
        Prescription prescription = new Prescription("PRES001", customer.getUserId(), doctor.getUserId(), prescribedMeds, new Date());
        try {
            doctor.issuePrescription(prescription);
            securityManager.logActivity(doctor.getUserId(), "ISSUED_PRESCRIPTION_" + prescription.getPrescriptionId());
            // Add to health record history
             healthManager.addPrescriptionToHistory(customer.getUserId(), prescription);
        } catch (PharmacyException e) {
             System.err.println("Error issuing prescription: " + e.getMessage());
             securityManager.logActivity(doctor.getUserId(), "ISSUE_PRESCRIPTION_FAILED");
        }

        customer.uploadPrescription(prescription);
        securityManager.logActivity(customer.getUserId(), "UPLOADED_PRESCRIPTION_" + prescription.getPrescriptionId());

        boolean verified = doctor.verifyPrescription(prescription);
        System.out.println("Prescription Verified: " + verified);
        if (verified) {
            securityManager.logActivity(doctor.getUserId(), "VERIFIED_PRESCRIPTION_" + prescription.getPrescriptionId());
        } else {
             securityManager.logActivity(doctor.getUserId(), "VERIFY_PRESCRIPTION_FAILED_" + prescription.getPrescriptionId());
        }

        // --- Ordering Flow ---
        System.out.println("\n--- Order Workflow ---");
        Order order = null; // Initialize order
        if (prescription.isVerified()) {
            order = new Order(customer); // Create order for the customer
            System.out.println("Order created: " + order.getOrderId() + " for Customer: " + customer.getName());
            securityManager.logActivity(customer.getUserId(), "ORDER_CREATED_" + order.getOrderId());

            try {
                // Add item requiring prescription
                 order.addItem(med2, 1); 
                // Add item not requiring prescription
                 order.addItem(med1, 2);
            } catch (MedicineOutOfStockException e) {
                System.err.println("Order Failed: " + e.getMessage());
                securityManager.logActivity(customer.getUserId(), "ORDER_ADD_ITEM_FAILED_OOS_" + order.getOrderId());
                order.setStatus(OrderStatus.CANCELLED); // Cancel order if item out of stock
            } catch (Exception e) { // Catch other potential errors during addItem
                 System.err.println("Error adding item to order: " + e.getMessage());
                 securityManager.logActivity(customer.getUserId(), "ORDER_ADD_ITEM_ERROR_" + order.getOrderId());
                  order.setStatus(OrderStatus.ERROR); 
            }
            
            // Only proceed if order is still pending (not cancelled/error)
            if (order.getStatus() == OrderStatus.PENDING) { 
                System.out.println("Order Total: " + order.getTotalAmount());
                customer.placeOrder(order);
                securityManager.logActivity(customer.getUserId(), "ORDER_PLACED_" + order.getOrderId());

                // --- Payment Flow ---
                System.out.println("\n--- Payment Workflow ---");
                try {
                    // Use PaymentGateway processPayment with BigDecimal
                    paymentGateway.processPayment(order.getOrderId(), order.getTotalAmount(), "CreditCard");
                    // If processPayment succeeds without exception:
                    order.setPaymentStatus(PaymentStatus.PAID);
                    order.setStatus(OrderStatus.PROCESSING);
                    System.out.println("Payment Successful!");
                    securityManager.logActivity(customer.getUserId(), "PAYMENT_SUCCESS_" + order.getOrderId());
                    
                    // Send notification using template
                    Map<String, String> params = new HashMap<>();
                    params.put("orderId", order.getOrderId());
                    params.put("amount", String.format("%.2f", order.getTotalAmount()));
                    notificationService.sendNotificationFromTemplate(customer.getUserId(), "ORDER_CONFIRM_SMS", params);

                } catch (PaymentFailedException e) {
                    System.err.println("Payment Failed: " + e.getMessage());
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    order.setStatus(OrderStatus.PAYMENT_FAILED); // Specific status
                    securityManager.logActivity(customer.getUserId(), "PAYMENT_FAILED_" + order.getOrderId());
                    notificationService.sendNotification(customer.getUserId(), "Payment failed for order " + order.getOrderId() + ". Please contact support.", NotificationType.EMAIL);
                } catch (PharmacyException e) { // Catch other pharmacy/validation errors
                    System.err.println("Payment Error: " + e.getMessage());
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    order.setStatus(OrderStatus.ERROR);
                    securityManager.logActivity(customer.getUserId(), "PAYMENT_ERROR_" + order.getOrderId());
                }
            }
        } else {
            System.out.println("Cannot start order, prescription " + prescription.getPrescriptionId() + " is not verified.");
            securityManager.logActivity(customer.getUserId(), "ORDER_FAILED_PRESCRIPTION_UNVERIFIED");
        }

        // --- Post-Order Status ---
        if (order != null) {
             System.out.println("\n--- Final Order Status ---");
             System.out.println(order.generateInvoice());
        } else {
             System.out.println("\n--- No Order Processed ---");
        }

        // --- System Shutdown/Cleanup (Conceptual) ---
        System.out.println("\n--- Simulation End ---");
        System.out.println("Final Stock MED01: " + med1.getStock());
        System.out.println("Final Stock MED02: " + med2.getStock());
        System.out.println("Audit Log Entries: " + securityManager.getAuditLog().size());
        // Example: Process notification queue (in real app, this runs async)
        // notificationService.processNotificationQueue(); 
    }
} 