package com.example.fe.network;

import com.example.fe.models.ProductsResponse;
import com.example.fe.models.Category;
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
}
