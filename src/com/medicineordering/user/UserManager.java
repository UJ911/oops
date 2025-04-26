package com.medicineordering.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManager {
    private List<Customer> customers;
    private List<Doctor> doctors;
    
    private static UserManager instance;
    
    private UserManager() {
        this.customers = new ArrayList<>();
        this.doctors = new ArrayList<>();
    }
    
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    public void addCustomers(Customer... customers) {
        for (Customer customer : customers) {
            this.customers.add(customer);
        }
    }
    
    public void addDoctors(Doctor... doctors) {
        for (Doctor doctor : doctors) {
            this.doctors.add(doctor);
        }
    }
    
    public Optional<Customer> findCustomerByEmail(String email) {
        return customers.stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst();
    }
    
    public Optional<Doctor> findDoctorByEmail(String email) {
        return doctors.stream()
                .filter(d -> d.getEmail().equals(email))
                .findFirst();
    }
    
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }
    
    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors);
    }
} 