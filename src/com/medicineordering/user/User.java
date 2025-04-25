package com.medicineordering.user;

import java.io.Serializable;

public abstract class User implements Serializable {
    protected String userId;
    protected String name;
    protected String email;
    protected String password;
    protected String contactInfo;
    
    // Nested interface for authentication
    public interface AuthenticationProvider {
        boolean authenticate(String username, String password);
        boolean authenticate(String token);
    }
    
    // Nested static class for validation
    public static class Validator {
        public static boolean validateEmail(String email) {
            return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        }
        
        public static boolean validatePassword(String password) {
            return password != null && password.length() >= 8;
        }
    }
    
    // Overloaded constructors
    public User() {}
    
    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
    
    public User(String userId, String name, String email, String password, String contactInfo) {
        this(userId, name, email);
        this.password = password;
        this.contactInfo = contactInfo;
    }
    
    // Abstract methods
    public abstract boolean login(String username, String password);
    public abstract boolean login(String token);
    public abstract void register();
    public abstract void updateProfile();
    
    // Getters and setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
} 