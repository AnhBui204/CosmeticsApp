package com.example.fe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final String EXTRA_OPEN_FRAGMENT = "open_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Liên kết Activity này với file layout activity_main.xml
        setContentView(R.layout.activity_main);

        // If launched with intent asking to open orders, do it; else keep default behavior
        Intent intent = getIntent();
        boolean openOrders = intent != null && "orders".equals(intent.getStringExtra(EXTRA_OPEN_FRAGMENT));

        // 2. Chỉ tải Fragment khi Activity được tạo lần đầu tiên.
        if (savedInstanceState == null) {
            // 3. Tạo một instance của Fragment bạn muốn hiển thị
            Fragment fragmentToShow;
            if (openOrders) {
                fragmentToShow = new MyOrdersFragment();
            } else {
                // Default fragment if your app expects different default, use home or existing fragment
                fragmentToShow = new MyOrdersFragment(); // keep current default as MyOrdersFragment for now
            }

            // 4. Lấy trình quản lý Fragment (FragmentManager)
            FragmentManager fragmentManager = getSupportFragmentManager();

            // 5. Bắt đầu một "giao dịch" (transaction) để thêm, xóa, hoặc thay thế Fragment
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // 6. Thay thế nội dung của 'fragment_container'
            //    (đã định nghĩa trong activity_main.xml) bằng 'myOrdersFragment'
            fragmentTransaction.replace(R.id.fragment_container, fragmentToShow);

            // 7. Hoàn tất (commit) giao dịch
            fragmentTransaction.commit();
        }

        // Nếu savedInstanceState không null, Android sẽ tự động
        // khôi phục Fragment từ trạng thái đã lưu trước đó.
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // update activity intent

        boolean openOrders = intent != null && "orders".equals(intent.getStringExtra(EXTRA_OPEN_FRAGMENT));
        if (openOrders) {
            // Replace current container with MyOrdersFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new MyOrdersFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}