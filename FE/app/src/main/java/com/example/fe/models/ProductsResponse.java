package com.example.fe.models;

import java.util.List;

public class ProductsResponse {
    private List<Product> products;
    private int page;
    private int totalPages;
    private int totalProducts;

    public List<Product> getProducts() {
        return products;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalProducts() {
        return totalProducts;
    }
}
