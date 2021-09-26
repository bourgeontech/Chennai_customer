package com.online.estore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.online.estore.R;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityForgotBinding;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.VolleyInterface;

import org.json.JSONObject;

import java.util.HashMap;

import static com.online.estore.utils.UrlConstants.REQ_CHECK_PHONE_NUM;

public class ForgotActivity extends BaseActivity implements View.OnClickListener, VolleyInterface {

    ActivityForgotBinding binding;
    String num;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot);

        mActivity = this;
        reference = FirebaseDatabase.getInstance().getReference();

        binding.btnGoBack.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGoBack:
            case R.id.back:
                finish();
                break;
            case R.id.btnConfirm:
                check();
                break;
        }
    }

    private void check() {
        num = binding.edtPhone.getText().toString().trim();
        if (num.equals("")) {
            Toast.makeText(mActivity, "Enter phone number", Toast.LENGTH_SHORT).show();
        } else if (num.length() < 10) {
            Toast.makeText(mActivity, "Enter valid phone number", Toast.LENGTH_SHORT).show();
        } else {
            checkInDb();
        }
    }

    private void checkInDb() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("phone", num);
        String url = UrlConstants.CHECK_PHONE_NUM;
        volleyUtils.callApi(mActivity, this, url, REQ_CHECK_PHONE_NUM, params, 1, true);


    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {

        if (requestcode == REQ_CHECK_PHONE_NUM) {
            checkNumexists(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void checkNumexists(JSONObject response) {
        try {
            String code = response.getString("code");
            if (code.equals("1")) {
                Intent intent = new Intent(mActivity, ForgotOtpActivity.class);
                intent.putExtra("number", num);
                startActivity(intent);
            } else {
                Toast.makeText(mActivity, "" + response.getString("status"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}