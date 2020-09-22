package com.moziz.qricedeliveryboy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.moziz.qricedeliveryboy.R;
import com.moziz.qricedeliveryboy.pojo.NotiOffPojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{


    Context mContext;
   List<NotiOffPojo> listOfNotifiy;
    SimpleDateFormat orderDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat convertDate = new SimpleDateFormat("dd/MM/yyyy");
    // RecyclerView recyclerView;
    public NotificationAdapter(Context context, List<NotiOffPojo> listOfNotifi) {
        this.mContext = context;
        this.listOfNotifiy = listOfNotifi;

    }
    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.notification_layout, parent, false);
        return new NotificationAdapter.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, int position) {
        holder.contentTxt.setText(listOfNotifiy.get(position).getTitle());
        String dateStr = listOfNotifiy.get(position).getDate();
        try {
            if(dateStr!=null && !dateStr.isEmpty()) {
                Date date = orderDate.parse(dateStr);
                holder.date.setText(convertDate.format(date));
            }else
                holder.date.setText("");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return listOfNotifiy.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView contentTxt,date;

        public ViewHolder(View itemView) {
            super(itemView);
            contentTxt =  itemView.findViewById(R.id.notificationTxt);
            date = itemView.findViewById(R.id.date);

        }
    }
}