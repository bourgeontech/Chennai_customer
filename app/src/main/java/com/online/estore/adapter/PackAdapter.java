package com.online.estore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.online.estore.R;
import com.online.estore.databinding.ItemCartBinding;
import com.online.estore.databinding.ItemPackBinding;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.models.PackDomain;

import java.util.ArrayList;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.ViewHolder> {

    Context context;
    ArrayList<PackDomain> productList;
    ClickedItem clickedItem;

    public PackAdapter(Context context, ArrayList<PackDomain> productList, ClickedItem clickedItem) {
        this.context = context;
        this.productList = productList;
        this.clickedItem = clickedItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPackBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_pack, parent, false);

        return new PackAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.binding.tvPrice.setText("₹" + productList.get(position).getSpecial_price());
        holder.binding.tvNum.setText(productList.get(position).getQuantity() + " " + productList.get(position).getUnit());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.clicked("selected", productList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        if (productList != null)
            return productList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPackBinding binding;

        public ViewHolder(@NonNull ItemPackBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
