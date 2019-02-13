package com.tripnetra.tripnetra.hotels.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;

import java.util.Objects;

public class Hotel_OverView_Frag extends Fragment{

    String Hname,Hdesc;
    TextView DescrptTv;

    public Hotel_OverView_Frag() {}

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotel_overview, container, false);

        DescrptTv = view.findViewById(R.id.decrptv);

        assert getArguments() != null;
        Hdesc = getArguments().getString("hoteldesc");
        Hname = ((G_Class) Objects.requireNonNull(getActivity()).getApplicationContext()).getHotelName();

        TextView hoteltv = view.findViewById(R.id.hotelname);
        hoteltv.setText(Hname);

        if(Objects.equals(Hdesc, "")) {
            DescrptTv.setText(" "+Hname+" is a nice Hotel With Very Good Facilities");
        }else{
            if (Build.VERSION.SDK_INT >= 24) {
                DescrptTv.setText(Html.fromHtml(Hdesc, 1));
            } else {
                DescrptTv.setText(Html.fromHtml(Hdesc));
            }
        }

        return view;
    }

}