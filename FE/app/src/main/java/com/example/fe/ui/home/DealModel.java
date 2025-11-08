package com.example.fe.ui.home;

public class DealModel {
    private String price;
    private String description;
    private int imageRes;

    public DealModel(String price, String description, int imageRes) {
        this.price = price;
        this.description = description;
        this.imageRes = imageRes;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public int getImageRes() {
        return imageRes;
    }
}