package com.example.fe.network;

public class AddItemRequest {
    private String productId;
    private int quantity;

    public AddItemRequest(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}

