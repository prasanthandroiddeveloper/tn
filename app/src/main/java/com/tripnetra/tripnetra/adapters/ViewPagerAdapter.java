package com.tripnetra.tripnetra.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class  ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {super(manager);}

    @Override
    public Fragment getItem(int position) {return fragmentList.get(position);}

    @Override
    public int getCount() {return fragmentList.size();}

    public void addFrag(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragTitles.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {return fragTitles.get(position);}

}