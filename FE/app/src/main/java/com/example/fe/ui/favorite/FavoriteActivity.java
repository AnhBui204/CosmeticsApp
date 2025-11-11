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
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.network.AddToWishlistRequest;
import com.example.fe.models.Product;
import com.example.fe.ui.category.CategoryActivity;
import com.example.fe.ui.home.HomeActivity;
import com.example.fe.ui.profile.ProfileActivity;
import com.example.fe.utils.SessionManager;
import com.example.fe.data.UserData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        favoriteList = new ArrayList<>();
        adapter = new FavoriteAdapter(favoriteList);
        rvFavorites.setAdapter(adapter);

        // load wishlist from server
        fetchWishlist();

        // Search and sort can be handled later
        EditText etSearch = findViewById(R.id.etSearch);
        ImageView ivSort = findViewById(R.id.ivSort);
        ivSort.setOnClickListener(v -> Toast.makeText(FavoriteActivity.this,
                "Sort by price clicked", Toast.LENGTH_SHORT).show());
    }

    private void fetchWishlist() {
        SessionManager session = new SessionManager(this);
        UserData user = session.getUser();
        if (user == null || user.getId() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem danh sách yêu thích", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getId();
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.getWishlist(userId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteList.clear();
                    favoriteList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FavoriteActivity.this, "Không thể tải danh sách yêu thích", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(FavoriteActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToWishlist(String productId, int position) {
        SessionManager session = new SessionManager(this);
        UserData user = session.getUser();
        if (user == null || user.getId() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        AddToWishlistRequest req = new AddToWishlistRequest(productId);
        api.addToWishlist(user.getId(), req).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    fetchWishlist();
                    Toast.makeText(FavoriteActivity.this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FavoriteActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(FavoriteActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromWishlist(String productId, int position) {
        SessionManager session = new SessionManager(this);
        UserData user = session.getUser();
        if (user == null || user.getId() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xóa yêu thích", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.removeFromWishlist(user.getId(), productId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    fetchWishlist();
                    Toast.makeText(FavoriteActivity.this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FavoriteActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(FavoriteActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adapter using backend Product model
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
            holder.tvName.setText(p.getName());
            holder.tvPrice.setText(String.format(Locale.US, "$%.2f", p.getSalePrice() != null ? p.getSalePrice() : p.getPrice()));
            holder.tvQuantity.setText("Qty: " + p.getStockQuantity());
            if (p.getImages() != null && !p.getImages().isEmpty()) {
                com.bumptech.glide.Glide.with(holder.itemView.getContext()).load(p.getImages().get(0)).into(holder.ivProduct);
            } else {
                holder.ivProduct.setImageResource(R.drawable.bg_welcome);
            }

            // Star rating if available
            Double avgObj = p.getRatingAverage();
            float avg = avgObj != null ? avgObj.floatValue() : 0f;
            holder.ratingBar.setNumStars(5);
            holder.ratingBar.setRating(avg);

            // favorite heart click -> remove
            holder.ivFavorite.setOnClickListener(v -> removeFromWishlist(p.getId(), position));

            // add to cart click -> existing behaviour
            holder.ivAdd.setOnClickListener(v -> Toast.makeText(FavoriteActivity.this, p.getName() + " added to cart (not implemented)", Toast.LENGTH_SHORT).show());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class FavViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct, ivAdd, ivFavorite;
            TextView tvName, tvPrice, tvQuantity;
            RatingBar ratingBar;

            FavViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProduct = itemView.findViewById(R.id.ivProduct);
                ivAdd = itemView.findViewById(R.id.ivAdd);
                ivFavorite = itemView.findViewById(R.id.ivFavorite);
                tvName = itemView.findViewById(R.id.tvProductName);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                ratingBar = itemView.findViewById(R.id.ratingBar);
            }
        }
    }
}
