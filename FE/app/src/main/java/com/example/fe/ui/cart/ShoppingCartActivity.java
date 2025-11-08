package com.example.fe.ui.cart;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.example.fe.ui.category.Category;
import com.example.fe.ui.home.ProductModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private TextView tvSubtotal, tvDelivery, tvTotal, tvMore, tvEdit;
    private RecyclerView rvCartItems;
    private List<ProductModel> cartItems;
    private List<ProductModel> visibleItems;
    private CartAdapter adapter;
    private boolean showingAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_shopping_cart);

        ImageButton btnBack = findViewById(R.id.btnBack);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDelivery = findViewById(R.id.tvDelivery);
        tvTotal = findViewById(R.id.tvTotal);
        tvMore = findViewById(R.id.tvMore);
        tvEdit = findViewById(R.id.tvEdit);
        MaterialButton btnCheckout = findViewById(R.id.btnCheckout);
        rvCartItems = findViewById(R.id.rvCartItems);

        btnBack.setOnClickListener(v -> finish());
        btnCheckout.setOnClickListener(v ->
                Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show()
        );

        setupCartList();

        double subtotal = 35.96;
        double delivery = 2.00;
        double total = subtotal + delivery;

        tvSubtotal.setText(String.format("$%.2f", subtotal));
        tvDelivery.setText(String.format("$%.2f", delivery));
        tvTotal.setText(String.format("$%.2f", total));
    }

    private void setupCartList() {
        cartItems = new ArrayList<>();

        Category makeup = new Category("Makeup", "Son môi cao cấp", "CAT01", R.drawable.img_lipstick_red);
        cartItems.add(new ProductModel("Son môi đỏ", "$7.90", R.drawable.img_lipstick_red, makeup));
        cartItems.add(new ProductModel("Kem nền sáng", "$12.50", R.drawable.img_moisturizer, makeup));
        cartItems.add(new ProductModel("Phấn má hồng", "$9.56", R.drawable.img_deal_3, makeup));
        cartItems.add(new ProductModel("Phấn phủ kiềm dầu", "$6.00", R.drawable.img_deal_2, makeup));
        cartItems.add(new ProductModel("Chì kẻ mày", "$5.99", R.drawable.img_lipstick_red, makeup));

        // Ban đầu chỉ hiển thị 4 sản phẩm
        visibleItems = new ArrayList<>(cartItems.subList(0, Math.min(4, cartItems.size())));

        adapter = new CartAdapter(visibleItems);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);

        // Nếu còn nhiều hơn 4 thì hiển thị "+ X More"
        if (cartItems.size() > 4) {
            int extra = cartItems.size() - 4;
            tvMore.setText("+ " + extra + " More");
        } else {
            tvMore.setVisibility(TextView.GONE);
        }

        // Sự kiện khi nhấn "More"
        tvMore.setOnClickListener(v -> {
            if (!showingAll) {
                visibleItems.clear();
                visibleItems.addAll(cartItems);
                adapter.notifyDataSetChanged();
                tvMore.setText("Show less");
                showingAll = true;
            } else {
                visibleItems.clear();
                visibleItems.addAll(cartItems.subList(0, Math.min(4, cartItems.size())));
                adapter.notifyDataSetChanged();
                int extra = cartItems.size() - 4;
                tvMore.setText("+ " + extra + " More");
                showingAll = false;
            }
        });

        tvEdit.setOnClickListener(v ->
                Toast.makeText(this, "Edit cart items", Toast.LENGTH_SHORT).show()
        );
    }
}
