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
import com.example.fe.ui.favorite.FavoriteActivity;
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

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

        if (productId != null && !productId.isEmpty()) {
            // load product detail from API
            ApiService api = ApiClient.getClient().create(ApiService.class);
            api.getProductById(productId).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Product p = response.body();
                        tvName.setText(p.getName() != null ? p.getName() : "Product");
                        tvPrice.setText(p.getPrice() >= 0 ? String.format("$%.2f", p.getPrice()) : "$0");
                        String imgUrl = (p.getImages() != null && !p.getImages().isEmpty()) ? p.getImages().get(0) : null;
                        if (imgUrl != null && !imgUrl.isEmpty()) {
                            Glide.with(ProductDetailActivity.this).load(imgUrl).placeholder(R.drawable.ic_image_placeholder).error(R.drawable.ic_image_placeholder).into(img);
                        } else {
                            img.setImageResource(imageRes);
                        }
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

        btnAdd.setOnClickListener(v ->
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show());

        btnBuy.setOnClickListener(v ->
                Toast.makeText(this, "Proceed to checkout", Toast.LENGTH_SHORT).show());
    }
}
