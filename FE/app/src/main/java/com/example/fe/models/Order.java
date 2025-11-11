package com.example.fe.models;

public class Order {
    private final String orderNumber;
    private final String date;
    private final String trackingNumber;
    private final int quantity;
    private final double subtotal;
    private final String status; // "Pending", "Delivered", "Cancelled"
    private final String customerName;
    private final String itemsSummary;

    public Order(String orderNumber, String date, String trackingNumber, int quantity, double subtotal, String status) {
        this(orderNumber, date, trackingNumber, quantity, subtotal, status, "", "");
    }

    public Order(String orderNumber, String date, String trackingNumber, int quantity, double subtotal, String status, String customerName, String itemsSummary) {
        this.orderNumber = orderNumber;
        this.date = date;
        this.trackingNumber = trackingNumber;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.status = status;
        this.customerName = customerName;
        this.itemsSummary = itemsSummary;
    }

    // Getters
    public String getOrderNumber() {
        return orderNumber;
    }

    public String getDate() {
        return date;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getItemsSummary() {
        return itemsSummary;
    }
}
