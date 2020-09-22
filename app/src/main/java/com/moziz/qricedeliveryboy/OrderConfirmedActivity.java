package com.moziz.qricedeliveryboy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class OrderConfirmedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);
    }

    public void backToHomepage(View view) {
        SharedPreferences cart = getSharedPreferences("Cart",MODE_PRIVATE);
        SharedPreferences.Editor cartEdito = cart.edit();
        cartEdito.clear();
        cartEdito.apply();
        SharedPreferences sharedPreferences = getSharedPreferences("Order",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(OrderConfirmedActivity.this,HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("comeFrom","order_confirm");
        intent.putExtras(bundle);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences cart = getSharedPreferences("Cart",MODE_PRIVATE);
        SharedPreferences.Editor cartEdito = cart.edit();
        cartEdito.clear();
        cartEdito.apply();
        SharedPreferences sharedPreferences = getSharedPreferences("Order",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(OrderConfirmedActivity.this,HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("comeFrom","order_confirm");
        intent.putExtras(bundle);
        startActivity(intent);
        finishAffinity();
    }
}
