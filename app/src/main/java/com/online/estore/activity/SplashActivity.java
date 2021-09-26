package com.online.estore.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.online.estore.R;
import com.online.estore.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (LATTITUDE.equals("0") || LATTITUDE.equals("")) {
                    Intent in = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, TempActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        };
        Handler mHandler = new Handler();
        mHandler.postDelayed(mRunnable, SPLASH_DISPLAY_LENGTH);


    }
}