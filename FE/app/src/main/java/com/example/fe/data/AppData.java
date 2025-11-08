package com.example.fe.data;

import com.example.fe.R;
import com.example.fe.ui.category.Category;
import com.example.fe.ui.home.ProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppData {
    public static final List<Category> categoryList = new ArrayList<>();
    public static final List<ProductModel> productList = new ArrayList<>();

    static {
        // Categories
        categoryList.add(new Category("Moisturizing Cream", "Hydrate and nourish your skin", "C01", R.drawable.img_moisturizer));
        categoryList.add(new Category("Lipstick", "Long-lasting and vibrant colors", "C02", R.drawable.img_lipstick_red));
        categoryList.add(new Category("Face Mask", "Revitalize your complexion", "C03", R.drawable.img_face_mask));
        categoryList.add(new Category("Sunscreen", "Protect your skin from UV rays", "C04", R.drawable.img_promo_2));
        categoryList.add(new Category("Perfume", "A scent that lasts all day", "C05", R.drawable.img_deal_3));

        // C01 — Moisturizing Cream
        productList.add(new ProductModel("Kem Dưỡng Ẩm Ban Ngày", "$18", R.drawable.img_moisturizer, getCategoryById("C01")));
        productList.add(new ProductModel("Kem Dưỡng Ẩm Ban Đêm", "$22", R.drawable.img_moisturizer, getCategoryById("C01")));
        productList.add(new ProductModel("Gel Dưỡng Da Aloe Vera", "$14", R.drawable.img_moisturizer, getCategoryById("C01")));

        // C02 — Lipstick
        productList.add(new ProductModel("Son Môi Đỏ Classic", "$15", R.drawable.img_lipstick_red, getCategoryById("C02")));
        productList.add(new ProductModel("Son Hồng Pastel", "$16", R.drawable.img_lipstick_red, getCategoryById("C02")));
        productList.add(new ProductModel("Son Nude Matte", "$18", R.drawable.img_lipstick_red, getCategoryById("C02")));

        // C03 — Face Mask
        productList.add(new ProductModel("Mặt Nạ Dưỡng Da Vitamin C", "$12", R.drawable.img_face_mask, getCategoryById("C03")));
        productList.add(new ProductModel("Mặt Nạ Collagen", "$14", R.drawable.img_face_mask, getCategoryById("C03")));
        productList.add(new ProductModel("Clay Mask Giảm Dầu Nhờn", "$10", R.drawable.img_face_mask, getCategoryById("C03")));

        // C04 — Sunscreen
        productList.add(new ProductModel("Kem Chống Nắng SPF50+", "$20", R.drawable.img_promo_2, getCategoryById("C04")));
        productList.add(new ProductModel("Sữa Chống Nắng SPF35", "$18", R.drawable.img_promo_2, getCategoryById("C04")));
        productList.add(new ProductModel("Xịt Chống Nắng Body", "$22", R.drawable.img_promo_2, getCategoryById("C04")));

        // C05 — Perfume
        productList.add(new ProductModel("Nước Hoa Hương Hoa Hồng", "$30", R.drawable.img_deal_3, getCategoryById("C05")));
        productList.add(new ProductModel("Nước Hoa Gỗ Trầm Nam Tính", "$35", R.drawable.img_deal_3, getCategoryById("C05")));
        productList.add(new ProductModel("Nước Hoa Mùi Trái Cây Tươi Mát", "$28", R.drawable.img_deal_3, getCategoryById("C05")));

    }

    public static Category getCategoryById(String id) {
        if (id == null) return null;
        for (Category c : categoryList) if (id.equals(c.getCategoryID())) return c;
        return null;
    }

    // ----- Helpers tiện dụng -----

    /** Lấy sản phẩm theo Category ID */
    public static List<ProductModel> getProductsByCategory(String categoryId) {
        if (categoryId == null) return new ArrayList<>(productList);
        List<ProductModel> out = new ArrayList<>();
        for (ProductModel p : productList) {
            if (p.getCategory() != null && categoryId.equals(p.getCategory().getCategoryID())) {
                out.add(p);
            }
        }
        return out;
    }

    /** Sort theo giá tăng dần (return bản copy) */
    public static List<ProductModel> getProductsByCategoryPriceAsc(String categoryId) {
        List<ProductModel> out = getProductsByCategory(categoryId);
        Collections.sort(out, Comparator.comparingInt(p -> parsePrice(p.getPrice())));
        return out;
    }

    /** Tìm kiếm theo tên (có thể kết hợp category) */
    public static List<ProductModel> searchProducts(String query, String categoryId) {
        String q = query == null ? "" : query.toLowerCase();
        List<ProductModel> base = getProductsByCategory(categoryId);
        List<ProductModel> out = new ArrayList<>();
        for (ProductModel p : base) {
            String name = p.getName() == null ? "" : p.getName().toLowerCase();
            if (name.contains(q)) out.add(p);
        }
        return out;
    }

    private static int parsePrice(String priceStr) {
        if (priceStr == null) return Integer.MAX_VALUE;
        try { return Integer.parseInt(priceStr.replace("$","").replace(",","").trim()); }
        catch (Exception e) { return Integer.MAX_VALUE; }
    }
}
