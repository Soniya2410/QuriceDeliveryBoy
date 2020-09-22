package com.moziz.qricedeliveryboy.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.moziz.qricedeliveryboy.R;
import com.moziz.qricedeliveryboy.Utils.Constant;
import com.moziz.qricedeliveryboy.adapter.NotificationAdapter;
import com.moziz.qricedeliveryboy.api.APIClient;
import com.moziz.qricedeliveryboy.api.Api;
import com.moziz.qricedeliveryboy.pojo.NotiOffPojo;
import com.eyalbira.loadingdots.LoadingDots;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class NotificationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @BindView(R.id.notificationRecylerview)
    RecyclerView notificationRecylerview;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.loaderLayout)
    LinearLayout loaderLayout;
    @BindView(R.id.loader)
    LoadingDots loader;
    @BindView(R.id.nodataLayout)
            LinearLayout noDataLayout;
    private Context mContext;
  private NotificationAdapter notificationAdapter ;
  List<NotiOffPojo> listOfNotifiy;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat fmt = new SimpleDateFormat("dd MMM");
    SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-MM-dd");
    private static String TAG = "NotificationandOffers";
    Api api;
    private int userID = 0;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View notificationView = inflater.inflate(R.layout.fragment_notification, container, false);
        ButterKnife.bind(this,notificationView);
        mContext = getContext();

        SharedPreferences login = mContext.getSharedPreferences(Constant.MyPREFERENCES,MODE_PRIVATE);
        userID = login.getInt("deliveryboy_id",0);
        api = new APIClient().getClient(mContext).create(Api.class);
        if(Constant.isConnected(mContext))
        {
            if(userID!=0) {
                loader.startAnimation();
                loaderLayout.setVisibility(View.VISIBLE);
                notificationList(userID);
            }
        }

        return notificationView;
    }

    private void notificationList(int userID){
        try {

            Call<ResponseBody> call = api.notificationList(userID);
            Log.d(TAG, "Notification List post Url " + call.request().url());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d(TAG, " " + response.body());
                    loader.stopAnimation();
                    loaderLayout.setVisibility(View.GONE);
                    listOfNotifiy = new ArrayList<>();

                    try {
                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            Log.e(TAG,"Response "+responseString);
                            JSONObject js= new JSONObject(responseString);
                            if (js.getInt("code") == 200) {
                                JSONArray jsonArray = js.getJSONArray("data");
                                for(int i = 0;i<jsonArray.length();i++){
                                    JSONObject notiJS = jsonArray.getJSONObject(i);
                                    NotiOffPojo notiOffPojo = new NotiOffPojo();
                                    notiOffPojo.setTitle(notiJS.getString("notification"));
                                    notiOffPojo.setNotiOffID(notiJS.getInt("id"));
                                    if(!notiJS.isNull("expiry_date"))
                                    notiOffPojo.setDate(notiJS.getString("expiry_date"));
                                    listOfNotifiy.add(notiOffPojo);
                                }
                                if(listOfNotifiy.size()>0){
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                                    noDataLayout.setVisibility(View.GONE);
                                    notificationAdapter = new NotificationAdapter(mContext,listOfNotifiy);
                                    notificationRecylerview.setAdapter(notificationAdapter);
                                }else{
                                    swipeRefreshLayout.setVisibility(View.GONE);
                                    noDataLayout.setVisibility(View.VISIBLE);
                                }
//                                Constant.presentToast(mContext,js.getString("message"));

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
