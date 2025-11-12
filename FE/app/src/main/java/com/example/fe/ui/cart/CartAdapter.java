package com.example.fe.ui.cart;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fe.R;
import com.example.fe.ui.home.ProductModel;
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.utils.SessionManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<ProductModel> visibleItems;
    private final List<ProductModel> masterItems; // reference to full cart list in activity
    private final OnCartChangedListener listener;
    private static final String TAG = "CartAdapter";

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public CartAdapter(List<ProductModel> visibleItems, List<ProductModel> masterItems, OnCartChangedListener listener) {
        this.visibleItems = visibleItems;
        this.masterItems = masterItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel product = visibleItems.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        // Load image: prefer network imageUrl, fallback to drawable
        String imageUrl = product.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(product.getImageRes());
        }

        // set initial quantity from model
        holder.tvQuantity.setText(String.valueOf(product.getQuantity()));

        holder.btnMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                holder.tvQuantity.setText(String.valueOf(quantity));
                // update visible and master
                int pos = holder.getBindingAdapterPosition();
                if (pos >= 0 && pos < visibleItems.size()) {
                    ProductModel old = visibleItems.get(pos);
                    ProductModel replaced = new ProductModel(old.getName(), old.getPrice(), old.getImageUrl(), old.getImageRes(), old.getCategory(), old.getSku(), quantity, old.getUnitPrice());
                    replaced.setId(old.getId());
                    replaced.setCartItemId(old.getCartItemId());
                    visibleItems.set(pos, replaced);
                    // update master: find by cartItemId if exists otherwise try to match by name+unitPrice
                    syncToMaster(replaced);
                    notifyItemChanged(pos);
                    if (listener != null) listener.onCartChanged();
                }
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            quantity++;
            holder.tvQuantity.setText(String.valueOf(quantity));
            int pos = holder.getBindingAdapterPosition();
            if (pos >= 0 && pos < visibleItems.size()) {
                ProductModel old = visibleItems.get(pos);
                ProductModel replaced = new ProductModel(old.getName(), old.getPrice(), old.getImageUrl(), old.getImageRes(), old.getCategory(), old.getSku(), quantity, old.getUnitPrice());
                replaced.setId(old.getId());
                replaced.setCartItemId(old.getCartItemId());
                visibleItems.set(pos, replaced);
                syncToMaster(replaced);
                notifyItemChanged(pos);
                if (listener != null) listener.onCartChanged();
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            // If this item has server cartItemId and user logged in -> call API, otherwise remove locally
            SessionManager session = new SessionManager(holder.itemView.getContext());
            com.example.fe.data.UserData user = session.getUser();
            String cartItemId = product.getCartItemId();

            if (user != null && user.getId() != null && cartItemId != null && !cartItemId.isEmpty()) {
                ApiService api = ApiClient.getClient(holder.itemView.getContext()).create(ApiService.class);
                api.removeItemFromCart(user.getId(), cartItemId).enqueue(new Callback<com.example.fe.models.Cart>() {
                    @Override
                    public void onResponse(Call<com.example.fe.models.Cart> call, Response<com.example.fe.models.Cart> response) {
                        if (response.isSuccessful()) {
                            int pos = holder.getBindingAdapterPosition();
                            if (pos >= 0 && pos < visibleItems.size()) {
                                ProductModel removed = visibleItems.remove(pos);
                                notifyItemRemoved(pos);
                                // also remove from master
                                removeFromMaster(removed);
                                if (listener != null) listener.onCartChanged();
                            }
                            Toast.makeText(holder.itemView.getContext(), "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                        } else {
                            String err = "";
                            try {
                                if (response.errorBody() != null) err = response.errorBody().string();
                            } catch (IOException ioe) {
                                err = ioe.getMessage();
                            }
                            Log.e(TAG, "removeItem failed code=" + response.code() + " body=" + err);
                            Toast.makeText(holder.itemView.getContext(), "Xóa thất bại: " + response.code() + " - " + (err.isEmpty() ? "" : err), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.example.fe.models.Cart> call, Throwable t) {
                        Log.e(TAG, "removeItem onFailure: " + t.getMessage(), t);
                        Toast.makeText(holder.itemView.getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                int pos = holder.getBindingAdapterPosition();
                if (pos >= 0 && pos < visibleItems.size()) {
                    ProductModel removed = visibleItems.remove(pos);
                    notifyItemRemoved(pos);
                    removeFromMaster(removed);
                    if (listener != null) listener.onCartChanged();
                }
            }
        });
    }

    private void syncToMaster(ProductModel replaced) {
        if (masterItems == null) return;
        // try match by cartItemId
        if (replaced.getCartItemId() != null) {
            for (int i = 0; i < masterItems.size(); i++) {
                ProductModel m = masterItems.get(i);
                if (replaced.getCartItemId().equals(m.getCartItemId())) {
                    masterItems.set(i, replaced);
                    return;
                }
            }
        }
        // fallback: match by product id
        if (replaced.getId() != null) {
            for (int i = 0; i < masterItems.size(); i++) {
                ProductModel m = masterItems.get(i);
                if (replaced.getId() != null && replaced.getId().equals(m.getId())) {
                    masterItems.set(i, replaced);
                    return;
                }
            }
        }
        // last fallback: match by name + unit price
        for (int i = 0; i < masterItems.size(); i++) {
            ProductModel m = masterItems.get(i);
            if (m.getName().equals(replaced.getName()) && m.getUnitPrice() == replaced.getUnitPrice()) {
                masterItems.set(i, replaced);
                return;
            }
        }
    }

    private void removeFromMaster(ProductModel removed) {
        if (masterItems == null) return;
        // try cartItemId
        if (removed.getCartItemId() != null) {
            for (int i = 0; i < masterItems.size(); i++) {
                if (removed.getCartItemId().equals(masterItems.get(i).getCartItemId())) {
                    masterItems.remove(i);
                    return;
                }
            }
        }
        // try id
        if (removed.getId() != null) {
            for (int i = 0; i < masterItems.size(); i++) {
                if (removed.getId().equals(masterItems.get(i).getId())) {
                    masterItems.remove(i);
                    return;
                }
            }
        }
        // fallback by matching name+unitPrice
        for (int i = 0; i < masterItems.size(); i++) {
            ProductModel m = masterItems.get(i);
            if (m.getName().equals(removed.getName()) && m.getUnitPrice() == removed.getUnitPrice()) {
                masterItems.remove(i);
                return;
            }
        }
    }

    @Override
    public int getItemCount() {
        return visibleItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvQuantity;
        ImageButton btnMinus, btnPlus, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
