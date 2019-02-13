package com.tripnetra.tripnetra.tours;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Date_Picker_Dialog;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Tours_Main_Act extends AppCompatActivity {

    ArrayList<String> CNamesList,CIdsList;
    TextView CInTv;
    AutoCompleteTextView CityATv;
    String Cindate,Cityname,Cityid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tours_main);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.layout_act_bar);
        }

        CInTv = findViewById(R.id.cindatetv);
        CityATv = findViewById(R.id.autosearch);

        Cindate = Utils.DatetoStr(new Date(),0);
        CInTv.setText(Utils.DatetoStr(new Date(),1));

        getcities();

        CInTv.setOnClickListener(view ->
                new Date_Picker_Dialog(Tours_Main_Act.this,System.currentTimeMillis(),System.currentTimeMillis() + 31536000000L).
                DateDialog(date -> {
                    Cindate = date;
                    CInTv.setText(Utils.ChangeDateFormat(date,1));
                }));
    }

    public void getcities() {

        Map<String, String> params = new HashMap<>();
        params.put("type", "getcities");

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.CAR_PLACES_URL, null, params, true, response -> {
            try {
                CNamesList = new ArrayList<>();CIdsList = new ArrayList<>();
                JSONArray jarray = new JSONArray(response);
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jobj = jarray.getJSONObject(i);
                    CNamesList.add(jobj.getString("city_name").trim());
                    CIdsList.add(jobj.getString("city_details_id").trim());
                }

                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(Tours_Main_Act.this, R.layout.textview_layout, CNamesList);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                CityATv.setThreshold(1);
                CityATv.setAdapter(cityAdapter);
                CityATv.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    Cityname = String.valueOf(arg0.getItemAtPosition(arg2));
                    Cityid = CIdsList.get(CNamesList.indexOf(Cityname));
                    CityATv.clearFocus();
                    Utils.clearfocus(Tours_Main_Act.this);
                });
            }catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(Tours_Main_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
            }
        });

    }

    public void searchtrip(View v){
        String Dummname = CityATv.getText().toString().trim();

        if(CNamesList.contains(Dummname)) {
            Cityname = Dummname;
            Cityid = CIdsList.get(CNamesList.indexOf(Cityname));
            Intent intent = new Intent(this, Tour_Search_Act.class);
            intent.putExtra("cityid", Cityid);
            intent.putExtra("cityname", Cityname);
            intent.putExtra("cindate", Cindate);
            startActivity(intent);
        }else{
            CityATv.setError("Please Select Your Destination");
            CityATv.requestFocus();
        }
    }
}