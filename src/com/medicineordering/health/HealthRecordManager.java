package com.medicineordering.health;

import com.medicineordering.user.User;
import com.medicineordering.prescription.Prescription;
import com.medicineordering.exception.PharmacyException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

// Assuming HealthRecord is a class defined elsewhere or needs to be created.
// Placeholder definition for compilation:
class HealthRecord implements Serializable { /* ... properties ... */ }

public class HealthRecordManager implements Serializable {
    private Map<String, List<HealthRecord>> patientRecords;
    private Map<String, List<Prescription>> prescriptionHistory;
    
    public HealthRecordManager() {
        this.patientRecords = new HashMap<>();
        this.prescriptionHistory = new HashMap<>();
    }
    
    public void storeRecord(String patientId, HealthRecord record) throws PharmacyException {
        if (patientId == null || record == null) {
            throw new PharmacyException("Patient ID and record cannot be null");
        }
        
        List<HealthRecord> records = patientRecords.getOrDefault(patientId, new ArrayList<>());
        records.add(record);
        patientRecords.put(patientId, records);
    }
    
    public List<HealthRecord> retrieveRecord(String patientId) throws PharmacyException {
        if (patientId == null) {
            throw new PharmacyException("Patient ID cannot be null");
        }
        
        List<HealthRecord> records = patientRecords.get(patientId);
        if (records == null) {
            throw new PharmacyException("No records found for patient: " + patientId);
        }
        return new ArrayList<>(records);
    }
    
    public void updateRecord(String patientId, HealthRecord oldRecord, HealthRecord newRecord) 
            throws PharmacyException {
        if (patientId == null || oldRecord == null || newRecord == null) {
            throw new PharmacyException("Patient ID and records cannot be null");
        }
        
        List<HealthRecord> records = patientRecords.get(patientId);
        if (records == null) {
            throw new PharmacyException("No records found for patient: " + patientId);
        }
        
        int index = records.indexOf(oldRecord);
        if (index == -1) {
            throw new PharmacyException("Record not found for update");
        }
        
        records.set(index, newRecord);
    }
    
    public void deleteRecord(String patientId, HealthRecord record) throws PharmacyException {
        if (patientId == null || record == null) {
            throw new PharmacyException("Patient ID and record cannot be null");
        }
        
        List<HealthRecord> records = patientRecords.get(patientId);
        if (records == null) {
            return;
        }
        
        if (!records.remove(record)) {
            // Record wasn't found, maybe log this?
        }
    }
    
    public void addPrescriptionToHistory(String patientId, Prescription prescription) 
            throws PharmacyException {
        if (patientId == null || prescription == null) {
            throw new PharmacyException("Patient ID and prescription cannot be null");
        }
        
        List<Prescription> prescriptions = prescriptionHistory.getOrDefault(patientId, new ArrayList<>());
        prescriptions.add(prescription);
        prescriptionHistory.put(patientId, prescriptions);
    }
    
    public List<Prescription> getPrescriptionHistory(String patientId) throws PharmacyException {
        if (patientId == null) {
            throw new PharmacyException("Patient ID cannot be null");
        }
        
        List<Prescription> prescriptions = prescriptionHistory.get(patientId);
        if (prescriptions == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(prescriptions);
    }

    // Nested static class for validation (as per plan)
    public static class RecordValidator {
        public static boolean validateRecord(Object record) {
            // TODO: Implement actual validation logic for health records, e.g., check HealthRecord fields
            System.out.println("Validating health record...");
            return record != null && record instanceof HealthRecord; // Basic check
        }
    }
} 