package com.online.estore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.online.estore.R;
import com.online.estore.base.BaseActivity;
import com.online.estore.databinding.ActivityBillBinding;
import com.online.estore.models.CartDomain;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class BillActivity extends BaseActivity {

    ActivityBillBinding binding;
    BillAdapter adapter;
    ArrayList<CartDomain> billList = new ArrayList<>();
    String total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bill);

        mActivity = this;

        total = getIntent().getStringExtra("total");
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        billList = (ArrayList<CartDomain>) args.getSerializable("ARRAYLIST");

        if (billList != null) {
            Log.d("billList-->", billList.size() + "");
        } else {
            Log.d("billList-->", "null");
        }

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        setupRecycler();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_bitMap_to_Apps();
            }
        });


        binding.tvtotal.setText(total);
        binding.tvNum.setText(billList.size() + "");

    }

    public void share_bitMap_to_Apps() {

        Bitmap bitmap = loadBitmapFromView(binding.titleLayout);
        Intent i = new Intent(Intent.ACTION_SEND);

        i.setType("image/*");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        i.putExtra(Intent.EXTRA_STREAM, getImageUri(mActivity, bitmap));
        i.putExtra(Intent.EXTRA_TEXT, "From E Store App");
        try {
            startActivity(Intent.createChooser(i, "Select ..."));
        } catch (android.content.ActivityNotFoundException ex) {

            ex.printStackTrace();
        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }


    private void setupRecycler() {
        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new BillAdapter(mActivity, billList);
        binding.recycler.setAdapter(adapter);
    }
}