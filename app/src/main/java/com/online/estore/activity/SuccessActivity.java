package com.online.estore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.online.estore.R;
import com.online.estore.adapter.CartAdapter;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivitySuccessBinding;
import com.online.estore.models.CartDomain;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.VolleyInterface;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.online.estore.utils.UrlConstants.REQ_EDIT_CUSTOMER;
import static com.online.estore.utils.UrlConstants.REQ_Suggestion;

public class SuccessActivity extends BaseActivity implements View.OnClickListener, VolleyInterface {

    ActivitySuccessBinding binding;
    ArrayList<CartDomain> list = new ArrayList<>();
    String total;

    Dialog inputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_success);

        mActivity = this;

        total = getIntent().getStringExtra("total");
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        list = (ArrayList<CartDomain>) args.getSerializable("ARRAYLIST");

        binding.btnTick.setOnClickListener(this);
        binding.callCustomerCare.setOnClickListener(this);
        binding.btnSuggestion.setOnClickListener(this);
        binding.imgShare.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTick: {
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from", "0");
                intent.putExtra("pincode", PINCODE);
                finish();
                startActivity(intent);
            }
            break;
            case R.id.callCustomerCare:
                callCustomerCare();
                break;
            case R.id.btnSuggestion:
                setProgressBar();
                break;
            case R.id.imgShare:
                Intent intent = new Intent(mActivity, BillActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) list);
                intent.putExtra("BUNDLE", args);
                intent.putExtra("total", total);
                startActivity(intent);
                break;
        }
    }

    public void setProgressBar() {
        inputDialog = new Dialog(mActivity, R.style.AlertDialogCustom);
        inputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inputDialog.setContentView(R.layout.input_layout);
        inputDialog.setCancelable(true);

        Objects.requireNonNull(inputDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        inputDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        if (!((Activity) mActivity).isFinishing()) {
            inputDialog.show();
        }

        final EditText input = inputDialog.findViewById(R.id.input);
        TextView btnSubmit = inputDialog.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = input.getText().toString().trim();
                if (!s.equals("")) {
                    inputDialog.dismiss();
                    callSuggestionService(s);
                } else {
                    Toast.makeText(mActivity, "Enter your suggestion", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void callSuggestionService(String s) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));
        params.put("suggestion", s);
        String url = UrlConstants.Suggestion;
        volleyUtils.callApi(mActivity, this, url, REQ_Suggestion, params, 1, true);
    }


    private void callCustomerCare() {
        Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+919847132202"));
        startActivity(intentDial);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_Suggestion) {
            suggestionResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void suggestionResponse(JSONObject response) {
        try {
            String code = response.getString("code");
            Toast.makeText(mActivity, "" + response.getString("status"), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}