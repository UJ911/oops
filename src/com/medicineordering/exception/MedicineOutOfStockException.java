package com.medicineordering.exception;

public class MedicineOutOfStockException extends PharmacyException {
    public MedicineOutOfStockException(String medicineName) {
        super("Medicine out of stock: " + medicineName);
    }
} 