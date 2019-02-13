package com.tripnetra.tripnetra.tours.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.tours.Tour_Search_Act;

import java.util.Objects;

public class Tour_Filter_Frag extends DialogFragment {

    RangeBar rangeBar;
    TextView MinTv,MaxTv;
    CheckBox CboxInside,CboxOutside;

    public Tour_Filter_Frag() {}

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_filter, container, false);

        CboxInside = view.findViewById(R.id.CboxIn);
        CboxOutside = view.findViewById(R.id.CboxOut);
        rangeBar = view.findViewById(R.id.rangebar);
        MinTv = view.findViewById(R.id.minval);
        MaxTv = view.findViewById(R.id.maxval);

        CboxInside.setChecked(true);
        CboxOutside.setChecked(true);

        view.findViewById(R.id.reset).setOnClickListener(v -> {
            rangeBar.setRangePinsByValue(0,50000);
            MinTv.setText("0");
            MaxTv.setText("50000");
            CboxInside.setChecked(true);
            CboxOutside.setChecked(true);
        });

        view.findViewById(R.id.filter).setOnClickListener(v -> {
            Tour_Search_Act tsa = (Tour_Search_Act)getActivity();
            Bundle b = new Bundle();
            b.putString("lowprice",rangeBar.getLeftPinValue());
            b.putString("highprice",rangeBar.getRightPinValue());
            b.putString("incity",(CboxInside.isChecked()) ? "4" : "0");
            b.putString("outcity",(CboxOutside.isChecked()) ? "2" : "0");
            assert tsa != null;
            tsa.getfilter(b);

            dismiss();
        });

        rangeBar.setOnRangeBarChangeListener((rangeBar, leftPinIndex, rightPinIndex, leftPinValue, rightPinValue) -> {
            MinTv.setText(leftPinValue);
            MaxTv.setText(rightPinValue);
        });

        rangeBar.setPinTextFormatter(value -> value);//to show larger values like 100000

        return view;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = Objects.requireNonNull(getDialog().getWindow()).getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;//1300;//
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }
}
