package com.example.fe.ui.cart;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.R;
import com.example.fe.ui.home.ProductModel;
import com.example.fe.utils.SessionManager;
import com.example.fe.data.UserData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CustomerCheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_checkout);

        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvUserPhone = findViewById(R.id.tvUserPhone);
        TextView tvUserAddress = findViewById(R.id.tvUserAddress);
        TextView tvCheckoutSubtotal = findViewById(R.id.tvCheckoutSubtotal);
        TextView tvCheckoutDelivery = findViewById(R.id.tvCheckoutDelivery);
        TextView tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);
        RecyclerView rvCheckoutItems = findViewById(R.id.rvCheckoutItems);

        // get user from session manager
        SessionManager session = new SessionManager(this);

        final UserData user = session.getUser();
        if (user != null) {
            tvUserName.setText(user.getFullName() != null ? user.getFullName() : "-");
            tvUserPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "-");
            // address: try default address from session addresses (SessionManager stores addresses as JSON)
            if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
                tvUserAddress.setText(user.getAddresses().get(0).getStreet());
            } else {
                tvUserAddress.setText("-");
            }
        }

        // receive items from CartStore
        List<ProductModel> tempItems = CartStore.getCartItems();
        final List<ProductModel> items = (tempItems != null) ? tempItems : new ArrayList<>();


        CheckoutAdapter adapter = new CheckoutAdapter(items);
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setAdapter(adapter);

        double subtotal = 0.0;
        for (ProductModel pm : items) subtotal += pm.getUnitPrice() * pm.getQuantity();
        double delivery = (subtotal > 0) ? 2.00 : 0.00;
        final double total = subtotal + delivery;

        tvCheckoutSubtotal.setText(String.format("$%.2f", subtotal));
        tvCheckoutDelivery.setText(String.format("$%.2f", delivery));
        tvCheckoutTotal.setText(String.format("$%.2f", total));

        findViewById(R.id.btnPay).setOnClickListener(v -> {
            if (items.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống, không thể thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }
            createPayOSOrder(user, items, total);
        });


    }
    private void createPayOSOrder(UserData currentUser, List<ProductModel> items, double totalAmount) {
        new Thread(() -> {
            try {
                URL url = new URL("https://leisureless-yasmin-inappreciatively.ngrok-free.dev/api/payment/create-order");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // Gom thông tin order
                JSONObject json = new JSONObject();
                json.put("amount", (int) totalAmount); // tổng tiền
                json.put("orderId", "ORDER_" + System.currentTimeMillis());
                json.put("userId", currentUser.getId());
                json.put("paymentMethod", "payos");
                json.put("shippingAddress", getUserAddress(currentUser));

                // Có thể gửi danh sách sản phẩm (nếu backend cần)
                // JSONArray productArray = new JSONArray();
                // for (ProductModel p : items) {
                //     JSONObject prod = new JSONObject();
                //     prod.put("name", p.getProductName());
                //     prod.put("quantity", p.getQuantity());
                //     prod.put("price", p.getUnitPrice());
                //     productArray.put(prod);
                // }
                // json.put("items", productArray);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes("UTF-8"));
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) response.append(line);
                    in.close();

                    JSONObject result = new JSONObject(response.toString());
                    String checkoutUrl = result.getString("url");

                    runOnUiThread(() -> openPaymentUrl(checkoutUrl));
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Tạo đơn PayOS thất bại", Toast.LENGTH_SHORT).show()
                    );
                }
                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private String getUserAddress(UserData user) {
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            UserData.Address addr = user.getAddresses().get(0);
            return addr.getStreet() + ", " + addr.getWard() + ", " +
                    addr.getDistrict() + ", " + addr.getCity();
        }
        return "Không có địa chỉ";
    }

    private void openPaymentUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Không tìm thấy trình duyệt", Toast.LENGTH_SHORT).show();
        }
    }

}
