package com.online.estore.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.online.estore.R;
import com.online.estore.adapter.CartAdapter;
import com.online.estore.databinding.ItemBillBinding;
import com.online.estore.databinding.ItemCartBinding;
import com.online.estore.models.CartDomain;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {

    Context context;
    ArrayList<CartDomain> billList;

    public BillAdapter(Context context, ArrayList<CartDomain> billList) {
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBillBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_bill, parent, false);

        return new BillAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.productName.setText(billList.get(position).getProduct_name());
        holder.binding.pAmount.setText(billList.get(position).getSpecial_price());
        holder.binding.pQuantity.setText(billList.get(position).getQuantity());
    }

    @Override
    public int getItemCount() {
        if (billList != null)
            return billList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemBillBinding binding;

        public ViewHolder(@NonNull ItemBillBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
