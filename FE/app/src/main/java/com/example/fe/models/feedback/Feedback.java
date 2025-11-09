package com.example.fe.models.feedback;

/**
 * Feedback model representing a customer's feedback for a specific product in an order.
 */
public class Feedback {
    private final String orderId;
    private final String productSku;
    private final float rating;
    private final String comment;
    private final String customerEmail;

    public Feedback(String orderId, String productSku, float rating, String comment, String customerEmail) {
        this.orderId = orderId;
        this.productSku = productSku;
        this.rating = rating;
        this.comment = comment;
        this.customerEmail = customerEmail;
    }

    public String getOrderId() { return orderId; }
    public String getProductSku() { return productSku; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCustomerEmail() { return customerEmail; }
}
