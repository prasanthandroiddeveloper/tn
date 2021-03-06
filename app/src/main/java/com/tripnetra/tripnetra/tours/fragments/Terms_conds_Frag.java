package com.tripnetra.tripnetra.tours.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tobiasrohloff.view.NestedScrollWebView;
import com.tripnetra.tripnetra.R;

public class Terms_conds_Frag extends Fragment {

    public Terms_conds_Frag() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_terms_conds, container, false);

        NestedScrollWebView mainTv = view.findViewById(R.id.textview);
        assert getArguments() != null;
        mainTv.loadData(getArguments().getString("sightseen_terms_conditions"),"text/html",null);

        return view;
    }

}
