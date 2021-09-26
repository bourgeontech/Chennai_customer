package com.online.estore.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.online.estore.R;
import com.online.estore.adapter.OrderAdapter;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityMyOrderBinding;
import com.online.estore.models.OrderDomain;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.VolleyInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import static com.online.estore.utils.UrlConstants.REQ_CART_LIST;
import static com.online.estore.utils.UrlConstants.REQ_myOrderList;

public class MyOrderActivity extends BaseActivity implements VolleyInterface {

    ActivityMyOrderBinding binding;
    OrderAdapter adapter;
    ArrayList<OrderDomain> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_order);

        mActivity = this;

        setupRecyclerView();

        callService();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setupRecyclerView() {
        binding.recylerView.setHasFixedSize(true);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new OrderAdapter(mActivity, orderList);
        binding.recylerView.setAdapter(adapter);
    }

    private void callService() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));

        String url = UrlConstants.myOrderList;
        volleyUtils.callApi(mActivity, this, url, REQ_myOrderList, params, 1, true);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_myOrderList) {
            orderListResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void orderListResponse(JSONObject response) {
        try {

            orderList.clear();
            String code = response.getString("code");

            if (code.equals("1")) {
                JSONArray order_detail = response.getJSONArray("order_detail");
                for (int i = 0; i < order_detail.length(); i++) {
                    JSONObject jsonObject = order_detail.getJSONObject(i);
                    String order_id = jsonObject.getString("order_id");
                    String order_date = jsonObject.getString("order_date");
                    String order_time = jsonObject.getString("order_time");
                    String payable_amount = jsonObject.getString("payable_amount");
                    String delivery_status = jsonObject.getString("delivery_status");

                    orderList.add(new OrderDomain(order_id, order_date, order_time, payable_amount, delivery_status));
                }
                //sort
                orderList.sort(new Sorter());

                if (orderList.size() == 0) {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyLayout.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    binding.recylerView.scheduleLayoutAnimation();
                }
            } else {
                binding.emptyLayout.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Sorting the orderlist by Order Id
    public class Sorter implements Comparator<OrderDomain>
    {
        @Override
        public int compare(OrderDomain o1, OrderDomain o2) {
            return o2.getOrder_id().compareTo(o1.getOrder_id());
        }
    }
}