package com.medicineordering.notification;
/**
 * NotificationService is responsible for managing and sending notifications to users.
 * It supports basic and templated messages, reminders, and allows template creation.
 */
public class NotificationService {
    private String[] templates;//storing name and value pairs as alternates         
    private String[] notificationQueue;//storing what notifications areyet to be sent out
    private int templateCount = 0;//temmplate and queue count initialised to 0
    private int queueCount = 0;

    public NotificationService() {
        templates = new String[20]; 
        notificationQueue = new String[100];
    }

    public void sendNotification(String userId, String message) {//overloaded method to send a simple notification to an existing user
        if (queueCount == notificationQueue.length) {
            expandQueue();
        }
        notificationQueue[queueCount++] = "To: " + userId + " | Msg: " + message;
    }

    public void sendNotification(String userId, String message, String type) {///overloaded method to send a typed notification to an existing user
        if (queueCount == notificationQueue.length) {
            expandQueue();
        }
        notificationQueue[queueCount++] = "To: " + userId + " | Type: " + type + " | Msg: " + message;//has a notification type
    }

    public void scheduleReminder(String userId, java.util.Date time) {
        if (queueCount == notificationQueue.length) {
            expandQueue();
        }
        notificationQueue[queueCount++] = "Reminder scheduled for " + userId + " at " + time.toString();
    }

    public void createTemplate(String name, String content) {
        if (templateCount >= templates.length - 2) {
            expandTemplates();
        }
        templates[templateCount++] = name;
        templates[templateCount++] = content;
    }

    private void expandQueue() {//expands when the array is full
        String[] newQueue = new String[notificationQueue.length * 2];
        System.arraycopy(notificationQueue, 0, newQueue, 0, notificationQueue.length);
        notificationQueue = newQueue;
    }

    private void expandTemplates() {
        String[] newTemplates = new String[templates.length * 2];
        System.arraycopy(templates, 0, newTemplates, 0, templates.length);
        templates = newTemplates;
    }
}
