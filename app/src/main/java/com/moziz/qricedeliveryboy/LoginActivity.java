package com.moziz.qricedeliveryboy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.moziz.qricedeliveryboy.Utils.Constant;
import com.moziz.qricedeliveryboy.api.APIClient;
import com.moziz.qricedeliveryboy.api.Api;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.mobileNumber)
    AppCompatEditText mobileNumber;
    @BindView(R.id.password)
    AppCompatEditText password;
    @BindView(R.id.forgotPassword)
    AppCompatTextView forgotPassword;
    Api api;
    private static String TAG = "LoginActivity";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        api = new APIClient().getClient(this).create(Api.class);
        getLoactionPermission();
        mobileNumber.setOnFocusChangeListener((view, b) -> mobileNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_green, 0, 0, 0));
        password.setOnFocusChangeListener((view, b) -> password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password_green, 0, 0, 0));
    }

    public void goToForgotPassword(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forgot_password_layout_first);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setAttributes(wlp);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        AppCompatButton cancel = dialog.findViewById(R.id.cancel);
        AppCompatEditText emailValue = dialog.findViewById(R.id.forgotMobile);
        AppCompatButton resetPassowrd = dialog.findViewById(R.id.resetPassword);
        cancel.setOnClickListener(v -> dialog.dismiss());
        emailValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                emailValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_green, 0, 0, 0);
            }
        });
        resetPassowrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailValue.getText() != null && emailValue.getText().toString().isEmpty()) {
                    Constant.presentToast(LoginActivity.this, "Please enter your email id");
                } else {
                    dialog.dismiss();
                    if (Constant.isConnected(LoginActivity.this)) {
                        Constant.dialogIsLoading(LoginActivity.this);
                        forgotPassword(emailValue.getText().toString());
                    }

                }
            }
        });

        dialog.show();
    }

    public void goToHomePage(View view) {
        if(mobileNumber.getText() !=null &&  mobileNumber.getText().toString().isEmpty()){
            Constant.presentToast(this,"Phone number is empty");
        }    else if(mobileNumber.getText().toString().length() < 10)
        {
            Constant.presentToast(this,"Phone Number is not valid");
//        }
//        else if(!mobileNumber.getText().toString().contains(".com"))
//        {
//            Constant.presentToast(this,"Enter Valid Email");
        }else if(password.getText() !=null && password.getText().toString().isEmpty()){
            Constant.presentToast(LoginActivity.this,"Password is Empty");
        }else{
            if(Constant.isConnected(LoginActivity.this)){
                Constant.dialogIsLoading(LoginActivity.this);
                login(mobileNumber.getText().toString(),password.getText().toString());
            }
        }
    }
    private void getLoactionPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
//            Toast.makeText(this, "Get Current Location", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions",
                    123, perms);
        }
    }
    private void login(String mobile,String password){
        try {
            JSONObject js= new JSONObject();
            js.put("mobile", mobile);
            js.put("password", password);

            Log.d(TAG, "Login PARAMS " + js.toString());
            RequestBody body = RequestBody.create(js.toString(), MediaType.parse("application/json"));
            Call<ResponseBody> call = api.login(body);
            Log.d(TAG, "Login post Url " + call.request().url());
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
                                Constant.presentToast(LoginActivity.this,"Login Successfully");
                                SharedPreferences login = getSharedPreferences(Constant.MyPREFERENCES,MODE_PRIVATE);
                                SharedPreferences.Editor loginEditor = login.edit();
                                loginEditor.putInt("deliveryboy_id",js.getInt("deliveryboy_id"));
                                loginEditor.putBoolean("login",true);
                                loginEditor.apply();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finishAffinity();
                            } else  {
                                Constant.presentToast(LoginActivity.this,js.getString("message"));

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Constant.dialogIsDismiss();
                    Toast.makeText(LoginActivity.this, "Please check your internet connections", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void forgotPassword(String mobile){
        try {
            JSONObject js= new JSONObject();
            js.put("mobile", mobile);

            Log.d(TAG, "forgotPassword PARAMS " + js.toString());
            RequestBody body = RequestBody.create(js.toString(),MediaType.parse("application/json"));
            Call<ResponseBody> call = api.forgotPassword(body);
            Log.d(TAG, "forgotPassword post Url " + call.request().url());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d(TAG, " " + response.body());
                    Constant.dialogIsDismiss();
                    try {
                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            Log.e(TAG," forgotPassword Response "+responseString);
                            JSONObject js= new JSONObject(responseString);
                            if (js.getInt("code") == 200) {
                                Intent intent = new Intent(LoginActivity.this,OtpVerificationActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("mobile",mobile);
                                bundle.putString("comeForm","login");
                                bundle.putInt("deliveryboy_id",js.getInt("deliveryboy_id"));
                                intent.putExtras(bundle);
                                startActivity(intent);
//                                Constant.presentToast(LoginActivity.this,js.getString("message"));
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Constant.dialogIsDismiss();
                    Toast.makeText(LoginActivity.this, "Please check your internet connections", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}