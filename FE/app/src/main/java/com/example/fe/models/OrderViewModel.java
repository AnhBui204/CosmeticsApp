// ĐÃ SỬA PACKAGE
package com.example.fe.models;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fe.models.Order;
// ĐÃ SỬA IMPORT
import com.example.fe.network.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.utils.SessionManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends AndroidViewModel {

    private final ApiService apiService;
    // SessionManager vẫn hữu ích để lấy thông tin user khác, nhưng không cần cho token ở đây
    private final SessionManager sessionManager;

    private final MutableLiveData<List<Order>> myOrders = new MutableLiveData<>();
    private final MutableLiveData<Order> orderDetails = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public OrderViewModel(@NonNull Application application) {
        super(application);

        // ĐÃ SỬA: Dùng getClient(application) để lấy client đã xác thực với Interceptor
        apiService = ApiClient.getClient(application).create(ApiService.class);

        sessionManager = new SessionManager(application);
    }

    // LiveData Getters
    public LiveData<List<Order>> getMyOrdersLiveData() { return myOrders; }
    public LiveData<Order> getOrderDetailsLiveData() { return orderDetails; }
    public LiveData<String> getErrorLiveData() { return error; }
    public LiveData<Boolean> getIsLoadingLiveData() { return isLoading; }

    // Gọi API lấy tất cả đơn hàng
    public void fetchMyOrders() {
        isLoading.setValue(true);

        // ĐÃ XÓA: Không cần lấy token thủ công
        // String token = sessionManager.getToken();
        // if (token == null) { ... }

        // ĐÃ SỬA: Không cần truyền token, Interceptor sẽ tự thêm
        apiService.getMyOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    myOrders.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch orders: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("API call failed: " + t.getMessage());
            }
        });
    }

    // Gọi API lấy chi tiết 1 đơn hàng
    public void fetchOrderDetails(String orderCode) {
        isLoading.setValue(true);

        // ĐÃ XÓA: Không cần lấy token thủ công

        // ĐÃ SỬA: Không cần truyền token
        apiService.getOrderByCode(orderCode).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    orderDetails.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch details: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("API call failed: " + t.getMessage());
            }
        });
    }
}