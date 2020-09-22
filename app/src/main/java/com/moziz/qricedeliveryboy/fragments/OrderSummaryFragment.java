package com.moziz.qricedeliveryboy.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.moziz.qricedeliveryboy.HomeActivity;
import com.moziz.qricedeliveryboy.MapsActivity;
import com.moziz.qricedeliveryboy.OrderConfirmedActivity;
import com.moziz.qricedeliveryboy.R;
import com.moziz.qricedeliveryboy.Utils.Constant;
import com.moziz.qricedeliveryboy.adapter.ItemAdapter;
import com.moziz.qricedeliveryboy.api.APIClient;
import com.moziz.qricedeliveryboy.api.Api;
import com.moziz.qricedeliveryboy.pojo.ProductPojo;
import com.eyalbira.loadingdots.LoadingDots;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderSummaryFragment extends Fragment {

    Context mContext;
    public OrderSummaryFragment() {
        // Required empty public constructor
    }
    @BindView(R.id.ratingCardView)
    CardView ratingCardView;
    @BindView(R.id.itemsValue)
    AppCompatTextView itemValue;
    @BindView(R.id.loader)
    LoadingDots loader;
    @BindView(R.id.loaderLayout)
    LinearLayout loaderLayout;
    @BindView(R.id.orderDate)
    AppCompatTextView orderDate;
    @BindView(R.id.userName)
            AppCompatTextView userName;
    @BindView(R.id.address)
            AppCompatTextView address;
    @BindView(R.id.phoneNumber)
            AppCompatTextView phoneNumber;
    @BindView(R.id.order_ID)
            AppCompatTextView orderIDNumber;
    @BindView(R.id.order_status)
            AppCompatTextView orderStatus;
    @BindView(R.id.cityState)
            AppCompatTextView cityState;
    @BindView(R.id.totalAmount)
            AppCompatTextView totalAmount;
    @BindView(R.id.discount)
            AppCompatTextView discount;
    @BindView(R.id.grandTotal)
            AppCompatTextView grandTotal;
    @BindView(R.id.paymentMode)
            AppCompatTextView paymentMode;
    @BindView(R.id.deliveryDate)
            AppCompatTextView deliveryDate;
    @BindView(R.id.getDirectionBtn)
    AppCompatButton getDirectionBtn;
    @BindView(R.id.orderDelivery)
            AppCompatButton orderDelivery;
    @BindView(R.id.paymentStatus)
            AppCompatTextView paymentStatus;
List<ProductPojo> listOfProductPojo;
    ItemAdapter itemAdapter ;
    RecyclerView recylerView;
    float rating = 0;
    String reviewCommentsStr = "";
    int orderID = 0;
    private String[] imageArrthird = {"Pending","Delivery","Cancel"};
    private static String TAG = "OrderSummary";
    Api api;
    int userID = 0;
    String comeFrom = "";
    String state="";
    String city = "";
    String addressText="";
    double latit = 0.0;
    double logitu = 0.0;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy");
    SimpleDateFormat dateParse = new SimpleDateFormat("dd-MM-yy");
    SimpleDateFormat dateParse2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View orderView = inflater.inflate(R.layout.fragment_order_summary, container, false);
        ButterKnife.bind(this,orderView);
        mContext = getContext();
        ((HomeActivity)mContext).getSupportActionBar().setTitle("Order Details");
        SharedPreferences login = mContext.getSharedPreferences(Constant.MyPREFERENCES,MODE_PRIVATE);
        userID = login.getInt("user_id",0);

        api = new APIClient().getClient(mContext).create(Api.class);


        if(getArguments() !=null){
            orderID = getArguments().getInt("order_id");
            comeFrom = getArguments().getString("comeFrom");
            Log.e(TAG,"COME FROM "+comeFrom);
            if(comeFrom !=null && comeFrom.equals("order") || comeFrom.equals("pending")){
                getDirectionBtn.setVisibility(View.VISIBLE);
            }else
                getDirectionBtn.setVisibility(View.GONE);

            if(Constant.isConnected(mContext)){
                loader.startAnimation();
                loaderLayout.setVisibility(View.VISIBLE);
                orderDetails(userID,orderID);
            }


        }

        itemValue.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_layout);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            window.setAttributes(wlp);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            AppCompatImageButton close = dialog.findViewById(R.id.close);
            AppCompatTextView totalCount = dialog.findViewById(R.id.totalItemCount);
            recylerView = dialog.findViewById(R.id.itemRecylerView);
            itemAdapter = new ItemAdapter(mContext,listOfProductPojo);
            recylerView.setAdapter(itemAdapter);
            totalCount.setText("Total Item : "+listOfProductPojo.size()+" Items");
