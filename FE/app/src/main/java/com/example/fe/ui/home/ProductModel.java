package com.example.fe.ui.home;

import com.example.fe.ui.category.Category;

public class ProductModel {
    private String name;
    private String price;
    private int image;
    private Category category;

    public ProductModel(String name, String price, int image, Category category) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImage() { return image; }
    public Category getCategory() { return category; }
}
