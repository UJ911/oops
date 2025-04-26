package com.medicineordering.order;

import com.medicineordering.user.Customer;
import com.medicineordering.inventory.Medicine; // Needed for invoice generation
import com.medicineordering.exception.MedicineOutOfStockException;
import com.medicineordering.exception.PharmacyException;
import com.medicineordering.payment.PaymentStatus;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;

public class Order {

    // Nested Interface from plan
    public interface StatusUpdateListener {
        void onStatusUpdated(String orderId, OrderStatus newStatus);
    }

    private String orderId;
    private Customer customer; // Reference to the Customer object
    private Map<Medicine, Integer> items;
    private Date orderDate;
    private OrderStatus status; // Use Enum
    private PaymentStatus paymentStatus; // Use Enum
    private StatusUpdateListener statusListener; // Optional listener
    private BigDecimal totalAmount; // Use BigDecimal
    private String shippingAddress;
    private Date estimatedDeliveryDate;

    // Constructors - Simplified, assuming Customer object is available
    // Removed constructors taking only customerId as fetching customer needs a service layer
    public Order(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null for an Order");
        }
        this.orderId = UUID.randomUUID().toString();
        this.customer = customer;
        this.items = new HashMap<>();
        this.orderDate = new Date();
        this.status = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.shippingAddress = customer.getContactInfo(); // Default to customer's address
        this.estimatedDeliveryDate = calculateEstimatedDeliveryDate();
    }

    public Order(Customer customer, List<OrderItem> items) {
        this(customer);
        if (items != null) {
             this.items = new HashMap<>();
             for (OrderItem item : items) {
                 this.items.put(item.getMedicine(), item.getQuantity());
                 this.totalAmount = this.totalAmount.add(item.calculateSubtotal());
             }
        }
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
        items.put(medicine, items.getOrDefault(medicine, 0) + quantity);
        totalAmount = totalAmount.add(medicine.getPrice().multiply(BigDecimal.valueOf(quantity)));
        System.out.println("Added to order " + orderId + ": " + medicine.getName() + " x" + quantity);
    }
    
    // Methods from plan
    public void calculateTotal() {
        this.totalAmount = BigDecimal.ZERO;
        if (this.items != null) {
            for (Map.Entry<Medicine, Integer> entry : items.entrySet()) {
                Medicine medicine = entry.getKey();
                Integer quantity = entry.getValue();
                this.totalAmount = this.totalAmount.add(medicine.getPrice().multiply(BigDecimal.valueOf(quantity)));
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
        invoice.append("\n=== Order Invoice ===\n");
        invoice.append("Order ID: ").append(orderId).append("\n");
        invoice.append("Customer: ").append(customer.getName()).append("\n");
        invoice.append("Order Date: ").append(orderDate).append("\n");
        invoice.append("Status: ").append(status).append("\n");
        invoice.append("\nItems:\n");
        
        for (Map.Entry<Medicine, Integer> entry : items.entrySet()) {
            Medicine medicine = entry.getKey();
            Integer quantity = entry.getValue();
            BigDecimal itemTotal = medicine.getPrice().multiply(BigDecimal.valueOf(quantity));
            
            invoice.append(String.format("- %s (x%d) @ $%.2f = $%.2f\n",
                medicine.getName(), quantity, medicine.getPrice(), itemTotal));
        }
        
        invoice.append("\nTotal Amount: $").append(String.format("%.2f", totalAmount));
        invoice.append("\nShipping Address: ").append(shippingAddress);
        invoice.append("\nEstimated Delivery: ").append(estimatedDeliveryDate);
        
        return invoice.toString();
    }

    private Date calculateEstimatedDeliveryDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(orderDate);
        cal.add(Calendar.DAY_OF_MONTH, 3); // Default delivery estimate of 3 days
        return cal.getTime();
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    // No setter for orderId
    public Customer getCustomer() { return customer; }
    // No setter for customer
    public Map<Medicine, Integer> getItems() { return Collections.unmodifiableMap(items); }
    public Date getOrderDate() { return orderDate; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { updateStatus(status); /* Use update method */ }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { 
        System.out.println("Updating payment status for order " + orderId + " to " + paymentStatus);
        this.paymentStatus = paymentStatus; 
    }
    public StatusUpdateListener getStatusListener() { return statusListener; }
    public void setStatusListener(StatusUpdateListener statusListener) { this.statusListener = statusListener; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public Date getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
} 