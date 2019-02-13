package com.tripnetra.tripnetra.cars.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.cars.Car_Search_Act;
import com.tripnetra.tripnetra.tours.Tour_Search_Act;

import java.util.Objects;

public class Car_Sort_DFrag extends DialogFragment implements View.OnClickListener{

    TextView namelow,namehigh,pricelow,pricehigh;
    String Type;

    public Car_Sort_DFrag() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_car_sort, container, false);

        Type = Objects.requireNonNull(getArguments()).getString("type");

        namelow = view.findViewById(R.id.nameasort);
        namehigh = view.findViewById(R.id.namezsort);
        pricelow = view.findViewById(R.id.pricelowsort);
        pricehigh = view.findViewById(R.id.pricehighsort);

        namelow.setOnClickListener(this);
        namehigh.setOnClickListener(this);
        pricelow.setOnClickListener(this);
        pricehigh.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        String sortby = "";
        if(v==namelow){
            sortby="namelow";
        }else if(v==namehigh){
            sortby="namehigh";
        }else if(v==pricelow){
            sortby="pricelow";
        }else if(v==pricehigh){
            sortby="pricehigh";
        }
        if(Objects.equals(Type, "car")) {
            Car_Search_Act hs = (Car_Search_Act) getActivity();
            assert hs != null;
            hs.getsort(sortby);
        }else{
            Tour_Search_Act ts = (Tour_Search_Act) getActivity();
            assert ts != null;
            ts.getsort(sortby);
        }
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