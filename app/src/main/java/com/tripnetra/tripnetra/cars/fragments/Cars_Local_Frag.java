package com.tripnetra.tripnetra.cars.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.utils.Time_Picker_Dialog;
import com.tripnetra.tripnetra.cars.Car_Search_Act;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Date_Picker_Dialog;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Cars_Local_Frag extends Fragment implements View.OnClickListener{

    AutoCompleteTextView ATVCity,ATVPickup,ATVDrop;
    TextView DateTv,TimeTv;
    Boolean servehere = false;
    String CityName,Cityid,PickLoc,DropLoc,JourDate,PickTime,JDispDate,pickupid,dropid;
    List<String> Citylist,PickList,Cityids,Pickids;
    Activity activity;
    G_Class g_class ;

    public Cars_Local_Frag() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cars_local, container, false);

        activity = getActivity();

        ATVCity = view.findViewById(R.id.CityATV);
        ATVPickup = view.findViewById(R.id.PickATV);
        ATVDrop = view.findViewById(R.id.DropATV);
        DateTv = view.findViewById(R.id.dateTV);
        TimeTv = view.findViewById(R.id.TimeTV);

        g_class = (G_Class) activity.getApplicationContext();

        Citylist = new ArrayList<>();PickList= new ArrayList<>();
        DateTv.setOnClickListener(this);
        TimeTv.setOnClickListener(this);
        view.findViewById(R.id.submitbutton).setOnClickListener(this);

        getcities();

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return view;
    }

    public void getcities() {

        Map<String, String> params = new HashMap<>();
        params.put("type", "getcities");

        new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.CAR_PLACES_URL, null, params, true, response -> {
            try {
                Cityids = new ArrayList<>();
                JSONArray jarray = new JSONArray(response);
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jobj = jarray.getJSONObject(i);
                    Citylist.add(jobj.getString("city_name"));
                    Cityids.add(jobj.getString("city_details_id"));
                }

                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(Objects.requireNonNull(activity), R.layout.textview_layout, Citylist);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ATVCity.setThreshold(1);
                ATVCity.setAdapter(cityAdapter);
                ATVCity.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {

                    CityName = String.valueOf(arg0.getItemAtPosition(arg2));
                    Cityid = Cityids.get(Citylist.indexOf(CityName));
                    ATVCity.clearFocus();
                    Utils.clearfocus(activity);
                    getpickups();
                });

            }catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(activity,getResources().getString(R.string.something_wrong),"Ok",true);
            }
        });

    }

    public void getpickups() {

        Map<String, String> params = new HashMap<>();
        params.put("cityid", Cityid);
        params.put("type", "getpickups");

        new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.CAR_PLACES_URL, null, params, true, response -> {

            if (Objects.equals(response, "NO result")) {
                servehere = false;
            } else {
                try {
                    Pickids = new ArrayList<>();
                    servehere = true;
                    JSONArray jarray = new JSONArray(response);
                    for (int i = 0; i < jarray.length(); i++) {
                        PickList.add(jarray.getJSONObject(i).getString("place"));
                        Pickids.add(jarray.getJSONObject(i).getString("place_details_id"));
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(activity), R.layout.textview_layout, PickList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    ATVPickup.setThreshold(1);
                    ATVPickup.setAdapter(arrayAdapter);
                    ATVPickup.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                        PickLoc = String.valueOf(arg0.getItemAtPosition(arg2));
                        pickupid = Pickids.get(PickList.indexOf(PickLoc));

                        ATVPickup.clearFocus();
                        Utils.clearfocus(activity);
                    });

                    ATVDrop.setThreshold(1);
                    ATVDrop.setAdapter(arrayAdapter);
                    ATVDrop.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                        DropLoc = String.valueOf(arg0.getItemAtPosition(arg2));
                        dropid = Pickids.get(PickList.indexOf(DropLoc));
                        ATVDrop.clearFocus();
                        Utils.clearfocus(activity);
                    });

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", true);
                }
            }

        });
    }

    public void verifydata() {
        LinearLayout MainLay = activity.findViewById(R.id.mainlay);
        CityName = ATVCity.getText().toString().trim();
        PickLoc = ATVPickup.getText().toString().trim();
        DropLoc = ATVDrop.getText().toString().trim();

        if(CityName==null){
            Snackbar.make(MainLay, "Please Select a Place", Snackbar.LENGTH_SHORT).show();
        }else if(Objects.equals(PickLoc, "")){
            Snackbar.make(MainLay, "Please Enter PickUp Point", Snackbar.LENGTH_SHORT).show();
        }else if(Objects.equals(DropLoc, "")){
            Snackbar.make(MainLay, "Please Enter Drop Point", Snackbar.LENGTH_SHORT).show();
        }else if(JourDate==null){
            Snackbar.make(MainLay, "Please Select Date of Journey", Snackbar.LENGTH_SHORT).show();
        }else if(PickTime==null){
            Snackbar.make(MainLay, "Please Select PickUp Time", Snackbar.LENGTH_SHORT).show();
        }else {
            if (servehere) {
                uploaddata();
            } else {
                TextView showText = new TextView(activity);
                showText.setText(R.string._9393939150);showText.setTextColor(Color.BLUE);showText.setTypeface(null, Typeface.BOLD);showText.setAutoLinkMask(4);
                showText.setTextSize(17);showText.setTextIsSelectable(true);showText.setGravity(Gravity.CENTER_HORIZONTAL);
                new AlertDialog.Builder(activity).setTitle("Sorry").setMessage("We Dont Serve Online At " + CityName + "\nYou Can Request @ ")
                        .setView(showText)
                        .setPositiveButton("OK", (dialog, id) -> { })
                        .setCancelable(true).create().show();
            }
        }

    }

    public void uploaddata() {

        Bundle bundle = new Bundle();
        bundle.putString("type","local");
        bundle.putString("cityname",CityName);
        bundle.putString("cityid",Cityid);
        bundle.putString("pickup",PickLoc);
        bundle.putString("pickupid",pickupid);
        bundle.putString("drop",DropLoc);
        bundle.putString("dropid",dropid);
        bundle.putString("date",JourDate);
        bundle.putString("Dispdate",JDispDate);
        bundle.putString("time",PickTime);

        Intent intent = new Intent(activity, Car_Search_Act.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.dateTV:

                long now = System.currentTimeMillis() - 1000;
                new Date_Picker_Dialog(activity,now,now+31536000000L).DateDialog(date -> {
                    JourDate = date;
                    JDispDate = Utils.ChangeDateFormat(date,4);
                    DateTv.setText(JDispDate);
                });

                break;
            case R.id.TimeTV:
                new Time_Picker_Dialog(activity).TimeDialog(date -> {
                    PickTime = date;
                    TimeTv.setText(PickTime);
                });
                break;
            case R.id.submitbutton:
                verifydata();
                break;
        }
    }
}
