package com.tripnetra.tripnetra.tours.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tobiasrohloff.view.NestedScrollWebView;
import com.tripnetra.tripnetra.R;

public class Cancel_policy_Frag extends Fragment {

    public Cancel_policy_Frag() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancel_policy, container, false);

        NestedScrollWebView mainTv = view.findViewById(R.id.textview);
        assert getArguments() != null;
        mainTv.loadData(getArguments().getString("cancellation_policy"),"text/html",null);

        return view;
    }

}
