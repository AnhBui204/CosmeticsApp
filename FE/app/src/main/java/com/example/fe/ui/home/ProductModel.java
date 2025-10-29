package com.example.fe.ui.home;

public class ProductModel {
    public String name, category, price;
    public int image;

    public ProductModel(String name, String category, String price, int image) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.image = image;
    }
}
