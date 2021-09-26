package com.online.estore.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.online.estore.R;
import com.online.estore.adapter.CategoryAdapter;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityCategoryBinding;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.models.CartDomain;
import com.online.estore.models.CategoryDomain;
import com.online.estore.utils.CommonUtils;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.VolleyInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.online.estore.utils.UrlConstants.REQ_CART_LIST;

public class CategoryActivity extends BaseActivity implements View.OnClickListener, ClickedItem, TextWatcher, VolleyInterface {

    ActivityCategoryBinding binding;
    CategoryAdapter adapter;
    ArrayList<CategoryDomain> productList = new ArrayList<>();

    String shop_id = "";
    String shopName = "All Categories";
    String from = "";

    ArrayList<String> selectedList = new ArrayList<>();

    String pincode;
    ArrayList<CartDomain> cartList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category);

        mActivity = this;

        from = getIntent().getStringExtra("from");

        liseteners();

        if (from.equals("1")) {
            shop_id = getIntent().getStringExtra("shop_id");
            shopName = getIntent().getStringExtra("shopName");
        } else {
            pincode = getIntent().getStringExtra("pincode");
        }


        setupRecyclerView();

        callService();

        binding.titleImage.setText(shopName);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartItems();
    }

    private void liseteners() {

        binding.btnAdd.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.cartLayout.setOnClickListener(this);
        binding.edtSearch.addTextChangedListener(this);
    }

    private void getCartItems() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));

        String url = UrlConstants.CART_LIST;
        volleyUtils.callApi(mActivity, this, url, REQ_CART_LIST, params, 1, false);

    }


    private void callService() {

        HashMap<String, Object> params = new HashMap<>();
        if (shop_id.equals("")) {
            params.put("pincode", ClickedPincode);
        } else {
            params.put("shop_id", shop_id);
        }
        String url = UrlConstants.CATEGORY_LIST;
        volleyUtils.callApi(mActivity, this, url, UrlConstants.REQ_CATEGORY_LIST, params, 1, true);

    }

    private void setupRecyclerView() {
        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new CategoryAdapter(mActivity, productList, this);
        binding.recylerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.cartLayout:
                startActivity(new Intent(mActivity, CartActivity.class));
                break;
        }
    }

    @Override
    public void clicked(String type, Object object) {

        final CategoryDomain productDomain = (CategoryDomain) object;
        if (type.equals("check")) {
            checkedOnProduct(productDomain);
        } else if (type.equals("click")) {

            Intent intent = new Intent(mActivity, SubCategoryActivity.class);
            intent.putExtra("category_id", productDomain.getCategory_id());
            intent.putExtra("category", productDomain.getCategory_title());
            intent.putExtra("shop_id", productDomain.getShop_id());
            if (from.equals("0"))
                intent.putExtra("showall", "true");
            else
                intent.putExtra("showall", "false");
            if (shopName.equals("All Categories")) {
                intent.putExtra("shopName", "All Subcategories");
            } else {
                intent.putExtra("shopName", shopName);
            }
            startActivity(intent);

        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuinfo) {
        super.onCreateContextMenu(menu, view, menuinfo);
        menu.setHeaderTitle("Choose");
        menu.add(menu.FIRST, Menu.NONE, 0, "DELETE CATEGORY");

    }

    private void checkedOnProduct(CategoryDomain productDomain) {

        if (selectedList.contains(productDomain.getCategory_id())) {
            selectedList.remove(productDomain.getCategory_id());
        } else {
            selectedList.add(productDomain.getCategory_id());
        }

        adapter.selectedList = selectedList;

        if (selectedList.size() > 0) {
            binding.btnNext.setVisibility(View.VISIBLE);
        } else {
            binding.btnNext.setVisibility(View.GONE);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filter(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void filter(String toString) {

        ArrayList<CategoryDomain> newList = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {

            if (productList.get(i).getCategory_title().toLowerCase().contains(toString.toLowerCase())) {
                newList.add(productList.get(i));
            }
        }

        if (newList.size() > 0) {
            adapter.productList = newList;
            binding.recylerView.setVisibility(View.VISIBLE);
            binding.emptyLayout.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            binding.recylerView.scheduleLayoutAnimation();
        } else {
            binding.recylerView.setVisibility(View.GONE);
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.tvEmpty.setText("No categories found...");
        }

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == UrlConstants.REQ_CATEGORY_LIST) {
            categoryListResponse(response);
        } else if (requestcode == REQ_CART_LIST) {
            cartListResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void cartListResponse(JSONObject response) {

        cartList.clear();
        try {
            if (response.getString("code").equals("1")) {

                JSONArray product = response.getJSONArray("product");
                if (product.length() > 0) {
                    if (product.length() > 9) {
                        binding.cartNum.setText("9+");
                    } else {
                        binding.cartNum.setText(product.length() + "");
                    }
                    binding.cartNum.setVisibility(View.VISIBLE);
                    startAnimation();
                } else {
                    binding.cartNum.clearAnimation();
                    binding.cartNum.setVisibility(View.GONE);
                }
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
        binding.cartNum.startAnimation(anim);
    }

    private void categoryListResponse(JSONObject response) {


        productList.clear();
        try {
            if (response.getString("code").equals("1")) {

                JSONArray category = response.getJSONArray("category");

                for (int i = 0; i < category.length(); i++) {

                    JSONObject jsonObject = category.getJSONObject(i);
                    String shop_id = jsonObject.getString("shop_id");
                    String category_id = jsonObject.getString("category_id");
                    String category_title = jsonObject.getString("category_title");
                    String category_description = jsonObject.getString("category_description");
                    String category_image = jsonObject.getString("category_image");

                    productList.add(new CategoryDomain(shop_id, category_id, category_title, category_description, category_image));

                }

                if (productList.size() == 0) {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.recylerView.setVisibility(View.GONE);
                    binding.tvEmpty.setText("No categories found...");
                } else {
                    binding.emptyLayout.setVisibility(View.GONE);
                    binding.recylerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    binding.recylerView.scheduleLayoutAnimation();
                }
            } else {
                binding.emptyLayout.setVisibility(View.VISIBLE);
                binding.tvEmpty.setText(response.getString("status"));
            }

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}