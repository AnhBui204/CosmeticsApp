package com.example.fe.models;

import java.util.List;

public class Cart {
    public String id;
    public String userId;
    public List<CartItem> items;
    public double totalAmount;

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public List<CartItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
}
