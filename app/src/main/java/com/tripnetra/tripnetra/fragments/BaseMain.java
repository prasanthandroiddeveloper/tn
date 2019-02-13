package com.tripnetra.tripnetra.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.account.Acc_Login_Main_Act;
import com.tripnetra.tripnetra.utils.SharedPrefs;
import com.tripnetra.tripnetra.utils.Utils;

import static com.tripnetra.tripnetra.utils.Constants.MULTIPLE_PERMISSIONS_CODE;

public class BaseMain extends AppCompatActivity implements FragmentChangeListener{

    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_main);

        navigation = findViewById(R.id.navigation);

        //BottomNavHelper.disableShiftMode(navigation);

        navigation.setOnNavigationItemSelectedListener(item -> {

            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = new Home_Frag();
                    break;
                case R.id.tripcash:
                    selectedFragment = (new SharedPrefs(BaseMain.this).getLoggedin()) ? new Cash_History_Frag() : new Acc_Login_Main_Act();
                    break;
                case R.id.account:
                    selectedFragment = (new SharedPrefs(BaseMain.this).getLoggedin()) ? new Acc_main_Frag() : new Acc_Login_Main_Act();
                    break;
                case R.id.offer:
                    selectedFragment = new Offers_Frag();
                    break;
                case R.id.ticket:
                    selectedFragment = new Faq_Frag();
                    break;
            }
            if(selectedFragment == null){selectedFragment = new Home_Frag();}
            getFragmentManager().beginTransaction().replace(R.id.framecontainer, selectedFragment).commit();
            return true;
        });
        navigation.setOnNavigationItemReselectedListener(item -> { });

        getFragmentManager().beginTransaction().replace(R.id.framecontainer, new Home_Frag()).commit();

        Utils.checkPermissions(this);
    }

    @Override
    public void replaceFragment(final Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.framecontainer, fragment).commit();
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(() -> exit = false, 3000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissionsList[], @NonNull int[] grantResults)  {
        if (requestCode == MULTIPLE_PERMISSIONS_CODE &&  grantResults.length > 0) {
            Toast.makeText(this, "TripNetra App Needs Permissions to run", Toast.LENGTH_SHORT).show();
        }
    }

}