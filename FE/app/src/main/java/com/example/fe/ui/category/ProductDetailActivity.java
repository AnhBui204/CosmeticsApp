package com.example.fe.ui.category;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fe.R;
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.network.AddItemRequest;
import com.example.fe.models.Product;
import com.example.fe.utils.SessionManager;
import com.example.fe.ui.cart.CartStore;
import com.example.fe.ui.cart.CustomerCheckoutActivity;
import com.example.fe.ui.home.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    // active product fields used for "Buy now"
    private String activeImageUrl;
    private double activeUnitPrice = 0.0;
    private int activeImageRes = R.drawable.ic_image_placeholder;
    private String activeName;
    private String activeProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView imgCart = findViewById(R.id.btnCart);
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, com.example.fe.ui.cart.ShoppingCartActivity.class);
            startActivity(intent);
        });

        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnFav  = findViewById(R.id.btnFav);
        ImageView img       = findViewById(R.id.imgProduct);
        TextView tvName     = findViewById(R.id.tvName);
        TextView tvPrice    = findViewById(R.id.tvPrice);
        TextView tvDetails  = findViewById(R.id.tvDetails);
        Button btnAdd       = findViewById(R.id.btnAddToCart);
        Button btnBuy       = findViewById(R.id.btnBuyNow);

        // Nhận dữ liệu từ Intent (productId ưu tiên)
        String productId = getIntent().getStringExtra("productId");
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        int imageRes = getIntent().getIntExtra("imageResId", R.drawable.ic_image_placeholder);

        // initialize active fields from intent as fallback
        activeImageUrl = imageUrl;
        activeImageRes = imageRes;
        activeName = name;
        activeProductId = productId;
        activeUnitPrice = parsePriceString(price);

        if (productId != null && !productId.isEmpty()) {
            // load product detail from API
            ApiService api = ApiClient.getClient().create(ApiService.class);
            api.getProductById(productId).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Product p = response.body();
                        tvName.setText(p.getName() != null ? p.getName() : "Product");
                        tvPrice.setText(p.getPrice() >= 0 ? String.format(Locale.US, "$%.2f", p.getPrice()) : "$0");
                        String imgUrl = (p.getImages() != null && !p.getImages().isEmpty()) ? p.getImages().get(0) : null;
                        if (imgUrl != null && !imgUrl.isEmpty()) {
                            Glide.with(ProductDetailActivity.this).load(imgUrl).placeholder(R.drawable.ic_image_placeholder).error(R.drawable.ic_image_placeholder).into(img);
                        } else {
                            img.setImageResource(imageRes);
                        }

                        // update active fields from fetched product
                        activeName = p.getName() != null ? p.getName() : activeName;
                        activeProductId = p.getId() != null ? p.getId() : activeProductId;
                        activeImageUrl = imgUrl != null ? imgUrl : activeImageUrl;
                        activeImageRes = imageRes;
                        activeUnitPrice = p.getSalePrice() != null ? p.getSalePrice() : p.getPrice();

                    } else {
                        // fallback to intent data
                        tvName.setText(name != null ? name : "Product");
                        tvPrice.setText(price != null ? price : "$0");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(ProductDetailActivity.this).load(imageUrl).placeholder(R.drawable.ic_image_placeholder).error(R.drawable.ic_image_placeholder).into(img);
                        } else {
                            img.setImageResource(imageRes);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    // fallback to intent data
                    tvName.setText(name != null ? name : "Product");
                    tvPrice.setText(price != null ? price : "$0");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(ProductDetailActivity.this).load(imageUrl).placeholder(R.drawable.ic_image_placeholder).error(R.drawable.ic_image_placeholder).into(img);
                    } else {
                        img.setImageResource(imageRes);
                    }
                }
            });
        } else {
            // No productId -> use intent data
            tvName.setText(name != null ? name : "Product");
            tvPrice.setText(price != null ? price : "$0");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_image_placeholder).error(R.drawable.ic_image_placeholder).into(img);
            } else {
                img.setImageResource(imageRes);
            }
        }

        tvDetails.setText("Praesent commodo cursus magna, vel scelerisque nisl consectetur. " +
                "Nullam quis risus eget urna mollis ornare vel eu leo.");

        btnBack.setOnClickListener(v -> finish());

        btnFav.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            Toast.makeText(this, v.isSelected() ? "Added to wishlist" : "Removed from wishlist", Toast.LENGTH_SHORT).show();
        });

        // New: add to cart network call
        btnAdd.setOnClickListener(v -> {
            SessionManager session = new SessionManager(ProductDetailActivity.this);
            com.example.fe.data.UserData user = session.getUser();
            if (user == null || user.getId() == null) {
                Toast.makeText(ProductDetailActivity.this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            String targetUserId = user.getId();
            String pid = productId;
            if (pid == null) {
                // try to use id stored in intent extras (may be named "id")
                pid = getIntent().getStringExtra("id");
            }
            if (pid == null) {
                Toast.makeText(ProductDetailActivity.this, "Không thể xác định sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            v.setEnabled(false);
            AddItemRequest req = new AddItemRequest(pid, 1);
            ApiService api = ApiClient.getClient(ProductDetailActivity.this).create(ApiService.class);
            api.addItemToCart(targetUserId, req).enqueue(new Callback<com.example.fe.models.Cart>() {
                @Override
                public void onResponse(Call<com.example.fe.models.Cart> call, Response<com.example.fe.models.Cart> response) {
                    v.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Thêm giỏ hàng thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<com.example.fe.models.Cart> call, Throwable t) {
                    v.setEnabled(true);
                    Toast.makeText(ProductDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBuy.setOnClickListener(v -> {
            // create a one-item cart and proceed to checkout
            List<ProductModel> items = new ArrayList<>();
            String priceStr = String.format(Locale.US, "$%.2f", activeUnitPrice);
            // use a lightweight dummy Category (same package) to construct ProductModel
            com.example.fe.ui.category.Category dummy = new com.example.fe.ui.category.Category("", "", "", R.drawable.img_product_placeholder);
            ProductModel pm = new ProductModel(activeName != null ? activeName : "Product", priceStr, activeImageUrl, activeImageRes, dummy, "", 1, activeUnitPrice);
            pm.setId(activeProductId);
            items.add(pm);
            CartStore.setCartItems(items);
            Intent intent = new Intent(ProductDetailActivity.this, CustomerCheckoutActivity.class);
            startActivity(intent);
        });
    }

    private double parsePriceString(String price) {
        if (price == null) return 0.0;
        try {
            String cleaned = price.replaceAll("[^0-9.]", "");
            if (cleaned.isEmpty()) return 0.0;
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
