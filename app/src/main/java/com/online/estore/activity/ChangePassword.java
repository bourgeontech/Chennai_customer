package com.online.estore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.online.estore.R;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityChangePasswordBinding;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.VolleyInterface;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;

import static com.online.estore.utils.UrlConstants.REQ_EDIT_CUSTOMER;

public class ChangePassword extends BaseActivity implements View.OnClickListener, VolleyInterface {

    ActivityChangePasswordBinding binding;
    String p1, p2, p3;

    boolean showPassword1 = true, showPassword2 = true, showPassword3 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);

        mActivity = this;

        binding.btnUpdate.setOnClickListener(this);
        binding.back.setOnClickListener(this);


        binding.showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPassword1 = !showPassword1;
                Log.d("showPassword", showPassword1 + "");

                if (showPassword1) {
                    Log.d("showPassword", "1");
                    binding.showPassword.setImageResource(R.drawable.viewpassword);
                    binding.p1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    Log.d("showPassword", "2");
                    binding.showPassword.setImageResource(R.drawable.hidepassword);
                    binding.p1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                binding.p1.setSelection(binding.p1.length());
            }
        });

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
            case R.id.back:
                finish();
                break;
        }
    }

    private void check() {
        p1 = binding.p1.getText().toString().trim();
        p2 = binding.p2.getText().toString().trim();
        p3 = binding.p3.getText().toString().trim();
        if (p1.equals("") || p1.length() < 8) {
            Toast.makeText(mActivity, "Enter valid password", Toast.LENGTH_SHORT).show();
        } else if (p2.equals("") || p2.length() < 8) {
            Toast.makeText(mActivity, "Enter valid password", Toast.LENGTH_SHORT).show();
        } else if (p3.equals("") || p3.length() < 8) {
            Toast.makeText(mActivity, "Enter valid password", Toast.LENGTH_SHORT).show();
        } else if (!p2.equals(p3)) {
            Toast.makeText(mActivity, "Passwords are not same", Toast.LENGTH_SHORT).show();
        } else if (p1.equals(p2)) {
            Toast.makeText(mActivity, "Choose a different password", Toast.LENGTH_SHORT).show();
        } else {
            updatePassword();
        }
    }

    private void updatePassword() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));
        params.put("customer_name", sharedPreferences.getString(Constants.CUSTOMER_NAME, ""));
        params.put("phone", sharedPreferences.getString(Constants.PHONE, ""));
        params.put("address", sharedPreferences.getString(Constants.ADDRESS, ""));
        params.put("pincode", sharedPreferences.getString(Constants.PINCODE, ""));
        params.put("landmark", sharedPreferences.getString(Constants.LANDMARK, ""));
        params.put("customer_photo", sharedPreferences.getString(Constants.CUSTOMER_PHOTO, ""));
        params.put("password", getSha1Hex(p2));
        params.put("old_password", getSha1Hex(p1));
        params.put("change_pass_flag", true);

        String url = UrlConstants.EDIT_CUSTOMER;
        volleyUtils.callApi(mActivity, this, url, REQ_EDIT_CUSTOMER, params, 1, true);

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

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {

        if (requestcode == REQ_EDIT_CUSTOMER) {
            passwordResponse(response);
        }
    }

    private void passwordResponse(JSONObject response) {

        try {
            String code = response.getString("code");
            Toast.makeText(mActivity, "" + response.getString("status"), Toast.LENGTH_SHORT).show();
            if (code.equals("1")) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }
}