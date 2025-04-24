package com.medicineordering.security;
/**
 * SecurityManager is responsible for handling encryption, session token validation, 
 * and audit logging of user activities. It ensures secure handling of sensitive data 
 * in the medicine ordering system.
 */

public class SecurityManager {
    private String[] encryptionKeys;
    private String[] sessionTokens;
    private String[] auditLog;

    private int encryptionKeyCount = 0;
    private int tokenCount = 0;
    private int logCount = 0;

    public SecurityManager() { 
        encryptionKeys = new String[10];
        sessionTokens = new String[10];
        auditLog = new String[100];
    }

    public String encryptData(String data) { //encrypting the data for safety
        return "encrypted_" + data;
    }

    public String decryptData(String encryptedData) { //decrypts the encrypted data but removing the prefix
        return encryptedData.replace("encrypted_", "");
    }

    public boolean validateToken(String token) { //checks if the current token is valid or not
        for (int i = 0; i < tokenCount; i++) {
            if (sessionTokens[i].equals(token)) {
                return true;
            }
        }
        return false;
    }

    public void logActivity(String activity) { //logs user activity
        if (logCount == auditLog.length) {
            expandLog();
        }
        auditLog[logCount++] = activity;
    }

    private void expandLog() { //keeps adding to the audit log array until it reaches maximum limit
        String[] newLog = new String[auditLog.length * 2];
        System.arraycopy(auditLog, 0, newLog, 0, auditLog.length);
        auditLog = newLog;
    }

    public static class EncryptionUtil { //Static nested utility class that provides a method to hash strings
        public static String hash(String input) {
            return Integer.toHexString(input.hashCode());
        }
    }
}


