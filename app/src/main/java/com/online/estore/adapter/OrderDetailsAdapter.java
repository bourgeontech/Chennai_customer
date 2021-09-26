package com.online.estore.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
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
import com.online.estore.databinding.ItemOrderDetailsBinding;
import com.online.estore.models.OrderDetailsDomain;

import java.util.ArrayList;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    Context context;
    ArrayList<OrderDetailsDomain> orderDetailsList;

    public OrderDetailsAdapter(Context context, ArrayList<OrderDetailsDomain> orderDetailsList) {
        this.context = context;
        this.orderDetailsList = orderDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderDetailsBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_order_details, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String ss = orderDetailsList.get(position).getQuantity();
        final int[] pCount = {Integer.parseInt(ss)};

        try {
            byte[] decodedString = Base64.decode(orderDetailsList.get(position).getProduct_image(), Base64.NO_WRAP);
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

        holder.binding.productName.setText(orderDetailsList.get(position).getProduct_name());
        holder.binding.tvSpecialPrice.setText("Rs " + orderDetailsList.get(position).getPrice());
        holder.binding.tvUnit.setText(orderDetailsList.get(position).getUnit());

        holder.binding.productCount.setText(orderDetailsList.get(position).getQuantity());
    }

    @Override
    public int getItemCount() {
        if (orderDetailsList != null)
            return orderDetailsList.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemOrderDetailsBinding binding;

        public ViewHolder(@NonNull ItemOrderDetailsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
