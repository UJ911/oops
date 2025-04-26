package com.medicineordering.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

// Placeholder for AuditLog entry type
class AuditLogEntry {
    String userId;
    String action;
    java.util.Date timestamp;
    // ... other details
}

public class SecurityManager {
    private static final SecurityManager instance = new SecurityManager();
    private Map<String, List<ActivityLog>> activityLogs;

    private SecurityManager() {
        this.activityLogs = new HashMap<>();
    }

    public static SecurityManager getInstance() {
        return instance;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        String newHash = hashPassword(password);
        return newHash.equals(hashedPassword);
    }

    // Nested static class for EncryptionUtil (as per plan)
    public static class EncryptionUtil {
        // NOTE: This is a VERY basic example. Use proper crypto libraries (like JCE) for real applications.
        private static SecretKey secretKey; // Store key securely!

        static {
            // Generate a key upon class loading. In reality, load from secure storage.
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(256); // Use 256-bit AES
                secretKey = keyGen.generateKey();
            } catch (Exception e) {
                System.err.println("Error initializing encryption key: " + e.getMessage());
                // Handle key generation failure
            }
        }

        public static String encrypt(String data) {
            if (secretKey == null || data == null) return null;
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] encryptedBytes = cipher.doFinal(data.getBytes());
                return Base64.getEncoder().encodeToString(encryptedBytes);
            } catch (Exception e) {
                System.err.println("Encryption error: " + e.getMessage());
                return null;
            }
        }

        public static String decrypt(String encryptedData) {
            if (secretKey == null || encryptedData == null) return null;
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
                byte[] decryptedBytes = cipher.doFinal(decodedBytes);
                return new String(decryptedBytes);
            } catch (Exception e) {
                System.err.println("Decryption error: " + e.getMessage());
                return null;
            }
        }
    }

    // Properties from plan
    // Using String for key placeholder, should be SecretKey or similar
    // private Map<String, String> encryptionKeys; // Key management is complex

    // Constructor
    public SecurityManager() {
        // this.encryptionKeys = new HashMap<>();
        this.activityLogs = new HashMap<>();
    }

    // Methods using the nested static class
    public String encryptData(String data) {
        System.out.println("Encrypting data...");
        return EncryptionUtil.encrypt(data);
    }

    public String decryptData(String encryptedData) {
        System.out.println("Decrypting data...");
        return EncryptionUtil.decrypt(encryptedData);
    }

    // Methods from plan
    public String generateToken(String userId) {
        // TODO: Implement secure token generation
        String token = "TOKEN_" + userId + "_" + System.currentTimeMillis();
        logActivity(userId, "TOKEN_GENERATED");
        return token;
    }

    public boolean validateToken(String token) {
        // TODO: Implement token validation (e.g., check expiry, user association)
        boolean isValid = token != null && activityLogs.containsKey(token);
        System.out.println("Validating token... Result: " + isValid);
        String userId = activityLogs.get(token).get(0).getUserId();
        logActivity(userId != null ? userId : "UNKNOWN", isValid ? "TOKEN_VALIDATED" : "TOKEN_INVALID");
        return isValid;
    }
    
    public void invalidateToken(String token) {
        System.out.println("Invalidating token...");
        activityLogs.remove(token);
        logActivity(token, "TOKEN_INVALIDATED");
    }

    public void logActivity(String userId, String activity) {
        ActivityLog log = new ActivityLog(userId, activity);
        activityLogs.computeIfAbsent(userId, k -> new ArrayList<>()).add(log);
        System.out.println("Activity logged: " + log);
    }

    public List<ActivityLog> getUserActivities(String userId) {
        return activityLogs.getOrDefault(userId, new ArrayList<>());
    }

    // Getter for audit log (optional)
    public List<AuditLogEntry> getAuditLog() {
        List<AuditLogEntry> auditLog = new ArrayList<>();
        for (Map.Entry<String, List<ActivityLog>> entry : activityLogs.entrySet()) {
            for (ActivityLog log : entry.getValue()) {
                AuditLogEntry auditEntry = new AuditLogEntry();
                auditEntry.userId = entry.getKey();
                auditEntry.action = log.getActivity();
                auditEntry.timestamp = log.getTimestamp();
                auditLog.add(auditEntry);
            }
        }
        return auditLog;
    }

    private static class ActivityLog {
        private final String userId;
        private final String activity;
        private final Date timestamp;

        public ActivityLog(String userId, String activity) {
            this.userId = userId;
            this.activity = activity;
            this.timestamp = new Date();
        }

        public String getUserId() {
            return userId;
        }

        public String getActivity() {
            return activity;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return String.format("[%s] User %s: %s", timestamp, userId, activity);
        }
    }
} 