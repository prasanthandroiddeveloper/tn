package com.tripnetra.tripnetra;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.tripnetra.tripnetra.fragments.BaseMain;
import com.tripnetra.tripnetra.utils.Constants;

@SuppressWarnings("JniMissingFunction")
public class Splashscreen extends AppCompatActivity {

    static { System.loadLibrary("native-lib"); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appintro);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        G_Class gcv = (G_Class) getApplicationContext();
        gcv.setBaseurl(getBaseurl());
        gcv.setImageurl(getImageurl());
        gcv.setPaytmMid(getPaytmMid());
        gcv.setPayuMid(getPayuMid());

        new Thread(){
            @Override
            public void run(){
                try {
                    sleep(Constants.Splash_Timeout);
                    startActivity(new Intent(getApplicationContext(),BaseMain.class));
                } catch (InterruptedException e) {
                   // e.printStackTrace();
                }
                finish();
            }
        }.start();
    }

    public native String getBaseurl();
    public native String getImageurl();
    public native String getPaytmMid();
    public native String getPayuMid();


}
