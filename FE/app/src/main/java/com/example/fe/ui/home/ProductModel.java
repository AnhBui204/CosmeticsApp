package com.example.fe.ui.home;

import com.example.fe.ui.category.Category;

public class ProductModel {
    private String id; // new: backend product id
    private String name;
    private String price; // formatted price string
    // image: prefer imageUrl (network). Keep imageRes as fallback.
    private String imageUrl;
    private int imageRes;
    private Category category;
    private String sku;
    private int quantity;
    private double unitPrice;
    private String cartItemId; // optional: id of cart item in server

    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    public ProductModel(String name, String price, String imageUrl, int imageRes, Category category) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.imageRes = imageRes;
        this.category = category;
        this.sku = "";
        this.quantity = 1;
        this.unitPrice = 0.0;
    }

    // New constructor with sku/quantity/unitPrice
    public ProductModel(String name, String price, String imageUrl, int imageRes, Category category, String sku, int quantity, double unitPrice) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.imageRes = imageRes;
        this.category = category;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public int getImageRes() { return imageRes; }
    public Category getCategory() { return category; }

    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }

    public String getCartItemId() { return cartItemId; }
    public void setCartItemId(String cartItemId) { this.cartItemId = cartItemId; }
}
