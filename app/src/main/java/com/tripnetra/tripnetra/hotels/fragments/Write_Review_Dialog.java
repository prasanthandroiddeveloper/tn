package com.tripnetra.tripnetra.hotels.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class Write_Review_Dialog extends DialogFragment implements View.OnClickListener{

    EditText EtCname, EtBid, EtRev;
    String Name,Bookid,Review = "",Hid;
    int SerRating,QuaRating,FacRating,ConRating;
    RatingBar ServiceBar,QualityBar,FacilityBar,ConditionBar;
    Activity activity;
    G_Class g_class;

    public Write_Review_Dialog() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_rev_dialog, container, false);

        activity = getActivity();

        assert activity != null;
        g_class =(G_Class) activity.getApplicationContext();
        EtCname = view.findViewById(R.id.nametext);
        EtBid = view.findViewById(R.id.bookidtext);
        EtRev = view.findViewById(R.id.reviewtext);
        ServiceBar = view.findViewById(R.id.serviceRBar);
        QualityBar = view.findViewById(R.id.qualtyRBar);
        FacilityBar = view.findViewById(R.id.facltyRBar);
        ConditionBar = view.findViewById(R.id.condtnRBar);
        Button RevSubmit = view.findViewById(R.id.revsubmit);

        RevSubmit.setOnClickListener(this);

        Hid = g_class.getHotelId();

        ServiceBar.setRating(5f);
        QualityBar.setRating(5f);
        FacilityBar.setRating(5f);
        ConditionBar.setRating(5f);

        return view;
    }

    @Override
    public void onClick(View view) {
        SerRating = Math.round(ServiceBar.getRating());
        QuaRating = Math.round(QualityBar.getRating());
        FacRating = Math.round(FacilityBar.getRating());
        ConRating = Math.round(ConditionBar.getRating());

        Name = EtCname.getText().toString();
        Bookid = EtBid.getText().toString();
        Review = EtRev.getText().toString();

        if ((!EtCname.getText().toString().equals(""))&&(!EtBid.getText().toString().equals(""))){
            reviewupld();
        }else if((EtCname.getText().toString().equals(""))){
            Toast.makeText(activity," Please Enter Name ", Toast.LENGTH_SHORT).show();
        }else if((EtBid.getText().toString().equals(""))){
            Toast.makeText(activity," Please Enter Booking Id ", Toast.LENGTH_SHORT).show();
        }
    }

    private void reviewupld() {

        Map<String, String> params = new HashMap<>();
        params.put("hotelid", Hid);
        params.put("CustName", Name);
        params.put("CustBookId", Bookid);
        params.put("CustReview", Review);
        params.put("SerRating", String.valueOf(SerRating));
        params.put("QuaRating", String.valueOf(QuaRating));
        params.put("FacRating", String.valueOf(FacRating));
        params.put("ConRating", String.valueOf(ConRating));

        new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL+"postreview.php", null, params, false, response -> {
            switch (response) {
                case "Success":
                    Utils.setSingleBtnAlert(activity, "Review Submitted", "Ok", false);
                    break;
                case "WRONGID":
                    Utils.setSingleBtnAlert(activity, "Please Enter Valid Booking ID", "Ok", false);
                    break;
                default:
                    Utils.setSingleBtnAlert(activity, getResources().getString(R.string.error_occur), "Ok", false);
                    break;
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }
}