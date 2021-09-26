package com.online.estore.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.gson.Gson;
import com.online.estore.R;
import com.online.estore.adapter.OrderDetailsAdapter;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityOrderDetailsBinding;
import com.online.estore.models.OrderDetailsDomain;
import com.online.estore.models.OrderDomain;
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

import static com.online.estore.utils.UrlConstants.REQ_myOrderList;
import static com.online.estore.utils.UrlConstants.REQ_myProductList;

public class OrderDetailsActivity extends BaseActivity implements View.OnClickListener, VolleyInterface {

    ActivityOrderDetailsBinding binding;
    OrderDetailsAdapter adapter;
    ArrayList<OrderDetailsDomain> orderDetailsList = new ArrayList<>();
    OrderDomain orderDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);

        Gson gson = new Gson();
        String strObj = getIntent().getStringExtra("orderData");
        orderDomain = gson.fromJson(strObj, OrderDomain.class);

        mActivity = this;

        liseners();


        setupRecyclerView();

        getOrderDetails();

    }

    private void liseners() {
        binding.back.setOnClickListener(this);
        binding.help.setOnClickListener(this);
    }

    private void setupRecyclerView() {

        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new OrderDetailsAdapter(mActivity, orderDetailsList);
        binding.recylerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        } else if (v.getId() == R.id.help) {
            CommonUtils.setAlerDialog(mActivity, "Call Customer Care",
                    "You are about to call the Customer Care. Are you sure you want to call?", true, "Call", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            callCustomerCare();
                        }
                    });
        }
    }

    private void callCustomerCare() {
        Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+919847132202"));
        startActivity(intentDial);
    }

    private void getOrderDetails() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("order_id", orderDomain.getOrder_id());

        String url = UrlConstants.myProductList;
        volleyUtils.callApi(mActivity, this, url, REQ_myProductList, params, 1, true);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_myProductList) {
            productListResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void productListResponse(JSONObject response) {
        try {
            String code = response.getString("code");
            if (code.equals("1")) {


                JSONArray category = response.getJSONArray("product");

                for (int i = 0; i < category.length(); i++) {

                    JSONObject jsonObject = category.getJSONObject(i);
                    String product_id = jsonObject.getString("product_id");
                    String quantity = jsonObject.getString("quantity");
                    String product_name = jsonObject.getString("product_name");
                    String product_image = jsonObject.getString("product_image");
                  //  String stock_availability = jsonObject.getString("stock_availability");
                    String stock_availability = "";
                    String unit = jsonObject.getString("pack");
                   // String delivery_status = jsonObject.getString("delivery_status");
                    String delivery_status = "";
                    String price = jsonObject.getString("price");

                    orderDetailsList.add(new OrderDetailsDomain(product_id, quantity, product_name, product_image, stock_availability,
                            unit, delivery_status, price));

                }
                if (orderDetailsList.size() > 0) {
                    binding.table.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    binding.recylerView.scheduleLayoutAnimation();
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}