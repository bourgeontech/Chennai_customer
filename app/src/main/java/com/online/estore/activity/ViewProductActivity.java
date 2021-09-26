package com.online.estore.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.online.estore.R;
import com.online.estore.adapter.ViewProductAdapter;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityViewProductsBinding;
import com.online.estore.interfaces.AddtoCartInterface;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.models.CartDomain;
import com.online.estore.models.ProductDomain;
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

import static com.online.estore.utils.UrlConstants.REQ_ADD_TO_CART;
import static com.online.estore.utils.UrlConstants.REQ_CART_LIST;
import static com.online.estore.utils.UrlConstants.REQ_PRODUCT_LIST;

public class ViewProductActivity extends BaseActivity implements View.OnClickListener, AddtoCartInterface, TextWatcher, VolleyInterface {

    ActivityViewProductsBinding binding;
    ViewProductAdapter adapter;
    ArrayList<ProductDomain> productList = new ArrayList<>();


    String category_id = "";
    String category = "";
    String sub_category_ids = "";
    String shop_id = "";
    String shopName = "";
    String from = "";

    String product_name = "";
    String t1 = "";

    String showall = "";

    ArrayList<CartDomain> cartList = new ArrayList<>();

    String pincode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_products);

        mActivity = this;

        from = getIntent().getStringExtra("from");
        showall = getIntent().getStringExtra("showall");
        pincode = getIntent().getStringExtra("pincode");

        binding.back.setOnClickListener(this);
        binding.gotoCart.setOnClickListener(this);
        binding.tvCat2.setOnClickListener(this);
        binding.tvCat.setOnClickListener(this);
        binding.home.setOnClickListener(this);

        if (from.equals("1")) {
            category_id = getIntent().getStringExtra("category_id");
            category = getIntent().getStringExtra("category");
            sub_category_ids = getIntent().getStringExtra("sub_category_ids");
            shop_id = getIntent().getStringExtra("shop_id");
            shopName = getIntent().getStringExtra("shopName");
            t1 = getIntent().getStringExtra("t1");
        } else {
            product_name = getIntent().getStringExtra("product_name");
        }

