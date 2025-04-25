package com.medicineordering.health;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

// Basic HealthRecord class definition
public class HealthRecord implements Serializable {
    private String recordId;
    private String patientId;
    private Date recordDate;
    private String notes;
    private List<String> allergies; // Example field
    private List<String> conditions; // Example field

    public HealthRecord(String recordId, String patientId, Date recordDate, String notes, List<String> allergies, List<String> conditions) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.recordDate = recordDate;
        this.notes = notes;
        this.allergies = allergies;
        this.conditions = conditions;
    }

    // Getters and Setters (essential ones)
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public Date getRecordDate() { return recordDate; }
    public void setRecordDate(Date recordDate) { this.recordDate = recordDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    public List<String> getConditions() { return conditions; }
    public void setConditions(List<String> conditions) { this.conditions = conditions; }

    // equals() and hashCode() are important for collection operations (like in HealthRecordManager update/delete)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthRecord that = (HealthRecord) o;
        return Objects.equals(recordId, that.recordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }

    @Override
    public String toString() {
        return "HealthRecord{" +
               "recordId='" + recordId + '\'' +
               ", patientId='" + patientId + '\'' +
               ", recordDate=" + recordDate +
               '}';
    }
} 