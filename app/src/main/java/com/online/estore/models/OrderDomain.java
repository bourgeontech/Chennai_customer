package com.online.estore.models;

public class OrderDomain {
    String order_id;
    String order_date;
    String order_time;
    String payable_amount;
    String delivery_status;


    public OrderDomain(String order_id, String order_date, String order_time, String payable_amount, String delivery_status) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.order_time = order_time;
        this.payable_amount = payable_amount;
        this.delivery_status = delivery_status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public String getPayable_amount() {
        return payable_amount;
    }

    public String getDelivery_status() {
        return delivery_status;
    }
}
