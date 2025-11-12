package com.example.fe.data;

import com.google.gson.annotations.SerializedName;

public class TopProductData {

    @SerializedName("productId")
    private String productId;

    @SerializedName("name")
    private String name;

    @SerializedName("totalQuantity")
    private int totalQuantity;

    // --- THÊM TRƯỜNG MỚI ---
    @SerializedName("totalRevenue")
    private double totalRevenue;

    // Getters
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public int getTotalQuantity() { return totalQuantity; }

    // --- THÊM GETTER MỚI ---
    public double getTotalRevenue() { return totalRevenue; }
}