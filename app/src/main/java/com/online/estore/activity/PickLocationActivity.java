package com.online.estore.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.online.estore.utils.Constants.PERMISSIONS;
import static com.online.estore.utils.UrlConstants.REQ_CUSTOMER_LOCATION_SAVE;

public class PickLocationActivity extends BaseActivity implements View.OnClickListener, VolleyInterface {


    ActivityPickLocationBinding binding;
    private double currentLatitude;
    private double currentLongitude;

    LocationManager locationManager;

    private String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    String shopId;
    String shopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_location);

        mActivity = this;

        binding.btnAddLocation.setOnClickListener(this);

//        if (EasyPermissions.hasPermissions(mActivity, perms)) {
//            getLocation();
//        } else {
//            requestPerms();
//        }

    }


    private void requestPerms() {

        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, PERMISSIONS, perms)
                        .setRationale("Need Location Permissions")
                        .build());

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
                    ActivityCompat.requestPermissions(PickLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                        LocationServices.getFusedLocationProviderClient(mActivity).
                                removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int lastLocationIndex = locationResult.getLocations().size() - 1;
                            currentLatitude = locationResult.getLocations().get(lastLocationIndex).getLatitude();
                            currentLongitude = locationResult.getLocations().get(lastLocationIndex).getLongitude();

                            updateLocations();

                        }

                        CommonUtils.cancelProgressBar();
                    }
                }, Looper.getMainLooper());
    }

    private void updateLocations() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));
        params.put("latitude", currentLatitude);
        params.put("longitude", currentLongitude);
        params.put("pincode", sharedPreferences.getString(Constants.PINCODE, "0"));

        editor.putString(Constants.LATTITUDE, String.valueOf(currentLatitude));
        editor.putString(Constants.LONGITUDE, String.valueOf(currentLongitude));
        editor.apply();

        String url = UrlConstants.CUSTOMER_LOCATION_SAVE;
        volleyUtils.callApi(mActivity, this, url, REQ_CUSTOMER_LOCATION_SAVE, params, 1, true);
    }


    private void gotoDashBoard() {
        Intent intent = new Intent(PickLocationActivity.this, TempActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_CUSTOMER_LOCATION_SAVE) {
            locationSaved(response);

        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    private void locationSaved(JSONObject response) {
        try {
            if (response.getString("code").equals("1")) {
                gotoDashBoard();
            } else {
                Toast.makeText(mActivity, response.getString("status"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}