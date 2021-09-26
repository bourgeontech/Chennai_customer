package com.online.estore.models;

import java.io.Serializable;

public class CartDomain implements Serializable {
    String shop_id;
    String category_id;
    String sub_category_id;
    String product_id;
    String quantity;
    String product_name;
    String special_price;
    String discount;
    String product_image;
    String unit;

    public CartDomain() {
    }

    public CartDomain(String shop_id, String category_id, String sub_category_id, String product_id, String quantity, String product_name, String special_price, String discount, String product_image, String unit) {
        this.shop_id = shop_id;
        this.category_id = category_id;
        this.sub_category_id = sub_category_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.product_name = product_name;
        this.special_price = special_price;
        this.discount = discount;
        this.product_image = product_image;
        this.unit = unit;
    }

    public String getShop_id() {
        return shop_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getSub_category_id() {
        return sub_category_id;
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

    public String getSpecial_price() {
        return special_price;
    }

    public String getDiscount() {
        return discount;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getUnit() {
        return unit;
    }

    public CartDomain getCartDomain(String shop_id, String category_id, String sub_category_id, String product_id, String quantity, String product_name, String special_price, String discount, String product_image) {
        CartDomain cartDomain = new CartDomain();
        cartDomain.shop_id = shop_id;
        cartDomain.category_id = category_id;
        cartDomain.sub_category_id = sub_category_id;
        cartDomain.product_id = product_id;
        cartDomain.quantity = quantity;
        cartDomain.product_name = product_name;
        cartDomain.special_price = special_price;
        cartDomain.discount = discount;
        cartDomain.product_image = product_image;
        return cartDomain;
    }

    @Override
    public String toString() {
        return "CartDomain{" +
                "shop_id='" + shop_id + '\'' +
                ", category_id='" + category_id + '\'' +
                ", sub_category_id='" + sub_category_id + '\'' +
                ", product_id='" + product_id + '\'' +
                ", quantity='" + quantity + '\'' +
                ", product_name='" + product_name + '\'' +
                ", special_price='" + special_price + '\'' +
                ", discount='" + discount + '\'' +
                ", product_image='" + product_image + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
