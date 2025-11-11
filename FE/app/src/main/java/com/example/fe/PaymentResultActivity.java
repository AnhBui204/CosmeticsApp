package com.example.fe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaymentResultActivity extends AppCompatActivity {
    private ImageView imgStatus;
    private TextView tvMessage;
    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_result); // set layout trước

        imgStatus = findViewById(R.id.imgPaymentStatus);
        tvMessage = findViewById(R.id.tvPaymentMessage);
        btnDone = findViewById(R.id.btnPaymentDone);

        btnDone.setOnClickListener(v -> finish()); // quay về màn hình trước

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && "myapp".equals(uri.getScheme())) {
            String status = uri.getQueryParameter("status"); // "success" hoặc "cancel"
            String orderCode = uri.getQueryParameter("orderCode");

            if (orderCode != null && status != null) {
                updateOrderOnServer(orderCode, status);
            }

            if ("success".equals(status)) {
                tvMessage.setText("✅ Thanh toán thành công!");
                tvMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvMessage.setText("❌ Thanh toán thất bại!");
                tvMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }


            btnDone.setVisibility(android.view.View.VISIBLE); // hiện nút quay về
        }
    }

    private void updateOrderOnServer(String orderCode, String status) {
        new Thread(() -> {
            try {
                System.out.println("Sending updateOrder request: orderCode=" + orderCode + ", status=" + status);
                URL url = new URL("https://leisureless-yasmin-inappreciatively.ngrok-free.dev/api/payment/update-order");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("orderCode", orderCode);
                json.put("status", status);

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
                    System.out.println("Order update response: " + response);
                } else {
                    System.out.println("Failed to update order, code: " + responseCode);
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
