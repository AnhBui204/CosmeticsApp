package com.example.fe.data;

import com.google.gson.annotations.SerializedName;

public class RevenueData {

    @SerializedName("date")
    private String date;

    @SerializedName("revenue")
    private double revenue;

    // Getters
    public String getDate() {
        return date;
    }

    public double getRevenue() {
        return revenue;
    }
}