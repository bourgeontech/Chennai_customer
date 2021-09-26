package com.online.estore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.core.view.Change;
import com.google.firebase.messaging.FirebaseMessaging;
import com.online.estore.R;
import com.online.estore.adapter.SideBarAdapter;
import com.online.estore.adapter.StoreAdapter;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityMainBinding;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.interfaces.Refresh;
import com.online.estore.models.CartDomain;
import com.online.estore.models.CategoryDomain;
import com.online.estore.models.DrawerDomain;
import com.online.estore.models.ShopDomain;
import com.online.estore.utils.CommonUtils;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.VolleyInterface;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.online.estore.utils.UrlConstants.REQ_CART_LIST;

public class MainActivity extends BaseActivity implements VolleyInterface, TextWatcher, View.OnClickListener, ClickedItem, Refresh {

    ActivityMainBinding binding;
    StoreAdapter adapter;
    ArrayList<ShopDomain> shopList = new ArrayList<>();
    ArrayList<CartDomain> cartList = new ArrayList<>();

    SideBarAdapter sideBarAdapter;
    List<DrawerDomain> menuList = new ArrayList<>();
    //    String pincode = "";
    String cTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mActivity = this;

        cTopic = "c" + CUSTOMER_ID;

//        pincode = getIntent().getStringExtra("pincode");

        binding.container.edtSearch.addTextChangedListener(this);
        binding.container.cartLayout.setOnClickListener(this);
        binding.container.navIcon.setOnClickListener(this);
        binding.container.location.setOnClickListener(this);

        setUpRecycler();

        setUpSidebar();

    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.container.edtSearch.setText("");
        getShops();
        getCartItems();

    }

    private void getCartItems() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));
        String url = UrlConstants.CART_LIST;
        volleyUtils.callApi(mActivity, this, url, REQ_CART_LIST, params, 1, false);
    }


    private void setUpSidebar() {

        menuList.clear();
        menuList.add(new DrawerDomain(R.drawable.home_icon, "Dashboard"));
        menuList.add(new DrawerDomain(R.drawable.person_icon, "Edit Profile"));
        menuList.add(new DrawerDomain(R.drawable.order_icon, "My Orders"));
        menuList.add(new DrawerDomain(R.drawable.lock_icon, "Change Password"));
        menuList.add(new DrawerDomain(R.drawable.logout_icon, "Logout"));

        binding.layoutNav.menuListView.setHasFixedSize(true);
        binding.layoutNav.menuListView.setLayoutManager(new LinearLayoutManager(this));
        sideBarAdapter = new SideBarAdapter(this, menuList, this);
        binding.layoutNav.menuListView.setAdapter(sideBarAdapter);
    }

    private void setUpRecycler() {
        binding.container.recycler.setHasFixedSize(true);
        binding.container.recycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new StoreAdapter(this, shopList, this);
        binding.container.recycler.setAdapter(adapter);
    }


    private void getShops() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("pincode", ClickedPincode);
        String url = UrlConstants.SHOP_LIST;
        volleyUtils.callApi(mActivity, this, url, UrlConstants.REQ_SHOP_LIST, params, 1, true);

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {

        if (requestcode == UrlConstants.REQ_SHOP_LIST) {
            shopListResponse(response);
        } else if (requestcode == REQ_CART_LIST) {
            cartListResponse(response);
        }

    }

    private void cartListResponse(JSONObject response) {

        cartList.clear();
        try {
            if (response.getString("code").equals("1")) {

                JSONArray product = response.getJSONArray("product");
                if (product.length() > 0) {
                    if (product.length() > 9) {
                        binding.container.cartNum.setText("9+");
                    } else {
                        binding.container.cartNum.setText(product.length() + "");
                    }
                    binding.container.cartNum.setVisibility(View.VISIBLE);
                    startAnimation();
                } else {
                    binding.container.cartNum.clearAnimation();
                    binding.container.cartNum.setVisibility(View.GONE);
                }
            } else {
                binding.container.cartNum.clearAnimation();
                binding.container.cartNum.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAnimation() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        binding.container.cartNum.startAnimation(anim);
    }

    private void shopListResponse(JSONObject response) {

        shopList.clear();
        try {

            if (response.getString("code").equals("1")) {

                JSONArray shop = response.getJSONArray("shop");
                for (int i = 0; i < shop.length(); i++) {

                    JSONObject object = shop.getJSONObject(i);
                    String shop_id = object.getString("shop_id");
                    String shop_name = object.getString("shop_name");
                    String rating = object.getString("rating");
                    String open_time = object.getString("open_time");
                    String close_time = object.getString("close_time");
                    String latitude = object.getString("latitude");
                    String longitude = object.getString("longitude");

                    if (!latitude.equals("") && !longitude.equals("")) {
                        shopList.add(new ShopDomain(shop_id, shop_name, rating, open_time, close_time, latitude, longitude));
                    }
                }

                if (shopList.size() == 0) {
                    binding.container.emptyLayout.setVisibility(View.VISIBLE);
                    binding.container.recycler.setVisibility(View.GONE);
                } else {
                    binding.container.emptyLayout.setVisibility(View.GONE);
                    binding.container.recycler.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    binding.container.recycler.scheduleLayoutAnimation();
                }
            } else {
                binding.container.emptyLayout.setVisibility(View.VISIBLE);
                binding.container.recycler.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            filter(s.toString());
        } else {
            adapter.shopList = shopList;
            adapter.notifyDataSetChanged();
            binding.container.recycler.scheduleLayoutAnimation();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void filter(String toString) {

        ArrayList<ShopDomain> newList = new ArrayList<>();
        for (int i = 0; i < shopList.size(); i++) {

            if (shopList.get(i).getShop_name().toLowerCase().contains(toString.toLowerCase())) {
                newList.add(shopList.get(i));
            }
        }

        if (newList.size() == 0) {
            binding.container.emptyLayout.setVisibility(View.VISIBLE);
            binding.container.recycler.setVisibility(View.GONE);
        } else {
            binding.container.emptyLayout.setVisibility(View.GONE);
            binding.container.recycler.setVisibility(View.VISIBLE);
            adapter.shopList = newList;
            adapter.notifyDataSetChanged();
            binding.container.recycler.scheduleLayoutAnimation();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cartLayout:
                startActivity(new Intent(mActivity, CartActivity.class));
                break;
            case R.id.navIcon:
                binding.drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.location:
                startActivity(new Intent(mActivity, ChangeLocationActivity.class));
                break;
        }
    }

    @Override
    public void clicked(String type, Object object) {

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        String item = (String) object;
        switch (item) {
            case "Dashboard":
                Intent intent = new Intent(mActivity, TempActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
                break;
            case "Edit Profile":
                startActivity(new Intent(mActivity, ProfileActivity.class));
                break;
            case "Change Password":
                startActivity(new Intent(mActivity, ChangePassword.class));
                break;
            case "My Orders":
                startActivity(new Intent(mActivity, MyOrderActivity.class));
                break;
            case "Logout":
                CommonUtils.setAlerDialog(mActivity, "Confirm Logout", "Are you sure you want to logout?", true, "LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logoutFunction();
                    }
                });
                break;
        }
    }

    private void logoutFunction() {
        CommonUtils.setProgressBar(mActivity);
        editor.clear();
        editor.apply();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(cTopic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.cancelProgressBar();
                Log.d("topic-->", "UnSubscribed");
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(mActivity, TempActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void refresh() {
        getShops();
    }

}