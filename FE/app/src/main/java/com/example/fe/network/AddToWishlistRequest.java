package com.example.fe.network;

public class AddToWishlistRequest {
    private String productId;

    public AddToWishlistRequest(String productId) {
        this.productId = productId;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
}

