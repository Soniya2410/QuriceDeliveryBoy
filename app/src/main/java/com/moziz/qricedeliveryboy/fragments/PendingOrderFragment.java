package com.moziz.qricedeliveryboy.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.moziz.qricedeliveryboy.HomeActivity;
import com.moziz.qricedeliveryboy.R;
import com.moziz.qricedeliveryboy.Utils.Constant;
import com.moziz.qricedeliveryboy.adapter.OrderAdapter;
import com.moziz.qricedeliveryboy.api.APIClient;
import com.moziz.qricedeliveryboy.api.Api;
import com.moziz.qricedeliveryboy.pojo.OrderPojo;
import com.eyalbira.loadingdots.LoadingDots;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PendingOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingOrderFragment extends Fragment {

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
    private static String TAG ="PendingOrder";
    @BindView(R.id.loaderLayout)
    LinearLayout loaderLayout;
    @BindView(R.id.loader)
    LoadingDots loader;
    @BindView(R.id.pendingOrderRecylerView)
    RecyclerView pendingOrderRecylerView;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.nodataLayout)
            LinearLayout noDataLayout;
    List<OrderPojo> listOfOrder;
    OrderAdapter orderAdapter;

    public PendingOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PendingOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingOrderFragment newInstance(String param1, String param2) {
        PendingOrderFragment fragment = new PendingOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View pendingOrderView = inflater.inflate(R.layout.fragment_pending_order, container, false);
        mContext = getContext();
        ButterKnife.bind(this,pendingOrderView);
        ((HomeActivity)mContext).getSupportActionBar().setTitle("Pending Order");
        SharedPreferences login = mContext.getSharedPreferences(Constant.MyPREFERENCES,MODE_PRIVATE);
        userID = login.getInt("deliveryboy_id",0);
        api = new APIClient().getClient(mContext).create(Api.class);
        if(Constant.isConnected(mContext))
        {
            if(userID!=0) {
                loader.startAnimation();
                loaderLayout.setVisibility(View.VISIBLE);
                pendingOrderList(userID);
            }
        }
        return pendingOrderView;
    }

    private void pendingOrderList(int userID){
        try {
            Log.e(TAG,"USer ID "+userID);

            Call<ResponseBody> call = api.deliveryOrdersList(userID,"pending");
            Log.d(TAG, "PastOrder List post Url " + call.request().url());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d(TAG, " " + response.body());
                    loader.stopAnimation();
                    loaderLayout.setVisibility(View.GONE);
                    listOfOrder = new ArrayList<>();
                    try {
                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            Log.e(TAG,"Response "+responseString);
                            JSONObject js= new JSONObject(responseString);
                            if (js.getInt("code") == 200) {
                                JSONArray jsonArray = js.getJSONArray("data");
                                for(int i = 0;i<jsonArray.length();i++){
                                    JSONObject notiJS = jsonArray.getJSONObject(i);
                                    OrderPojo orderPojo = new OrderPojo();
                                    orderPojo.setOrderID(notiJS.getInt("order_id"));
                                    orderPojo.setOrderNumber(notiJS.getString("orderid"));
                                    orderPojo.setTotalProducts(notiJS.getInt("total_products"));
                                    if(!notiJS.isNull("grand_total"))
                                        orderPojo.setGrandTotal(notiJS.getInt("grand_total"));
                                    else
                                        orderPojo.setGrandTotal(0);
                                    orderPojo.setUserName(notiJS.getString("user_name"));
                                    orderPojo.setStatus(notiJS.getString("status"));
                                    orderPojo.setOrderDate(notiJS.getString("order_date"));
                                    orderPojo.setDeliverScheduler(notiJS.getString("delivery_schedule"));
                                    orderPojo.setOrderStatus("pending");
                                    listOfOrder.add(orderPojo);
                                }
                                if(listOfOrder.size()>0){
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                                    noDataLayout.setVisibility(View.GONE);
                                    orderAdapter = new OrderAdapter(mContext,listOfOrder);
                                    pendingOrderRecylerView.setAdapter(orderAdapter);
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