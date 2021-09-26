package com.online.estore.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.annotations.NotNull;
import com.online.estore.R;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityProfileBinding;
import com.online.estore.utils.CommonUtils;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.VolleyInterface;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.online.estore.utils.UrlConstants.REQ_EDIT_CUSTOMER;

public class ProfileActivity extends BaseActivity implements View.OnClickListener, VolleyInterface, EasyPermissions.PermissionCallbacks {

    ActivityProfileBinding binding;
    String name, address, landmark;
    private String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    Uri imageUri;
    String profileImage;
    boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        mActivity = this;

//        CommonUtils.showLabeledImage(CUSTOMER_NAME.toUpperCase().charAt(0), binding.userImage, CommonUtils.getRandomColor());

        binding.edtName.setText(CUSTOMER_NAME);
        binding.edtAddress.setText(ADDRESS);
        binding.edtPin.setText(PINCODE);
        binding.edtLandMark.setText(LANDMARK);
        try {
            if (!CUSTOMER_PHOTO.equals("")) {
                byte[] decodedString = Base64.decode(CUSTOMER_PHOTO, Base64.NO_WRAP);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                binding.profileImage.setImageBitmap(decodedByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.btnEdit.setOnClickListener(this);
        binding.profileImage.setOnClickListener(this);
        binding.back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEdit:
                check();
                break;
            case R.id.back:
                finish();
                break;

            case R.id.profile_image:
                if (EasyPermissions.hasPermissions(mActivity, perms)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 101);
                } else {
                    requestPerms();
                }
                break;
        }
    }

    private void requestPerms() {

        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, 102, perms)
                        .setRationale("Need storage permissions")
                        .build());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perm) {
        if (EasyPermissions.hasPermissions(mActivity, perms)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 101);
        } else {
            Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
    }

    private void check() {
        name = binding.edtName.getText().toString().trim();
        address = binding.edtAddress.getText().toString().trim();
        landmark = binding.edtLandMark.getText().toString().trim();

        if (name.equals("")) {
            Toast.makeText(mActivity, "Enter name", Toast.LENGTH_SHORT).show();
        } else if (address.equals("")) {
            Toast.makeText(mActivity, "Enter address", Toast.LENGTH_SHORT).show();
        } else if (landmark.equals("")) {
            Toast.makeText(mActivity, "Enter landmark", Toast.LENGTH_SHORT).show();
        } else {
            editProfile();
        }
    }

    private void editProfile() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));
        params.put("customer_name", name);
        params.put("phone", sharedPreferences.getString(Constants.PHONE, "0"));
        params.put("address", address);
        params.put("pincode", PINCODE);
        params.put("landmark", landmark);

        if (imageChanged) {
            Bitmap bitmap = loadBitmapFromView(binding.profileImage);
            convertIntoString(bitmap);
            params.put("customer_photo", profileImage);
        } else {
            params.put("customer_photo", CUSTOMER_PHOTO);
        }
        params.put("password", "");
        params.put("old_password", "");
        params.put("change_pass_flag", false);

        String url = UrlConstants.EDIT_CUSTOMER;
        volleyUtils.callApi(mActivity, this, url, REQ_EDIT_CUSTOMER, params, 1, true);
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    private void convertIntoString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); // bm is the bitmap object
        byte[] b = baos.toByteArray();
        profileImage = Base64.encodeToString(b, Base64.DEFAULT);
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_EDIT_CUSTOMER) {
            try {
                String code = response.getString("code");
                Toast.makeText(mActivity, response.getString("status"), Toast.LENGTH_SHORT).show();
                if (code.equals("1")) {
                    editor.putString(Constants.ADDRESS, address);
                    editor.putString(Constants.LANDMARK, landmark);
                    editor.putString(Constants.CUSTOMER_NAME, name);
                    if (imageChanged) {
                        editor.putString(Constants.CUSTOMER_PHOTO, profileImage);
                    }
                    editor.apply();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            final InputStream imageStream;
            try {
                imageUri = data.getData();
                if (imageUri != null) {
                    imageChanged = true;
                    imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    binding.profileImage.setImageBitmap(selectedImage);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mActivity, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
}