//            ratingBar.setNumStars(5);
            dialog.setCancelable(false);
            close.setOnClickListener(v1 -> dialog.dismiss());
            dialog.show();
        });
        getDirectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("address",addressText);
                bundle.putString("city",city);
                bundle.putString("state",state);
                bundle.putDouble("lati",latit);
                bundle.putDouble("logi",logitu);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        orderDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constant.isConnected(mContext)){
                    Constant.dialogIsLoading(mContext);
                    updateOrderStatus(orderID,"Delivered");
                }

            }
        });
        return orderView;
    }

    private void orderDetails(int userID,int orderID){
        try {

            Call<ResponseBody> call = api.orderDetails(userID,orderID);
            Log.d(TAG, "Notification List post Url " + call.request().url());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d(TAG, " " + response.body());
                    loader.stopAnimation();
                    loaderLayout.setVisibility(View.GONE);

                    try {
                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            Log.e(TAG, "Response " + responseString);
                            JSONObject js = new JSONObject(responseString);
                            if (js.getInt("code") == 200) {
                                JSONObject jsonObj = js.getJSONObject("data");
                                if(!jsonObj.isNull("orderid")){
                                    orderIDNumber.setText("Order ID : "+jsonObj.getString("orderid"));
                                }else
                                {
                                    orderIDNumber.setText("Order ID :");
                                }
                                if(!jsonObj.isNull("order_date")){

                                    Date date = dateParse.parse(jsonObj.getString("order_date"));
                                    orderDate.setText(""+fmt.format(date));
                                }else{
                                    orderDate.setText("");
                                }
                                JSONObject addressObj = jsonObj.getJSONObject("delivery_address");
                                Log.e(TAG,"Address "+addressObj);
                                if(!addressObj.isNull("name")){
                                    userName.setText(addressObj.getString("name"));
                                }else
                                    userName.setText("");
                            if(!addressObj.isNull("mobile_number")){
                               phoneNumber.setText("Phone No : "+addressObj.getString("mobile_number"));
                                }else
                                   phoneNumber.setText("Phone No : ");
                            if(!addressObj.isNull("address")){
                                address.setText(addressObj.getString("address"));
                                addressText = addressObj.getString("address");
                            }else
                                address.setText("");
                            if(!addressObj.isNull("city") && !addressObj.isNull("state")){
                                if(!addressObj.isNull("pincode")) {
                                    city = addressObj.getString("city");
                                    state =   addressObj.getString("state")+" "+addressObj.getString("pincode");
                                    cityState.setText(addressObj.getString("city") + " " + addressObj.getString("pincode") + " " +
                                            addressObj.getString("state"));

                                }
                                else cityState.setText(addressObj.getString("city")+" "+addressObj.getString("state"));

                            }else
                                cityState.setText("");
                            if(!jsonObj.isNull("latitude")){
                                latit = jsonObj.getDouble("latitude");
                            }
                            if(!jsonObj.isNull("longitude")){
                                logitu = jsonObj.getDouble("longitude");
                            }
                            if(!jsonObj.isNull("total_items"))
                                itemValue.setText(jsonObj.getString("total_items")+" Items");
                            if(!jsonObj.isNull("item_total")){
                                totalAmount.setText(getString(R.string.rupee_symbol)+" "+jsonObj.getString("item_total")+".00");
                            }else
                                totalAmount.setText(getString(R.string.rupee_symbol)+" 0.00");
                            if(!jsonObj.isNull("grand_total"))
                                grandTotal.setText(getString(R.string.rupee_symbol)+" "+jsonObj.getString("grand_total")+".00");
                            else
                                grandTotal.setText(getString(R.string.rupee_symbol)+ "0.00");
                            if(!jsonObj.isNull("discount_amount"))
                                discount.setText(getString(R.string.rupee_symbol)+" "+jsonObj.getString("discount_amount")+".00");
                            else
                                discount.setText(getString(R.string.rupee_symbol)+" 0.00");
                            if(!jsonObj.isNull("status")){
                                orderStatus.setText("Status : "+jsonObj.getString("status"));
                                paymentStatus.setText(jsonObj.getString("status"));
                            }else {
                                orderStatus.setText("Status : ");
                                paymentStatus.setText("");
                            }
                            if(!jsonObj.isNull("deliver_date")) {
//                                Date date = dateParse2.parse(jsonObj.getString("deliver_date"));
//                                deliveryDate.setText(""+fmt.format(date));
                                deliveryDate.setText(jsonObj.getString("deliver_date"));
                            }else
                                deliveryDate.setText("");
                        if(!jsonObj.isNull("payment_method"))
                            paymentMode.setText(jsonObj.getString("payment_method"));
                        else
                            paymentMode.setText("");
                        if(!comeFrom.equals("cancelled") || !comeFrom.equals("delivery")) {
                            if (!jsonObj.isNull("delivery_btn")) {
                                if (jsonObj.getInt("delivery_btn") == 1) {
                                    orderDelivery.setVisibility(View.VISIBLE);
                                } else
                                    orderDelivery.setVisibility(View.GONE);
                            } else
                                orderDelivery.setVisibility(View.GONE);
                        }else
                            orderDelivery.setVisibility(View.GONE);
                        if(!jsonObj.isNull("review")){
                            JSONObject revJosn = jsonObj.getJSONObject("review");
                            rating = revJosn.getInt("rating");
                            reviewCommentsStr = revJosn.getString("review");
                        }


                        JSONArray prodArr = jsonObj.getJSONArray("products");
                        listOfProductPojo = new ArrayList<>();
                        for(int i=0;i<prodArr.length();i++){
                            ProductPojo proPojo = new ProductPojo();
                            JSONObject jsO = prodArr.getJSONObject(i);
                            proPojo.setProductName(jsO.getString("product_name"));
                            proPojo.setImage(jsO.getString("image"));
                            proPojo.setQty(jsO.getInt("qty"));
                            proPojo.setPrice(jsO.getInt("price"));
                            listOfProductPojo.add(proPojo);
                        }
                        if(listOfProductPojo.size()>0){

                        }

                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Constant.dialogIsDismiss();
                    Toast.makeText(mContext, "Please check your internet connections", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void updateOrderStatus(int orderID, String status){
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
                    Constant.dialogIsDismiss();
                    try {
                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            Log.e(TAG,"Response "+responseString);
                            JSONObject js= new JSONObject(responseString);
//                            Toast.makeText(mContext,"Status will be changed",Toast.LENGTH_SHORT).show();
                            orderStatus.setText("Status : "+"Delivered");
                            paymentStatus.setText("Delivered");
                            startActivity(new Intent(mContext, OrderConfirmedActivity.class));
//                            listOfOrderPojo.remove(position);
//                            notifyDataSetChanged();
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
