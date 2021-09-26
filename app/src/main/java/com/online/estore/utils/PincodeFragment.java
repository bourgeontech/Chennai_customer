package com.online.estore.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.chaos.view.PinView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.annotations.NotNull;
import com.online.estore.R;
import com.online.estore.activity.MainActivity;

public class PincodeFragment extends BottomSheetDialogFragment {

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    public PincodeFragment() {
        // Required empty public constructor
    }


    OptionListener optionListener;

    public interface OptionListener {
        void optionSelected(String item);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NotNull final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.fragment_pincode, null);
        dialog.setContentView(contentView);


        sharedPreferences = getActivity().getSharedPreferences("ogl-app", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));


        final PinView pinView = contentView.findViewById(R.id.pinView);
        TextView submit = contentView.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pinView.getText().toString().trim();
                if (pin.length() == 6) {
                    editor.putString(Constants.ClickedPincode, pin);
                    editor.apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("from", "0");
                    intent.putExtra("pincode", pin);
                    dialog.dismiss();
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Enter pincode", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

    }


}
