package com.medicineordering.user;

import com.medicineordering.prescription.Prescription;
import com.medicineordering.exception.PharmacyException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends User implements Serializable {
    private String specialization;
    private String licenseNumber;
    private String hospitalAffiliation;
    private String availability;
    private List<Prescription> issuedPrescriptions;
    
    public Doctor() {
        super();
        this.issuedPrescriptions = new ArrayList<>();
    }
    
    public Doctor(String userId, String name, String email, String password, String contactInfo, String licenseNumber, String specialization, String availability) {
        super(userId, name, email, password, contactInfo);
        this.licenseNumber = licenseNumber;
        this.specialization = specialization;
        this.availability = availability;
        this.issuedPrescriptions = new ArrayList<>();
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public String getHospitalAffiliation() {
        return hospitalAffiliation;
    }
    
    public void setHospitalAffiliation(String hospitalAffiliation) {
        this.hospitalAffiliation = hospitalAffiliation;
    }
    
    public String getAvailability() {
        return availability;
    }
    
    public void setAvailability(String availability) {
        this.availability = availability;
    }
    
    public List<Prescription> getIssuedPrescriptions() {
        return new ArrayList<>(issuedPrescriptions);
    }
    
    public void issuePrescription(Prescription prescription) throws PharmacyException {
        if (prescription == null) {
            throw new PharmacyException("Prescription object cannot be null.");
        }
        if (!this.userId.equals(prescription.getDoctorId())) {
             throw new PharmacyException("Doctor " + this.userId + " cannot issue a prescription assigned to doctor " + prescription.getDoctorId());
        }
        
        System.out.println("Doctor " + getName() + " issuing prescription: " + prescription.getPrescriptionId());
        if (this.issuedPrescriptions == null) this.issuedPrescriptions = new ArrayList<>(); 
        this.issuedPrescriptions.add(prescription);
    }
    
    public boolean verifyPrescription(Prescription prescription) {
        if (prescription == null) {
            System.err.println("Cannot verify null prescription.");
            return false;
        }
        System.out.println("Verifying prescription " + prescription.getPrescriptionId() + " by Dr. " + getName());
        prescription.setIsVerified(true);
        return true;
    }
    
    public void consultPatient(String customerId) {
        System.out.println("Dr. " + getName() + " starting consultation with patient " + customerId + " (Simulation)");
    }
    
    public Prescription prescribeMedicine(String customerId, List<String> medicineNames) {
        System.out.println("Dr. " + getName() + " prescribing medicine for patient " + customerId + " (Requires full implementation)");
        return null;
    }
    
    @Override
    public boolean login(String username, String password) {
        System.out.println("Doctor login attempt for: " + username);
        return Validator.validateEmail(username) && Validator.validatePassword(password) && username.equals(this.email) && password.equals(this.password);
    }
    
    @Override
    public boolean login(String token) {
        System.out.println("Doctor login attempt with token.");
        return token != null && !token.isEmpty();
    }
    
    @Override
    public void register() {
        System.out.println("Registering new doctor: " + getName());
    }
    
    @Override
    public void updateProfile() {
        System.out.println("Updating profile for doctor: " + getName());
    }
    
    @Override
    public String getUserType() {
        return "DOCTOR";
    }
    
    @Override
    public String toString() {
        return String.format("Doctor[ID: %s, Name: %s, Specialization: %s, License: %s, Hospital: %s]",
                           getUserId(), getName(), specialization, licenseNumber, hospitalAffiliation);
    }
} 