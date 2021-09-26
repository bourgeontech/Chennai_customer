package com.online.estore.models;

public class ShopDomain {

    String shop_id;
    String shop_name;
    String rating;
    String open_time;
    String close_time;
    String latitude;
    String longitude;

    public ShopDomain(String shop_id, String shop_name, String rating, String open_time, String close_time, String latitude, String longitude) {
        this.shop_id = shop_id;
        this.shop_name = shop_name;
        this.rating = rating;
        this.open_time = open_time;
        this.close_time = close_time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getShop_id() {
        return shop_id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public String getRating() {
        return rating;
    }

    public String getOpen_time() {
        return open_time;
    }

    public String getClose_time() {
        return close_time;
    }
}
