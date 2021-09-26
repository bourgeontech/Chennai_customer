package com.online.estore.models;

public class OrderDetailsDomain {

    String product_id;
    String quantity;
    String product_name;
    String product_image;
    String stock_availability;
    String unit;
    String delivery_status;
    String price;

    public OrderDetailsDomain(String product_id, String quantity, String product_name, String product_image, String stock_availability, String unit, String delivery_status, String price) {
        this.product_id = product_id;
        this.quantity = quantity;
        this.product_name = product_name;
        this.product_image = product_image;
        this.stock_availability = stock_availability;
        this.unit = unit;
        this.delivery_status = delivery_status;
        this.price = price;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getStock_availability() {
        return stock_availability;
    }

    public String getUnit() {
        return unit;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public String getPrice() {
        return price;
    }
}
