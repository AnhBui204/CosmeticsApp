package com.example.fe.models;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Product {
    @SerializedName("_id")
    public String id;
    public String name;
    private String sku;
    private String description;
    public double price;
    private Double salePrice;
    public int stockQuantity;
    @com.google.gson.annotations.SerializedName("images")
    private com.google.gson.JsonElement imagesRaw;
    private String brand;
    private Object category;

    // ratings can be either an array (e.g. [5,4,3]) or a summary object { average: 4.2, count: 5 }
    private JsonElement ratings;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public List<String> getImages() {
        try {
            if (imagesRaw == null || imagesRaw.isJsonNull()) return null;
            List<String> out = new ArrayList<>();
            if (imagesRaw.isJsonArray()) {
                for (com.google.gson.JsonElement el : imagesRaw.getAsJsonArray()) {
                    try {
                        if (el.isJsonPrimitive()) {
                            out.add(el.getAsString());
                        } else if (el.isJsonObject()) {
                            com.google.gson.JsonObject eobj = el.getAsJsonObject();
                            String[] keys = new String[]{"url", "src", "path", "image"};
                            boolean added = false;
                            for (String k : keys) {
                                if (eobj.has(k) && eobj.get(k).isJsonPrimitive()) {
                                    try {
                                        out.add(eobj.get(k).getAsString());
                                        added = true;
                                        break;
                                    } catch (Exception ex) {
                                    }
                                }
                            }
                            if (!added) {
                                // fallback: if object has a primitive field, take first
                                for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : eobj.entrySet()) {
                                    if (entry.getValue().isJsonPrimitive()) {
                                        try {
                                            out.add(entry.getValue().getAsString());
                                            added = true;
                                            break;
                                        } catch (Exception ex) {
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        /* skip */
                    }
                }
                return out;
            } else if (imagesRaw.isJsonPrimitive()) {
                try {
                    out.add(imagesRaw.getAsString());
                    return out;
                } catch (Exception ex) {
                    return null;
                }
            } else if (imagesRaw.isJsonObject()) {
                // attempt to find common string fields
                com.google.gson.JsonObject obj = imagesRaw.getAsJsonObject();
                if (obj.size() == 0) return null;
                // try common keys
                String[] keys = new String[]{"0", "url", "image", "src", "path"};
                for (String k : keys) {
                    if (obj.has(k) && obj.get(k).isJsonPrimitive()) {
                        try {
                            out.add(obj.get(k).getAsString());
                            return out;
                        } catch (Exception ex) {
                        }
                    }
                }
                // fallback: take first primitive value
                for (java.util.Map.Entry<String, com.google.gson.JsonElement> e : obj.entrySet()) {
                    if (e.getValue().isJsonPrimitive()) {
                        try {
                            out.add(e.getValue().getAsString());
                            return out;
                        } catch (Exception ex) {
                        }
                    }
                }
                return null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getBrand() {
        return brand;
    }

    public Object getCategory() {
        return category;
    }

    public String getCategoryId() {
        if (category == null) return null;
        if (category instanceof String) return (String) category;
        try {
            java.util.Map map = (java.util.Map) category;
            Object id = map.get("_id");
            if (id == null) id = map.get("id");
            return id != null ? String.valueOf(id) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getCategoryName() {
        if (category == null) return null;
        if (category instanceof String) return (String) category;
        try {
            java.util.Map map = (java.util.Map) category;
            Object name = map.get("name");
            return name != null ? String.valueOf(name) : null;
        } catch (Exception e) {
            return null;
        }
    }

    // New: expose ratings raw as JsonElement for flexible parsing
    public JsonElement getRatingsRaw() {
        return ratings;
    }

    // Return a List<Double> if ratings is an array; otherwise null
    public List<Double> getRatingsList() {
        try {
            if (ratings == null || !ratings.isJsonArray()) return null;
            List<Double> out = new ArrayList<>();
            for (com.google.gson.JsonElement el : ratings.getAsJsonArray()) {
                try {
                    out.add(el.getAsDouble());
                } catch (Exception ex) {
                    // skip non-numeric
                }
            }
            return out;
        } catch (Exception e) {
            return null;
        }
    }

    // Compute/return average rating if possible (either from object {average} or from array values)
    public Double getRatingAverage() {
        try {
            if (ratings == null) return null;
            if (ratings.isJsonObject()) {
                com.google.gson.JsonElement avgEl = ratings.getAsJsonObject().get("average");
                if (avgEl != null && !avgEl.isJsonNull()) {
                    try {
                        return avgEl.getAsDouble();
                    } catch (Exception ex) {
                        /* fallthrough */
                    }
                }
            }
            // fallback: if array, compute average
            List<Double> list = getRatingsList();
            if (list != null && !list.isEmpty()) {
                double sum = 0;
                for (Double d : list) sum += d;
                return sum / list.size();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
