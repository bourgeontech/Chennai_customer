package com.online.estore.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.online.estore.R;

import java.util.Objects;
import java.util.Random;

public class CommonUtils {

    private static Dialog progressdialog;
    private static Dialog errorDIalog;

    public static void setProgressBar(Context context) {
        progressdialog = new Dialog(context, R.style.AlertDialogCustom);
        progressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressdialog.setContentView(R.layout.progressdialog);
        progressdialog.setCancelable(false);

        Objects.requireNonNull(progressdialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        progressdialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        if (!((Activity) context).isFinishing()) {
            progressdialog.show();
        }

    }

    public static void setErrorDialog(Context context, String stitle, String smsg) {
        errorDIalog = new Dialog(context, R.style.AlertDialogCustom);
        errorDIalog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDIalog.setContentView(R.layout.error_dialog);
        errorDIalog.setCancelable(true);

        Objects.requireNonNull(errorDIalog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        errorDIalog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        if (!((Activity) context).isFinishing()) {
            errorDIalog.show();
        }

        TextView btnDismiss = errorDIalog.findViewById(R.id.btnDismiss);
        TextView title = errorDIalog.findViewById(R.id.title);
        TextView msg = errorDIalog.findViewById(R.id.msg);

        title.setText(stitle);
        msg.setText(smsg);

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDIalog.dismiss();
            }
        });

    }

    public static void showLabeledImage(char customerName, ImageView imageView, int bgcolor) {
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .bold()
                .endConfig()
                .buildRound(String.valueOf(customerName), bgcolor);
        imageView.setImageDrawable(drawable);
    }

    public static int getRandomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static void cancelProgressBar() {

        if (progressdialog != null && progressdialog.isShowing()) {
            progressdialog.cancel();
        }

    }

    public static void setAlerDialog(Context context, String title, String msg, boolean needNegativeBtn, String posBtn, final DialogInterface.OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(posBtn, listener);
        if (needNegativeBtn) {
            builder.setNegativeButton(android.R.string.no, null);
        }

        builder.show();

    }


    public static boolean checkConnectivity(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null) {
                return false;
            } else if (info.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return false;
    }

    public static String districtCode(String ditrict) {
        String res = "";
        switch (ditrict.toLowerCase()) {
            case "thiruvananthapuram":
                res = "1";
                break;
            case "kollam":
                res = "2";
                break;
            case "pathanamthitta":
                res = "3";
                break;
            case "alappuzha":
                res = "4";
                break;
            case "kottayam":
                res = "5";
                break;
            case "idukki":
                res = "6";
                break;
            case "ernakulam":
                res = "7";
                break;
            case "thrissur":
                res = "8";
                break;
            case "palakkad":
                res = "9";
                break;
            case "malappuram":
                res = "10";
                break;
            case "kozhikode":
                res = "11";
                break;
            case "wayanad":
                res = "12";
                break;
            case "kannur":
                res = "13";
                break;
            case "kasaragod":
                res = "14";
                break;
        }

        return res;
    }
}
