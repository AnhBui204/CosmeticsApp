package com.example.fe.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fe.MainActivity; // import MainActivity to route to fragment-hosting activity
import com.example.fe.R;
import com.example.fe.ui.category.CategoryActivity;
import com.example.fe.ui.favorite.FavoriteActivity;
import com.example.fe.ui.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.ImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_profile);



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
