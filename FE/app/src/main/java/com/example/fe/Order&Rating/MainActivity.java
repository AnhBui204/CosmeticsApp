package com.example.fe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Liên kết Activity này với file layout activity_main.xml
        setContentView(R.layout.activity_main);

        // 2. Chỉ tải Fragment khi Activity được tạo lần đầu tiên.
        // Nếu không có 'if (savedInstanceState == null)',
        // một Fragment mới sẽ được tạo chồng lên Fragment cũ mỗi khi xoay màn hình.
        if (savedInstanceState == null) {

            // 3. Tạo một instance của Fragment bạn muốn hiển thị
            Fragment myOrdersFragment = new MyOrdersFragment();

            // 4. Lấy trình quản lý Fragment (FragmentManager)
            FragmentManager fragmentManager = getSupportFragmentManager();

            // 5. Bắt đầu một "giao dịch" (transaction) để thêm, xóa, hoặc thay thế Fragment
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // 6. Thay thế nội dung của 'fragment_container'
            //    (đã định nghĩa trong activity_main.xml) bằng 'myOrdersFragment'
            fragmentTransaction.replace(R.id.fragment_container, myOrdersFragment);

            // 7. Hoàn tất (commit) giao dịch
            fragmentTransaction.commit();
        }

        // Nếu savedInstanceState không null, Android sẽ tự động
        // khôi phục Fragment từ trạng thái đã lưu trước đó.
    }
}