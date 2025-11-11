package com.example.fe.ui.seller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.example.fe.models.Product;
import com.example.fe.models.ProductsResponse;
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.ui.seller.adapter.SellerProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SellerProductAdapter adapter;
    private FloatingActionButton fabAdd;

    private static final int REQ_CREATE_PRODUCT = 1001;
    private static final int REQ_EDIT_PRODUCT = 1002;

    public SellerProductsFragment() {
        // required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_seller_products, container, false);

        recyclerView = v.findViewById(R.id.recyclerSellerProducts);
        fabAdd = v.findViewById(R.id.fabAddProduct);

        adapter = new SellerProductAdapter(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        if (fabAdd != null) {
            fabAdd.setOnClickListener(view -> {
                Intent i = new Intent(requireActivity(), com.example.fe.ui.seller.ProductEditActivity.class);
                i.putExtra("mode", "create");
                startActivityForResult(i, REQ_CREATE_PRODUCT);
            });
        }

        adapter.setOnProductActionListener(new SellerProductAdapter.OnProductActionListener() {
            @Override
            public void onEdit(Product product) {
                Intent i = new Intent(requireActivity(), com.example.fe.ui.seller.ProductEditActivity.class);
                i.putExtra("mode", "edit");
                i.putExtra("productId", product.getId());
                startActivityForResult(i, REQ_EDIT_PRODUCT);
            }

            @Override
            public void onDelete(Product product) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Xóa sản phẩm")
                        .setMessage("Bạn có chắc muốn xóa sản phẩm này?")
                        .setNegativeButton("Hủy", null)
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            ApiService service = ApiClient.getClient().create(ApiService.class);
                            Call<okhttp3.ResponseBody> call = service.deleteProduct(product.getId());
                            call.enqueue(new Callback<okhttp3.ResponseBody>() {
                                @Override
                                public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        adapter.removeProduct(product.getId());
                                        Toast.makeText(requireContext(), "Sản phẩm đã được xóa", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "Xóa không thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                                    Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }).show();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.getItemCount() == 0) loadProducts();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requireActivity().RESULT_OK) {
            // refresh after create/edit
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
                    Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductsResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // expose a public refresh method so the activity header refresh button can trigger a product reload
    public void refreshProducts() {
        loadProducts();
    }
}