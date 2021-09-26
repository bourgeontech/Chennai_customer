package com.online.estore.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.online.estore.R;
import com.online.estore.activity.OrderDetailsActivity;
import com.online.estore.databinding.ItemCartBinding;
import com.online.estore.databinding.ItemOrderBinding;
import com.online.estore.models.OrderDomain;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    Context context;
    ArrayList<OrderDomain> orderList;

    public OrderAdapter(Context context, ArrayList<OrderDomain> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_order, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.binding.tvName.setText("Order ID : "+orderList.get(position).getOrder_id());
        holder.binding.tvDate.setText(orderList.get(position).getOrder_date()+" "+orderList.get(position).getOrder_time());
        holder.binding.tvLocation.setText("Amount : Rs " + orderList.get(position).getPayable_amount());

        if (orderList.get(position).getDelivery_status().equals("Undelivered")){
            holder.binding.tvPhone.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.binding.tvPhone.setTextColor(context.getResources().getColor(R.color.green));
        }

        holder.binding.tvPhone.setText("Status : "+orderList.get(position).getDelivery_status());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                Gson gson = new Gson();
                intent.putExtra("orderData", gson.toJson(orderList.get(position)));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (orderList != null)
            return orderList.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemOrderBinding binding;

        public ViewHolder(@NonNull ItemOrderBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
