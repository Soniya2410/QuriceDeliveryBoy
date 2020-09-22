package com.moziz.qricedeliveryboy.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.moziz.qricedeliveryboy.R;
import com.moziz.qricedeliveryboy.Utils.Constant;
import com.moziz.qricedeliveryboy.api.APIClient;
import com.moziz.qricedeliveryboy.api.Api;
import com.eyalbira.loadingdots.LoadingDots;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MyOrderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Context mContext;
    Api api;
    int userID = 0;
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
    public MyOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myOrderView = inflater.inflate(R.layout.fragment_my_order, container, false);
        mContext = getContext();
        ButterKnife.bind(this,myOrderView);
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
        return myOrderView;
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
}