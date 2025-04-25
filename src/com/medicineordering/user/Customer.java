package com.medicineordering.user;

import com.medicineordering.interfaces.Discountable;
import com.medicineordering.interfaces.PriorityService;
import com.medicineordering.prescription.Prescription;
import com.medicineordering.order.Order;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User implements Discountable, PriorityService {
    private List<Prescription> prescriptions;
    private List<Order> orders;
    private String paymentInfo;
    private boolean isPremium;
    
    public Customer() {
        super();
        this.prescriptions = new ArrayList<>();
        this.orders = new ArrayList<>();
    }
    
    public Customer(String userId, String name, String email, String password, String contactInfo) {
        super(userId, name, email, password, contactInfo);
        this.prescriptions = new ArrayList<>();
        this.orders = new ArrayList<>();
    }
    
    @Override
    public boolean login(String username, String password) {
        System.out.println("Customer login attempt for: " + username);
        return Validator.validateEmail(username) && Validator.validatePassword(password) && username.equals(this.email) && password.equals(this.password);
    }
    
    @Override
    public boolean login(String token) {
        System.out.println("Customer login attempt with token.");
        return token != null && !token.isEmpty();
    }
    
    @Override
    public void register() {
        System.out.println("Registering new customer: " + getName());
    }
    
    @Override
    public void updateProfile() {
        System.out.println("Updating profile for customer: " + getName());
    }
    
    // Implementation of Discountable interface
    @Override
    public double applyDiscount(double amount) {
        return isPremium ? amount * 0.9 : amount; // 10% discount for premium customers
    }
    
    // Implementation of PriorityService interface
    @Override
    public boolean isPriorityCustomer() {
        return isPremium;
    }
    
    @Override
    public int getPriorityLevel() {
        return isPremium ? 1 : 0;
    }
    
    // Methods for prescription management
    public void uploadPrescription(Prescription prescription) {
        if (prescription == null || !prescription.getPatientId().equals(this.userId)) {
            System.err.println("Cannot upload null or mismatched prescription for user " + this.userId);
            return;
        }
        System.out.println("Uploading/Associating prescription " + prescription.getPrescriptionId() + " for customer: " + getName());
        // Ensure list is initialized
        if (this.prescriptions == null) this.prescriptions = new ArrayList<>(); 
        this.prescriptions.add(prescription);
    }
    
    public List<Prescription> getPrescriptions() {
        return new ArrayList<>(prescriptions); // Return copy
    }
    
    // Methods for order management
    public void placeOrder(Order order) {
        if (order == null || order.getCustomer() != this) {
             System.err.println("Cannot place null or mismatched order for user " + this.userId);
             return;
        }
        System.out.println("Placing order " + order.getOrderId() + " for customer: " + getName());
        // Ensure list is initialized
        if (this.orders == null) this.orders = new ArrayList<>();
        this.orders.add(order);
        // Optionally, trigger notifications or other actions
    }
    
    public List<Order> getOrders() {
        return new ArrayList<>(orders); // Return copy
    }
    
    // Helper method for token validation
    private boolean validateToken(String token) {
        return token != null && !token.isEmpty();
    }
    
    // Getters and setters
    public String getPaymentInfo() {
        return paymentInfo;
    }
    
    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }
    
    public boolean isPremium() {
        return isPremium;
    }
    
    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public Order orderMedicine(List<String> medicineIds, String prescriptionId) {
        // TODO: This needs more context - likely involves creating an Order object 
        // and adding OrderItems. Requires Medicine lookup service.
        System.out.println("Ordering medicine for customer: " + getName() + " (Requires full implementation)");
        return null; // Placeholder
    }

    public void trackOrder(String orderId) {
        // TODO: Implement order tracking logic - needs Order lookup service.
        System.out.println("Tracking order " + orderId + " for customer: " + getName() + " (Requires full implementation)");
    }

    public boolean makePayment(String orderId, String paymentMethod) {
        // TODO: Implement payment logic - needs Order lookup & Payment service.
        System.out.println("Making payment for order " + orderId + " using " + paymentMethod + " (Requires full implementation)");
        return false; // Placeholder
    }
} 