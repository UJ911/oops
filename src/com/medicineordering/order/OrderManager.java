package com.medicineordering.order;

import com.medicineordering.user.Customer;
import com.medicineordering.inventory.Medicine;
import com.medicineordering.security.SecurityManager;
import com.medicineordering.notification.NotificationService;
import java.util.*;

public class OrderManager {
    private static OrderManager instance;
    private final SecurityManager securityManager;
    private final NotificationService notificationService;

    private OrderManager() {
        this.securityManager = new SecurityManager();
        this.notificationService = new NotificationService();
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public void trackOrder(String orderId, Customer customer) {
        Optional<Order> orderOpt = customer.getOrders().stream()
            .filter(o -> o.getOrderId().equals(orderId))
            .findFirst();

        orderOpt.ifPresentOrElse(
            this::displayOrderTracking,
            () -> {
                throw new IllegalArgumentException("Order not found with ID: " + orderId);
            }
        );
    }

    public List<Order> getCustomerOrders(Customer customer) {
        return customer.getOrders();
    }

    private void displayOrderTracking(Order order) {
        StringBuilder tracking = new StringBuilder();
        tracking.append("\n=== Order Tracking Details ===\n");
        tracking.append("Order ID: ").append(order.getOrderId()).append("\n");
        tracking.append("Status: ").append(order.getStatus().getDescription()).append("\n");
        tracking.append("Payment Status: ").append(order.getPaymentStatus().getDescription()).append("\n");
        tracking.append("Ordered on: ").append(order.getOrderDate()).append("\n");
        tracking.append("Estimated Delivery: ").append(order.getEstimatedDeliveryDate()).append("\n");
        tracking.append("Shipping Address: ").append(order.getShippingAddress()).append("\n");
        
        tracking.append("\nOrder Timeline:\n");
        tracking.append("✓ Order Placed: ").append(order.getOrderDate()).append("\n");
        
        if (order.getStatus().ordinal() >= OrderStatus.PROCESSING.ordinal()) {
            tracking.append("✓ Processing Started\n");
        }
        if (order.getStatus().ordinal() >= OrderStatus.SHIPPED.ordinal()) {
            tracking.append("✓ Shipped\n");
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            tracking.append("✓ Delivered\n");
        }

        System.out.println(tracking.toString());
    }

    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        
        // Send notification to customer
        Map<String, String> params = new HashMap<>();
        params.put("orderId", order.getOrderId());
        params.put("status", newStatus.getDescription());
        notificationService.sendNotificationFromTemplate(
            order.getCustomer().getUserId(),
            "ORDER_STATUS_UPDATE",
            params
        );
        
        securityManager.logActivity(order.getCustomer().getUserId(), 
            "ORDER_STATUS_UPDATED_" + order.getOrderId() + "_" + newStatus);
    }

    public String formatOrderSummary(Order order) {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Order %s: %s (Placed on: %s)", 
            order.getOrderId(), 
            order.getStatus().getDescription(),
            order.getOrderDate()));
        return summary.toString();
    }
} 