package com.moziz.qricedeliveryboy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.moziz.qricedeliveryboy.Utils.Constant;

public class MainActivity extends AppCompatActivity {
    SharedPreferences login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = getSharedPreferences(Constant.MyPREFERENCES,MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(login.getBoolean("login",false)) {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        },3000);
    }
}