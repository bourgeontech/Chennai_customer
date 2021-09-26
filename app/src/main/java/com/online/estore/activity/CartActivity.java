package com.online.estore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.online.estore.R;
import com.online.estore.adapter.CartAdapter;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityCartBinding;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.models.CartDomain;
import com.online.estore.models.ProductDomain;
import com.online.estore.utils.CartEmptyFragment;
import com.online.estore.utils.CommonUtils;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.NotiInterface;
import com.online.estore.volleyservice.VolleyInterface;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.online.estore.utils.UrlConstants.REQ_ADD_TO_CART;
import static com.online.estore.utils.UrlConstants.REQ_CART_DELETE;
import static com.online.estore.utils.UrlConstants.REQ_CART_LIST;
import static com.online.estore.utils.UrlConstants.REQ_DELIVERYFEE;
import static com.online.estore.utils.UrlConstants.REQ_EDIT_CART;
import static com.online.estore.utils.UrlConstants.REQ_PLACE_ORDER;

public class CartActivity extends BaseActivity implements VolleyInterface, View.OnClickListener, ClickedItem, NotiInterface , PaymentResultListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    ActivityCartBinding binding;
    CartAdapter adapter;
    ArrayList<CartDomain> cartList = new ArrayList<>();

    String tot = "0";
    double ttot= 0;
    int dFee = 0;
    double ttotal = 0;
    String paymethod = null,Raz_payment_id = "abcdefg";
    CartEmptyFragment cartEmptyFragment = new CartEmptyFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Checkout.preload(getApplicationContext());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);

        mActivity = this;

        cartEmptyFragment.setCancelable(false);

        binding.btnPlaceOrder.setOnClickListener(this);
        binding.back.setOnClickListener(this);

        setupRecycler();

        getCartItems();


    }

    private void getDeliveryFee() {
        String url = UrlConstants.GETDELIVERYFEE;
        HashMap<String, Object> params = new HashMap<>();

        final JSONArray jsonArray = new JSONArray();
        try {

            for (int i=0,j = 0; j <= cartList.size(); j++) {
                JSONObject jsonObject = new JSONObject();
                if(j == 0){
                    jsonObject.put("$cust_id", CUSTOMER_ID);
                }else{
                    jsonObject.put("shop_id", cartList.get(i).getShop_id());
                    i=i+1;
                }
                jsonArray.put(jsonObject);

            }

            final JSONObject mainObj = new JSONObject();
            mainObj.put("", jsonArray);

            Log.d("jsonArray-->", jsonArray.toString());


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            CommonUtils.cancelProgressBar();
                            try {
                                JSONObject response = new JSONObject(s);
                                Log.d("response-->", response.toString());
                                String code = response.getString("code");
                                if (code.equals("1")) {
                                    dFee = Integer.parseInt(response.getString("deliveryfee"));
                                    binding.deliveryfee.setText("Rs."+dFee);
                                    ttotal = ttotal + dFee;
                                    ttot = ttot + (dFee * 100);
                                    binding.tvSubTotal.setText("Rs." + ttotal);
                                } else {
                                    Toast.makeText(mActivity, "We can't get the Delivery Fee", Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CommonUtils.cancelProgressBar();
                    Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return super.getHeaders();
                }

                @Override
                public byte[] getBody() {
                    return jsonArray.toString().getBytes();
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(stringRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getCartItems() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));

        String url = UrlConstants.CART_LIST;


        volleyUtils.callApi(mActivity, this, url, REQ_CART_LIST, params, 1, true);

    }

    private void setupRecycler() {

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new CartAdapter(mActivity, cartList, this);
        binding.recycler.setAdapter(adapter);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_CART_LIST) {
            cartListResponse(response);
        } else if (requestcode == REQ_CART_DELETE) {
            getCartItems();
        } else if (requestcode == REQ_EDIT_CART) {
            editcartResponse(response);
        }
        getDeliveryFee();
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void editcartResponse(JSONObject response) {
        try {
            String code = response.getString("code");
            if (code.equals("1")) {
                getCartItems();
            } else {
                Toast.makeText(mActivity, "" + response.getString("status"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cartListResponse(JSONObject response) {

        cartList.clear();
        try {
            if (response.getString("code").equals("1")) {

                JSONArray product = response.getJSONArray("product");
                if (product.length() > 0) {

                    double total = 0;

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

                        int q = Integer.parseInt(quantity);
                        double t = Double.parseDouble(special_price);
                        total = total + (q * t);



                        cartList.add(new CartDomain(shop_id, category_id, sub_category_id, product_id, quantity, product_name,
                                special_price, discount, product_image, unit));

                    }
                    ttot = ttot+dFee;
                    ttot=total*100;
                    tot = String.format("%.2f", total);

                    ttotal = total;

                    binding.tvtotal.setText("Rs." + tot);
                    binding.tvSubTotal.setText("Rs." + tot);


                    adapter.notifyDataSetChanged();
                    binding.recycler.scheduleLayoutAnimation();


                } else {
                    cartEmptyFragment.show(getSupportFragmentManager(), cartEmptyFragment.getTag());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlaceOrder:
                funcpaymentMethod();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void funcpaymentMethod() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_bottom_sheet,
                (LinearLayout)findViewById(R.id.bottomSheetContainer)
        );
        final RadioGroup paymentradiogroup;
        paymentradiogroup = (RadioGroup)bottomSheetView.findViewById(R.id.paymentmethods);
        bottomSheetView.findViewById(R.id.paymentproceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkId = paymentradiogroup.getCheckedRadioButtonId();
                if(checkId == -1){//no radio button selected
                    Toast.makeText(CartActivity.this,"Select one Payment Option to Proceed",Toast.LENGTH_SHORT).show();
                } else{
                    switch (checkId){
                        case R.id.radioonline:
                            CommonUtils.setProgressBar(mActivity);
                            createOrder();
                    }
                    switch (checkId){
                        case R.id.radiocod:
                            Toast.makeText(CartActivity.this,"Cash on Delivery",Toast.LENGTH_SHORT).show();
                            placeOrder(paymethod="Cash_on_delivery",null);
                    }
                    bottomSheetDialog.dismiss();
                }
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    public void createOrder(){
        String url = UrlConstants.CREATE_ORDER;
        HashMap<String, Object> params = new HashMap<>();

        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("customer_id", CUSTOMER_ID);

            jsonObject.put("total", ttot+dFee);



            final JSONObject mainObj = new JSONObject();
            mainObj.put("", jsonObject);

            Log.d("jsonArray-->", jsonObject.toString());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject payment_response = new JSONObject(response);
                        Log.d("payment_response-->", payment_response.toString());
                        String order_id = payment_response.getString("Raz_order_id");
                        int amount = payment_response.getInt("amount");
                        String name = payment_response.getString("name");
                        String contact = payment_response.getString("contact");

                        startPayment(order_id,amount,name,contact);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CommonUtils.cancelProgressBar();
                    Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return super.getHeaders();
                }

                @Override
                public byte[] getBody() {
                    return jsonObject.toString().getBytes();
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(stringRequest);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }



    public void startPayment(String order_id,int amount,String name,String contact) {

        int tamount= (int) ttot;

        Checkout checkout = new Checkout();
        checkout.setImage(R.mipmap.ic_launcher);
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();
            options.put("order_id", order_id);
            options.put("amount", ttot);//pass amount in currency subunits 1 = 100
            options.put("currency", "INR");
            //   options.put("customer_id",CUSTOMER_ID);
            options.put("name", "Estore");
            options.put("prefill.name",name);
            options.put("prefill.contact",contact);
            // options.put("prefill.email", "customer@gmail.com");
            // options.put("notes",notes);
            options.put("description", "Get the best deal");
            options.put("theme.color", "#ffd24e");
            options.put("theme.backdrop_color", "#0000ff");
            options.put("theme.hide_topbar", "false");
            options.put("modal.animation","false");
            options.put("modal.confirm_close","true");
            options.put("timeout","300");
            options.put("remember_customer","true");
            options.put("send_sms_hash","true");



            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
        CommonUtils.cancelProgressBar();
    }

    private void placeOrder(String paymethod,String Raz_pay_id) {

        String url = UrlConstants.PLACE_ORDER;
        HashMap<String, Object> params = new HashMap<>();

        final JSONArray jsonArray = new JSONArray();
        try {
            for (int i=0,j = 0; j <= cartList.size(); j++) {
                JSONObject jsonObject = new JSONObject();
                if(j == 0){
                    jsonObject.put("customer_id", CUSTOMER_ID);
                    jsonObject.put("dFee", dFee);
                    if(paymethod.equals("online")){
                        jsonObject.put("payment_method", "online");
                        jsonObject.put("Raz_pay_id",Raz_pay_id );
                    }else{
                        jsonObject.put("payment_method", "cash_on_delivery");
                    }
                }else {
                    //     JSONObject jsonObject = new JSONObject();
                    jsonObject.put("customer_id", CUSTOMER_ID);

                    jsonObject.put("shop_id", cartList.get(i).getShop_id());
                    jsonObject.put("total_amount", cartList.get(i).getShop_id());
                    jsonObject.put("price", cartList.get(i).getSpecial_price());
                    jsonObject.put("category_id", cartList.get(i).getCategory_id());
                    jsonObject.put("sub_category_id", cartList.get(i).getSub_category_id());
                    jsonObject.put("product_id", cartList.get(i).getProduct_id());
                    jsonObject.put("quantity", cartList.get(i).getQuantity());
//                jsonObject.put("unit", cartList.get(i).uni());
//                jsonObject.put("unit", "Kg");
                    i=i+1;
                }


                jsonArray.put(jsonObject);


            }


            final JSONObject mainObj = new JSONObject();
            mainObj.put("", jsonArray);

            Log.d("jsonArray-->", jsonArray.toString());


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            CommonUtils.cancelProgressBar();

                            try {
                                JSONObject response = new JSONObject(s);
                                Log.d("response-->", response.toString());
                                String code = response.getString("code");
                                if (code.equals("1")) {
                                    notification();
                                    Toast.makeText(mActivity, "order Placed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CommonUtils.cancelProgressBar();
                    Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return super.getHeaders();
                }

                @Override
                public byte[] getBody() {
                    return jsonArray.toString().getBytes();
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(stringRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void clicked(String type, Object object) {
        CartDomain cartDomain = (CartDomain) object;
        if (type.equals("delete")) {
            deleteFromCart(cartDomain);
        } else {
            updateCart(cartDomain, type);
        }

    }

    private void updateCart(CartDomain productDomain, String num) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("customer_id", CUSTOMER_ID);
        params.put("quantity", num);
        params.put("product_id", productDomain.getProduct_id());
        params.put("price", productDomain.getSpecial_price());

//        params.put("unit", productDomain.getUnit());
        String url = UrlConstants.EDIT_CART;
        volleyUtils.callApi(mActivity, this, url, REQ_EDIT_CART, params, 1, true);

    }


    private void deleteFromCart(CartDomain cartDomain) {
        String url = UrlConstants.CART_DELETE;
        HashMap<String, Object> params = new HashMap<>();
        params.put("product_id", cartDomain.getProduct_id());
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));
        volleyUtils.callApi(mActivity, this, url, REQ_CART_DELETE, params, 1, true);
    }


    private void notification() {

        Set<CartDomain> uniquecart = new HashSet<>(cartList);
        int i = 0;
        for (CartDomain cartDomain : uniquecart) {
            Log.d("cartDomain-->", cartDomain.getShop_id());
            callNotificationAPI(cartDomain.getShop_id(), i);
            i++;
        }
    }

    private void callNotificationAPI(String shop_id, int i) {

        try {
            HashMap<String, Object> params = new HashMap<>();

            JSONObject bodyObject = new JSONObject();
            bodyObject.put("title", "New Order!!!");
            bodyObject.put("text", CUSTOMER_NAME + " ordered some items from your shop");
            bodyObject.put("click_action", "OPEN_ACTIVITY");
            bodyObject.put("sound", "default");

            JSONObject dataObject = new JSONObject();
            dataObject.put("type", "common");

            params.put("to", "/topics/" + shop_id);
            params.put("priority", "high");
            params.put("type", "common");
            params.put("notification", bodyObject);
            params.put("data", dataObject);

            JSONObject androidObject = new JSONObject();
            JSONObject androidNotObject = new JSONObject();
            androidNotObject.put("sound", "default");
            androidObject.put("notification", androidNotObject);
            params.put("android", androidObject);

            volleyUtils.sendNotification(mActivity, "common", params, this, i);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void NotificationSuccess(JSONObject response, int requestcode) {
        Log.d("requestcode-->", requestcode + "");
        // if (requestcode == cartList.size()) {
        Intent intent = new Intent(mActivity, SuccessActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST", cartList);
        intent.putExtra("BUNDLE", args);
        intent.putExtra("total", tot+dFee);
        finish();
        startActivity(intent);
        //  }
    }

    @Override
    public void NotificationError(String msg, int requestcode) {

    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.d("Payment_Success-->", s);
        Toast.makeText(this,"Payment Successful\n"+s,Toast.LENGTH_SHORT).show();
        //String Raz_payment_id;

        placeOrder(paymethod="online",s);
    }



    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this,"Payment Failed!\n"+s,Toast.LENGTH_LONG).show();
    }
}