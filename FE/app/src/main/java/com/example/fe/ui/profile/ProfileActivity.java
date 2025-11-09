package com.example.fe.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fe.LoginActivity;
import com.example.fe.MainActivity; // import MainActivity to route to fragment-hosting activity
import com.example.fe.R;
import com.example.fe.ui.category.CategoryActivity;
import com.example.fe.ui.favorite.FavoriteActivity;
import com.example.fe.ui.home.HomeActivity;
import com.example.fe.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_profile);
        SessionManager sessionManager = new SessionManager(this);

        TextView tvName = findViewById(R.id.tvName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        tvName.setText(sessionManager.getName());
        tvEmail.setText(sessionManager.getEmail());

        // Khởi tạo icon setting
        ivSettings = findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang ProfileSettingActivity
                startActivity(new Intent(ProfileActivity.this, ProfileSettingActivity.class));
            }
        });

        // --- NEW: My Order click listener ---
        View menuItemMyOrder = findViewById(R.id.menuItemMyOrder);
        if (menuItemMyOrder != null) {
            menuItemMyOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start MainActivity and instruct it to show MyOrdersFragment
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.putExtra("open_fragment", "orders");
                    // Reuse existing MainActivity if present
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
        }
        // --- END NEW ---
        View menuItemLogout = findViewById(R.id.menuItemLogout);
        menuItemLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Log out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            SessionManager sessionManager = new SessionManager(ProfileActivity.this);
                            sessionManager.clearSession();

                            // Quay về LoginActivity
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });


        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.getMenu().findItem(R.id.nav_profile).setChecked(true);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_categories) {
                    startActivity(new Intent(ProfileActivity.this, CategoryActivity.class));
                    return true;
                } else if (id == R.id.nav_favourite) {
                    startActivity(new Intent(ProfileActivity.this, FavoriteActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    return true;
                }
                return false;
            }
        });
    }
}
