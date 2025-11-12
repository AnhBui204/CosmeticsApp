package com.example.fe.models;

import java.util.List;

public class VouchersResponse {
    private int total;
    private int page;
    private int limit;
    private List<Voucher> data;

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public List<Voucher> getData() { return data; }
    public void setData(List<Voucher> data) { this.data = data; }
}

