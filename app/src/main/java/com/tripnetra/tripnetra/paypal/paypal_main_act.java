package com.tripnetra.tripnetra.paypal;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tripnetra.tripnetra.R;

public class paypal_main_act extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        findViewById(R.id.proceedbtn).setOnClickListener(view -> { });

    }

    public boolean isPackageInstalled(String packageName) {
        try{
            getApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
            return true;
        }catch(PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}