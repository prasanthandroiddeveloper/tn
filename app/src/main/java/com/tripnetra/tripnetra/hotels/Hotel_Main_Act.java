package com.tripnetra.tripnetra.hotels;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.fragments.BaseMain;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Date_Picker_Dialog;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.tripnetra.tripnetra.utils.Constants.Room_Select_Code;

public class Hotel_Main_Act extends AppCompatActivity implements View.OnTouchListener {

    AutoCompleteTextView hsearch;
    TextView CindateTv,CinmonthTv,CindayTv,CoutdateTv,CoutmonthTv,CoutdayTv,NightsTv,RoomsTv,AdultTv,ChildTv;
    String FromDate,ToDate,HorCname, DispCin, DispCout, NightCount;
    long minDate = System.currentTimeMillis() - 1000;
    int RCount=1,ACount=1,CCount=0;
    int[] Adult_list={1,0,0},Child_list={0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_main);

        hsearch = findViewById(R.id.searchtv);
        CindateTv = findViewById(R.id.cindatetv);
        CoutdateTv = findViewById(R.id.coutdatetv);
        CindayTv = findViewById(R.id.cindaytv);
        CoutdayTv = findViewById(R.id.coutdaytv);
        CinmonthTv = findViewById(R.id.cinmontv);
        CoutmonthTv = findViewById(R.id.coutmontv);
        NightsTv = findViewById(R.id.nightstv);
        RoomsTv = findViewById(R.id.rcounttv);
        AdultTv = findViewById(R.id.acounttv);
        ChildTv = findViewById(R.id.ccounttv);

        RoomsTv.setText(String.valueOf(RCount));
        AdultTv.setText(String.valueOf(ACount));
        ChildTv.setText(String.valueOf(CCount));
        if(getIntent().getExtras()!=null){
            String cname = getIntent().getExtras().getString("cname");
            hsearch.setText(cname);
        }

        FromDate = Utils.DatetoStr(System.currentTimeMillis(),0);ToDate = Utils.DatetoStr(System.currentTimeMillis()+86400000L,0);

        setdiffrncs();

        searchhotel();

