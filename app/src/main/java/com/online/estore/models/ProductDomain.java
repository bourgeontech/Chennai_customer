package com.online.estore.models;

public class ProductDomain {

    String product_id;
    String shop_id;
    String category_id;
    String product_name;
    String orginal_price;
    String special_price;
    String discount;
    String unit;
    String product_image;
    String sub_category_id;
    String stock_availability;
    String show_for_sale;


    public ProductDomain(String product_id, String shop_id, String category_id, String product_name, String orginal_price, String special_price, String discount, String unit, String product_image, String sub_category_id, String stock_availability, String show_for_sale) {
        this.product_id = product_id;
        this.shop_id = shop_id;
        this.category_id = category_id;
        this.product_name = product_name;
        this.orginal_price = orginal_price;
        this.special_price = special_price;
        this.discount = discount;
        this.unit = unit;
        this.product_image = product_image;
        this.sub_category_id = sub_category_id;
        this.stock_availability = stock_availability;
        this.show_for_sale = show_for_sale;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getOrginal_price() {
        return orginal_price;
    }

    public String getSpecial_price() {
        return special_price;
    }

    public String getDiscount() {
        return discount;
    }

    public String getUnit() {
        return unit;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getSub_category_id() {
        return sub_category_id;
    }

    public String getStock_availability() {
        return stock_availability;
    }

    public String getShow_for_sale() {
        return show_for_sale;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setOrginal_price(String orginal_price) {
        this.orginal_price = orginal_price;
    }

    public void setSpecial_price(String special_price) {
        this.special_price = special_price;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public void setSub_category_id(String sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public void setStock_availability(String stock_availability) {
        this.stock_availability = stock_availability;
    }

    public void setShow_for_sale(String show_for_sale) {
        this.show_for_sale = show_for_sale;
    }
}
