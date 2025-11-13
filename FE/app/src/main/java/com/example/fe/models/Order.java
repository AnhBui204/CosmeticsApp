// com/example/fe/models/Order.java
package com.example.fe.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Order {

    @SerializedName("_id")
    private String id;

    @SerializedName("orderCode")
    private String orderNumber; // (orderCode từ API)

    @SerializedName("userId")
    private String userId;

    @SerializedName("items")
    private List<OrderItem> items;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("shippingAddress")
    private ShippingAddress shippingAddress;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private Date date;

    // --- GETTERS (Giữ nguyên) ---
    public String getId() { return id; }
    public String getUserId() {
        return userId;
    }
    public String getOrderNumber() { return orderNumber; }
    public int getQuantity() {
        if (items == null) return 0;
        int totalQty = 0;
        for (OrderItem item : items) {
            totalQty += item.getQuantity();
        }
        return totalQty;
    }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Date getDate() { return date; }
    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public List<OrderItem> getItems() { return items; }

    // --- THÊM CÁC HÀM SETTERS NÀY VÀO ---
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}