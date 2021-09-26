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
import com.online.estore.databinding.ItemCategoryBinding;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.models.CategoryDomain;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    public ArrayList<CategoryDomain> productList;
    ClickedItem clickedItem;
    public ArrayList<String> selectedList = new ArrayList<>();

    public CategoryAdapter(Context context, ArrayList<CategoryDomain> productList, ClickedItem clickedItem) {
        this.context = context;
        this.productList = productList;
        this.clickedItem = clickedItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_category, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final boolean[] ischecked = {false};

//        if (selectedList.contains(productList.get(position).getCategory_id())) {
//            holder.binding.checkbox.setImageResource(R.drawable.checkbox_filled);
//        } else {
//            holder.binding.checkbox.setImageResource(R.drawable.checkbox_empty);
//        }


        Log.d("Category_image-->", productList.get(position).getCategory_image());
        try {
            byte[] decodedString = Base64.decode(productList.get(position).getCategory_image(), Base64.NO_WRAP);
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


        holder.binding.productName.setText(productList.get(position).getCategory_title());
        holder.binding.productDescription.setText(productList.get(position).getCategory_description());

        holder.binding.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (ischecked[0]) {
//                    ischecked[0] = false;
//                    holder.binding.checkbox.setImageResource(R.drawable.checkbox_empty);
//                } else {
//                    ischecked[0] = true;
//                    holder.binding.checkbox.setImageResource(R.drawable.checkbox_filled);
//
//                }

                clickedItem.clicked("check", productList.get(position));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.clicked("click", productList.get(position));
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickedItem.clicked("long", productList.get(position));
                return false;
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

        ItemCategoryBinding binding;

        public ViewHolder(@NonNull ItemCategoryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
