package com.example.fe.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.example.fe.models.Product;
import com.example.fe.models.ProductsResponse;
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.ui.cart.ShoppingCartActivity;
import com.example.fe.ui.home.ProductModel;
import com.example.fe.ui.home.RecommendedAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private TextView tvGreeting, chipPopular, chipLowPrice;
    private RecyclerView recyclerProducts;
    private RecommendedAdapter adapter;
    private final List<ProductModel> currentList = new ArrayList<>();

    private String categoryId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_productlist);

        // --- Setup ---
        tvGreeting = findViewById(R.id.tvGreeting);
        recyclerProducts = findViewById(R.id.recyclerProducts);
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));

        chipPopular = findViewById(R.id.chipPopular);
        chipLowPrice = findViewById(R.id.chipLowPrice);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Lấy categoryId từ Intent
        categoryId = getIntent().getStringExtra("categoryId");

        // --- Navigation ---
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageView btnCart = findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> startActivity(new Intent(this, ShoppingCartActivity.class)));

        // --- Adapter ---
        adapter = new RecommendedAdapter(currentList);
        recyclerProducts.setAdapter(adapter);

        // --- Load sản phẩm từ API ---
        loadProducts(categoryId, null);

        // --- Chips sort ---
        chipPopular.setOnClickListener(v -> loadProducts(categoryId, null)); // mặc định sort theo mới nhất
        chipLowPrice.setOnClickListener(v -> loadProducts(categoryId, "price_asc"));
    }

    private void loadProducts(String categoryId, String sortType) {
        // Gọi API
        Call<ProductsResponse> call = apiService.getProductsByCategory(categoryId, 1, 50, sortType);
        call.enqueue(new Callback<ProductsResponse>() {
            @Override
            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductsResponse res = response.body();
                    List<Product> products = res.getProducts();

                    currentList.clear();
                    for (Product p : products) {
                        String imageUrl = (p.getImages() != null && !p.getImages().isEmpty())
                                ? p.getImages().get(0)
                                : null;
                        // If backend returns base64 data URI (very large), avoid storing it directly in model
                        if (imageUrl != null) {
                            if (imageUrl.startsWith("data:")) {
                                // data URI (base64). Glide supports it, but very large strings will cause
                                // memory / Binder issues if we keep many of them. Allow small ones only.
                                final int MAX_DATA_URI_LENGTH = 1024 * 1024; // 1 MB - allow larger base64 images to display
                                if (imageUrl.length() <= MAX_DATA_URI_LENGTH) {
                                    android.util.Log.i("ProductList", "Using small data URI for product " + p.getId());
                                    // keep imageUrl as is so adapter/Glide can load it
                                } else {
                                    android.util.Log.w("ProductList", "Skipping large data URI image for product " + p.getId() + " (size=" + imageUrl.length() + ")");
                                    imageUrl = null;
                                }
                            } else if (imageUrl.startsWith("/")) {
                                // relative path from server, prefix base url
                                imageUrl = ApiClient.getBaseUrl() + imageUrl.substring(1);
                            } else if (!(imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
                                // maybe missing scheme, prepend base URL
                                imageUrl = ApiClient.getBaseUrl() + imageUrl;
                            }
                        }

                        // debug: log final imageUrl so we can see what will be loaded
                        android.util.Log.d("ProductList", "product=" + p.getId() + " finalImageUrl=" + (imageUrl != null ? imageUrl : "<null>"));

                        currentList.add(new ProductModel(
                                p.getName(),
                                "$" + p.getPrice(),
                                imageUrl,
                                R.drawable.ic_image_placeholder,
                                null
                        ));
                        // set backend id on model
                        currentList.get(currentList.size() - 1).setId(p.getId());
                    }

                    adapter.notifyDataSetChanged();
                    tvGreeting.setText("Products (" + currentList.size() + ")");
                } else {
                    Toast.makeText(ProductListActivity.this, "Không tải được sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductsResponse> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}