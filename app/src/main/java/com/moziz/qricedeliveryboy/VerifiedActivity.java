package com.moziz.qricedeliveryboy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerifiedActivity extends AppCompatActivity {

    @BindView(R.id.continueBtn)
    AppCompatButton continueBtn;
        int userID =0;
    private static String TAG = "VerifiedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified);
        ButterKnife.bind(this);
        if(getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            userID = bundle.getInt("deliveryboy_id");

        }
    }
   public void goToHomePage(View view){

       Intent intent = new Intent(VerifiedActivity.this,LoginActivity.class);
       Bundle bundle = new Bundle();
       bundle.putInt("deliveryboy_id",userID);
       intent.putExtras(bundle);
       startActivity(intent);
        finishAffinity();
    }


}
