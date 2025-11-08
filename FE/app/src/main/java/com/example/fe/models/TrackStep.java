package com.example.fe.models;

public class TrackStep {
    String status;
    String dateTime;
    boolean isCompleted;

    public TrackStep(String status, String dateTime, boolean isCompleted) {
        this.status = status;
        this.dateTime = dateTime;
        this.isCompleted = isCompleted;
    }

    // Getters (Để Adapter có thể truy cập)
    public String getStatus() {
        return status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
