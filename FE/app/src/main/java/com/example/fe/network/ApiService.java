package com.example.fe.network;

import com.example.fe.data.TopProductData;
import com.example.fe.models.Order;
import com.example.fe.models.ProductsResponse;
import com.example.fe.models.Category;
import com.example.fe.data.RevenueData;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Body;

public interface ApiService {
    @GET("api/products")
    Call<ProductsResponse> getProducts(@Query("page") int page, @Query("limit") int limit);

    @GET("api/products/{id}")
    Call<com.example.fe.models.Product> getProductById(@retrofit2.http.Path("id") String id);

    @POST("api/products")
    Call<com.example.fe.models.Product> createProduct(@retrofit2.http.Body com.example.fe.models.Product body);

    @PUT("api/products/{id}")
    Call<com.example.fe.models.Product> updateProduct(@retrofit2.http.Path("id") String id, @retrofit2.http.Body com.example.fe.models.Product body);

    @DELETE("api/products/{id}")
    Call<okhttp3.ResponseBody> deleteProduct(@retrofit2.http.Path("id") String id);

    @GET("api/categories")
    Call<List<Category>> getCategories();

    @GET("api/categories/{id}/products")
    Call<ProductsResponse> getProductsByCategory(
            @Path("id") String categoryId,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("sort") String sort
    );
@POST("api/cart/{userId}/items")
    Call<com.example.fe.models.Cart> addItemToCart(@Path("userId") String userId, @Body com.example.fe.network.AddItemRequest request);

    // Remove item from cart for a specific user: DELETE /api/cart/{userId}/items/{itemId}
    @DELETE("api/cart/{userId}/items/{itemId}")
    Call<com.example.fe.models.Cart> removeItemFromCart(@Path("userId") String userId, @Path("itemId") String itemId);

    // Get cart for specific user: GET /api/cart/{userId}
    @GET("api/cart/{userId}")
    Call<com.example.fe.models.Cart> getCartByUser(@Path("userId") String userId);

    @GET("api/revenue/seller-revenue")
    Call<List<RevenueData>> getSellerRevenue(
            @Query("sellerId") String id
    );
    @GET("api/revenue/top-selling-products")
    Call<List<TopProductData>> getTopSellingProducts(
            @Query("sellerId") String sellerId
    );
    @GET("api/users/{userId}/wishlist")
    Call<List<com.example.fe.models.Product>> getWishlist(@Path("userId") String userId);

    @POST("api/users/{userId}/wishlist")
    Call<List<com.example.fe.models.Product>> addToWishlist(@Path("userId") String userId, @Body com.example.fe.network.AddToWishlistRequest body);

    @DELETE("api/users/{userId}/wishlist/{productId}")
    Call<List<com.example.fe.models.Product>> removeFromWishlist(@Path("userId") String userId, @Path("productId") String productId);

    @GET("api/orders")
    Call<List<Order>> getMyOrders();

    @GET("api/orders/by-code/{orderCode}")
    Call<Order> getOrderByCode(
            @Path("orderCode") String orderCode
    );
}
