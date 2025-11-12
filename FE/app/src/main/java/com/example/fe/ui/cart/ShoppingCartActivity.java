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
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.utils.SessionManager;
import com.example.fe.data.UserData;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        btnCheckout.setOnClickListener(v -> {
            // pass current cart to checkout via CartStore
            CartStore.setCartItems(cartItems);
            startActivity(new android.content.Intent(ShoppingCartActivity.this, CustomerCheckoutActivity.class));
        });

        // Try to load cart from server if user logged in
        SessionManager session = new SessionManager(this);
        UserData user = session.getUser();
        if (user != null && user.getId() != null) {
            fetchCartFromServer(user.getId());
        } else {
            setupCartListWithSample();
        }
    }

    private void fetchCartFromServer(String userId) {
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.getCartByUser(userId).enqueue(new Callback<com.example.fe.models.Cart>() {
            @Override
            public void onResponse(Call<com.example.fe.models.Cart> call, Response<com.example.fe.models.Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mapCartToUI(response.body());
                } else {
                    // fallback to sample data
                    setupCartListWithSample();
                    Toast.makeText(ShoppingCartActivity.this, "Không thể tải giỏ hàng (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.fe.models.Cart> call, Throwable t) {
                setupCartListWithSample();
                Toast.makeText(ShoppingCartActivity.this, "Lỗi mạng khi tải giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mapCartToUI(com.example.fe.models.Cart cart) {
        cartItems = new ArrayList<>();
        if (cart.getItems() != null) {
            for (com.example.fe.models.CartItem item : cart.getItems()) {
                // product may be populated
                com.example.fe.models.Product p = item.getProduct();
                String name = (p != null && p.getName() != null) ? p.getName() : "Unknown";
                double priceDouble = (p != null) ? (p.getSalePrice() != null ? p.getSalePrice() : p.getPrice()) : 0.0;
                String priceStr = String.format("$%.2f", priceDouble);
                String imageUrl = (p != null && p.getImages() != null && !p.getImages().isEmpty()) ? p.getImages().get(0) : null;
                com.example.fe.ui.category.Category dummyCat = new Category("", "", "", R.drawable.img_product_placeholder);
                ProductModel pm = new ProductModel(name, priceStr, imageUrl, R.drawable.img_product_placeholder, dummyCat);
                // attach ids so adapter can call delete endpoint
                pm.setId(p != null ? p.getId() : null);
                pm.setCartItemId(item.getId());
                pm = setQuantityAndUnitPrice(pm, item.getQuantity(), priceDouble);
                cartItems.add(pm);
            }
        }

        // visible subset
        visibleItems = new ArrayList<>(cartItems.subList(0, Math.min(4, cartItems.size())));
        runOnUiThread(() -> {
            adapter = new CartAdapter(visibleItems, cartItems, () -> recomputeTotals());
            rvCartItems.setLayoutManager(new LinearLayoutManager(ShoppingCartActivity.this));
            rvCartItems.setAdapter(adapter);

            // update totals if available
            // compute subtotal locally from product unitPrice * quantity
            double subtotal = 0.0;
            for (ProductModel pmItem : cartItems) {
                subtotal += pmItem.getUnitPrice() * pmItem.getQuantity();
            }
            double delivery = (cartItems.isEmpty()) ? 0.0 : 2.00;
            double total = subtotal + delivery;
            tvSubtotal.setText(String.format("$%.2f", subtotal));
            tvDelivery.setText(String.format("$%.2f", delivery));
            tvTotal.setText(String.format("$%.2f", total));

            if (cartItems.size() > 4) {
                int extra = cartItems.size() - 4;
                tvMore.setText("+ " + extra + " More");
            } else {
                tvMore.setVisibility(TextView.GONE);
            }

            setupMoreAndEditHandlers();
        });
    }

    private ProductModel setQuantityAndUnitPrice(ProductModel pm, int qty, double unitPrice) {
        // uses existing constructor convenience; update quantity using reflection-like setters are not present,
        // so just create a new ProductModel with same metadata including quantity
        com.example.fe.ui.category.Category cat = pm.getCategory();
        ProductModel newPm = new ProductModel(pm.getName(), pm.getPrice(), pm.getImageUrl(), pm.getImageRes(), cat, pm.getSku(), qty, unitPrice);
        newPm.setId(pm.getId());
        newPm.setCartItemId(pm.getCartItemId());
        return newPm;
    }

    private void setupCartListWithSample() {
        cartItems = new ArrayList<>();

        Category makeup = new Category("Makeup", "Son môi cao cấp", "CAT01", R.drawable.img_lipstick_red);
        // Use constructor that sets quantity and unitPrice so subtotal can be computed
        cartItems.add(new ProductModel("Son môi đỏ", "$7.90", null, R.drawable.img_lipstick_red, makeup, "", 1, 7.90));
        cartItems.add(new ProductModel("Kem nền sáng", "$12.50", null, R.drawable.img_moisturizer, makeup, "", 1, 12.50));
        cartItems.add(new ProductModel("Phấn má hồng", "$9.56", null, R.drawable.img_deal_3, makeup, "", 1, 9.56));
        cartItems.add(new ProductModel("Phấn phủ kiềm dầu", "$6.00", null, R.drawable.img_deal_2, makeup, "", 1, 6.00));
        cartItems.add(new ProductModel("Chì kẻ mày", "$5.99", null, R.drawable.img_lipstick_red, makeup, "", 1, 5.99));

        // Ban đầu chỉ hiển thị 4 sản phẩm
        visibleItems = new ArrayList<>(cartItems.subList(0, Math.min(4, cartItems.size())));

        adapter = new CartAdapter(visibleItems, cartItems, () -> recomputeTotals());
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);

        // Nếu còn nhiều hơn 4 thì hiển thị "+ X More"
        if (cartItems.size() > 4) {
            int extra = cartItems.size() - 4;
            tvMore.setText("+ " + extra + " More");
        } else {
            tvMore.setVisibility(TextView.GONE);
        }

        // compute and set totals for sample
        double subtotal = 0.0;
        for (ProductModel pm : cartItems) subtotal += pm.getUnitPrice() * pm.getQuantity();
        double delivery = (cartItems.isEmpty()) ? 0.0 : 2.00;
        double total = subtotal + delivery;
        tvSubtotal.setText(String.format("$%.2f", subtotal));
        tvDelivery.setText(String.format("$%.2f", delivery));
        tvTotal.setText(String.format("$%.2f", total));

        setupMoreAndEditHandlers();

        tvEdit.setOnClickListener(v ->
                Toast.makeText(this, "Edit cart items", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupMoreAndEditHandlers() {
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

    private void recomputeTotals() {
        runOnUiThread(() -> {
            double subtotal = 0.0;
            if (cartItems != null) {
                for (ProductModel pm : cartItems) subtotal += pm.getUnitPrice() * pm.getQuantity();
            }
            double delivery = (cartItems.isEmpty()) ? 0.0 : 2.00;
            double total = subtotal + delivery;
            tvSubtotal.setText(String.format("$%.2f", subtotal));
            tvDelivery.setText(String.format("$%.2f", delivery));
            tvTotal.setText(String.format("$%.2f", total));
        });
    }
}
