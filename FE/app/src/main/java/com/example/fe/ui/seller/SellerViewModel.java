package com.example.fe.ui.seller;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel; // Dùng ViewModel (không phải AndroidViewModel)
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fe.data.RevenueData;
import com.example.fe.data.TopProductData; // Import model TopProduct
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
// Không cần SessionManager

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerViewModel extends ViewModel {

    private final ApiService apiService;

    // LiveData cho dữ liệu biểu đồ
    private final MutableLiveData<List<RevenueData>> _revenueData = new MutableLiveData<>();
    public LiveData<List<RevenueData>> getRevenueData() {
        return _revenueData;
    }

    // LiveData cho top sản phẩm
    private final MutableLiveData<List<TopProductData>> _topProducts = new MutableLiveData<>();
    public LiveData<List<TopProductData>> getTopProducts() {
        return _topProducts;
    }

    // LiveData cho thông báo lỗi
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> getError() {
        return _error;
    }

    // Constructor (không cần Application)
    public SellerViewModel() {
        super();
        // Dùng .getClient() như file ApiClient của bạn
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    /**
     * Hàm gọi API doanh thu, nhận sellerId
     */
    public void loadRevenueData(String sellerId) {
        if (sellerId == null || sellerId.isEmpty()) {
            _error.postValue("Lỗi: Không tìm thấy Seller ID (Revenue)");
            return;
        }

        // Gọi API với sellerId
        apiService.getSellerRevenue(sellerId).enqueue(new Callback<List<RevenueData>>() {
            @Override
            public void onResponse(Call<List<RevenueData>> call, Response<List<RevenueData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _revenueData.postValue(response.body());
                } else {
                    _error.postValue("Failed to load revenue data: " + response.code());
                    Log.e("SellerViewModel", "API Error (Revenue): " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<RevenueData>> call, Throwable t) {
                _error.postValue("Network Error (Revenue): " + t.getMessage());
                Log.e("SellerViewModel", "Network Failure (Revenue): " + t.getMessage());
            }
        });
    }

    /**
     * Hàm gọi API Top Products, nhận sellerId
     */
    public void loadTopProducts(String sellerId) {
        if (sellerId == null || sellerId.isEmpty()) {
            _error.postValue("Lỗi: Không tìm thấy Seller ID (Top Products)");
            return;
        }

        apiService.getTopSellingProducts(sellerId).enqueue(new Callback<List<TopProductData>>() {
            @Override
            public void onResponse(Call<List<TopProductData>> call, Response<List<TopProductData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _topProducts.postValue(response.body());
                } else {
                    _error.postValue("Failed to load top products: " + response.code());
                    Log.e("SellerViewModel", "API Error (Top Products): " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<TopProductData>> call, Throwable t) {
                _error.postValue("Network Error (Top Products): " + t.getMessage());
                Log.e("SellerViewModel", "Network Failure (Top Products): " + t.getMessage());
            }
        });
    }
}