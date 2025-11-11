package com.example.fe.ui.seller;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.example.fe.models.Product;
import com.example.fe.models.ProductsResponse;
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.ui.seller.adapter.SellerProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.ImageButton;
import android.content.Intent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SellerProductAdapter adapter;
    private FloatingActionButton fabAdd;

    private static final int REQ_CREATE_PRODUCT = 1001;
    private static final int REQ_EDIT_PRODUCT = 1002;
    private boolean isFabExpanded = false;
    private boolean isFabHiddenByScroll = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_product_list);

    recyclerView = findViewById(R.id.recyclerSellerProducts);
    fabAdd = findViewById(R.id.fabAddProduct);

    adapter = new SellerProductAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // ---- ADD BUTTON ----
        // default: set FAB half-hidden after layout
        fabAdd.post(() -> {
            float fabWidth = fabAdd.getWidth();
            if (fabWidth == 0) fabWidth = dpToPx(56);

            float margin = dpToPx(20);
            fabAdd.setTranslationX(fabWidth / 2f + margin); // half-hidden including margin
            isFabExpanded = false;
        });

        // Click to expand/collapse
        fabAdd.setOnClickListener(v -> {
            float fabWidth = fabAdd.getWidth();
            float margin = dpToPx(20);
            if (fabWidth == 0) fabWidth = dpToPx(56);

            if (!isFabExpanded) {
                // Expand to fully visible
                fabAdd.animate()
                        .translationX(0)
                        .setDuration(250)
                        .withEndAction(() -> {
                            isFabExpanded = true;
                            // Launch create product activity
                            Intent i = new Intent(SellerProductListActivity.this, ProductEditActivity.class);
                            i.putExtra("mode", "create");
                            startActivityForResult(i, REQ_CREATE_PRODUCT);
                        })
                        .start();
            } else {
                // Collapse back to half-hidden
                fabAdd.animate()
                        .translationX(fabWidth / 2f + margin)
                        .setDuration(250)
                        .withEndAction(() -> isFabExpanded = false)
                        .start();
            }
        });

        // hide FAB to the right while user scrolls down; restore when scrolling up or idle
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                if (dy != 0 && isFabExpanded) {
                    float fabWidth = fabAdd.getWidth();
                    if (fabWidth == 0) fabWidth = dpToPx(56);
                    fabAdd.animate()
                            .translationX(fabWidth / 2f)
                            .setDuration(200)
                            .start();
                    isFabExpanded = false;
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isFabHiddenByScroll) {
                    // restore when idle
                    fabAdd.animate().translationX(0).setDuration(200).start();
                    isFabHiddenByScroll = false;
                }
            }
        });

        // ---- EDIT/DELETE BUTTONS ON ITEMS ----
        // setup action listeners
        adapter.setOnProductActionListener(new com.example.fe.ui.seller.adapter.SellerProductAdapter.OnProductActionListener() {
            @Override
            public void onEdit(Product product) {
                android.content.Intent i = new android.content.Intent(SellerProductListActivity.this, com.example.fe.ui.seller.ProductEditActivity.class);
                i.putExtra("mode", "edit");
                i.putExtra("productId", product.getId());
                startActivityForResult(i, REQ_EDIT_PRODUCT);
            }

            @Override
            public void onDelete(Product product) {
                // confirm then call API
                new androidx.appcompat.app.AlertDialog.Builder(SellerProductListActivity.this)
                        .setTitle("Xóa sản phẩm")
                        .setMessage("Bạn có chắc muốn xóa sản phẩm này?")
                        .setNegativeButton("Hủy", null)
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            ApiService service = ApiClient.getClient().create(ApiService.class);
                            retrofit2.Call<okhttp3.ResponseBody> call = service.deleteProduct(product.getId());
                            call.enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                                @Override
                                public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        adapter.removeProduct(product.getId());
                                        android.widget.Toast.makeText(SellerProductListActivity.this, "Sản phẩm đã được xóa", android.widget.Toast.LENGTH_SHORT).show();
                                    } else {
                                        android.widget.Toast.makeText(SellerProductListActivity.this, "Xóa không thành công", android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                                    android.widget.Toast.makeText(SellerProductListActivity.this, "Lỗi: " + t.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                                }
                            });
                        }).show();
            }
        });

    // initial load is handled in onResume to avoid duplicate calls when activity is recreated
    }

    // ---- ADD BUTTON ANIMATION LOGIC ----
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // product was created/updated; refresh list
            loadProducts();
        }
    }

    // ---- DATA REFRESH LOGIC ---
    @Override
    protected void onResume() {
        super.onResume();
        // refresh product list after possible create/edit
        // Only request if adapter is empty to avoid duplicate simultaneous network calls
        if (adapter.getItemCount() == 0) {
            loadProducts();
        }
    }

    private void loadProducts() {
        ApiService service = ApiClient.getClient().create(ApiService.class);
        Call<ProductsResponse> call = service.getProducts(1, 50);
        call.enqueue(new Callback<ProductsResponse>() {
            @Override
            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> items = response.body().getProducts();
                    adapter.setProducts(items);
                } else {
                    Toast.makeText(SellerProductListActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductsResponse> call, Throwable t) {
                Toast.makeText(SellerProductListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
