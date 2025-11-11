package com.example.fe.ui.seller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.app.Dialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fe.R;
import com.example.fe.models.Product;
import com.example.fe.models.Category;
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductEditActivity extends AppCompatActivity {

	private static final int REQUEST_CODE_PICK_IMAGES = 1001;

	private EditText etName, etSku, etDescription, etPrice, etSalePrice, etStock, etBrand;
	private Spinner spinnerCategory;
	private Button btnPickImages, btnSaveProduct;
	private ImageButton btnBack;
	private TextView tvTitle;
	private LinearLayout llImagesContainer;

	private final List<Uri> selectedUris = new ArrayList<>();
	private final List<String> imagesDataUrls = new ArrayList<>();
	// the currently-displayed ordered list of images (may contain existing URLs or newly-picked data: URLs)
	private final List<String> displayedImages = new ArrayList<>();
	// keep existing images from product when editing (initial source)
	private final List<String> existingImages = new ArrayList<>();

	private ApiService apiService;
	private boolean isEdit = false;
	private String editingProductId = null;
	private final List<Category> categories = new ArrayList<>();
	private String editingCategoryIdToSelect = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_edit);

		etName = findViewById(R.id.etName);
		etSku = findViewById(R.id.etSku);
		etDescription = findViewById(R.id.etDescription);
		etPrice = findViewById(R.id.etPrice);
		etSalePrice = findViewById(R.id.etSalePrice);
		etStock = findViewById(R.id.etStock);
		etBrand = findViewById(R.id.etBrand);
		spinnerCategory = findViewById(R.id.spinnerCategory);

		tvTitle = findViewById(R.id.tvTitle);

		btnPickImages = findViewById(R.id.btnPickImages);
		btnSaveProduct = findViewById(R.id.btnSaveProduct);
		btnBack = findViewById(R.id.btnBack);
		llImagesContainer = findViewById(R.id.llImagesContainer);

		apiService = ApiClient.getClient().create(ApiService.class);

		loadCategories();

		btnBack.setOnClickListener(v -> finish());

		// set header title depending on create vs edit mode
		String productId = getIntent().getStringExtra("productId");
		if (productId != null && !productId.isEmpty()) {
			isEdit = true;
			editingProductId = productId;
			// show Edit header
			tvTitle.setText("Edit Product");
			// load product data (populateFields will run after categories load)
			loadProduct(productId);
		} else {
			// create mode
			isEdit = false;
			tvTitle.setText("Add a product");
		}
		btnPickImages.setOnClickListener(v -> openImagePicker());

		btnSaveProduct.setOnClickListener(v -> saveProduct());
	}

	private int dpToPx(int dp) {
		float density = getResources().getDisplayMetrics().density;
		return (int) (dp * density + 0.5f);
	}

	private void openImagePicker() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		startActivityForResult(Intent.createChooser(intent, "Select Images"), REQUEST_CODE_PICK_IMAGES);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
			// clear only newly-picked state; keep existingImages until user removes them
			selectedUris.clear();
			imagesDataUrls.clear();
			displayedImages.clear();

			ClipData clip = data.getClipData();
			if (clip != null) {
				for (int i = 0; i < clip.getItemCount(); i++) {
					Uri uri = clip.getItemAt(i).getUri();
					if (uri != null) handlePickedUri(uri);
				}
			} else {
				Uri uri = data.getData();
				if (uri != null) handlePickedUri(uri);
			}

			// when picking new images we want to show newly-picked ones (replace preview),
			// but preserve existing images as part of the overall saved list unless removed by the user.
			// We'll show existingImages first (if any) then appended picked images.
			if (!existingImages.isEmpty()) displayedImages.addAll(existingImages);
			// imagesDataUrls were populated by handlePickedUri
			if (!imagesDataUrls.isEmpty()) displayedImages.addAll(imagesDataUrls);

			refreshImagesDisplay();
		}
	}

	private void handlePickedUri(Uri uri) {
		selectedUris.add(uri);
		// convert to base64 data URL (compress to JPEG)
		try {
			InputStream is = getContentResolver().openInputStream(uri);
			Bitmap bmp = BitmapFactory.decodeStream(is);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			byte[] bytes = baos.toByteArray();
			String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
			String dataUrl = "data:image/jpeg;base64," + base64;
			imagesDataUrls.add(dataUrl);
			// also add to display list so user can reorder/remove immediately
			displayedImages.add(dataUrl);
			refreshImagesDisplay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Redraw the thumbnails from displayedImages. Each thumbnail has controls to remove or move left/right,
	 * and a tap opens the full-size preview.
	 */
	private void refreshImagesDisplay() {
		llImagesContainer.removeAllViews();
		for (int i = 0; i < displayedImages.size(); i++) {
			final int idx = i;
			String img = displayedImages.get(i);
			// FrameLayout to overlay controls
			FrameLayout container = new FrameLayout(this);
			int thumbSize = dpToPx(120); // dp
			int outerMargin = dpToPx(6);
			LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(thumbSize, thumbSize);
			flp.setMargins(outerMargin, outerMargin, outerMargin, outerMargin);
			container.setLayoutParams(flp);
			// give a little inner padding so overlay controls are fully visible
			container.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
			container.setClipToPadding(false);

			ImageView iv = new ImageView(this);
			FrameLayout.LayoutParams ivLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			iv.setLayoutParams(ivLp);
			iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

			String loadUrl;
			if (img != null && (img.startsWith("http") || img.startsWith("data:"))) {
				loadUrl = img;
			} else {
				loadUrl = ApiClient.getBaseUrl() + img;
			}
			Glide.with(this).load(loadUrl).into(iv);

			// open full image on tap
			iv.setOnClickListener(v -> openFullImageDialog(loadUrl));

			// Remove button (top-right)
			Button btnRemove = new Button(this);
			btnRemove.setText("✕");
			btnRemove.setTextSize(12f);
			int btnSize = dpToPx(36);
			FrameLayout.LayoutParams remLp = new FrameLayout.LayoutParams(btnSize, btnSize, Gravity.TOP | Gravity.END);
			remLp.setMargins(0, dpToPx(2), dpToPx(2), 0);
			btnRemove.setLayoutParams(remLp);
			btnRemove.setMinWidth(0);
			btnRemove.setMinHeight(0);
			btnRemove.setPadding(0,0,0,0);
			btnRemove.setBackgroundColor(0x66FFFFFF);
			btnRemove.setOnClickListener(v -> {
				displayedImages.remove(idx);
				// keep imagesDataUrls in sync: remove any matching data urls
				imagesDataUrls.removeIf(s -> s.equals(img));
				// also update existingImages if we removed an existing one
				existingImages.removeIf(s -> s.equals(img));
				refreshImagesDisplay();
			});

			// Move left button (top-left)
			Button btnLeft = new Button(this);
			btnLeft.setText("◀");
			btnLeft.setTextSize(12f);
			FrameLayout.LayoutParams leftLp = new FrameLayout.LayoutParams(btnSize, btnSize, Gravity.TOP | Gravity.START);
			leftLp.setMargins(dpToPx(2), dpToPx(2), 0, 0);
			btnLeft.setLayoutParams(leftLp);
			btnLeft.setMinWidth(0);
			btnLeft.setMinHeight(0);
			btnLeft.setPadding(0,0,0,0);
			btnLeft.setBackgroundColor(0x55FFFFFF);
			btnLeft.setOnClickListener(v -> {
				if (idx > 0) {
					String tmp = displayedImages.get(idx - 1);
					displayedImages.set(idx - 1, displayedImages.get(idx));
					displayedImages.set(idx, tmp);
					refreshImagesDisplay();
				}
			});

			// Move right button (bottom-right)
			Button btnRight = new Button(this);
			btnRight.setText("▶");
			btnRight.setTextSize(12f);
			FrameLayout.LayoutParams rightLp = new FrameLayout.LayoutParams(btnSize, btnSize, Gravity.BOTTOM | Gravity.END);
			rightLp.setMargins(0,0,dpToPx(2), dpToPx(2));
			btnRight.setLayoutParams(rightLp);
			btnRight.setMinWidth(0);
			btnRight.setMinHeight(0);
			btnRight.setPadding(0,0,0,0);
			btnRight.setBackgroundColor(0x55FFFFFF);
			btnRight.setOnClickListener(v -> {
				if (idx < displayedImages.size() - 1) {
					String tmp = displayedImages.get(idx + 1);
					displayedImages.set(idx + 1, displayedImages.get(idx));
					displayedImages.set(idx, tmp);
					refreshImagesDisplay();
				}
			});

			container.addView(iv);
			container.addView(btnRemove);
			container.addView(btnLeft);
			container.addView(btnRight);

			llImagesContainer.addView(container);
		}
	}

	private void openFullImageDialog(String imageUrl) {
		Dialog d = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		ImageView iv = new ImageView(this);
		iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
		Glide.with(this).load(imageUrl).into(iv);
		iv.setOnClickListener(v -> d.dismiss());
		d.setContentView(iv);
		d.show();
	}

	private void saveProduct() {
		String name = etName.getText().toString().trim();
		if (name.isEmpty()) {
			Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
			return;
		}

		Map<String, Object> body = new HashMap<>();
		body.put("name", name);
		body.put("sku", etSku.getText().toString().trim());
		body.put("description", etDescription.getText().toString().trim());
		try {
			String p = etPrice.getText().toString().trim();
			if (!p.isEmpty()) body.put("price", Double.parseDouble(p));
		} catch (Exception ignored) {
		}
		try {
			String sp = etSalePrice.getText().toString().trim();
			if (!sp.isEmpty()) body.put("salePrice", Double.parseDouble(sp));
		} catch (Exception ignored) {
		}
		try {
			String sq = etStock.getText().toString().trim();
			if (!sq.isEmpty()) body.put("stockQuantity", Integer.parseInt(sq));
		} catch (Exception ignored) {
		}
		body.put("brand", etBrand.getText().toString().trim());
		// get selected category id from spinner
		if (spinnerCategory != null && !categories.isEmpty()) {
			int pos = spinnerCategory.getSelectedItemPosition();
			if (pos >= 0 && pos < categories.size()) {
				body.put("category", categories.get(pos).getId());
			}
		}

		// Use the current displayedImages (ordered) as the source of truth for what the user wants saved.
		if (!displayedImages.isEmpty()) {
			List<String> dedup = new ArrayList<>(new LinkedHashSet<>(displayedImages));
			body.put("images", dedup);
		}

		// convert body map to Product via Gson so ApiService signature matches
		Gson gson = new Gson();
		String json = gson.toJson(body);
		Product productBody = gson.fromJson(json, Product.class);

		if (isEdit && editingProductId != null) {
			apiService.updateProduct(editingProductId, productBody).enqueue(new Callback<Product>() {
				@Override
				public void onResponse(Call<Product> call, Response<Product> response) {
					if (response.isSuccessful()) {
						Toast.makeText(ProductEditActivity.this, "Product updated", Toast.LENGTH_SHORT).show();
						setResult(RESULT_OK);
						finish();
					} else {
						Toast.makeText(ProductEditActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFailure(Call<Product> call, Throwable t) {
					Toast.makeText(ProductEditActivity.this, "Network error", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			apiService.createProduct(productBody).enqueue(new Callback<Product>() {
				@Override
				public void onResponse(Call<Product> call, Response<Product> response) {
					if (response.isSuccessful()) {
						Toast.makeText(ProductEditActivity.this, "Product created", Toast.LENGTH_SHORT).show();
						setResult(RESULT_OK);
						finish();
					} else {
						Toast.makeText(ProductEditActivity.this, "Create failed", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFailure(Call<Product> call, Throwable t) {
					Toast.makeText(ProductEditActivity.this, "Network error", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	private void loadProduct(String productId) {
		apiService.getProductById(productId).enqueue(new Callback<Product>() {
			@Override
			public void onResponse(Call<Product> call, Response<Product> response) {
				if (response.isSuccessful() && response.body() != null) {
					populateFields(response.body());
				}
			}

			@Override
			public void onFailure(Call<Product> call, Throwable t) {
				Toast.makeText(ProductEditActivity.this, "Failed to load product", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void populateFields(Product p) {
		etName.setText(p.getName());
		etSku.setText(p.getSku());
		etDescription.setText(p.getDescription());
		etPrice.setText(String.valueOf(p.getPrice()));
		if (p.getSalePrice() != null) etSalePrice.setText(String.valueOf(p.getSalePrice()));
		etStock.setText(String.valueOf(p.getStockQuantity()));
		etBrand.setText(p.getBrand());

		List<String> imgs = p.getImages();
		if (imgs != null) {
			existingImages.clear();
			existingImages.addAll(imgs);
			// set displayed images ordered initially from existing images
			displayedImages.clear();
			displayedImages.addAll(existingImages);
			refreshImagesDisplay();
		}

		// set spinner selection to product's category if available
		String catId = p.getCategoryId();
		if (catId != null) {
			// if categories already loaded, select; otherwise save to select later
			if (!categories.isEmpty()) {
				for (int i = 0; i < categories.size(); i++) {
					if (catId.equals(categories.get(i).getId())) {
						spinnerCategory.setSelection(i);
						break;
					}
				}
			} else {
				editingCategoryIdToSelect = catId;
			}
		}
	}

	private void loadCategories() {
		apiService.getCategories().enqueue(new Callback<java.util.List<Category>>() {
			@Override
			public void onResponse(Call<java.util.List<Category>> call, Response<java.util.List<Category>> response) {
				if (response.isSuccessful() && response.body() != null) {
					categories.clear();
					categories.addAll(response.body());
					java.util.List<String> names = new java.util.ArrayList<>();
					for (Category c : categories) names.add(c.getName());
					ArrayAdapter<String> adapter = new ArrayAdapter<>(ProductEditActivity.this, android.R.layout.simple_spinner_item, names);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerCategory.setAdapter(adapter);

					// if product was loaded earlier and wants a selection, apply it
					if (editingCategoryIdToSelect != null) {
						for (int i = 0; i < categories.size(); i++) {
							if (editingCategoryIdToSelect.equals(categories.get(i).getId())) {
								spinnerCategory.setSelection(i);
								editingCategoryIdToSelect = null;
								break;
							}
						}
					}
				}
			}

			@Override
			public void onFailure(Call<java.util.List<Category>> call, Throwable t) {
				// silently fail - categories optional
			}
		});
	}
}
