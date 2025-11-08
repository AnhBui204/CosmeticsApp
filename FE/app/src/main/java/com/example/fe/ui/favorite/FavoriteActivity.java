package com.example.fe.ui.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.example.fe.ui.category.CategoryActivity;
import com.example.fe.ui.home.HomeActivity;
import com.example.fe.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private FavoriteAdapter adapter;
    private List<Product> favoriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_favorite);

        ImageView imgCart = findViewById(R.id.imgCart);
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, com.example.fe.ui.cart.ShoppingCartActivity.class);
            startActivity(intent);
        });


        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.getMenu().findItem(R.id.nav_favourite).setChecked(true);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(FavoriteActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_categories) {
                    startActivity(new Intent(FavoriteActivity.this, CategoryActivity.class));
                    return true;
                } else if (id == R.id.nav_favourite) {
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(FavoriteActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });

        // Setup RecyclerView 2 cột
        rvFavorites = findViewById(R.id.rvFavorites);
        rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));

        // Sample favorite data
        favoriteList = new ArrayList<>();
        favoriteList.add(new Product("Combo Beauty", 4, 19.99, 12, R.drawable.img_promo_2));
        favoriteList.add(new Product("Face Cream", 5, 24.99, 20, R.drawable.img_deal_4));
        favoriteList.add(new Product("Face Cream", 3, 14.99, 15, R.drawable.img_moisturizer));
        favoriteList.add(new Product("Face Mask", 2, 29.99, 30, R.drawable.img_face_mask));
        favoriteList.add(new Product("Cushion", 1, 14.55, 6, R.drawable.img_deal_3));
        favoriteList.add(new Product("Perfume", 5, 23.55, 25, R.drawable.img_facewash));
        favoriteList.add(new Product("Combo Lipstick", 4, 55.50, 100, R.drawable.img_lipstick_red));

        adapter = new FavoriteAdapter(favoriteList);
        rvFavorites.setAdapter(adapter);

        // Search and sort can be handled later
        EditText etSearch = findViewById(R.id.etSearch);
        ImageView ivSort = findViewById(R.id.ivSort);
        ivSort.setOnClickListener(v -> Toast.makeText(FavoriteActivity.this,
                "Sort by price clicked", Toast.LENGTH_SHORT).show());
    }

    // Product model class
    static class Product {
        String name;
        int stars; // 0-5
        double price;
        int quantity;
        int imageRes;

        Product(String name, int stars, double price, int quantity, int imageRes) {
            this.name = name;
            this.stars = stars;
            this.price = price;
            this.quantity = quantity;
            this.imageRes = imageRes;
        }
    }

    // Adapter
    class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavViewHolder> {

        List<Product> list;

        FavoriteAdapter(List<Product> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_favorite, parent, false);
            return new FavViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
            Product p = list.get(position);
            holder.tvName.setText(p.name);
            holder.tvPrice.setText("$" + p.price);
            holder.tvQuantity.setText("Qty: " + p.quantity);
            holder.ivProduct.setImageResource(p.imageRes);

            // Star rating
            holder.ratingBar.setNumStars(5);
            holder.ratingBar.setRating(p.stars);

            // Add click listener
            holder.ivAdd.setOnClickListener(v ->
                    Toast.makeText(FavoriteActivity.this, p.name + " added", Toast.LENGTH_SHORT).show());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class FavViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct, ivAdd;
            TextView tvName, tvPrice, tvQuantity;
            RatingBar ratingBar;

            FavViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProduct = itemView.findViewById(R.id.ivProduct);
                ivAdd = itemView.findViewById(R.id.ivAdd);
                tvName = itemView.findViewById(R.id.tvProductName);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                ratingBar = itemView.findViewById(R.id.ratingBar);
            }
        }
    }
}
