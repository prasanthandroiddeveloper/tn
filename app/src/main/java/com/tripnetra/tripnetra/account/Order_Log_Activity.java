package com.tripnetra.tripnetra.account;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.account.fragments.Orders_Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order_Log_Activity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    LoadingDialog loadDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_log);
        tabLayout=findViewById(R.id.tabLayout);

        viewPager = findViewById(R.id.viewpager);
        setSupportActionBar(findViewById(R.id.toolbar));

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("My Bookings");
        }
        loadDlg = new LoadingDialog(this);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadDlg.show();

        viewPager.setOffscreenPageLimit(3);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.AddFrag("Upcoming");
        adapter.AddFrag("Completed");
        adapter.AddFrag("Cancelled");

        viewPager.setAdapter(adapter);

        if(loadDlg.isShowing()){loadDlg.dismiss();}

        tabLayout.setupWithViewPager(viewPager);


    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm) {super(fm);}

        @Override
        public Fragment getItem(int position) {return Orders_Fragment.newInstance(mFragmentTitleList.get(position));}

        @Override
        public int getCount() {return mFragmentTitleList.size();}

        void AddFrag(String title) {mFragmentTitleList.add(title);}

        @Override
        public CharSequence getPageTitle(int position) {return mFragmentTitleList.get(position);}

    }


}