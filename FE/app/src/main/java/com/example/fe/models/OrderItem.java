package com.example.fe.models;

import com.google.gson.annotations.SerializedName;

public class OrderItem {
    public OrderItem(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
    @SerializedName("productId")
    private String productId;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private double price;

    @SerializedName("quantity")
    private int quantity;

    // Getters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
}