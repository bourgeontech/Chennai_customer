package com.online.estore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.online.estore.R;
import com.online.estore.adapter.SideBarAdapter;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityTempBinding;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.models.DrawerDomain;
import com.online.estore.utils.CommonUtils;
import com.online.estore.utils.Constants;
import com.online.estore.utils.PincodeFragment;

import java.util.ArrayList;
import java.util.List;

public class TempActivity extends BaseActivity implements View.OnClickListener, ClickedItem {

    ActivityTempBinding binding;
    PincodeFragment pincodeFragment = new PincodeFragment();

    SideBarAdapter sideBarAdapter;
    List<DrawerDomain> menuList = new ArrayList<>();
    String cTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_temp);

        mActivity = this;

        cTopic = "c" + CUSTOMER_ID;

        FirebaseMessaging.getInstance().subscribeToTopic(cTopic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("topic-->", "Subscribed");
            }
        });

        if (getIntent().hasExtra("goto")) {
            Intent intent = new Intent(mActivity, MainActivity.class);
            startActivity(intent);
        }


        setUpSidebar();

        binding.btnShop.setOnClickListener(this);
        binding.btnCategory.setOnClickListener(this);
        binding.btnSearch.setOnClickListener(this);
        binding.btnPincode.setOnClickListener(this);
        binding.navIcon.setOnClickListener(this);
        binding.logout.setOnClickListener(this);
        binding.location.setOnClickListener(this);

        onNewIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String type = intent.getStringExtra("type");
            if (type != null) {
                startActivity(new Intent(mActivity, MyOrderActivity.class));
            }
        } else {
            Log.d("intents-->", "null");
        }

    }

    private void setUpSidebar() {

        menuList.clear();
//        menuList.add(new DrawerDomain(R.drawable.home_icon, "Dashboard"));
        menuList.add(new DrawerDomain(R.drawable.person_icon, "Edit Profile"));
        menuList.add(new DrawerDomain(R.drawable.order_icon, "My Orders"));
        menuList.add(new DrawerDomain(R.drawable.lock_icon, "Change Password"));
        menuList.add(new DrawerDomain(R.drawable.logout_icon, "Logout"));

//        binding.layoutNav.menuListView.setHasFixedSize(true);
//        binding.layoutNav.menuListView.setLayoutManager(new LinearLayoutManager(this));
//        sideBarAdapter = new SideBarAdapter(this, menuList, this);
//        binding.layoutNav.menuListView.setAdapter(sideBarAdapter);
    }

    @Override
    public void clicked(String type, Object object) {

//        binding.drawerLayout.closeDrawer(GravityCompat.START);
        String item = (String) object;
        switch (item) {
            case "Dashboard":
                finish();
                break;
            case "Edit Profile":
                startActivity(new Intent(mActivity, ProfileActivity.class));
                break;
            case "Change Password":
                startActivity(new Intent(mActivity, ChangePassword.class));
                break;
            case "My Orders":
                startActivity(new Intent(mActivity, MyOrderActivity.class));
                break;
            case "Logout":
                CommonUtils.setAlerDialog(mActivity, "Confirm Logout", "Are you sure you want to logout?", true, "LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logoutFunction();
                    }
                });
                break;
        }
    }

    private void logoutFunction() {
        CommonUtils.setProgressBar(mActivity);
        editor.clear();
        editor.apply();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(cTopic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.cancelProgressBar();
                Log.d("topic-->", "UnSubscribed");
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                String searchKey = binding.btnSearchBar.getText().toString().trim();
                if (searchKey.equals("")) {
                    Toast.makeText(mActivity, "Enter product name", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString(Constants.ClickedPincode, PINCODE);
                    editor.apply();
                    binding.btnSearchBar.setText("");
                    Intent intent = new Intent(mActivity, ViewProductActivity.class);
                    intent.putExtra("from", "0");
                    intent.putExtra("product_name", searchKey);
                    intent.putExtra("pincode", PINCODE);
                    intent.putExtra("showall", "");
                    startActivity(intent);
                }
                break;
            case R.id.btnCategory:
                editor.putString(Constants.ClickedPincode, PINCODE);
                editor.apply();
                Intent catIntent = new Intent(mActivity, CategoryActivity.class);
                catIntent.putExtra("from", "0");
                catIntent.putExtra("pincode", PINCODE);
                startActivity(catIntent);
                break;
            case R.id.btnShop:
                editor.putString(Constants.ClickedPincode, PINCODE);
                editor.apply();
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.putExtra("from", "1");
                intent.putExtra("pincode", "");
                startActivity(intent);
                break;
            case R.id.btnPincode:
                pincodeFragment.show(getSupportFragmentManager(), pincodeFragment.getTag());
                break;
            case R.id.logout:
                CommonUtils.setAlerDialog(mActivity, "Confirm Logout", "Are you sure you want to logout?", true, "LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logoutFunction();
                    }
                });
                break;
            case R.id.location:
                Intent i = new Intent(this, LocationActivity.class);
                startActivityForResult(i, 501);
                break;
            case R.id.navIcon:
//                binding.drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 501) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                editor.putString(Constants.ClickedPincode, result);
                editor.apply();
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.putExtra("from", "0");
                intent.putExtra("pincode", result);
                startActivity(intent);
            }
        }
    }
}