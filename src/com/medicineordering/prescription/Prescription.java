package com.medicineordering.prescription;

import com.medicineordering.user.Doctor;
import com.medicineordering.user.Customer;
import com.medicineordering.inventory.Medicine;
import java.util.*;

public class Prescription {
    private String prescriptionId;
    private Customer customer;
    private Doctor doctor;
    private Map<Medicine, PrescriptionDetails> medicines;
    private Date issueDate;
    private Date expiryDate;
    private boolean isVerified;
    private String notes;
    private PrescriptionStatus status;

    public static class PrescriptionDetails {
        private int quantity;
        private String dosage;
        private String instructions;

        public PrescriptionDetails(int quantity, String dosage, String instructions) {
            this.quantity = quantity;
            this.dosage = dosage;
            this.instructions = instructions;
        }

        public int getQuantity() { return quantity; }
        public String getDosage() { return dosage; }
        public String getInstructions() { return instructions; }
    }

    public Prescription(Customer customer, Doctor doctor) {
        this.prescriptionId = UUID.randomUUID().toString();
        this.customer = customer;
        this.doctor = doctor;
        this.medicines = new HashMap<>();
        this.issueDate = new Date();
        this.expiryDate = calculateExpiryDate();
        this.isVerified = false;
        this.status = PrescriptionStatus.PENDING;
    }

    public void addMedicine(Medicine medicine, int quantity, String dosage, String instructions) {
        medicines.put(medicine, new PrescriptionDetails(quantity, dosage, instructions));
    }

    public void verify(Doctor verifyingDoctor) {
        if (!verifyingDoctor.equals(this.doctor)) {
            throw new IllegalArgumentException("Only the prescribing doctor can verify this prescription");
        }
        this.isVerified = true;
        this.status = PrescriptionStatus.VERIFIED;
    }

    private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(issueDate);
        cal.add(Calendar.MONTH, 6); // Prescriptions valid for 6 months by default
        return cal.getTime();
    }

    public String getPrescriptionDetails() {
        StringBuilder details = new StringBuilder();
        details.append("\n=== Prescription Details ===\n");
        details.append("ID: ").append(prescriptionId).append("\n");
        details.append("Patient: ").append(customer.getName()).append("\n");
        details.append("Doctor: Dr. ").append(doctor.getName()).append("\n");
        details.append("Issue Date: ").append(issueDate).append("\n");
        details.append("Expiry Date: ").append(expiryDate).append("\n");
        details.append("Status: ").append(status).append("\n");
        details.append("\nPrescribed Medicines:\n");

        for (Map.Entry<Medicine, PrescriptionDetails> entry : medicines.entrySet()) {
            Medicine medicine = entry.getKey();
            PrescriptionDetails details = entry.getValue();
            details.append(String.format("- %s\n", medicine.getName()));
            details.append(String.format("  Quantity: %d\n", details.getQuantity()));
            details.append(String.format("  Dosage: %s\n", details.getDosage()));
            details.append(String.format("  Instructions: %s\n", details.getInstructions()));
        }

        if (!notes.isEmpty()) {
            details.append("\nNotes: ").append(notes).append("\n");
        }

        return details.toString();
    }

    // Getters
    public String getPrescriptionId() { return prescriptionId; }
    public Customer getCustomer() { return customer; }
    public Doctor getDoctor() { return doctor; }
    public Map<Medicine, PrescriptionDetails> getMedicines() { return Collections.unmodifiableMap(medicines); }
    public Date getIssueDate() { return issueDate; }
    public Date getExpiryDate() { return expiryDate; }
    public boolean isVerified() { return isVerified; }
    public String getNotes() { return notes; }
    public PrescriptionStatus getStatus() { return status; }

    // Setters
    public void setNotes(String notes) { this.notes = notes; }
    public void setStatus(PrescriptionStatus status) { this.status = status; }
} 