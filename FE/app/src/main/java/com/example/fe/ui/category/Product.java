package com.example.fe.ui.category;

public class Product {
    private String name;
    private String description;
    private String quantity; // Đổi từ price thành quantity
    private int imageResId;

    public Product(String name, String description, String quantity, int imageResId) {
        this.name = name;
        this.description = description;
        this.quantity = quantity; // Cập nhật constructor
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getQuantity() { return quantity; } // Cập nhật getter
    public int getImageResId() { return imageResId; }
}