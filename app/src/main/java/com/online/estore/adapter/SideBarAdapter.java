package com.online.estore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.NotNull;
import com.online.estore.R;
import com.online.estore.databinding.SidebarItemBinding;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.models.DrawerDomain;


import java.util.List;

public class SideBarAdapter extends RecyclerView.Adapter<SideBarAdapter.ViewHolder> {

    Context context;
    List<DrawerDomain> menuList;
    ClickedItem clickedItem;

    public SideBarAdapter(Context context, List<DrawerDomain> menuList, ClickedItem clickedItem) {
        this.context = context;
        this.menuList = menuList;
        this.clickedItem = clickedItem;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        SidebarItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.sidebar_item, viewGroup, false);
        return new SideBarAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.binding.text.setText(menuList.get(i).getName());
        viewHolder.binding.drImage.setImageResource(menuList.get(i).getImage());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.clicked("", menuList.get(i).getName());
            }
        });

    }

    @Override
    public int getItemCount() {
        if (menuList != null)
            return menuList.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        SidebarItemBinding binding;

        public ViewHolder(@NonNull SidebarItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
