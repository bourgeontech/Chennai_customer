package com.online.estore.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.online.estore.R;
import com.online.estore.databinding.ItemViewProductBinding;
import com.online.estore.interfaces.AddtoCartInterface;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.models.PackDomain;
import com.online.estore.models.ProductDomain;
import com.online.estore.utils.PackFragment;
import com.online.estore.utils.UrlConstants;
import com.online.estore.volleyservice.VolleyInterface;
import com.online.estore.volleyservice.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.online.estore.utils.UrlConstants.REQ_packLit;

public class ViewProductAdapter extends RecyclerView.Adapter<ViewProductAdapter.ViewHolder> implements VolleyInterface, PackFragment.OptionListener {

    Context context;
    public ArrayList<ProductDomain> productList;
    AddtoCartInterface clickedItem;
    public ArrayList<String> selectedList = new ArrayList<>();

    int clickedPos = -1;

    public ViewProductAdapter(Context context, ArrayList<ProductDomain> productList, AddtoCartInterface clickedItem) {
        this.context = context;
        this.productList = productList;
        this.clickedItem = clickedItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewProductBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_view_product, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final int[] pCount = {1};
        final boolean[] ischecked = {false};
        try {
            byte[] decodedString = Base64.decode(productList.get(position).getProduct_image(), Base64.NO_WRAP);
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


        holder.binding.productName.setText(productList.get(position).getProduct_name());
        holder.binding.tvOriginalPrice.setText("Rs " + productList.get(position).getOrginal_price());
        holder.binding.tvSpecialPrice.setText("Rs " + productList.get(position).getSpecial_price());
        holder.binding.tvDiscount.setText("Save " + productList.get(position).getDiscount() + "%");
        holder.binding.tvUnit.setText(productList.get(position).getUnit());

        if (!productList.get(position).getStock_availability().equals("1")) {
            holder.binding.btnAddToCart.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.btnAddToCart.setBackgroundResource(R.drawable.curved_red);
        } else {
            holder.binding.btnAddToCart.setBackgroundResource(R.drawable.curved_green);
        }

        holder.binding.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pCount[0] > 1) {
                    pCount[0] = pCount[0] - 1;
                    holder.binding.productCount.setText(pCount[0] + "");
                    Animation shake;
                    shake = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.shake);
                    holder.binding.productCount.startAnimation(shake);
                }
            }
        });

        holder.binding.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pCount[0] < 99 && pCount[0] >= 1) {
                    pCount[0] = pCount[0] + 1;
                    holder.binding.productCount.setText(pCount[0] + "");
                    Animation shake;
                    shake = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.shake);
                    holder.binding.productCount.startAnimation(shake);
                }
            }
        });

        holder.binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productList.get(position).getStock_availability().equals("1")) {
                    String c = holder.binding.productCount.getText().toString().trim();
                    if (!c.equals("0")) {
                        clickedItem.clicked("cart", c, productList.get(position));
                    } else {
                        Toast.makeText(context, "Product count is zero", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Animation shake;
                    shake = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.shake);
                    holder.binding.root.startAnimation(shake);
                    Toast.makeText(context, "Product is not in stock", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.binding.tvUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedPos = position;
                getPackList(productList.get(position).getProduct_id());
            }
        });

    }

    private void getPackList(String product_id) {
        VolleyUtils volleyUtils = new VolleyUtils();
        String url = UrlConstants.packList;
        HashMap<String, Object> params = new HashMap<>();
        params.put("product_id", product_id);
        volleyUtils.callApi(context, this, url, REQ_packLit, params, 1, true);
    }

    @Override
    public int getItemCount() {
        if (productList != null)
            return productList.size();
        else
            return 0;
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestcode) {
        if (requestcode == REQ_packLit) {
            getListSuccess(response);
        }
    }

    private void getListSuccess(JSONObject response) {
        try {
            ArrayList<PackDomain> packList = new ArrayList<>();
            packList.clear();
            String code = response.getString("code");
            if (code.equals("1")) {

                JSONArray packs = response.getJSONArray("packs");
                for (int i = 0; i < packs.length(); i++) {
                    JSONObject jsonObject = packs.getJSONObject(i);
                    String pack_id = jsonObject.getString("pack_id");
                    String product_id = jsonObject.getString("product_id");
                    String unit = jsonObject.getString("unit");
                    String quantity = jsonObject.getString("quantity");
                    String description = jsonObject.getString("description");
                    String orginal_price = jsonObject.getString("orginal_price");
                    String special_price = jsonObject.getString("special_price");
                    String discount = jsonObject.getString("discount");

                    packList.add(new PackDomain(pack_id, product_id, unit, quantity, description, orginal_price, special_price, discount));
                }

                if (packList.size() == 0) {
                    Toast.makeText(context, "No packs", Toast.LENGTH_SHORT).show();
                } else {
                    PackFragment packFragment = new PackFragment(packList);
                    packFragment.setOptionListener(this);
                    packFragment.setCancelable(true);
                    packFragment.show(((FragmentActivity) context).getSupportFragmentManager(), packFragment.getTag());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ErrorResponse(String msg, int requestcode) {

    }

    @Override
    public void optionSelected(PackDomain categoryDomain) {
        Log.d("categoryDomain", categoryDomain.toString());
        if (clickedPos != -1) {
            productList.get(clickedPos).setDiscount(categoryDomain.getDiscount());
            productList.get(clickedPos).setOrginal_price(categoryDomain.getOrginal_price());
            productList.get(clickedPos).setSpecial_price(categoryDomain.getSpecial_price());
            productList.get(clickedPos).setSpecial_price(categoryDomain.getSpecial_price());
            productList.get(clickedPos).setUnit(categoryDomain.getQuantity() + " " + categoryDomain.getUnit());
            notifyDataSetChanged();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemViewProductBinding binding;

        public ViewHolder(@NonNull ItemViewProductBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        }
    }
}
