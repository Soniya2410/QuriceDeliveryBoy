package com.moziz.qricedeliveryboy.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.moziz.qricedeliveryboy.R;
import com.moziz.qricedeliveryboy.Utils.Constant;
import com.moziz.qricedeliveryboy.api.APIClient;
import com.moziz.qricedeliveryboy.api.Api;
import com.moziz.qricedeliveryboy.fragments.OrderSummaryFragment;
import com.moziz.qricedeliveryboy.pojo.OrderPojo;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class OrderRequestAdapter extends RecyclerView.Adapter<OrderRequestAdapter.ViewHolder>{


    Context mContext;
    List<OrderPojo> listOfOrderPojo;
    private static String TAG ="OrderAdpater";
    Api api;
    int userID = 0;
    SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy");
    SimpleDateFormat orderDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateParse2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
    // RecyclerView recyclerView;
    public OrderRequestAdapter(Context context, List<OrderPojo> listOfOrderPojo) {
        this.mContext = context;
        this.listOfOrderPojo = listOfOrderPojo;

    }
    @NonNull
    @Override
    public OrderRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.order_layout, parent, false);
        api = new APIClient().getClient(mContext).create(Api.class);
        SharedPreferences login = mContext.getSharedPreferences(Constant.MyPREFERENCES,MODE_PRIVATE);
        userID = login.getInt("user_id",0);
        return new ViewHolder(listItem);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(OrderRequestAdapter.ViewHolder holder, int position) {
        holder.name.setText(listOfOrderPojo.get(position).getUserName());
        holder.totalItem.setText(listOfOrderPojo.get(position).getTotalProducts()+" Items");
        holder.totalAmount.setText(mContext.getString(R.string.rupee_symbol)+" "+listOfOrderPojo.get(position).getGrandTotal());
        if(listOfOrderPojo.get(position).getDeliverScheduler() != null){
            holder.deliverySchedule.setText(listOfOrderPojo.get(position).getDeliverScheduler());
        }
        if(listOfOrderPojo.get(position).getOrderDate()!=null){
            String orderDateLocal = listOfOrderPojo.get(position).getOrderDate();
            try {
                Date date = orderDate.parse(orderDateLocal);
                holder.orderDate.setText(fmt.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        holder.orderNumber.setText("Order Id : "+listOfOrderPojo.get(position).getOrderNumber());
        holder.statusAns.setText(""+listOfOrderPojo.get(position).getStatus());
        if(listOfOrderPojo.get(position).getDeliveryBtn()==1){
            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
        }else{
            holder.accept.setVisibility(View.VISIBLE);
            holder.reject.setVisibility(View.VISIBLE);
        }



//        if(listOfOrderPojo.get(position).getOrderStatus().equals("delivery")){
//            holder.statusAns.setTextColor(mContext.getColor(R.color.colorPrimary));
//            holder.accept.setVisibility(View.GONE);
//            holder.reject.setVisibility(View.GONE);
//        }else if(listOfOrderPojo.get(position).getOrderStatus().equals("cancelled")){
//            holder.statusAns.setTextColor(mContext.getColor(R.color.red_dark));
//            holder.accept.setVisibility(View.GONE);
//            holder.reject.setVisibility(View.GONE);
//        }else if(listOfOrderPojo.get(position).getOrderStatus().equals("pending")){
//            holder.statusAns.setTextColor(mContext.getColor(R.color.colorPrimary));
//            holder.accept.setVisibility(View.GONE);
//            holder.reject.setVisibility(View.GONE);
//        }
        holder.cardView.setOnClickListener(v -> {
            OrderSummaryFragment orderSummaryFragment = new OrderSummaryFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("order_id",listOfOrderPojo.get(position).getOrderID());
            bundle.putString("comeFrom","order");
            orderSummaryFragment.setArguments(bundle);
            FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderSummaryFragment);
            fragmentTransaction.addToBackStack("tag");
            fragmentTransaction.commit();
        });
        holder.reject.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.reject_layout);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            window.setAttributes(wlp);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            AppCompatButton yes= dialog.findViewById(R.id.yes);
            AppCompatButton no = dialog.findViewById(R.id.no);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if(Constant.isConnected(mContext)){
                        updateOrderStatus(listOfOrderPojo.get(position).getOrderID(),"Cancelled",position,holder);
                    }
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });


            dialog.show();
        });
        holder.accept.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.reject_layout);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            window.setAttributes(wlp);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            AppCompatButton yes= dialog.findViewById(R.id.yes);
            AppCompatButton no = dialog.findViewById(R.id.no);
            AppCompatTextView title = dialog.findViewById(R.id.title);
            title.setText("Are you sure do you want accept this order?");
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if(Constant.isConnected(mContext)){
                        updateOrderStatus(listOfOrderPojo.get(position).getOrderID(),"Out for Delivery",position,holder);
                    }
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });


            dialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return listOfOrderPojo.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView statusAns;
        AppCompatTextView orderDate,totalItem,totalAmount,name,orderNumber,deliverySchedule;
        CardView cardView;
        AppCompatButton reject,accept;

        public ViewHolder(View itemView) {
            super(itemView);
            statusAns =  itemView.findViewById(R.id.statusAns);
            reject = itemView.findViewById(R.id.reject);
            accept = itemView.findViewById(R.id.accept);
            cardView = itemView.findViewById(R.id.cardView);
            orderDate = itemView.findViewById(R.id.orderDate);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            totalItem = itemView.findViewById(R.id.totalItem);
            name =itemView.findViewById(R.id.name);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            deliverySchedule = itemView.findViewById(R.id.deliverySchedule);
        }
    }


    private void updateOrderStatus(int orderID, String status,int position,ViewHolder holder){
        try {
            JSONObject js = new JSONObject();
            js.put("status",status);
            Log.e(TAG,"updateOrderStatus  "+js.toString());
            RequestBody body = RequestBody.create(js.toString(), MediaType.parse("application/json"));
            Call<ResponseBody> call = api.updateOrderStatus(userID,orderID,body);
            Log.d(TAG, "updateOrderStatus post Url " + call.request().url());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d(TAG, " " + response.body());

                    try {
                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            Log.e(TAG, "Response " + responseString);
                            JSONObject js = new JSONObject(responseString);

                            if (status.equals("Cancelled")) {

                                final Dialog dialog = new Dialog(mContext);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.reject_success_layout);
                                Window window = dialog.getWindow();
                                WindowManager.LayoutParams wlp = window.getAttributes();
                                window.setAttributes(wlp);
                                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                window.setBackgroundDrawableResource(android.R.color.transparent);
                                AppCompatButton ok = dialog.findViewById(R.id.ok);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
//                                        holder.accept.setVisibility(View.GONE);
//                                        holder.reject.setVisibility(View.GONE);
                                        listOfOrderPojo.remove(position);
                                        notifyDataSetChanged();
                                    }
                                });
                                dialog.show();

                            }else{
                                Toast.makeText(mContext,"Order is accepted",Toast.LENGTH_SHORT).show();
//                                holder.accept.setVisibility(View.GONE);
//                                holder.reject.setVisibility(View.GONE);
                                listOfOrderPojo.remove(position);
                                notifyDataSetChanged();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Constant.dialogIsDismiss();
                    Toast.makeText(mContext, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
