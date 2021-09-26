package com.online.estore.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.online.estore.utils.Constants;
import com.online.estore.volleyservice.VolleyUtils;

public class BaseActivity extends AppCompatActivity {


    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Activity mActivity;
    public VolleyUtils volleyUtils;
    public String CUSTOMER_ID;
    public String CUSTOMER_NAME;
    public String CUSTOMER_PHOTO;
    public String PHONE;
    public String ADDRESS;
    public String PINCODE;
    public String LANDMARK;
    public String CART_COUNT;
    public String LATTITUDE;
    public String LONGITUDE;
    public String ClickedPincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("ogl-app", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        volleyUtils = new VolleyUtils();

        CUSTOMER_ID = sharedPreferences.getString(Constants.CUSTOMER_ID, "");
        CUSTOMER_NAME = sharedPreferences.getString(Constants.CUSTOMER_NAME, "");
        CUSTOMER_PHOTO = sharedPreferences.getString(Constants.CUSTOMER_PHOTO, "");
        PHONE = sharedPreferences.getString(Constants.PHONE, "");
        ADDRESS = sharedPreferences.getString(Constants.ADDRESS, "");
        PINCODE = sharedPreferences.getString(Constants.PINCODE, "");
        LANDMARK = sharedPreferences.getString(Constants.LANDMARK, "");
        CART_COUNT = sharedPreferences.getString(Constants.CART_COUNT, "");
        LATTITUDE = sharedPreferences.getString(Constants.LATTITUDE, "");
        LONGITUDE = sharedPreferences.getString(Constants.LONGITUDE, "");
        ClickedPincode = sharedPreferences.getString(Constants.ClickedPincode, "");

    }
}