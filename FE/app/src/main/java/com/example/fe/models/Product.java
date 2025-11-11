package com.example.fe.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {
    @SerializedName("_id")
    public String id;
    public String name;
    private String sku;
    private String description;
    public double price;
    private Double salePrice;
    public int stockQuantity;
    public List<String> images;
    private String brand;
    /**
     * The backend may return `category` as either a String (ObjectId) or a populated object
     * { _id: ..., name: ... }. Use Object here and helpers to access id/name safely.
     */
    private Object category;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public List<String> getImages() {
        return images;
    }

    public String getBrand() {
        return brand;
    }

    public Object getCategory() {
        return category;
    }

    /**
     * Try to extract category id (ObjectId) from the category field.
     * Returns null if not available.
     */
    public String getCategoryId() {
        if (category == null) return null;
        if (category instanceof String) return (String) category;
        try {
            // category is likely a Map (LinkedTreeMap) with _id or id
            java.util.Map map = (java.util.Map) category;
            Object id = map.get("_id");
            if (id == null) id = map.get("id");
            return id != null ? String.valueOf(id) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getCategoryName() {
        if (category == null) return null;
        if (category instanceof String) return (String) category;
        try {
            java.util.Map map = (java.util.Map) category;
            Object name = map.get("name");
            return name != null ? String.valueOf(name) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
