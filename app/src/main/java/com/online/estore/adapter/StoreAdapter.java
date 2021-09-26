package com.online.estore.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.online.estore.R;
import com.online.estore.activity.CategoryActivity;
import com.online.estore.activity.MainActivity;
import com.online.estore.databinding.ItemShopBinding;
import com.online.estore.interfaces.ClickedItem;
import com.online.estore.interfaces.Refresh;
import com.online.estore.localdb.DatabaseHelper;
import com.online.estore.models.ShopDomain;
import com.online.estore.utils.CommonUtils;
import com.online.estore.utils.Constants;
import com.online.estore.utils.UrlConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    Context context;
    public ArrayList<ShopDomain> shopList;
    SharedPreferences sharedPreferences;
    String userLat, userLong;
    Refresh refresh;
    DatabaseHelper databaseHelper;

    public StoreAdapter(Context context, ArrayList<ShopDomain> shopList, Refresh refresh) {
        this.context = context;
        this.shopList = shopList;
        this.refresh = refresh;
        databaseHelper = new DatabaseHelper(context);
        sharedPreferences = sharedPreferences = context.getSharedPreferences("ogl-app", Context.MODE_PRIVATE);
        userLat = sharedPreferences.getString(Constants.LATTITUDE, "0");
        userLong = sharedPreferences.getString(Constants.LONGITUDE, "0");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShopBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_shop, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        if (position % 2 == 0)
            params.setMargins(getDp(16), 0, getDp(8), getDp(16));
        else
            params.setMargins(getDp(8), 0, getDp(16), getDp(16));

        holder.binding.root.setLayoutParams(params);

        final boolean[] isFav = {true};

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("shop_id", shopList.get(position).getShop_id());
                intent.putExtra("shopName", shopList.get(position).getShop_name());
                intent.putExtra("from", "1");
                context.startActivity(intent);
            }
        });

        holder.binding.tvShopName.setText(shopList.get(position).getShop_name());


        if (databaseHelper.checkIfExists(shopList.get(position).getShop_id())) {
//            holder.binding.btnFav.setImageResource(R.drawable.fav_icon_red);
            holder.binding.likeButton.setLiked(true);
        } else {
//            holder.binding.btnFav.setImageResource(R.drawable.fav_icon_white);
            holder.binding.likeButton.setLiked(false);
        }


        if (shopList.get(position).getLatitude().equals("") || shopList.get(position).getLatitude().equals("null")) {
            holder.binding.tvDistance.setText("Unavailable");
        } else {

            Location startPoint = new Location("locationA");

            startPoint.setLatitude(Double.parseDouble(userLat));
            startPoint.setLongitude(Double.parseDouble(userLong));

            Location endPoint = new Location("locationA");

            endPoint.setLatitude(Double.parseDouble(shopList.get(position).getLatitude()));
            endPoint.setLongitude(Double.parseDouble(shopList.get(position).getLongitude()));

            double distance = startPoint.distanceTo(endPoint);
            Log.d("distance-->", distance + "");

            double inKm = distance / 1000;
            holder.binding.tvDistance.setText(new DecimalFormat("##.#").format(inKm) + " Km Away");

        }


        String time = new SimpleDateFormat("HH:mm a").format(new Date());
        if (checktimings(time, shopList.get(position).getClose_time())) {
            if (checktimings(shopList.get(position).getOpen_time(), time)) {
                holder.binding.tvStatus.setText("Open");
                holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
                holder.binding.topLayout.setBackgroundResource(R.drawable.store_open_bg);
            } else {
                holder.binding.tvStatus.setText("Closed");
                holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                holder.binding.topLayout.setBackgroundResource(R.drawable.curved_gray);
            }
        } else {
            holder.binding.tvStatus.setText("Closed");
            holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            holder.binding.topLayout.setBackgroundResource(R.drawable.curved_gray);
        }


        holder.binding.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isFav[0] = !isFav[0];
                if (databaseHelper.checkIfExists(shopList.get(position).getShop_id())) {
                    databaseHelper.deleteContact(shopList.get(position));
                    holder.binding.btnFav.setImageResource(R.drawable.fav_icon_white);
                    Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                } else {
                    databaseHelper.addtoFav(shopList.get(position));
                    holder.binding.btnFav.setImageResource(R.drawable.fav_icon_red);
                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
                }


            }
        });


        holder.binding.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Log.d("liked->", "true");
                databaseHelper.addtoFav(shopList.get(position));
                Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Log.d("liked->", "false");
                databaseHelper.deleteContact(shopList.get(position));
                Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
            }
        });


        holder.binding.tvrating.setText(shopList.get(position).getRating());
        holder.binding.tvrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog rankDialog;
                rankDialog = new Dialog(context, R.style.FullHeightDialog);
                rankDialog.setContentView(R.layout.rank_dialog);
                rankDialog.setCancelable(true);
                final RatingBar ratingBar = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);

                TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
                text.setText(shopList.get(position).getShop_name());

                TextView updateButton = rankDialog.findViewById(R.id.rank_dialog_button);
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String rate = String.valueOf(ratingBar.getRating());
                        Log.d("rate-->", rate);
                        rankDialog.dismiss();
                        if (CommonUtils.checkConnectivity(context)) {
                            updateStoreRating(holder.binding.tvrating, shopList.get(position).getShop_id(), rate);
                        } else {
                            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                rankDialog.show();
            }
        });

    }

    private boolean checktimings(String time, String endtime) {

        Log.d("time-->", time + " -- " + endtime);
        String pattern = "HH:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1 != null) {
                return date1.before(date2);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateStoreRating(final TextView tvrating, String shop_id, String rate) {

        String url = UrlConstants.RATE_SHOP;
        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_id", sharedPreferences.getString(Constants.CUSTOMER_ID, "0"));
        params.put("shop_id", shop_id);
        params.put("rate", rate);
        final JSONObject parameters = new JSONObject(params);
        Log.d("request-->", parameters.toString());
        CommonUtils.setProgressBar(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonUtils.cancelProgressBar();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response-->", obj.toString());
                            Toast.makeText(context, "" + obj.getString("status"), Toast.LENGTH_SHORT).show();
//                            refresh.refresh();
                            tvrating.setText(obj.getString("rate"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtils.cancelProgressBar();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            public byte[] getBody() {
                return parameters.toString().getBytes();
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    @Override
    public int getItemCount() {
        if (shopList != null)
            return shopList.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemShopBinding binding;

        public ViewHolder(@NonNull ItemShopBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public int getDp(int yourdpmeasure) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                yourdpmeasure,
                r.getDisplayMetrics()
        );
    }
}
