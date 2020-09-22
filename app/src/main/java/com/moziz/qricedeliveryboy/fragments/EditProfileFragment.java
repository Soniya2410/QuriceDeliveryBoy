package com.moziz.qricedeliveryboy.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
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
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.name)
    AppCompatEditText name;
    @BindView(R.id.email)
            AppCompatEditText email;
    @BindView(R.id.address)
            AppCompatEditText address;
    @BindView(R.id.mobileNumber)
            AppCompatEditText mobileNumber;
    @BindView(R.id.loader)
    LoadingDots loader;
    @BindView(R.id.loaderLayout)
    LinearLayout loaderLayout;
    @BindView(R.id.updateBtn)
    AppCompatButton updateBtn;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Context mContext;
    Api api;
    int userID = 0;
    private static String TAG = "EditProfile";
    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
        View editProfileView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mContext = getContext();
        ButterKnife.bind(this,editProfileView);
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

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText()!=null && name.getText().toString().isEmpty()){

                }else if(email.getText() !=null &&  email.getText().toString().isEmpty()){
                    Constant.presentToast(mContext,"Email address is empty");
                }    else if(!email.getText().toString().matches(emailPattern))
                {
                    Constant.presentToast(mContext,"Enter Valid Email");
                }
                else if(!email.getText().toString().contains(".com"))
                {
                    Constant.presentToast(mContext,"Enter Valid Email");
                }else if(mobileNumber.getText()!=null && mobileNumber.getText().toString().isEmpty()){
                    Constant.presentToast(mContext,"Mobile Number is  empty");
                }else if(mobileNumber.getText() !=null && mobileNumber.getText().length() < 10) {
                    Constant.presentToast(mContext, "Mobile number is not valid");
                }else if(address.getText() !=null && address.getText().toString().isEmpty()){
                    Constant.presentToast(mContext, "Addres is empty");
                }else{
                    if(Constant.isConnected(mContext)){
                        Constant.dialogIsLoading(mContext);
                        updateProfile(userID,name.getText().toString(),email.getText().toString(),mobileNumber.getText().toString(),address.getText().toString());
                    }
                }
            }
        });
        return editProfileView;
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

                                if(!jsonObject.isNull("name")){
                                    name.setText(""+jsonObject.getString("name"));
                                }else
                                    name.setText("");
                                if(!jsonObject.isNull("email")){
                                    email.setText(""+jsonObject.getString("email"));
                                }else
                                    email.setText("");
                                if(!jsonObject.isNull("mobile")){
                                    mobileNumber.setText(""+jsonObject.getString("mobile"));
                                }else
                                    mobileNumber.setText("");
                                if(!jsonObject.isNull("address")){
                                    address.setText(""+jsonObject.getString("address"));
                                }else
                                    address.setText("");

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
    private void updateProfile(int userID,String name,String email,String mobile,String address){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email",email);
            jsonObject.put("name",name);
            jsonObject.put("mobile",mobile);
            jsonObject.put("address",address);

            Log.e(TAG,"updateProfile ID "+jsonObject.toString());
            RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
            Call<ResponseBody> call = api.updateProfile(userID,body);
            Log.d(TAG, "updateProfile List post Url " + call.request().url());
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
                            if (js.getInt("code") == 200) {
                                Constant.presentToast(mContext,"Updated Successfully");
//                                JSONObject jsonObject = js.getJSONObject("data");
                                FragmentManager fragmentManager = getParentFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.nav_host_fragment, new MyProfileFragment());
                                fragmentTransaction.commit();

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