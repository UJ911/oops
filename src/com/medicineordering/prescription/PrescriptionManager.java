package com.medicineordering.prescription;

import com.medicineordering.user.*;
import com.medicineordering.inventory.Medicine;
import com.medicineordering.security.SecurityManager;
import com.medicineordering.notification.NotificationService;
import java.util.*;

public class PrescriptionManager {
    private static PrescriptionManager instance;
    private final SecurityManager securityManager;
    private final NotificationService notificationService;
    private final UserManager userManager;

    private PrescriptionManager() {
        this.securityManager = new SecurityManager();
        this.notificationService = new NotificationService();
        this.userManager = UserManager.getInstance();
    }

    public static PrescriptionManager getInstance() {
        if (instance == null) {
            instance = new PrescriptionManager();
        }
        return instance;
    }

    public void uploadPrescription(Customer customer, String prescriptionId, String doctorEmail) {
        userManager.findDoctorByEmail(doctorEmail).ifPresentOrElse(
            doctor -> {
                Prescription prescription = new Prescription(customer, doctor);
                customer.addPrescription(prescription);
                securityManager.logActivity(customer.getUserId(), "PRESCRIPTION_UPLOADED");
            },
            () -> {
                throw new IllegalArgumentException("Doctor not found with the provided email.");
            }
        );
    }

    public List<Doctor> getAvailableDoctors() {
        return userManager.getAllDoctors();
    }

    public void requestPrescription(Customer customer, String doctorEmail) {
        userManager.findDoctorByEmail(doctorEmail).ifPresentOrElse(
            doctor -> {
                Map<String, String> params = new HashMap<>();
                params.put("patientName", customer.getName());
                params.put("patientContact", customer.getContactInfo());
                notificationService.sendNotificationFromTemplate(
                    doctor.getUserId(),
                    "PRESCRIPTION_REQUEST",
                    params
                );
            },
            () -> {
                throw new IllegalArgumentException("Doctor not found with the provided email.");
            }
        );
    }

    public Prescription issueNewPrescription(Doctor doctor, String patientEmail) {
        return userManager.findCustomerByEmail(patientEmail)
            .map(customer -> new Prescription(customer, doctor))
            .orElseThrow(() -> new IllegalArgumentException("Patient not found with the provided email."));
    }

    public void finalizePrescription(Prescription prescription) {
        prescription.verify(prescription.getDoctor());
        prescription.getCustomer().addPrescription(prescription);
        prescription.getDoctor().addIssuedPrescription(prescription);

        // Send notification to customer
        Map<String, String> params = new HashMap<>();
        params.put("doctorName", prescription.getDoctor().getName());
        params.put("prescriptionId", prescription.getPrescriptionId());
        notificationService.sendNotificationFromTemplate(
            prescription.getCustomer().getUserId(),
            "NEW_PRESCRIPTION_ISSUED",
            params
        );
    }

    public void verifyPrescription(Doctor doctor, String prescriptionId) {
        Optional<Prescription> prescriptionOpt = doctor.getIssuedPrescriptions().stream()
            .filter(p -> p.getPrescriptionId().equals(prescriptionId) && !p.isVerified())
            .findFirst();

        prescriptionOpt.ifPresentOrElse(
            prescription -> {
                prescription.verify(doctor);
                
                // Send notification to customer
                Map<String, String> params = new HashMap<>();
                params.put("doctorName", doctor.getName());
                params.put("prescriptionId", prescriptionId);
                notificationService.sendNotificationFromTemplate(
                    prescription.getCustomer().getUserId(),
                    "PRESCRIPTION_VERIFIED",
                    params
                );
                
                securityManager.logActivity(doctor.getUserId(), "PRESCRIPTION_VERIFIED_" + prescriptionId);
            },
            () -> {
                throw new IllegalArgumentException("No pending prescription found with ID: " + prescriptionId);
            }
        );
    }
} 