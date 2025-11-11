package com.example.fe.models.feedback;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple in-memory repository for Feedback sample data. Replace with real backend calls later.
 */
public class FeedbackRepository {
    private final List<Feedback> items = new ArrayList<>();

    public FeedbackRepository() {
        // sample feedbacks across different orders/products
        items.add(new Feedback("ORD1001", "LIP-RED-01", 4.5f, "Great product, fast delivery!", "customer1@gmail.com"));
        items.add(new Feedback("ORD1001", "LIP-BALM-02", 5.0f, "Loved the texture, will buy again.", "customer2@gmail.com"));
        items.add(new Feedback("ORD1002", "SERUM-01", 4.0f, "Good serum but packaging could improve.", "customer4@gmail.com"));
        items.add(new Feedback("ORD1003", "MOIS-002", 3.0f, "Packaging slightly damaged", "customer3@gmail.com"));
        items.add(new Feedback("ORD1004", "MASK-01", 2.5f, "Not as expected.", "customer5@gmail.com"));
    }

    public List<Feedback> getAll() {
        return new ArrayList<>(items);
    }

    public List<Feedback> getByOrderId(String orderId) {
        List<Feedback> out = new ArrayList<>();
        if (orderId == null) return out;
        for (Feedback f : items) {
            if (orderId.equalsIgnoreCase(f.getOrderId())) out.add(f);
        }
        return out;
    }

    public Feedback findForOrderProduct(String orderId, String productSku) {
        if (orderId == null || productSku == null) return null;
        for (Feedback f : items) {
            if (orderId.equalsIgnoreCase(f.getOrderId()) && productSku.equalsIgnoreCase(f.getProductSku())) return f;
        }
        return null;
    }
}

