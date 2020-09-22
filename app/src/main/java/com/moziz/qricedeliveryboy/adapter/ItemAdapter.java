package com.moziz.qricedeliveryboy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.moziz.qricedeliveryboy.R;
import com.moziz.qricedeliveryboy.pojo.ProductPojo;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{


    Context mContext;
    List<ProductPojo> imageArr;
    // RecyclerView recyclerView;
    public ItemAdapter(Context context, List<ProductPojo> imageArr) {
        this.mContext = context;
        this.imageArr = imageArr;

    }
    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_value_layout, parent, false);
        return new ItemAdapter.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        holder.productName.setText(imageArr.get(position).getProductName());
        holder.qty.setText("Quantity : "+imageArr.get(position).getQty());
        holder.amount.setText("Amount : "+mContext.getString(R.string.rupee_symbol)+imageArr.get(position).getPrice());
        Glide.with(mContext).load(imageArr.get(position).getImage()).placeholder(R.drawable.product).error(R.drawable.product).into(holder.prodcutImage);

    }


    @Override
    public int getItemCount() {
        return imageArr.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView status,review;
        public  AppCompatTextView amount,qty,productName,productDesc;
        LinearLayout productLayout;
        AppCompatImageView prodcutImage;
        public ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productDesc = itemView.findViewById(R.id.productDesc);
            amount = itemView.findViewById(R.id.amount);
            qty  = itemView.findViewById(R.id.qty);
            prodcutImage = itemView.findViewById(R.id.productImage);
//            productLayout = itemView.findViewById(R.id.productLayout);
        }
    }
}