package com.example.fe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.Button;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class IntroActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearLayout indicatorLayout;
    private int[] images = {
            R.drawable.intro1,
            R.drawable.intro2,
            R.drawable.intro3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        MaterialButton btnShoppingNow = findViewById(R.id.btnShoppingNow);

        // Gắn adapter cho ViewPager
        viewPager.setAdapter(new IntroAdapter(images));

        // Tạo chấm indicator
        setupIndicators(images.length);
        setCurrentIndicator(0);

        // Lắng nghe khi chuyển trang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
            }
        });

        // 👉 Khi click vào nút "Shopping now"
        btnShoppingNow.setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng IntroActivity để không quay lại khi nhấn back
        });
    }

    private void setupIndicators(int count) {
        ImageView[] dots = new ImageView[count];
        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.indicator_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            indicatorLayout.addView(dots[i], params);
        }
    }

    private void setCurrentIndicator(int index) {
        int count = indicatorLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView dot = (ImageView) indicatorLayout.getChildAt(i);
            if (i == index) {
                dot.setImageResource(R.drawable.indicator_active);
            } else {
                dot.setImageResource(R.drawable.indicator_inactive);
            }
        }
    }
}
