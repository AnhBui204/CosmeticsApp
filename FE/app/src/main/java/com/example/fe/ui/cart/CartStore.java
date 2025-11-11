package com.example.fe.ui.cart;

import com.example.fe.ui.home.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class CartStore {
    private static List<ProductModel> cartItems = new ArrayList<>();

    public static void setCartItems(List<ProductModel> items) {
        cartItems = items != null ? items : new ArrayList<>();
    }

    public static List<ProductModel> getCartItems() {
        return cartItems;
    }

    public static void clear() {
        cartItems = new ArrayList<>();
    }
}

