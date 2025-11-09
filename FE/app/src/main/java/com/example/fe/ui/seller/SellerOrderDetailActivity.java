package com.example.fe.ui.seller;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import android.content.res.ColorStateList;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.example.fe.R;
import com.example.fe.ui.home.ProductModel;
import android.util.Log;
import android.widget.RatingBar;
import com.example.fe.models.feedback.Feedback;
import com.example.fe.models.feedback.FeedbackRepository;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SellerOrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderId, tvOrderDate, tvTracking, tvCustomerName, tvQuantity, tvSubtotal, tvStatus;
    private RecyclerView recyclerProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_order_detail);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hiển thị nút back
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Sự kiện click back
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // TextViews và RecyclerView
        tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvTracking = findViewById(R.id.tv_tracking_number);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvQuantity = findViewById(R.id.tv_quantity_total);
        tvSubtotal = findViewById(R.id.tv_subtotal_total);
        tvStatus = findViewById(R.id.tv_order_status);
        recyclerProducts = findViewById(R.id.recycler_products);

        // Lấy dữ liệu từ intent
        String orderId = getIntent().getStringExtra("order_id");
        String date = getIntent().getStringExtra("order_date");
        String tracking = getIntent().getStringExtra("tracking");
        String customer = getIntent().getStringExtra("customer");
        String status = getIntent().getStringExtra("status");
        double subtotal = getIntent().getDoubleExtra("subtotal", 0.0);
        int quantity = getIntent().getIntExtra("quantity", 0);

        // Hiển thị dữ liệu
        tvOrderId.setText("Order #: " + orderId);
        tvOrderDate.setText("Date: " + date);
        tvTracking.setText("Tracking: " + tracking);
        tvCustomerName.setText("Customer: " + customer);
        tvSubtotal.setText(String.format(Locale.US, "Subtotal: $%.2f", subtotal));
        tvQuantity.setText("Total Quantity: " + quantity);
        tvStatus.setText("Status: " + status);

        // Set màu cho status
        if (status != null) {
            switch (status.toUpperCase()) {
                case "PENDING":
                    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.orange_500));
                    break;
                case "DELIVERED":
                    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.teal_700));
                    break;
                case "CANCELLED":
                    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red_500));
                    break;
            }
        }

        // Dữ liệu mẫu sản phẩm (sku, qty, unitPrice)
        List<ProductModel> products = new ArrayList<>();
        products.add(new ProductModel("Lipstick Red", "$55.00", R.drawable.img_deal_4, null, "LIP-RED-01", 2, 55.00));
        products.add(new ProductModel("Moisturizer", "$60.00", R.drawable.img_moisturizer, null, "MOIS-002", 1, 60.00));

        // Set product count in header
        TextView tvProductsCount = findViewById(R.id.tv_products_count);
        if (tvProductsCount != null) tvProductsCount.setText("(" + products.size() + ")");

        // compute subtotal from products (in case passed subtotal is stale)
        double computedSubtotal = 0.0;
        int computedQty = 0;
        for (ProductModel pm : products) {
            int q = pm.getQuantity() <= 0 ? 1 : pm.getQuantity();
            computedQty += q;
            computedSubtotal += (pm.getUnitPrice() > 0 ? pm.getUnitPrice() : 0.0) * q;
        }

        // Update totals shown
        tvSubtotal.setText(String.format(Locale.US, "Subtotal: $%.2f", computedSubtotal));
        tvQuantity.setText("Total Quantity: " + computedQty);

        // Color the status pill background according to status (use exact hex)
        if (status != null) {
            int color;
            switch (status.trim().toUpperCase()) {
                case "PENDING":
                    color = Color.parseColor("#CF6212");
                    break;
                case "DELIVERED":
                    color = Color.parseColor("#009254");
                    break;
                case "CANCELLED":
                    color = Color.parseColor("#C50000");
                    break;
                default:
                    color = Color.parseColor("#7A7A7A");
            }
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(color));
            tvStatus.setTextColor(Color.WHITE);
        }

        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        FeedbackRepository repo = new FeedbackRepository();
        List<Feedback> feedbacks = repo.getByOrderId(orderId); // optional pre-filter

        boolean isDeliveredFlag = status != null && status.trim().equalsIgnoreCase("DELIVERED");
        recyclerProducts.setAdapter(new ProductAdapter(products, repo, orderId, isDeliveredFlag));
        // add simple divider between product items
        recyclerProducts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Return Home button
        MaterialButton btnReturn = findViewById(R.id.btn_return_home);
        if (btnReturn != null) btnReturn.setOnClickListener(v -> finish());

        // old order-level feedback card kept (but product-level feedbacks are displayed inside each item)
        View cardFeedback = findViewById(R.id.card_feedback);
        if (cardFeedback != null) cardFeedback.setVisibility(View.GONE);
    }

    // Adapter cho danh sách sản phẩm
    static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductVH> {
        private final List<ProductModel> data;
        private final FeedbackRepository repo;
        private final String orderId;
        private final boolean isDelivered;

        ProductAdapter(List<ProductModel> data, FeedbackRepository repo, String orderId, boolean isDelivered) {
            this.data = data;
            this.repo = repo;
            this.orderId = orderId;
            this.isDelivered = isDelivered;
        }

        @Override
        @NonNull
        public ProductVH onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_seller_order_product, parent, false);
            return new ProductVH(view);
        }

        @Override
        public void onBindViewHolder(ProductVH holder, int position) {
            try {
                ProductModel p = data.get(position);
                if (holder.tvName != null) holder.tvName.setText(p.getName());
                // SKU and unit price/quantity (safe null-checks)
                if (holder.tvSku != null) holder.tvSku.setText("SKU: " + (p.getSku() == null ? "" : p.getSku()));
                int qty = p.getQuantity() <= 0 ? 1 : p.getQuantity();
                if (holder.tvQuantity != null) holder.tvQuantity.setText(String.format(Locale.US, "Qty: %d", qty));
                if (holder.tvUnitPrice != null) holder.tvUnitPrice.setText(String.format(Locale.US, "$%.2f", p.getUnitPrice() > 0 ? p.getUnitPrice() : 0.0));
                double line = (p.getUnitPrice() > 0 ? p.getUnitPrice() : 0.0) * qty;
                if (holder.tvLineTotal != null) holder.tvLineTotal.setText(String.format(Locale.US, "$%.2f", line));
                if (holder.img != null) holder.img.setImageResource(p.getImage());

                // Bind product-level feedback if order delivered
                if (isDelivered) {
                    // find feedback matching orderId + productSku
                    Feedback found = repo == null ? null : repo.findForOrderProduct(orderId, p.getSku());
                    View feedbackLayout = holder.itemView.findViewById(R.id.layout_product_feedback);
                    RatingBar rb = holder.itemView.findViewById(R.id.rating_product_feedback);
                    TextView tvComment = holder.itemView.findViewById(R.id.tv_product_feedback_comment);
                    if (found != null) {
                        if (feedbackLayout != null) feedbackLayout.setVisibility(View.VISIBLE);
                        if (rb != null) rb.setRating(found.getRating());
                        if (tvComment != null) tvComment.setText(found.getComment());
                    } else {
                        if (feedbackLayout != null) feedbackLayout.setVisibility(View.GONE);
                    }
                } else {
                    View feedbackLayout = holder.itemView.findViewById(R.id.layout_product_feedback);
                    if (feedbackLayout != null) feedbackLayout.setVisibility(View.GONE);
                }
            } catch (Exception ex) {
                Log.e("SellerOrderDetail", "Error binding product at position " + position, ex);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ProductVH extends RecyclerView.ViewHolder {
            ImageView img;
            TextView tvName, tvQuantity, tvSku, tvUnitPrice, tvLineTotal;

            ProductVH(android.view.View v) {
                super(v);
                img = v.findViewById(R.id.img_product);
                tvName = v.findViewById(R.id.tv_product_name);
                tvQuantity = v.findViewById(R.id.tv_product_quantity);
                tvSku = v.findViewById(R.id.tv_product_sku);
                tvUnitPrice = v.findViewById(R.id.tv_unit_price);
                tvLineTotal = v.findViewById(R.id.tv_line_total);
            }
        }
    }
}
