package com.tripnetra.tripnetra.hotels.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.hotels.Hotel_Search_Act;

import java.util.Objects;

public class Hotel_Sort_Frag extends DialogFragment {

    public Hotel_Sort_Frag() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotel_sort, container, false);

        view.findViewById(R.id.nameasort).setOnClickListener(v -> onClick("namelow"));
        view.findViewById(R.id.namezsort).setOnClickListener(v -> onClick("namehigh"));
        view.findViewById(R.id.pricelowsort).setOnClickListener(v -> onClick("pricelow"));
        view.findViewById(R.id.pricehighsort).setOnClickListener(v -> onClick("pricehigh"));
        view.findViewById(R.id.starlowsort).setOnClickListener(v -> onClick("starlow"));
        view.findViewById(R.id.starhighsort).setOnClickListener(v -> onClick("starhigh"));

        return view;
    }

    public void onClick(String sortby) {
        ((Hotel_Search_Act) Objects.requireNonNull(getActivity())).getsort(new Intent().putExtra("sortby",sortby));
        dismiss();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

}