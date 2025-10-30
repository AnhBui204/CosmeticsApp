package com.example.fe.ui.category;



import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe.R;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnFav  = findViewById(R.id.btnFav);
        ImageView img       = findViewById(R.id.imgProduct);
        TextView tvName     = findViewById(R.id.tvName);
        TextView tvPrice    = findViewById(R.id.tvPrice);
        TextView tvDetails  = findViewById(R.id.tvDetails);
        Button btnAdd       = findViewById(R.id.btnAddToCart);
        Button btnBuy       = findViewById(R.id.btnBuyNow);

        // Nhận dữ liệu từ Intent
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        int imageRes = getIntent().getIntExtra("imageResId", R.drawable.ic_image_placeholder);

        tvName.setText(name != null ? name : "Product");
        tvPrice.setText(price != null ? price : "$0");
        img.setImageResource(imageRes);
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
