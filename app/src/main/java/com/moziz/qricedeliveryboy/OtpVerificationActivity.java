package com.moziz.qricedeliveryboy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;


import com.moziz.qricedeliveryboy.Utils.Constant;
import com.moziz.qricedeliveryboy.api.APIClient;
import com.moziz.qricedeliveryboy.api.Api;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    @BindView(R.id.otp1)
    AppCompatEditText otp1;
    @BindView(R.id.otp2)
    AppCompatEditText otp2;
    @BindView(R.id.otp3)
    AppCompatEditText otp3;
    @BindView(R.id.otp4)
    AppCompatEditText otp4;
    @BindView(R.id.otp5)
    AppCompatEditText otp5;
    @BindView(R.id.otp6)
    AppCompatEditText otp6;
    @BindView(R.id.login)
    AppCompatButton loginBtn;
    @BindView(R.id.otpMobile)
    AppCompatTextView otpMobile;
    private String mobileNumber;
    String comeFrom = "";
    int userID =0;

    Api api;

    private static String TAG = "otpVerified";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ButterKnife.bind(this);
        api = new APIClient().getClient(this).create(Api.class);
        if(getIntent().getExtras()!=null){
            Bundle bun = getIntent().getExtras();
            if(bun !=null){
                mobileNumber = bun.getString("mobile");
                userID = bun.getInt("deliveryboy_id");
                comeFrom = bun.getString("comeForm");
                Log.e(TAG,"Come From "+comeFrom);
                otpMobile.setText("We have send you an OTP for\n Mobile Number Verification \n"+mobileNumber);
            }

        }
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    otp2.requestFocus();
                }

            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    otp3.requestFocus();
                }

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    otp4.requestFocus();
//                    loginBtn.setEnabled(true);
//                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_green));
//                    loginBtn.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    otp5.requestFocus();
                }
            }
        });
        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    otp6.requestFocus();
                }
            }
        });
        otp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    loginBtn.setEnabled(true);
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_yellow));
                    loginBtn.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
    }


    public void goToVerificationPage(View view) {
        if(otp1.getText() !=null && otp2.getText() !=null && otp3.getText() !=null && otp4.getText() !=null && otp5.getText() !=null
                && otp6.getText() !=null && otp1.getText().toString().isEmpty()
        && otp2.getText().toString().isEmpty() && otp3.getText().toString().isEmpty() && otp4.getText().toString().isEmpty() && otp5.getText().toString().isEmpty() && otp6.getText().toString().isEmpty()){
            Constant.presentToast(OtpVerificationActivity.this,"Please enter the valid Otp");
        }else{
            if(Constant.isConnected(OtpVerificationActivity.this)){
                if(userID!=0) {
                    Constant.dialogIsLoading(OtpVerificationActivity.this);
                    otpVerification(userID,otp1.getText().toString()+otp2.getText().toString()+otp3.getText().toString()+
                            otp4.getText().toString()+otp5.getText().toString()+otp6.getText().toString());
                }
            }
        }
//        startActivity(new Intent(OtpVerificationActivity.this,VerifiedActivity.class));
//        finish();
    }

    private void otpVerification(int userID ,String otp){
        try {
            JSONObject js = new JSONObject();
            js.put("mobile",mobileNumber);
            js.put("otp",otp);
            Log.d(TAG, "sendOTP PARAMS " + js.toString());
            Log.e(TAG,"USER ID "+userID);
            RequestBody body = RequestBody.create(js.toString(), MediaType.parse("application/json"));
            Call<ResponseBody> call = api.otpVerification(userID,body);
            Log.d(TAG, "Send OTP post Url " + call.request().url());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d(TAG, " " + response.body());
                    Constant.dialogIsDismiss();
//                    loader.stopAnimation();
//                    loaderLayout.setVisibility(View.GONE);
                    try {
                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            Log.e(TAG,"Response "+responseString);
                            JSONObject js= new JSONObject(responseString);
                            if (js.getInt("code") == 200) {
//                                SharedPreferences login = getSharedPreferences(Constant.MyPREFERENCES,MODE_PRIVATE);
//                                SharedPreferences.Editor loginEditor = login.edit();
//                                loginEditor.putInt("user_id",userID);
//                                loginEditor.apply();
//                                if(comeFrom !=null && comeFrom.equals("login")){
                                    Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("mobile",mobileNumber);
                                    bundle.putInt("deliveryboy_id", userID);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
//                                }
//                                else {
//                                    Intent intent = new Intent(OtpVerificationActivity.this, VerifiedActivity.class);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putInt("deliveryboy_id", userID);
//                                    intent.putExtras(bundle);
//                                    startActivity(intent);
//                                    finish();
//                                }
                            } else  {
                                Constant.presentToast(OtpVerificationActivity.this,js.getString("message"));
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Constant.dialogIsDismiss();
                    Toast.makeText(OtpVerificationActivity.this, "Please check your internet connections", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
