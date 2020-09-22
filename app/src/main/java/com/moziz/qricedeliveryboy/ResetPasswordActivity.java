package com.moziz.qricedeliveryboy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;


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

public class ResetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.mobileNumber)
    AppCompatEditText mobileNumber;
    @BindView(R.id.newPassword)
    AppCompatEditText newPassword;
    @BindView(R.id.confirmPassword)
    AppCompatEditText confirmPassword;
    private static String TAG = "ResetPassword";
    Api api;
    int userID = 0;
    String mobileNumberStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        api = new APIClient().getClient(this).create(Api.class);
        newPassword.setOnFocusChangeListener((view, b) -> newPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password_green, 0, 0, 0));
        confirmPassword.setOnFocusChangeListener((view, b) -> confirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password_green, 0, 0, 0));
        mobileNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mobileNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_green, 0, 0, 0);
            }
        });
        if(getIntent().getExtras()!=null){
            mobileNumberStr = getIntent().getExtras().getString("mobile");
            userID = getIntent().getExtras().getInt("deliveryboy_id");
            if(mobileNumberStr!=null){
                mobileNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_green, 0, 0, 0);
                mobileNumber.setText(mobileNumberStr);
            }
        }
    }

    public void backToHomepage(View view) {
    }

    public void goToHomePage(View view) {
        if(newPassword.getText() !=null && newPassword.getText().toString().isEmpty()){
            Constant.presentToast(ResetPasswordActivity.this,"New Password is empty");
        }else if(confirmPassword.getText() !=null && confirmPassword.getText().toString().isEmpty()){
            Constant.presentToast(ResetPasswordActivity.this,"Confirm Password is empty");
        }else if(!confirmPassword.getText().toString().equals(newPassword.getText().toString())){
            Constant.presentToast(ResetPasswordActivity.this,"Password is mismatch");
        }else{
            if(Constant.isConnected(ResetPasswordActivity.this)){
                Constant.dialogIsLoading(ResetPasswordActivity.this);
                resetPassword(userID,newPassword.getText().toString());
            }
        }
    }

    private void resetPassword(int userID ,String password){
        try {
            JSONObject js = new JSONObject();
            js.put("mobile",mobileNumberStr);
            js.put("password",password);
            Log.d(TAG, "sendOTP PARAMS " + js.toString());
            RequestBody body = RequestBody.create(js.toString(), MediaType.parse("application/json"));
            Call<ResponseBody> call = api.resetPassword(userID,body);
            Log.d(TAG, "Send OTP post Url " + call.request().url());
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
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                finishAffinity();

                            } else  {
                                Constant.presentToast(ResetPasswordActivity.this,js.getString("message"));
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Constant.dialogIsDismiss();
                    Toast.makeText(ResetPasswordActivity.this, "Please check your internet connections", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}