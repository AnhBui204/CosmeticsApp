package com.example.fe.ui.category;

public class Category {
    private String name;
    private String description;
    private String categoryID;
    private int imageResId;

    public Category(String name, String description, String categoryID, int imageResId) {
        this.name = name;
        this.description = description;
        this.categoryID = categoryID;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategoryID() { return categoryID; }
    public int getImageResId() { return imageResId; }
}
