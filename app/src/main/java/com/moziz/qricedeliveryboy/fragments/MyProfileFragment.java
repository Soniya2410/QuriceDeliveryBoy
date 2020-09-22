package com.moziz.qricedeliveryboy.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.editProfile)
    AppCompatButton editProfile;
    @BindView(R.id.loader)
    LoadingDots loader;
    @BindView(R.id.loaderLayout)
    LinearLayout loaderLayout;
    @BindView(R.id.deliveryBoyID)
    AppCompatTextView deliveryBoyID;
    @BindView(R.id.deliveryBoyNmae)
            AppCompatTextView deliveryBoyName;
    @BindView(R.id.deliveryBoyEmail)
            AppCompatTextView deliveryBoyEmail;
    @BindView(R.id.deliveryBoyMobile)
            AppCompatTextView deliveryBoyMobile;
    @BindView(R.id.deliveryBoyAddress)
            AppCompatTextView deliveryAddress;
    @BindView(R.id.logoutBtn)
            AppCompatButton logutBtn;
    Context mContext;
    Api api;
    int userID = 0;
    private static String TAG = "MyProfile";

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
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
        View myProfile = inflater.inflate(R.layout.fragment_my_profile, container, false);
        mContext = getContext();
        ButterKnife.bind(this,myProfile);

        SharedPreferences login = mContext.getSharedPreferences(Constant.MyPREFERENCES,MODE_PRIVATE);
        userID = login.getInt("deliveryboy_id",0);
        api = new APIClient().getClient(mContext).create(Api.class);
        if(Constant.isConnected(mContext))
        {
            if(userID!=0) {
                loader.startAnimation();
                loaderLayout.setVisibility(View.VISIBLE);
                getProfile(userID);
            }
        }
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, new EditProfileFragment());
                fragmentTransaction.commit();
            }
        });
        logutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.exit_layout);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                window.setAttributes(wlp);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);
                AppCompatButton exit = dialog.findViewById(R.id.exit);
                AppCompatButton cancel = dialog.findViewById(R.id.cancel);
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        SharedPreferences login = mContext.getSharedPreferences(Constant.MyPREFERENCES,MODE_PRIVATE);
                        SharedPreferences.Editor loginEdito = login.edit();
                        loginEdito.clear();
                        loginEdito.apply();
                        startActivity(new Intent(mContext, LoginActivity.class));
                        ((HomeActivity)mContext).finishAffinity();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        return myProfile;
    }
    private void getProfile(int userID){
        try {
            Log.e(TAG,"getProfile ID "+userID);

            Call<ResponseBody> call = api.getProfile(userID);
            Log.d(TAG, "getProfile List post Url " + call.request().url());
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
                                if(!jsonObject.isNull("id")){
                                    deliveryBoyID.setText(""+jsonObject.getString("id"));
                                }else
                                    deliveryBoyID.setText("");
                                if(!jsonObject.isNull("name")){
                                    deliveryBoyName.setText(""+jsonObject.getString("name"));
                                }else
                                    deliveryBoyName.setText("");
                                if(!jsonObject.isNull("email")){
                                    deliveryBoyEmail.setText(""+jsonObject.getString("email"));
                                }else
                                    deliveryBoyEmail.setText("");
                                if(!jsonObject.isNull("mobile")){
                                    deliveryBoyMobile.setText(""+jsonObject.getString("mobile"));
                                }else
                                    deliveryBoyMobile.setText("");
                                if(!jsonObject.isNull("address")){
                                    deliveryAddress.setText(""+jsonObject.getString("address"));
                                }else
                                    deliveryAddress.setText("");

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