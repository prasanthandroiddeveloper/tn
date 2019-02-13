package com.tripnetra.tripnetra.tours.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tobiasrohloff.view.NestedScrollWebView;
import com.tripnetra.tripnetra.R;

public class Description_Frag extends Fragment {

    public Description_Frag() {}

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_description, container, false);

        NestedScrollWebView mainTv = view.findViewById(R.id.textview);
        assert getArguments() != null;
        mainTv.loadData(getArguments().getString("sightseen_description")+"\n\n"+
                getArguments().getString("sightseen_inclusion"),"text/html",null);
        return view;
    }

}