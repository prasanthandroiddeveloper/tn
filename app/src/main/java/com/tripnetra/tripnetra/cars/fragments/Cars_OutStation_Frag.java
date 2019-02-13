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

public class Cars_OutStation_Frag extends Fragment implements View.OnClickListener{

    AutoCompleteTextView ATVDrop,ATVPickup;
    TextView DateTv,TimeTv;
    List<String> CityList,Cityids;
    String PickUpAt,DropAt,PickUpid,Dropid,JourDate,PickTime,JDispDate;
    Activity activity;

    public Cars_OutStation_Frag() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cars_outstation, container, false);

        ATVPickup = view.findViewById(R.id.PickATV);
        ATVDrop = view.findViewById(R.id.DropATV);
        DateTv = view.findViewById(R.id.dateTV);
        TimeTv = view.findViewById(R.id.TimeTV);
        activity = getActivity();

        CityList = new ArrayList<>();Cityids = new ArrayList<>();

        DateTv.setOnClickListener(this);
        TimeTv.setOnClickListener(this);
        view.findViewById(R.id.submitbutton).setOnClickListener(this);

        getcitynames();

        return view;
    }

    public void getcitynames() {

        Map<String, String> params = new HashMap<>();
        params.put("type", "getcities");

        new VolleyRequester(activity).ParamsRequest(1, ((G_Class) activity.getApplicationContext()).getBaseurl()+Config.CAR_PLACES_URL, null, params, true, response -> {

            try {
                JSONArray jarray = new JSONArray(response);
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jobj = jarray.getJSONObject(i);
                    CityList.add(jobj.getString("city_name").trim());
                    Cityids.add(jobj.getString("city_details_id").trim());
                }

                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(Objects.requireNonNull(activity), R.layout.textview_layout, CityList);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ATVPickup.setThreshold(1);
                ATVPickup.setAdapter(cityAdapter);
                ATVPickup.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    PickUpAt = String.valueOf(arg0.getItemAtPosition(arg2));
                    PickUpid = Cityids.get(CityList.indexOf(PickUpAt));

                    ATVPickup.clearFocus();
                    Utils.clearfocus(activity);
                });

                ATVDrop.setThreshold(1);
                ATVDrop.setAdapter(cityAdapter);
                ATVDrop.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    DropAt = String.valueOf(arg0.getItemAtPosition(arg2));
                    Dropid = Cityids.get(CityList.indexOf(DropAt));

                    ATVDrop.clearFocus();
                    Utils.clearfocus(activity);
                });
            }catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", true);
            }

        });
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

    public void verifydata(){
        PickUpAt = ATVPickup.getText().toString().trim();DropAt = ATVDrop.getText().toString().trim();
        LinearLayout MainLay = Objects.requireNonNull(activity).findViewById(R.id.mainlay);

        if(Objects.equals(PickUpAt, "")){
            Snackbar snackbar = Snackbar.make(MainLay, "Please Select a PickUp Place", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else if(Objects.equals(DropAt, "")){
            Snackbar snackbar = Snackbar.make(MainLay, "Please Select a Drop Place", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else if(JourDate==null){
            Snackbar snackbar = Snackbar.make(MainLay, "Please Select Journey Date", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else if(PickTime==null){
            Snackbar snackbar = Snackbar.make(MainLay, "Please Select PickUp Time", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else if(PickUpid == null || Dropid == null){

            TextView showText = new TextView(activity);
            showText.setText(R.string._9393939150);showText.setTextColor(Color.BLUE);showText.setTypeface(null, Typeface.BOLD);showText.setTextSize(17);
            showText.setTextIsSelectable(true);showText.setGravity(Gravity.CENTER_HORIZONTAL);showText.setAutoLinkMask(4);

            new AlertDialog.Builder(activity).setTitle("Sorry")
                    .setMessage("We Dont Serve online Between \n"+PickUpAt+" And "+DropAt+"\nYou Can Request @ ")
                    .setView(showText)
                    .setPositiveButton("OK", (dialog, id) -> {})
                    .setCancelable(true).create().show();
        }else{
            uploaddata();
        }
    }

    private void uploaddata() {

        Bundle bundle = new Bundle();
        bundle.putString("type","outstation");
        bundle.putString("pickup",PickUpAt);
        bundle.putString("drop",DropAt);
        bundle.putString("date",JourDate);
        bundle.putString("pickupid",PickUpid);
        bundle.putString("dropid",Dropid);
        bundle.putString("Dispdate",JDispDate);
        bundle.putString("time",PickTime);

        Intent intent = new Intent(activity, Car_Search_Act.class);
        intent.putExtras(bundle);
        Objects.requireNonNull(activity).startActivity(intent);

    }

}
