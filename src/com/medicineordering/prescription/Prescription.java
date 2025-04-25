package com.medicineordering.prescription;

import com.medicineordering.inventory.Medicine;
import java.util.List;
import java.util.Date;

public class Prescription {
    
    private String prescriptionId;
    private String patientId; // Link to Customer userId
    private String doctorId; // Link to Doctor userId
    private List<Medicine> medicines; // List of prescribed medicines
    private Date issuedDate;
    private boolean isVerified;
    private String imagePath; // For uploaded image

    // Constructor
    public Prescription(String prescriptionId, String patientId, String doctorId, List<Medicine> medicines, Date issuedDate) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.medicines = medicines;
        this.issuedDate = issuedDate;
        this.isVerified = false; // Prescriptions start as unverified
    }

    // Methods from plan
    public boolean verify(String verifyingDoctorId) {
        // TODO: Implement verification logic (e.g., check if verifyingDoctorId is valid, maybe check against doctorId?)
        System.out.println("Attempting to verify prescription " + prescriptionId + " by doctor " + verifyingDoctorId);
        // For simplicity, just mark as verified. Real logic needed.
        this.isVerified = true;
        System.out.println("Prescription " + prescriptionId + " verified.");
        return true;
    }

    public void uploadImage(String filePath) {
        // TODO: Implement image upload handling (e.g., save file path, maybe store image data)
        System.out.println("Uploading image for prescription " + prescriptionId + " from path: " + filePath);
        this.imagePath = filePath;
        // In a real system, this would involve file I/O
    }

    public String getPrescriptionDetails() {
        // TODO: Format and return prescription details
        StringBuilder details = new StringBuilder();
        details.append("Prescription ID: ").append(prescriptionId).append("\n");
        details.append("Patient ID: ").append(patientId).append("\n");
        details.append("Doctor ID: ").append(doctorId).append("\n");
        details.append("Issued Date: ").append(issuedDate).append("\n");
        details.append("Verified: ").append(isVerified).append("\n");
        details.append("Image Path: ").append(imagePath != null ? imagePath : "N/A").append("\n");
        details.append("Medicines:\n");
        if (medicines != null) {
            for (Medicine med : medicines) {
                details.append("  - ").append(med.getName()).append(" (ID: ").append(med.getMedicineId()).append(")\n");
            }
        }
        return details.toString();
    }

    // Getters and Setters
    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public List<Medicine> getMedicines() { return medicines; }
    public void setMedicines(List<Medicine> medicines) { this.medicines = medicines; }
    public Date getIssuedDate() { return issuedDate; }
    public void setIssuedDate(Date issuedDate) { this.issuedDate = issuedDate; }
    public boolean isVerified() { return isVerified; }
    public void setIsVerified(boolean isVerified) { this.isVerified = isVerified; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
} 