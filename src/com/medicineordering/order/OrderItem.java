package com.medicineordering.order;

import com.medicineordering.inventory.Medicine;
import java.math.BigDecimal;

public class OrderItem {
    
    private Medicine medicine; // Reference to the Medicine object
    private int quantity;
    private BigDecimal price; // Price per unit at the time of order

    // Constructor
    public OrderItem(Medicine medicine, int quantity) {
        if (medicine == null) {
            throw new IllegalArgumentException("Medicine cannot be null for an OrderItem");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive for an OrderItem");
        }
        this.medicine = medicine;
        this.quantity = quantity;
        this.price = medicine.getPrice(); // Store price at time of order
    }

    // Methods
    public BigDecimal calculateSubtotal() {
        // Multiply price by quantity
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("New quantity must be positive");
        }
        System.out.println("Updating quantity for item " + medicine.getName() + " from " + this.quantity + " to " + newQuantity);
        this.quantity = newQuantity;
        // Subtotal will need recalculation in the Order class
    }

    // Getters and Setters
    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        if (medicine == null) {
            throw new IllegalArgumentException("Medicine cannot be null for an OrderItem");
        }
        this.medicine = medicine;
        // Optionally update price if medicine reference changes? Or keep original price?
        this.price = medicine.getPrice(); 
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        updateQuantity(quantity);
    }

    public BigDecimal getPrice() {
        return price;
    }

    // Price is typically set at creation based on Medicine, so a setter might not be needed
    // public void setPrice(double price) {
    //     this.price = price;
    // }
} 