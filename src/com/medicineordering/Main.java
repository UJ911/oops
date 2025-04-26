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
import java.util.Scanner;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserManager userManager = UserManager.getInstance();
    private static final SecurityManager securityManager = new SecurityManager();
    private static final NotificationService notificationService = new NotificationService();
    private static final HealthRecordManager healthManager = new HealthRecordManager();
    private static final PaymentGateway paymentGateway;
    private static final OrderManager orderManager = OrderManager.getInstance();
    private static final PrescriptionManager prescriptionManager = PrescriptionManager.getInstance();
    
    static {
        List<String> supportedPayMethods = new ArrayList<>();
        supportedPayMethods.add("CreditCard");
        supportedPayMethods.add("DebitCard");
        paymentGateway = new PaymentGateway("GW1", supportedPayMethods);
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Online Medicine Ordering System");
        
        while (true) {
            try {
                showMainMenu();
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        registerUser();
                        break;
                    case 2:
                        loginUser();
                        break;
                    case 3:
                        System.out.println("Thank you for using our system. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
    
    private static void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private static void registerUser() {
        System.out.println("\n=== Registration ===");
        System.out.println("1. Register as Customer");
        System.out.println("2. Register as Doctor");
        System.out.print("Enter your choice: ");
        
        int choice = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        System.out.print("Enter contact info: ");
        String contactInfo = scanner.nextLine();
        
        String userId = UUID.randomUUID().toString();
        
        try {
            switch (choice) {
                case 1:
                    Customer customer = new Customer(userId, name, email, password, contactInfo);
                    userManager.addCustomers(customer);
                    System.out.println("Customer registered successfully!");
                    break;
                    
                case 2:
                    System.out.print("Enter license number: ");
                    String licenseNumber = scanner.nextLine();
                    
                    System.out.print("Enter specialization: ");
                    String specialization = scanner.nextLine();
                    
                    System.out.print("Enter availability (e.g., Mon-Fri 9am-5pm): ");
                    String availability = scanner.nextLine();
                    
                    Doctor doctor = new Doctor(userId, name, email, password, contactInfo, licenseNumber, specialization, availability);
                    userManager.addDoctors(doctor);
                    System.out.println("Doctor registered successfully!");
                    break;
                    
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
    
    private static void loginUser() {
        System.out.println("\n=== Login ===");
        System.out.println("1. Login as Customer");
        System.out.println("2. Login as Doctor");
        System.out.print("Enter your choice: ");
        
        int choice = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        try {
            switch (choice) {
                case 1:
                    loginCustomer(email, password);
                    break;
                case 2:
                    loginDoctor(email, password);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }
    
    private static void loginCustomer(String email, String password) {
        userManager.findCustomerByEmail(email).ifPresentOrElse(
            customer -> {
                if (customer.login(email, password)) {
                    System.out.println("Welcome, " + customer.getName() + "!");
                    securityManager.logActivity(customer.getUserId(), "CUSTOMER_LOGIN_SUCCESS");
                    showCustomerMenu(customer);
                } else {
                    System.out.println("Invalid credentials.");
                    securityManager.logActivity(email, "CUSTOMER_LOGIN_FAILED");
                }
            },
            () -> System.out.println("Customer not found.")
        );
    }
    
    private static void loginDoctor(String email, String password) {
        userManager.findDoctorByEmail(email).ifPresentOrElse(
            doctor -> {
                if (doctor.login(email, password)) {
                    System.out.println("Welcome, Dr. " + doctor.getName() + "!");
                    securityManager.logActivity(doctor.getUserId(), "DOCTOR_LOGIN_SUCCESS");
                    showDoctorMenu(doctor);
                } else {
                    System.out.println("Invalid credentials.");
                    securityManager.logActivity(email, "DOCTOR_LOGIN_FAILED");
                }
            },
            () -> System.out.println("Doctor not found.")
        );
    }
    
    private static void showCustomerMenu(Customer customer) {
        while (true) {
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1. View Prescriptions");
            System.out.println("2. View Orders");
            System.out.println("3. Place New Order");
            System.out.println("4. Upload Prescription");
            System.out.println("5. Track Order");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        // Show prescriptions
                        List<Prescription> prescriptions = customer.getPrescriptions();
                        if (prescriptions.isEmpty()) {
                            System.out.println("No prescriptions found.");
                        } else {
                            prescriptions.forEach(p -> System.out.println(p.getPrescriptionDetails()));
                        }
                        break;
                        
                    case 2:
                        // Show orders
                        List<Order> orders = customer.getOrders();
                        if (orders.isEmpty()) {
                            System.out.println("No orders found.");
                        } else {
                            orders.forEach(o -> System.out.println(o.generateInvoice()));
                        }
                        break;
                        
                    case 3:
                        // Place new order logic
                        placeNewOrder(customer);
                        break;
                        
                    case 4:
                        // Upload prescription logic
                        uploadPrescription(customer);
                        break;
                        
                    case 5:
                        // Track order logic
                        trackOrder(customer);
                        break;
                        
                    case 6:
                        System.out.println("Logging out...");
                        return;
                        
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
    
    private static void showDoctorMenu(Doctor doctor) {
        while (true) {
            System.out.println("\n=== Doctor Menu ===");
            System.out.println("1. View Issued Prescriptions");
            System.out.println("2. Issue New Prescription");
            System.out.println("3. Verify Prescription");
            System.out.println("4. Update Availability");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        // Show issued prescriptions
                        List<Prescription> prescriptions = doctor.getIssuedPrescriptions();
                        if (prescriptions.isEmpty()) {
                            System.out.println("No prescriptions issued.");
                        } else {
                            prescriptions.forEach(p -> System.out.println(p.getPrescriptionDetails()));
                        }
                        break;
                        
                    case 2:
                        // Issue new prescription logic
                        issueNewPrescription(doctor);
                        break;
                        
                    case 3:
                        // Verify prescription logic
                        verifyPrescription(doctor);
                        break;
                        
                    case 4:
                        // Update availability
                        System.out.print("Enter new availability (e.g., Mon-Fri 9am-5pm): ");
                        String availability = scanner.nextLine();
                        doctor.setAvailability(availability);
                        System.out.println("Availability updated successfully!");
                        break;
                        
                    case 5:
                        System.out.println("Logging out...");
                        return;
                        
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
    
    private static void placeNewOrder(Customer customer) {
        try {
            // Create new order
            Order order = new Order(customer);
            System.out.println("\n=== Place New Order ===");
            
            while (true) {
                System.out.println("\n1. Add Medicine to Order");
                System.out.println("2. View Current Order");
                System.out.println("3. Proceed to Payment");
                System.out.println("4. Cancel Order");
                System.out.print("Enter your choice: ");
                
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        // Add medicine to order
                        System.out.print("Enter medicine ID: ");
                        String medicineId = scanner.nextLine();
                        System.out.print("Enter quantity: ");
                        int quantity = Integer.parseInt(scanner.nextLine());
                        
                        try {
                            Medicine medicine = findMedicineById(medicineId);
                            if (medicine != null) {
                                if (medicine.isRequiresPrescription()) {
                                    // Check if customer has valid prescription for this medicine
                                    if (!hasValidPrescription(customer, medicine)) {
                                        System.out.println("Error: This medicine requires a valid prescription.");
                                        continue;
                                    }
                                }
                                order.addItem(medicine, quantity);
                                System.out.println("Medicine added to order successfully!");
                            } else {
                                System.out.println("Medicine not found.");
                            }
                        } catch (MedicineOutOfStockException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                        
                    case 2:
                        // View current order
                        System.out.println(order.generateInvoice());
                        break;
                        
                    case 3:
                        // Proceed to payment
                        if (order.getItems().isEmpty()) {
                            System.out.println("Cannot proceed with empty order.");
                            break;
                        }
                        
                        if (processPayment(order)) {
                            customer.placeOrder(order);
                            securityManager.logActivity(customer.getUserId(), "ORDER_PLACED_" + order.getOrderId());
                            System.out.println("Order placed successfully!");
                            return;
                        }
                        break;
                        
                    case 4:
                        // Cancel order
                        System.out.println("Order cancelled.");
                        return;
                        
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error placing order: " + e.getMessage());
        }
    }
    
    private static Medicine findMedicineById(String medicineId) {
        // TODO: Implement medicine lookup logic
        // This should connect to your medicine inventory system
        return null;
    }
    
    private static boolean hasValidPrescription(Customer customer, Medicine medicine) {
        List<Prescription> prescriptions = customer.getPrescriptions();
        return prescriptions.stream()
            .filter(p -> p.isVerified())
            .flatMap(p -> p.getMedicines().stream())
            .anyMatch(m -> m.getMedicineId().equals(medicine.getMedicineId()));
    }
    
    private static boolean processPayment(Order order) {
        System.out.println("\n=== Payment Processing ===");
        System.out.println("Total Amount: " + order.getTotalAmount());
        System.out.println("\nSelect Payment Method:");
        System.out.println("1. Credit Card");
        System.out.println("2. Debit Card");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            String paymentMethod = (choice == 1) ? "CreditCard" : "DebitCard";
            
            paymentGateway.processPayment(order.getOrderId(), order.getTotalAmount(), paymentMethod);
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setStatus(OrderStatus.PROCESSING);
            
            // Send confirmation notification
            Map<String, String> params = new HashMap<>();
            params.put("orderId", order.getOrderId());
            params.put("amount", String.format("%.2f", order.getTotalAmount()));
            notificationService.sendNotificationFromTemplate(order.getCustomer().getUserId(), "ORDER_CONFIRM_SMS", params);
            
            return true;
        } catch (PaymentFailedException e) {
            System.out.println("Payment failed: " + e.getMessage());
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            return false;
        } catch (Exception e) {
            System.out.println("Error processing payment: " + e.getMessage());
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.ERROR);
            return false;
        }
    }

    private static void uploadPrescription(Customer customer) {
        System.out.println("\n=== Upload Prescription ===");
        System.out.println("1. Upload Digital Prescription");
        System.out.println("2. Request New Prescription");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            
            switch (choice) {
                case 1:
                    System.out.print("Enter prescription ID: ");
                    String prescriptionId = scanner.nextLine();
                    System.out.print("Enter doctor's email: ");
                    String doctorEmail = scanner.nextLine();
                    
                    prescriptionManager.uploadPrescription(customer, prescriptionId, doctorEmail);
                    System.out.println("Prescription uploaded successfully! Awaiting verification.");
                    break;
                    
                case 2:
                    System.out.println("Available Doctors:");
                    prescriptionManager.getAvailableDoctors().forEach(d -> 
                        System.out.println(String.format("- Dr. %s (%s) - %s", 
                            d.getName(), d.getSpecialization(), d.getAvailability())));
                    
                    System.out.print("\nEnter doctor's email to request prescription: ");
                    String email = scanner.nextLine();
                    
                    prescriptionManager.requestPrescription(customer, email);
                    System.out.println("Prescription request sent successfully!");
                    break;
                    
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("Error uploading prescription: " + e.getMessage());
        }
    }

    private static void trackOrder(Customer customer) {
        System.out.println("\n=== Track Order ===");
        List<Order> orders = orderManager.getCustomerOrders(customer);
        
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        
        System.out.println("Your Orders:");
        orders.forEach(order -> System.out.println(orderManager.formatOrderSummary(order)));
        
        System.out.print("\nEnter Order ID to track (or press Enter to go back): ");
        String orderId = scanner.nextLine();
        
        if (!orderId.trim().isEmpty()) {
            try {
                orderManager.trackOrder(orderId, customer);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void issueNewPrescription(Doctor doctor) {
        System.out.println("\n=== Issue New Prescription ===");
        System.out.print("Enter patient's email: ");
        String patientEmail = scanner.nextLine();
        
        try {
            Prescription prescription = prescriptionManager.issueNewPrescription(doctor, patientEmail);
            
            while (true) {
                System.out.println("\n1. Add Medicine");
                System.out.println("2. Add Notes");
                System.out.println("3. Review and Finish");
                System.out.println("4. Cancel");
                System.out.print("Enter your choice: ");
                
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        System.out.print("Enter medicine ID: ");
                        String medicineId = scanner.nextLine();
                        Medicine medicine = findMedicineById(medicineId);
                        
                        if (medicine != null) {
                            System.out.print("Enter quantity: ");
                            int quantity = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter dosage (e.g., '1 tablet twice daily'): ");
                            String dosage = scanner.nextLine();
                            System.out.print("Enter special instructions: ");
                            String instructions = scanner.nextLine();
                            
                            prescription.addMedicine(medicine, quantity, dosage, instructions);
                            System.out.println("Medicine added to prescription.");
                        } else {
                            System.out.println("Medicine not found.");
                        }
                        break;
                        
                    case 2:
                        System.out.print("Enter prescription notes: ");
                        String notes = scanner.nextLine();
                        prescription.setNotes(notes);
                        System.out.println("Notes added to prescription.");
                        break;
                        
                    case 3:
                        System.out.println("\nPrescription Review:");
                        System.out.println(prescription.getPrescriptionDetails());
                        System.out.print("Confirm and issue prescription? (y/n): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                            prescriptionManager.finalizePrescription(prescription);
                            System.out.println("Prescription issued successfully!");
                            return;
                        }
                        break;
                        
                    case 4:
                        System.out.println("Prescription cancelled.");
                        return;
                        
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error issuing prescription: " + e.getMessage());
        }
    }

    private static void verifyPrescription(Doctor doctor) {
        System.out.println("\n=== Verify Prescription ===");
        System.out.print("Enter prescription ID: ");
        String prescriptionId = scanner.nextLine();
        
        try {
            prescriptionManager.verifyPrescription(doctor, prescriptionId);
            System.out.println("Prescription verified successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
} 