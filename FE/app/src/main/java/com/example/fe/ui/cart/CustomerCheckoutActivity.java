package com.example.fe.ui.cart;

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
        UserData user = session.getUser();
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
        List<ProductModel> items = CartStore.getCartItems();
        if (items == null) items = new ArrayList<>();

        CheckoutAdapter adapter = new CheckoutAdapter(items);
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setAdapter(adapter);

        double subtotal = 0.0;
        for (ProductModel pm : items) subtotal += pm.getUnitPrice() * pm.getQuantity();
        double delivery = 2.00;
        double total = subtotal + delivery;

        tvCheckoutSubtotal.setText(String.format("$%.2f", subtotal));
        tvCheckoutDelivery.setText(String.format("$%.2f", delivery));
        tvCheckoutTotal.setText(String.format("$%.2f", total));

        findViewById(R.id.btnPay).setOnClickListener(v -> Toast.makeText(this, "Pay - not implemented yet", Toast.LENGTH_SHORT).show());
    }
}
