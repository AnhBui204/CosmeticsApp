package com.example.fe.models;



import java.io.Serializable;

public class VoucherItem implements Serializable {
    private String code;
    private String title;
    private String description;
    private String endDate;
    private String status;

    public VoucherItem(String code, String title, String description, String endDate, String status) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.status = status;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
