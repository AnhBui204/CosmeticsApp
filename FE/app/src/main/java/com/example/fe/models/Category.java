package com.example.fe.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("_id")
    private String id;
    private String name;

    public String getId() { return id; }
    public String getName() { return name; }
}
