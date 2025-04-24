package com.medicineordering.order;
/**
 * OrderItem represents a single medicine entry in a customer's order.
 * It includes the medicine name, quantity ordered, and price per unit.
 */

public class OrderItem {
    private String medicine;
    private int quantity;
    private double price;

    public OrderItem(String medicine, int quantity, double price) {
        this.medicine = medicine;
        this.quantity = quantity;
        this.price = price;
    }

    public double calculateSubtotal() {
        return quantity * price;
    }

    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }

    public String getMedicine() {
        return medicine;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
