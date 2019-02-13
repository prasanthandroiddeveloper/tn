package com.tripnetra.tripnetra.cars;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.ViewPagerAdapter;
import com.tripnetra.tripnetra.cars.fragments.Cars_Local_Frag;
import com.tripnetra.tripnetra.cars.fragments.Cars_OutStation_Frag;

public class Cars_Main_Act extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_main);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.layout_act_bar);
        }

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);

    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Cars_Local_Frag car_loc = new Cars_Local_Frag();
        Cars_OutStation_Frag car_out = new Cars_OutStation_Frag();

        adapter.addFrag(car_loc, "Local Trip");
        adapter.addFrag(car_out, "OutStation");
        viewPager.setAdapter(adapter);
    }

}