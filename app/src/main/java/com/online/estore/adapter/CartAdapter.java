package com.online.estore.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.online.estore.R;
import com.online.estore.databinding.ItemCartBinding;
import com.online.estore.databinding.ItemShopBinding;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.models.CartDomain;
import com.online.estore.models.ProductDomain;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;

import java.util.ArrayList;
import java.util.HashMap;

import static com.online.estore.utils.UrlConstants.REQ_ADD_TO_CART;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    ArrayList<CartDomain> cartList;
    ClickedItem clickedItem;

    public CartAdapter(Context context, ArrayList<CartDomain> cartList, ClickedItem clickedItem) {
        this.context = context;
        this.cartList = cartList;
        this.clickedItem = clickedItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_cart, parent, false);

        return new CartAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String ss = cartList.get(position).getQuantity();
        final int[] pCount = {Integer.parseInt(ss)};

        try {
            byte[] decodedString = Base64.decode(cartList.get(position).getProduct_image(), Base64.NO_WRAP);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(40));

            Glide.with(context)
                    .load(decodedByte)
                    .centerCrop()
                    .apply(requestOptions)
                    .placeholder(0)
                    .into(holder.binding.productImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.binding.productName.setText(cartList.get(position).getProduct_name());
        holder.binding.tvOriginalPrice.setText("Rs " + cartList.get(position).getSpecial_price());
        holder.binding.tvSpecialPrice.setText("Rs " + cartList.get(position).getSpecial_price());
        holder.binding.tvDiscount.setText("Save " + cartList.get(position).getDiscount() + "%");
        holder.binding.tvUnit.setText(cartList.get(position).getUnit());

        holder.binding.productCount.setText(cartList.get(position).getQuantity());

        holder.binding.deleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.clicked("delete", cartList.get(position));
            }
        });

        holder.binding.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pCount[0] > 0) {
                    pCount[0] = pCount[0] - 1;
                    holder.binding.productCount.setText(pCount[0] + "");
                    clickedItem.clicked(String.valueOf(pCount[0]), cartList.get(position));
                }
            }
        });

        holder.binding.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pCount[0] >= 0) {
                    pCount[0] = pCount[0] + 1;
                    holder.binding.productCount.setText(pCount[0] + "");
                    clickedItem.clicked(String.valueOf(pCount[0]), cartList.get(position));
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        if (cartList != null)
            return cartList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemCartBinding binding;

        public ViewHolder(@NonNull ItemCartBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
