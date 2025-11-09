package com.example.fe.ui.home;

import com.example.fe.ui.category.Category;

public class ProductModel {
    private String name;
    private String price; // formatted price string
    private int image;
    private Category category;
    private String sku;
    private int quantity;
    private double unitPrice;

    public ProductModel(String name, String price, int image, Category category) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.category = category;
        this.sku = "";
        this.quantity = 1;
        this.unitPrice = 0.0;
    }

    // New constructor with sku/quantity/unitPrice
    public ProductModel(String name, String price, int image, Category category, String sku, int quantity, double unitPrice) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.category = category;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImage() { return image; }
    public Category getCategory() { return category; }

    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
}
