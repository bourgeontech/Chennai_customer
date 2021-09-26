package com.online.estore.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.annotations.NotNull;
import com.online.estore.R;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityPickLocationBinding;
import com.online.estore.utils.CommonUtils;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.VolleyInterface;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.online.estore.utils.Constants.PERMISSIONS;
import static com.online.estore.utils.UrlConstants.REQ_CUSTOMER_LOCATION_SAVE;

public class LocationActivity extends BaseActivity implements View.OnClickListener {


    ActivityPickLocationBinding binding;
    private double currentLatitude;
    private double currentLongitude;

    LocationManager locationManager;

    private String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    String postalCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_location);

        mActivity = this;

        binding.back.setVisibility(View.VISIBLE);
        binding.tvAddlocation.setText("Choose My Location");

        binding.btnAddLocation.setOnClickListener(this);
        binding.back.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT).show();
//        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddLocation:

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS);
                } else {
                    final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    } else {
                        getCurrentLocation();
                    }
                }


                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to turn on Location first")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getCurrentLocation() {

        CommonUtils.setProgressBar(mActivity);
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(mActivity).
                requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        CommonUtils.cancelProgressBar();
                        LocationServices.getFusedLocationProviderClient(mActivity).
                                removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int lastLocationIndex = locationResult.getLocations().size() - 1;
                            currentLatitude = locationResult.getLocations().get(lastLocationIndex).getLatitude();
                            currentLongitude = locationResult.getLocations().get(lastLocationIndex).getLongitude();
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(mActivity, Locale.getDefault());

//                            binding.locationLayout.setVisibility(View.VISIBLE);

                            try {
                                addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                postalCode = addresses.get(0).getPostalCode();
//                                String knownName = addresses.get(0).getFeatureName();

                                binding.edtPinCode.setText("Pincode : " + postalCode);
                                Log.d("location-->", address + " " + city + " " + state + " " + country + " " + postalCode);

                                setErrorDialog(address);
//                                gotoDashBoard();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }


                    }
                }, Looper.getMainLooper());
    }

    private void gotoDashBoard() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", postalCode);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    public void setErrorDialog(String address) {
        final Dialog errorDIalog;
        errorDIalog = new Dialog(mActivity, R.style.AlertDialogCustom);
        errorDIalog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDIalog.setContentView(R.layout.select_location);
        errorDIalog.setCancelable(true);

        Objects.requireNonNull(errorDIalog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        errorDIalog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        if (!mActivity.isFinishing()) {
            errorDIalog.show();
        }

        TextView tvLocation = errorDIalog.findViewById(R.id.tvLocation);
        TextView btnSearch = errorDIalog.findViewById(R.id.btnSearch);
        TextView btnCategory = errorDIalog.findViewById(R.id.btnCategory);
        TextView btnShop = errorDIalog.findViewById(R.id.btnShop);

        tvLocation.setText(address);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDIalog.dismiss();

                editor.putString(Constants.ClickedPincode, postalCode);
                editor.apply();
                Intent intent = new Intent(mActivity, ViewProductActivity.class);
                intent.putExtra("from", "0");
                intent.putExtra("product_name", "");
                intent.putExtra("pincode", postalCode);
                intent.putExtra("showall", "");
                finish();
                startActivity(intent);
            }
        });

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDIalog.dismiss();
                editor.putString(Constants.ClickedPincode, postalCode);
                editor.apply();
                Intent catIntent = new Intent(mActivity, CategoryActivity.class);
                catIntent.putExtra("from", "0");
                catIntent.putExtra("pincode", postalCode);
                finish();
                startActivity(catIntent);
            }
        });

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDIalog.dismiss();
                editor.putString(Constants.ClickedPincode, postalCode);
                editor.apply();
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.putExtra("from", "0");
                intent.putExtra("pincode", postalCode);
                finish();
                startActivity(intent);
            }
        });

    }
}