        findViewById(R.id.mainlay).setOnTouchListener(this);

    }

    private void setdiffrncs() {

        CindayTv.setText(Utils.ChangeDateFormat(FromDate,8));CoutdayTv.setText(Utils.ChangeDateFormat(ToDate,8));
        CinmonthTv.setText(Utils.ChangeDateFormat(FromDate,7));CoutmonthTv.setText(Utils.ChangeDateFormat(ToDate,7));
        CindateTv.setText(Utils.ChangeDateFormat(FromDate,6));CoutdateTv.setText(Utils.ChangeDateFormat(ToDate,6));

        DispCin = (Utils.ChangeDateFormat(FromDate,6)+" "+Utils.ChangeDateFormat(ToDate,7));
        DispCout = (Utils.ChangeDateFormat(FromDate,6)+" "+Utils.ChangeDateFormat(ToDate,7));

        Date d1 = Utils.StrtoDate(0,FromDate);
        Date d2 = Utils.StrtoDate(0,ToDate);
        long diff;
        if(d1 == null || d2 == null){
            diff = 0 ;
        }else{
            diff = Math.abs(d1.getTime() - d2.getTime())/86400000;
        }

        NightsTv.setText(String.valueOf(diff));
        NightCount = String.valueOf(diff);

    }

    public void searchhotel(){

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL+"getlocdata.php", null, null, false, response -> {
            try {
                JSONArray JA = new JSONArray(response);
                String[] str1 = new String[JA.length()],str2 = new String[JA.length()];

                for (int i = 0; i < JA.length(); i++) {
                    JSONObject json = JA.getJSONObject(i);
                    str2[i] = json.getString("city_name");
                    str1[i] = json.getString("hotel_name");
                }

                final List<String> list = new ArrayList<>();

                Collections.addAll(list, str2);
                Collections.addAll(list, str1);
                HashSet<String> hashSet = new HashSet<>(list);
                list.clear();
                list.addAll(hashSet);

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.textview_layout, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                hsearch.setThreshold(1);
                hsearch.setAdapter(dataAdapter);
                hsearch.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    HorCname = String.valueOf(arg0.getItemAtPosition(arg2)).trim();
                    hsearch.clearFocus();
                    Utils.clearfocus(Hotel_Main_Act.this);
                });
            }catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(Hotel_Main_Act.this,getResources().getString(R.string.error_occur),"Ok",true);
            }
        });

    }

    public void stdate(final View v) {

        minDate = System.currentTimeMillis() - 1000;

        new Date_Picker_Dialog(this,minDate,System.currentTimeMillis() - 1000+31536000000L).DateDialog(sdate -> {

            FromDate = sdate;

            Calendar newcal = Calendar.getInstance();
            newcal.setTime(Utils.StrtoDate(0,sdate));
            newcal.add(Calendar.DATE, 1);

            minDate = newcal.getTimeInMillis();

            ToDate = Utils.DatetoStr(newcal.getTime(),0);

            setdiffrncs();

            etdate(v);
        });
    }

    public void etdate(View v) {

        Calendar newcal = Calendar.getInstance();
        newcal.setTime(Utils.StrtoDate(0,FromDate));
        newcal.add(Calendar.DATE, 1);
        minDate = newcal.getTimeInMillis();

        new Date_Picker_Dialog(this,minDate,System.currentTimeMillis() - 1000+31536000000L).DateDialog(sdate -> {
            ToDate = sdate;
            setdiffrncs();
        });
    }

    public void roomselect(View v) {
        /*Bundle bundle = new Bundle();
        bundle.putString("rno", RCount);
        bundle.putString("ano", ACount);
        bundle.putString("cno", CCount);
        Add_Room_Fragment adf = new Add_Room_Fragment();
        adf.setArguments(bundle);
        adf.show(getSupportFragmentManager(),"Select Room");*/

        Intent intent = new Intent(this, Add_Room_New_Activity.class);
        intent.putExtra("roomcount",RCount);
        intent.putExtra("adult_list",Adult_list);
        intent.putExtra("child_list",Child_list);
        startActivityForResult(intent, Room_Select_Code);
    }

   /* public void getval(Bundle bb){
        RCount= bb.getString("rcount");
        ACount=bb.getString("acount");
        CCount=bb.getString("ccount");
        RoomsTv.setText(RCount);
        AdultTv.setText(ACount);
        ChildTv.setText(CCount);
   }*/

    public void searchhotels(View v){

        hsearch.clearFocus();
        Utils.clearfocus(Hotel_Main_Act.this);
        HorCname = hsearch.getText().toString().trim();

        if(Objects.equals(HorCname, "")){
            Toast.makeText(this, "Please Select Location",Toast.LENGTH_SHORT).show();
        }else {

            ((G_Class) getApplicationContext()).setRoomDetails(FromDate,ToDate,String.valueOf(RCount),String.valueOf(ACount),String.valueOf(CCount),NightCount);

            Bundle bundle = new Bundle();
            bundle.putString("HorCname", HorCname);
            bundle.putString("fromdate", FromDate);
            bundle.putString("todate", ToDate);
            bundle.putString("rcount", String.valueOf(RCount));
            bundle.putString("acount", String.valueOf(ACount));
            bundle.putString("ccount", String.valueOf(CCount));
            bundle.putString("inday", DispCin);
            bundle.putString("outday", DispCout);

            Intent intent = new Intent(this, Hotel_Search_Act.class);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    }

    @Override
    public boolean onTouch(View vw, MotionEvent event) {
        switch(vw.getId()) {
            case R.id.mainlay:
                Utils.clearfocus(Hotel_Main_Act.this);
                vw.performClick();
                break;
            default:break;
        }
        return false;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, BaseMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Room_Select_Code && resultCode == RESULT_OK && data!=null){

            Adult_list = data.getIntArrayExtra("adult_list");
            Child_list = data.getIntArrayExtra("child_list");
            RCount = data.getIntExtra("roomcount",1);

            ACount = 0;for(int co : Adult_list){ACount += co;}
            CCount = 0;for(int co : Child_list){CCount += co;}

            RoomsTv.setText(String.valueOf(RCount));
            AdultTv.setText(String.valueOf(ACount));
            ChildTv.setText(String.valueOf(CCount));
        }
    }

}