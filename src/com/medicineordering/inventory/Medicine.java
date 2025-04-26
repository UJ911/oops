package com.medicineordering.inventory;

import java.math.BigDecimal;
import com.medicineordering.exception.MedicineOutOfStockException;
import com.medicineordering.exception.PharmacyException;

// Consider adding imports if Description becomes a complex type or for other future needs
// import java.math.BigDecimal;

public class Medicine {
    
    private String medicineId;
    private String name;
    private String description;
    private BigDecimal price; // Use BigDecimal
    private int stockQuantity;
    private boolean requiresPrescription;
    private String manufacturer;
    private String category;

    // Constructors using BigDecimal
    public Medicine(String name, BigDecimal price) {
        // Basic validation
        if (name == null || name.trim().isEmpty() || price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid name or price for Medicine");
        }
        this.name = name;
        this.price = price;
        this.requiresPrescription = false; // Default value
        this.stockQuantity = 0; // Default stock
        // Generate ID if needed
        // this.medicineId = UUID.randomUUID().toString();
    }

    public Medicine(String name, BigDecimal price, boolean requiresPrescription) {
        this(name, price);
        this.requiresPrescription = requiresPrescription;
    }
    
    public Medicine(String medicineId, String name, String description, BigDecimal price, 
                   int stockQuantity, boolean requiresPrescription, String manufacturer, String category) {
        this(name, price, requiresPrescription);
        if (medicineId == null || medicineId.trim().isEmpty()) {
             throw new IllegalArgumentException("Medicine ID cannot be empty");
        }
         if (stockQuantity < 0) {
             throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.medicineId = medicineId;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.manufacturer = manufacturer;
        this.category = category;
    }

    // Methods
    public boolean checkStock(int quantity) {
        return this.stockQuantity >= quantity;
    }

    public String getDetails() {
        return String.format("ID: %s, Name: %s, Price: %.2f, Stock: %d, Requires Prescription: %b", 
                              medicineId, name, price, stockQuantity, requiresPrescription);
    }

    // Updated to throw exception if stock is insufficient
    public synchronized void decreaseStock(int quantity) throws MedicineOutOfStockException {
        if (quantity <= 0) {
             throw new IllegalArgumentException("Quantity to decrease must be positive.");
        }
        if (this.stockQuantity < quantity) {
            throw new MedicineOutOfStockException(this.name);
        }
        System.out.println("Decreasing stock for " + name + " by " + quantity + ". Old stock: " + stockQuantity);
        this.stockQuantity -= quantity;
        System.out.println("New stock: " + this.stockQuantity);
    }
    
    public synchronized void increaseStock(int quantity) {
         if (quantity <= 0) {
             throw new IllegalArgumentException("Quantity to increase must be positive.");
        }
        System.out.println("Increasing stock for " + name + " by " + quantity + ". Old stock: " + stockQuantity);
        this.stockQuantity += quantity;
         System.out.println("New stock: " + this.stockQuantity);
    }

    // Getters and Setters
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isRequiresPrescription() { return requiresPrescription; }
    public void setRequiresPrescription(boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }
    public int getStockQuantity() { return stockQuantity; }
    public String getManufacturer() { return manufacturer; }
    public String getCategory() { return category; }
    // Removed setStock to enforce updates via increase/decrease methods
    // public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return String.format("Medicine[id=%s, name=%s, price=%.2f, inStock=%d]",
            medicineId, name, price, stockQuantity);
    }
} 