package com.example.fe.ui.seller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.fe.R;
import com.example.fe.models.Product;

import java.util.ArrayList;
import java.util.List;

public class SellerProductAdapter extends RecyclerView.Adapter<SellerProductAdapter.ViewHolder> {

	public interface OnProductActionListener {
		void onEdit(Product product);
		void onDelete(Product product);
	}

	private final List<Product> products = new ArrayList<>();
	private final Context context;
	private OnProductActionListener listener;

	public SellerProductAdapter(Context context) {
		this.context = context;
	}

	public void setOnProductActionListener(OnProductActionListener l) {
		this.listener = l;
	}

	public void setProducts(List<Product> items) {
		products.clear();
		if (items != null) products.addAll(items);
		notifyDataSetChanged();
	}

	public void removeProduct(String id) {
		for (int i = 0; i < products.size(); i++) {
			if (products.get(i).getId().equals(id)) {
				products.remove(i);
				notifyItemRemoved(i);
				return;
			}
		}
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller_product, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Product p = products.get(position);
		holder.tvName.setText(p.getName());
		holder.tvPrice.setText(String.format("%.2f", p.getPrice()));
		holder.tvStock.setText("Stock: " + p.getStockQuantity());

		if (p.getImages() != null && !p.getImages().isEmpty()) {
			String url = p.getImages().get(0);
			String finalUrl = url;
			// if the URL is not an absolute http(s) URL, prefix with API base URL
			// skip prefixing for data URIs (base64) which start with "data:"
			if (finalUrl != null && !finalUrl.startsWith("http") && !finalUrl.startsWith("data:")) {
				String base = com.example.fe.network.ApiClient.getBaseUrl();
				if (finalUrl.startsWith("/")) finalUrl = finalUrl.substring(1);
				if (!base.endsWith("/")) base = base + "/";
				finalUrl = base + finalUrl;
			}
			RequestOptions opts = new RequestOptions()
					.centerCrop()
					.placeholder(R.drawable.ic_placeholder)
					.error(R.drawable.ic_placeholder)
					.fallback(R.drawable.ic_placeholder)
					.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

			Glide.with(context)
					.load(finalUrl)
					.apply(opts)
					.into(holder.ivImage);
		} else {
			holder.ivImage.setImageResource(R.drawable.ic_placeholder);
		}

		holder.btnEdit.setOnClickListener(v -> {
			if (listener != null) listener.onEdit(p);
		});

		holder.btnDelete.setOnClickListener(v -> {
			if (listener != null) listener.onDelete(p);
		});
	}

	@Override
	public int getItemCount() {
		return products.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView ivImage;
		TextView tvName, tvPrice, tvStock;
		ImageButton btnEdit, btnDelete;

		ViewHolder(@NonNull View itemView) {
			super(itemView);
			ivImage = itemView.findViewById(R.id.ivProductImage);
			tvName = itemView.findViewById(R.id.tvProductName);
			tvPrice = itemView.findViewById(R.id.tvProductPrice);
			tvStock = itemView.findViewById(R.id.tvProductStock);
			btnEdit = itemView.findViewById(R.id.btnEditProduct);
			btnDelete = itemView.findViewById(R.id.btnDeleteProduct);
		}
	}
}
