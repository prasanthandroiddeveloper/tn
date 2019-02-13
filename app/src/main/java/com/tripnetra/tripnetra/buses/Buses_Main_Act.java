package com.tripnetra.tripnetra.buses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

public class Buses_Main_Act extends AppCompatActivity {

    TextView OnDateTv, OnDayTv, OnMonTv;
    AutoCompleteTextView FromTV, ToTV;
    ArrayList<String> CNamesList, CIdsList, MainNamesList;
    String JDate, FCity_id, FCity_name, TCity_id, TCity_name;
    Intent Main_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses_main);

        OnDateTv = findViewById(R.id.cindatetv);
        OnDayTv = findViewById(R.id.cindaytv);
        OnMonTv = findViewById(R.id.cinmontv);
        FromTV = findViewById(R.id.FromTv);
        ToTV = findViewById(R.id.ToTv);

        long fdate = System.currentTimeMillis();

        JDate = Utils.DatetoStr(fdate,0);
        OnDayTv.setText(Utils.DatetoStr(fdate,8));
        OnMonTv.setText(Utils.DatetoStr(fdate,7));
        OnDateTv.setText(Utils.DatetoStr(fdate,6));

        getcities();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void dateselect(View v) {

        long md = System.currentTimeMillis() - 1000;
        new Date_Picker_Dialog(this,md,md+ 31536000000L).DateDialog(date -> {

            JDate = Utils.ChangeDateFormat(date,0);

            OnDateTv.setText(Utils.ChangeDateFormat(date,6));
            OnDayTv.setText(Utils.ChangeDateFormat(date,8));
            OnMonTv.setText(Utils.ChangeDateFormat(date,7));

        });

    }

    public void changeautos(View v) {
        FromTV.clearFocus();
        ToTV.clearFocus();
        String ss = FromTV.getText().toString();
        FromTV.setText(ToTV.getText().toString());
        ToTV.setText(ss);
    }

    public void getcities() {

        Map<String, String> params = new HashMap<>();
        params.put("type", "getcities");

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL+"bus/details.php", null, params, true, response -> {

            //todo place below code here when webservices finished

            try {
                CNamesList = new ArrayList<>();
                CIdsList = new ArrayList<>();
                MainNamesList = new ArrayList<>();

      /*  InputStream is = getAssets().open("city.txt");//todo replace with above rest call
        byte[] buffer = new byte[is.available()];
        //noinspection ResultOfMethodCallIgnored
        is.read(buffer);
        is.close();
        String response = new String(buffer);*/

                JSONArray jarray = new JSONObject(response).getJSONArray("stationDetails");

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jobj = jarray.getJSONObject(i);
                    CNamesList.add(jobj.getString("station").trim());
                    CIdsList.add(jobj.getString("stationId").trim());
                    MainNamesList.add(jobj.getString("station").trim());
                }

                loadautotv(FromTV, "");
                loadautotv(ToTV, "");

                FromTV.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    FromTV.setText(String.valueOf(arg0.getItemAtPosition(arg2)));
                    FromTV.clearFocus();

                    loadautotv(ToTV, String.valueOf(arg0.getItemAtPosition(arg2)));
                    Utils.clearfocus(Buses_Main_Act.this);
                });

                ToTV.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    ToTV.setText(String.valueOf(arg0.getItemAtPosition(arg2)));
                    ToTV.clearFocus();

                    loadautotv(FromTV, String.valueOf(arg0.getItemAtPosition(arg2)));
                    Utils.clearfocus(Buses_Main_Act.this);
                });

            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(Buses_Main_Act.this, getResources().getString(R.string.something_wrong), "Ok", true);
            }


    });
    }

    private void loadautotv(AutoCompleteTextView ATV,String ss) {

        ArrayList<String> Dlist = CNamesList;
        if(!ss.equals("")){ Dlist.remove(ss);}

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.textview_layout, Dlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ATV.setThreshold(1);
        ATV.setAdapter(dataAdapter);
    }

    public void submitdata(View v){

        TCity_name = ToTV.getText().toString().trim();
        FCity_name = FromTV.getText().toString().trim();

        FCity_id = (MainNamesList.contains(FCity_name)) ? CIdsList.get(MainNamesList.indexOf(FCity_name)) : "" ;
        TCity_id = (MainNamesList.contains(TCity_name)) ? CIdsList.get(MainNamesList.indexOf(TCity_name)) : "" ;

        if(FCity_id.equals("") || FCity_name.equals("")){
            Toast.makeText(this,"Enter From City",Toast.LENGTH_SHORT).show();
        }else if(TCity_id.equals("") || TCity_name.equals("")){
            Toast.makeText(this,"Enter To City",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Buses_Main_Act.this,Darshan_Hotel_search.class);
            intent.putExtra("fromid",FCity_id);
            intent.putExtra("toid",TCity_id);
            intent.putExtra("fromname",FCity_name);
            intent.putExtra("toname",TCity_name);
            intent.putExtra("date",JDate);

            Main_intent = intent;

            startActivityForResult(new Intent(this,Darshan_room_select.class), 326);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==326 && data!=null){
            Main_intent.putExtra("roomcount",data.getStringExtra("rooms"));
            Main_intent.putExtra("personcount",data.getStringExtra("persons"));
            Main_intent.putExtra("rcount1",data.getStringExtra("rcount1"));
            Main_intent.putExtra("rcount2",data.getStringExtra("rcount2"));
            Main_intent.putExtra("rcount3",data.getStringExtra("rcount3"));
            startActivity(Main_intent);
        }
    }

}