//        binding.viewCart.setOnClickListener(this);
        binding.edtSearch.addTextChangedListener(this);

        setupRecyclerView();

        if (showall.equals("true") || showall.equals("")) {
            binding.tvCat.setVisibility(View.VISIBLE);
            binding.tvCat2.setVisibility(View.GONE);
            binding.tvCatimg1.setVisibility(View.GONE);
            binding.tvCatimg2.setVisibility(View.GONE);
            binding.tvCat3.setVisibility(View.VISIBLE);
            binding.tvCat.setText("All products");
        } else {
            binding.tvCat.setVisibility(View.VISIBLE);
            binding.tvCat2.setVisibility(View.VISIBLE);
            binding.tvCat3.setVisibility(View.VISIBLE);
            binding.tvCatimg1.setVisibility(View.VISIBLE);
            binding.tvCatimg2.setVisibility(View.VISIBLE);
            binding.tvCat.setText("Home");
            binding.tvCat2.setText(t1);
            binding.tvCat3.setText(category);
        }

        if (from.equals("0")) {
            binding.titleImage.setText("All Products");
        } else {
            binding.titleImage.setText(shopName);
        }
        callService();

        getCartList();

    }

    private void getCartList() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));
        String url = UrlConstants.CART_LIST;
        volleyUtils.callApi(mActivity, this, url, REQ_CART_LIST, params, 1, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void callService() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("pincode", ClickedPincode);
        if (!product_name.equals("")) {
            params.put("product_name", product_name);
        } else if (!shop_id.equals("")) {
            params.put("shop_id", shop_id);
            params.put("sub_category_id", sub_category_ids);
            params.put("product_name", "a");
        }
        String url = UrlConstants.PRODUCT_LIST;
        volleyUtils.callApi(mActivity, this, url, REQ_PRODUCT_LIST, params, 1, true);

    }


    private void setupRecyclerView() {
        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new ViewProductAdapter(mActivity, productList, this);
        binding.recylerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tvCat2: {
                Intent intent = new Intent();
                setResult(502, intent);
                finish();
                break;
            }
            case R.id.gotoCart: {
                startActivity(new Intent(mActivity, CartActivity.class));
                break;
            }
            case R.id.tvCat: {
                Intent intent = new Intent();
                setResult(501, intent);
                finish();
                break;
            }
            case R.id.home: {
                Intent intent = new Intent(mActivity, TempActivity.class);
                intent.putExtra("goto", "home");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("controls-->", "here" + requestCode + " " + resultCode);
        if (resultCode == 501) {
            Log.d("controls-->", "here1");
            finish();
        }
    }


    private void addProductToCart(ProductDomain productDomain, String num) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("product_id", productDomain.getProduct_id());
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, ""));
        params.put("shop_id", shop_id);
        params.put("category_id", category_id);
        params.put("quantity", num);
        params.put("price", productDomain.getSpecial_price());
        params.put("sub_category_id", productDomain.getSub_category_id());

        String url = UrlConstants.ADD_TO_CART;
        volleyUtils.callApi(mActivity, this, url, REQ_ADD_TO_CART, params, 1, true);
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

        ArrayList<ProductDomain> newList = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {

            if (productList.get(i).getProduct_name().toLowerCase().contains(toString.toLowerCase())) {
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

        if (requestcode == REQ_ADD_TO_CART) {
            addtoCartRespnse(response);
            getCartList();
        } else if (requestcode == REQ_PRODUCT_LIST) {
            getProductResponse(response);
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

                    for (int i = 0; i < product.length(); i++) {
                        JSONObject jsonObject = product.getJSONObject(i);
                        String shop_id = jsonObject.getString("shop_id");
                        String category_id = jsonObject.getString("category_id");
                        String sub_category_id = jsonObject.getString("sub_category_id");
                        String product_id = jsonObject.getString("product_id");
                        String quantity = jsonObject.getString("quantity");
                        String product_name = jsonObject.getString("product_name");
                        String special_price = jsonObject.getString("special_price");
                        String discount = jsonObject.getString("discount");
                        String product_image = jsonObject.getString("product_image");
                        String unit = jsonObject.getString("unit");
                        cartList.add(new CartDomain(shop_id, category_id, sub_category_id, product_id, quantity, product_name,
                                special_price, discount, product_image, unit));

                    }

                    if (cartList.size() > 0) {
                        binding.cartNum.setVisibility(View.VISIBLE);
                        binding.cartNum.setText(cartList.size() + "");
                    } else {
                        binding.cartNum.setVisibility(View.GONE);
                    }

                } else {
                    binding.cartNum.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTotal() {
        int total = 0;
        for (CartDomain cartDomain : cartList) {
            int price = Integer.parseInt(cartDomain.getSpecial_price());
            total = total + price;
        }
        return total + "";
    }


    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void addtoCartRespnse(JSONObject response) {

        try {
            if (response.getString("code").equals("1")) {
                Toast.makeText(mActivity, "Successfully added to cart", Toast.LENGTH_SHORT).show();
            } else if (response.getString("code").equals("2")) {
                Toast.makeText(mActivity, response.getString("status") + "", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProductResponse(JSONObject response) {


        productList.clear();
        try {
            if (response.getString("code").equals("1")) {

                JSONArray category = response.getJSONArray("product");

                for (int i = 0; i < category.length(); i++) {

                    JSONObject jsonObject = category.getJSONObject(i);
                    String product_id = jsonObject.getString("product_id");
                    String shop_id = jsonObject.getString("shop_id");
                    String category_id = jsonObject.getString("category_id");
                    String product_name = jsonObject.getString("product_name");
                    String orginal_price = jsonObject.getString("orginal_price");
                    String special_price = jsonObject.getString("special_price");
                    String discount = jsonObject.getString("discount");
                    String sub_category_id = jsonObject.getString("sub_category_id");
                    String unit = "Kg";
                    if (jsonObject.has("unit")) {
                        unit = jsonObject.getString("unit");
                    }

                    String stock_availability = jsonObject.getString("stock_availability");
                    String show_for_sale = jsonObject.getString("show_for_sale");


                    String product_image = jsonObject.getString("product_image");
                    productList.add(new ProductDomain(product_id, shop_id, category_id,
                            product_name, orginal_price, special_price, discount, unit, product_image, sub_category_id, stock_availability, show_for_sale));
                }

                if (productList.size() == 0) {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.recylerView.setVisibility(View.GONE);
                    binding.tvEmpty.setText("No products found...");
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void clicked(String type, String num, Object object) {
        ProductDomain productDomain = (ProductDomain) object;
        if (type.equals("click")) {
//            showDeleteDialog(productDomain);
        } else if (type.equals("cart")) {
            addProductToCart(productDomain, num);
        }
    }
}