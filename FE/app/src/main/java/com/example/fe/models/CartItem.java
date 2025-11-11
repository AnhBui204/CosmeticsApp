package com.example.fe.models;

import com.google.gson.annotations.SerializedName;

public class CartItem {
    @SerializedName("_id")
    public String id;

    // When backend populates, productId is the Product object. Otherwise it's an id string —
    // using Object and helper to extract id/name/images could be more flexible, but here we keep Product.
    public Product productId;
    public int quantity;
    public double priceAtAdd;

    public String getId() { return id; }
    public Product getProduct() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPriceAtAdd() { return priceAtAdd; }
}
