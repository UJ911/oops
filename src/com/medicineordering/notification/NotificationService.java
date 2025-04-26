package com.medicineordering.notification;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

// Enum for Notification Type (used in overloaded method)
public enum NotificationType {
    SMS, EMAIL, PUSH
}

// Placeholder for Template data
class NotificationTemplate {
    String templateId;
    String content;
    NotificationType type;
    // ... other template properties
}

public class NotificationService {
    private static final NotificationService instance = new NotificationService();
    private final Map<String, List<String>> userNotifications;
    private final Map<String, NotificationTemplate> templates;
    private final Queue<NotificationJob> notificationQueue;
    private final Map<String, String> notificationTemplates;

    private NotificationService() {
        this.userNotifications = new HashMap<>();
        this.templates = new HashMap<>();
        this.notificationQueue = new LinkedList<>();
        this.notificationTemplates = new HashMap<>();
        initializeTemplates();
    }

    public static NotificationService getInstance() {
        return instance;
    }

    public void sendNotification(String userId, String message) {
        userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(message);
        System.out.println("Notification sent to user " + userId + ": " + message);
    }

    public List<String> getNotifications(String userId) {
        return new ArrayList<>(userNotifications.getOrDefault(userId, new ArrayList<>()));
    }

    public void clearNotifications(String userId) {
        userNotifications.remove(userId);
    }

    public void markNotificationAsRead(String userId, String notification) {
        List<String> notifications = userNotifications.get(userId);
        if (notifications != null) {
            notifications.remove(notification);
            if (notifications.isEmpty()) {
                userNotifications.remove(userId);
            }
        }
    }

    // Properties from plan
    private Map<String, NotificationTemplate> templates; // Key: templateId
    private Queue<NotificationJob> notificationQueue; // Queue for async processing
    private Map<String, String> notificationTemplates;

    // Placeholder for job details
    private static class NotificationJob {
        String userId;
        String message;
        NotificationType type;
        // ... other details like priority, scheduled time
    }

    private void initializeTemplates() {
        notificationTemplates.put("ORDER_CONFIRM_SMS", "Your order #%s has been confirmed");
        notificationTemplates.put("ORDER_SHIPPED_SMS", "Your order #%s has been shipped");
        notificationTemplates.put("PRESCRIPTION_VERIFIED_SMS", "Your prescription #%s has been verified");
        notificationTemplates.put("PAYMENT_RECEIVED_SMS", "Payment of %s received for order #%s");
    }

    // Methods from plan (including Overloaded Methods)
    public void sendNotification(String userId, String message, NotificationType type) {
        System.out.println("Sending [" + type + "] notification to User [" + userId + "]: " + message);
        queueNotification(userId, message, type);
    }

    public void sendNotificationFromTemplate(String userId, String templateId, Object... args) {
        String template = notificationTemplates.get(templateId);
        if (template != null) {
            String message = String.format(template, args);
            sendNotification(userId, message);
        }
    }

    public void scheduleReminder(String userId, String message, NotificationType type, long delayMillis) {
        System.out.println("Scheduling [" + type + "] reminder for User [" + userId + "] in " + delayMillis + "ms: " + message);
    }

    public void createTemplate(String templateId, String content, NotificationType type) {
        System.out.println("Creating template: " + templateId + " Type: " + type);
        NotificationTemplate newTemplate = new NotificationTemplate();
        newTemplate.templateId = templateId;
        newTemplate.content = content;
        newTemplate.type = type;
        templates.put(templateId, newTemplate);
    }

    // Example method to process the queue (could be run by a background thread)
    public void processNotificationQueue() {
        while (!notificationQueue.isEmpty()) {
            NotificationJob job = notificationQueue.poll();
            if (job != null) {
                sendNotification(job.userId, job.message);
            }
        }
    }

    // Method to add to the queue
    private void queueNotification(String userId, String message, NotificationType type) {
        NotificationJob job = new NotificationJob();
        job.userId = userId;
        job.message = message;
        job.type = type;
        notificationQueue.offer(job);
        System.out.println("Queued notification for user: " + userId);
    }

    // Getters
    public Map<String, NotificationTemplate> getTemplates() { return new HashMap<>(templates); }
    public Queue<NotificationJob> getNotificationQueue() { return new LinkedList<>(notificationQueue); }
} 