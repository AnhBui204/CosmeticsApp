package com.example.fe.api;

import com.example.fe.data.UserData;
import com.example.fe.models.Order;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {


    @PUT("api/users/me")
    Call<UserData> updateProfile(@Body Map<String, Object> body);

    @PUT("api/users/change-password")
    Call<Void> changePassword(@Body Map<String, String> body);
    @GET("api/orders")
    Call<List<Order>> getUserOrders();
    @GET("api/orders/by-code/{orderCode}")
    Call<Order> getOrderDetail(@Path("orderCode") String orderCode);

    @GET("api/orders") // hoặc /api/orders?sellerId=...
    Call<List<Order>> getSellerOrders(@Query("status") String status);
}
