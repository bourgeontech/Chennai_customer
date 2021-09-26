package com.online.estore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.online.estore.R;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityChangePassword2Binding;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.VolleyInterface;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;

import static com.online.estore.utils.UrlConstants.REQ_EDIT_CUSTOMER;
import static com.online.estore.utils.UrlConstants.REQ_FORGOT_PASSWORD;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener, VolleyInterface {

    ActivityChangePassword2Binding binding;
    String number;

    String p2, p3;

    boolean showPassword1 = true, showPassword2 = true, showPassword3 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password2);

        mActivity = this;

        number = getIntent().getStringExtra("number");

        binding.btnUpdate.setOnClickListener(this);


        binding.showPassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPassword2 = !showPassword2;
                Log.d("showPassword", showPassword2 + "");

                if (showPassword2) {
                    Log.d("showPassword", "1");
                    binding.showPassword2.setImageResource(R.drawable.viewpassword);
                    binding.p2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    Log.d("showPassword", "2");
                    binding.showPassword2.setImageResource(R.drawable.hidepassword);
                    binding.p2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                binding.p2.setSelection(binding.p2.length());
            }
        });

        binding.showPassword3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPassword3 = !showPassword3;
                Log.d("showPassword", showPassword3 + "");

                if (showPassword3) {
                    Log.d("showPassword", "1");
                    binding.showPassword3.setImageResource(R.drawable.viewpassword);
                    binding.p3.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    Log.d("showPassword", "2");
                    binding.showPassword3.setImageResource(R.drawable.hidepassword);
                    binding.p3.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                binding.p3.setSelection(binding.p3.length());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdate:
                check();
                break;
        }
    }

    private void check() {
        p2 = binding.p2.getText().toString().trim();
        p3 = binding.p3.getText().toString().trim();
        if (p2.equals("") || p2.length() < 8) {
            Toast.makeText(mActivity, "Enter valid password", Toast.LENGTH_SHORT).show();
        } else if (p3.equals("") || p3.length() < 8) {
            Toast.makeText(mActivity, "Enter valid password", Toast.LENGTH_SHORT).show();
        } else if (!p2.equals(p3)) {
            Toast.makeText(mActivity, "Passwords are not same", Toast.LENGTH_SHORT).show();
        } else {
            updatePassword();
        }

    }

    private void updatePassword() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("phone", number);
        params.put("new_password", getSha1Hex(p2));

        String url = UrlConstants.FORGOT_PASSWORD;
        volleyUtils.callApi(mActivity, this, url, REQ_FORGOT_PASSWORD, params, 1, true);

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_FORGOT_PASSWORD) {
            passwordResponse(response);
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void passwordResponse(JSONObject response) {
        try {
            String code = response.getString("code");
            Toast.makeText(mActivity, "" + response.getString("status"), Toast.LENGTH_SHORT).show();
            if (code.equals("1")) {
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSha1Hex(String clearString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}