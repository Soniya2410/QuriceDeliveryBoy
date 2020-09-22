package com.moziz.qricedeliveryboy.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.moziz.qricedeliveryboy.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static final String MyPREFERENCES = "login";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static boolean isGetAllMessageRunning = false;
//    public static  CircularDotsLoader loader;
public static AVLoadingIndicatorView avi;
    public static  Dialog dialog;
    public static boolean isConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
    public static boolean checkAndRequestPermissions(Context context, Activity activity) {
        int accessFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int recordState = ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if(accessFineLocation != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(recordState != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(activity,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

  static   void startAnim(){
        avi.show();
        // or avi.smoothToShow();
    }

 static    void stopAnim(){
        avi.hide();
        // or avi.smoothToHide();
    }
    public  static void dialogIsLoading(Context context){
//        Context context = scanForActivity(view.getContext());
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_layout);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setAttributes(wlp);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        avi = dialog.findViewById(R.id.aviLoader);
        startAnim();
       // loader = dialog.findViewById(R.id.progress_circular);
       // loader.setAnimDur(300);
       // loader.setShowRunningShadow(true);
       // loader.startAnimation();

        dialog.show();
    }
    public static void dialogIsDismiss(){
        if(dialog !=null && dialog.isShowing()) {
            stopAnim();
            dialog.dismiss();
        }
//        if(loader!=null && loader.isShown() ){
//            loader.stopAnimation();
//            dialog.dismiss();
//        }
    }
    public static void presentToast(Context mContext,String message){
        Toast.makeText(mContext,""+message,Toast.LENGTH_SHORT).show();

    }
    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity)cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)cont).getBaseContext());

        return null;
    }

}
