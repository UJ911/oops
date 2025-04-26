package com.medicineordering.exception;

public class MedicineOutOfStockException extends RuntimeException {
    public MedicineOutOfStockException(String message) {
        super(message);
    }

    public MedicineOutOfStockException(String medicineName, int requestedQuantity, int availableQuantity) {
        super(String.format("Medicine '%s' is out of stock. Requested: %d, Available: %d",
            medicineName, requestedQuantity, availableQuantity));
    }
} 