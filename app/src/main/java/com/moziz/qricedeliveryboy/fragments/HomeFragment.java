package com.moziz.qricedeliveryboy.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.moziz.qricedeliveryboy.HomeActivity;
import com.moziz.qricedeliveryboy.LoginActivity;
import com.moziz.qricedeliveryboy.R;
import com.moziz.qricedeliveryboy.Utils.Constant;
import com.moziz.qricedeliveryboy.api.APIClient;
import com.moziz.qricedeliveryboy.api.Api;
import com.eyalbira.loadingdots.LoadingDots;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    Context mContext;
    int userID = 0;
    Api api;
    private static String TAG = "HomeFragment";
    @BindView(R.id.loaderLayout)
    LinearLayout loaderLayout;
    @BindView(R.id.loader)
    LoadingDots loader;
    @BindView(R.id.pendingOrder)
    AppCompatTextView pendingOrder;
    @BindView(R.id.deliveryOrder)
    AppCompatTextView deliveryOrder;
    @BindView(R.id.cancelledOrder)
    AppCompatTextView cancelledOrder;
    @BindView(R.id.pendingOrderLayout)
    LinearLayout pendingOrderLayout;
    @BindView(R.id.cancelledOrderLayout)
    LinearLayout cancelledOrderLayout;
    @BindView(R.id.deliveryOrderLayout)
    LinearLayout deliveryOrderLayout;
    String token="";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getContext();
        ButterKnife.bind(this,root);
        SharedPreferences login = mContext.getSharedPreferences(Constant.MyPREFERENCES,MODE_PRIVATE);
        userID = login.getInt("deliveryboy_id",0);
        api = new APIClient().getClient(mContext).create(Api.class);
        if(Constant.isConnected(mContext))
        {
            if(userID!=0) {
                loader.startAnimation();
                loaderLayout.setVisibility(View.VISIBLE);
                dashBoard(userID);
            }
        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                token = task.getResult().getToken();
                if(Constant.isConnected(mContext)){
                    updateToken(token);
                }

            }
        });

        pendingOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, new PendingOrderFragment());
                fragmentTransaction.commit();
            }
        });
        cancelledOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, new CancelledOrderFragment());
                fragmentTransaction.commit();
            }
        });
        deliveryOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, new DeliveryOrderFragment());
                fragmentTransaction.commit();
            }
        });
        return root;
    }
    private void dashBoard(int userID){
        try {
            Log.e(TAG,"dashBoard ID "+userID);

            Call<ResponseBody> call = api.dashBoard(userID);
            Log.d(TAG, "dashBoard List post Url " + call.request().url());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d(TAG, " " + response.body());
                    loader.stopAnimation();
                    loaderLayout.setVisibility(View.GONE);
                    try {
                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            Log.e(TAG,"Response "+responseString);
                            JSONObject js= new JSONObject(responseString);
                            if (js.getInt("code") == 200) {
                                JSONObject jsonObject = js.getJSONObject("data");
                                if(!jsonObject.isNull("pendingorders")){
                                    pendingOrder.setText(""+jsonObject.getInt("pendingorders"));
                                }else
                                    pendingOrder.setText("");
                                if(!jsonObject.isNull("deliveredorders")){
                                    deliveryOrder.setText(""+jsonObject.getInt("deliveredorders"));
                                }else
                                    deliveryOrder.setText("");
                                if(!jsonObject.isNull("cancelledorders")){
                                    cancelledOrder.setText(""+jsonObject.getInt("cancelledorders"));
                                }else
                                    cancelledOrder.setText("");

                            } else  {
                                Constant.presentToast(mContext,js.getString("message"));
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

    private void updateToken(String token){
        try {
            JSONObject js= new JSONObject();
            js.put("token", token);

            Log.d(TAG, "token PARAMS " + js.toString());
            Log.d(TAG,"Delivery Boy ID "+userID);
            RequestBody body = RequestBody.create(js.toString(), MediaType.parse("application/json"));
            Call<ResponseBody> call = api.updateToken(userID,body);
            Log.d(TAG, "token post Url " + call.request().url());
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
}