package com.medicineordering.order;

import com.medicineordering.user.Customer;
import com.medicineordering.inventory.Medicine; // Needed for invoice generation
import com.medicineordering.exception.MedicineOutOfStockException;
import com.medicineordering.exception.PharmacyException;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.math.BigDecimal;

public class Order {

    // Nested Interface from plan
    public interface StatusUpdateListener {
        void onStatusUpdated(String orderId, OrderStatus newStatus);
    }

    private String orderId;
    private Customer customer; // Reference to the Customer object
    private List<OrderItem> items;
    private BigDecimal totalAmount; // Use BigDecimal
    private OrderStatus status; // Use Enum
    private PaymentStatus paymentStatus; // Use Enum
    private StatusUpdateListener statusListener; // Optional listener

    // Constructors - Simplified, assuming Customer object is available
    // Removed constructors taking only customerId as fetching customer needs a service layer
    public Order(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null for an Order");
        }
        this.orderId = "ORD-" + UUID.randomUUID().toString(); // Generate ID
        this.customer = customer;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
    }

    public Order(Customer customer, List<OrderItem> items) {
        this(customer);
        if (items != null) {
             this.items = new ArrayList<>(items); // Use provided items
        }
        this.calculateTotal(); // Calculate total
    }

    // Method to add item, decrease stock, and recalculate total
    public void addItem(Medicine medicine, int quantity) throws MedicineOutOfStockException {
         if (medicine == null || quantity <= 0) {
             throw new IllegalArgumentException("Invalid medicine or quantity to add.");
        }
        // Check stock before adding
        if (!medicine.checkStock(quantity)) {
            throw new MedicineOutOfStockException(medicine.getName());
        }
        
        // Decrease stock first (can throw exception)
        medicine.decreaseStock(quantity);
        
        // If stock decrease successful, add item
        OrderItem newItem = new OrderItem(medicine, quantity);
        if (this.items == null) { // Should not happen with constructor init
            this.items = new ArrayList<>();
        }
        this.items.add(newItem);
        System.out.println("Added to order " + orderId + ": " + medicine.getName() + " x" + quantity);
        calculateTotal(); // Recalculate after adding
    }
    
    // Methods from plan
    public void calculateTotal() {
        this.totalAmount = BigDecimal.ZERO;
        if (this.items != null) {
            for (OrderItem item : this.items) {
                this.totalAmount = this.totalAmount.add(item.calculateSubtotal());
            }
        }
        System.out.println("Calculated total for order " + orderId + ": " + this.totalAmount);
    }

    // processPayment method removed - should be handled by passing Order to PaymentService/Processor

    public void updateStatus(OrderStatus newStatus) {
        System.out.println("Updating status for order " + orderId + " from " + this.status + " to " + newStatus);
        this.status = newStatus;
        if (statusListener != null) {
            statusListener.onStatusUpdated(this.orderId, newStatus);
        }
    }

    public String generateInvoice() {
        System.out.println("Generating invoice for order " + orderId);
        StringBuilder invoice = new StringBuilder();
        invoice.append("--- Invoice ---\
");
        invoice.append(String.format("Order ID: %s\
", orderId));
        invoice.append(String.format("Customer: %s (ID: %s)\
", customer.getName(), customer.getUserId()));
        invoice.append("Items:\n");
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                 invoice.append(String.format("  - %s (ID: %s) x %d @ %.2f = %.2f\n",
                                             item.getMedicine().getName(),
                                             item.getMedicine().getMedicineId(),
                                             item.getQuantity(),
                                             item.getPrice(), // BigDecimal
                                             item.calculateSubtotal())); // BigDecimal
            }
        } else {
            invoice.append("  (No items)\
");
        }
        invoice.append(String.format("Total Amount: %.2f\
", totalAmount)); // BigDecimal
        invoice.append(String.format("Order Status: %s\
", status));
        invoice.append(String.format("Payment Status: %s\
", paymentStatus));
        invoice.append("---------------\
");
        return invoice.toString();
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    // No setter for orderId
    public Customer getCustomer() { return customer; }
    // No setter for customer
    public List<OrderItem> getItems() { return new ArrayList<>(items); } // Return copy
    // Items managed via addItem method
    public BigDecimal getTotalAmount() { return totalAmount; }
    // totalAmount is calculated
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { updateStatus(status); /* Use update method */ }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { 
        System.out.println("Updating payment status for order " + orderId + " to " + paymentStatus);
        this.paymentStatus = paymentStatus; 
    }
    public StatusUpdateListener getStatusListener() { return statusListener; }
    public void setStatusListener(StatusUpdateListener statusListener) { this.statusListener = statusListener; }
} 