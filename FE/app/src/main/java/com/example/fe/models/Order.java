package com.example.fe;

public class Order {
    private String orderNumber;
    private String date;
    private String trackingNumber;
    private int quantity;
    private double subtotal;
    private String status; // "Pending", "Delivered", "Cancelled"

    public Order(String orderNumber, String date, String trackingNumber, int quantity, double subtotal, String status) {
        this.orderNumber = orderNumber;
        this.date = date;
        this.trackingNumber = trackingNumber;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.status = status;
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
}

