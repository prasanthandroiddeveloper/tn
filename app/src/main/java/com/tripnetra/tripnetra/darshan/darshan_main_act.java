package com.tripnetra.tripnetra.darshan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.hotels.Add_Room_New_Activity;
import com.tripnetra.tripnetra.utils.Date_Picker_Dialog;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.tripnetra.tripnetra.utils.Constants.Room_Select_Code;

public class darshan_main_act extends AppCompatActivity {

    AutoCompleteTextView pnameTv;
    String SName, SId,FromDate,ToDate,DispCin, DispCout;
    TextView CindateTv,CinmonthTv,CindayTv,ddateTv,dmonthTv,ddayTv,ACountTv,CCountTv,RoomCounTv;
    List<String> namesList, idsList;
    int RCount = 1,ACount = 1,CCount = 0;
    int[] adult_list={1,0,0},child_list={0,0,0};
    long minDate = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.darshan_act);

        pnameTv = findViewById(R.id.psearchtv);

        ACountTv = findViewById(R.id.acounttv);
        CCountTv = findViewById(R.id.ccounttv);
        RoomCounTv = findViewById(R.id.rcounttv);

        CindateTv = findViewById(R.id.cindatetv);
        CindayTv = findViewById(R.id.cindaytv);
        CinmonthTv = findViewById(R.id.cinmontv);
        ddateTv = findViewById(R.id.darshandatetv);
        ddayTv = findViewById(R.id.darshandaytv);
        dmonthTv = findViewById(R.id.darshanmontv);

        ACountTv.setText(String.valueOf(ACount));
        CCountTv.setText(String.valueOf(CCount));
        RoomCounTv.setText(String.valueOf(RCount));

        gethnamesdet();

        FromDate = Utils.DatetoStr(System.currentTimeMillis(),0);ToDate = Utils.DatetoStr(System.currentTimeMillis()+86400000L,0);

        setdate();

    }

    private void gethnamesdet() {
        try {

            InputStream is = getAssets().open("packages.txt");
            byte[] buffer = new byte[is.available()];

            is.read(buffer);
            is.close();
            String response = new String(buffer);

            JSONArray jarr = new JSONArray(response);
            namesList = new ArrayList<>();
            idsList = new ArrayList<>();

            for (int i = 0; i < jarr.length(); i++) {
                JSONObject json = jarr.getJSONObject(i);
                namesList.add(json.getString("sightseen_name"));
                idsList.add(json.getString("sightseen_id"));
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(darshan_main_act.this, R.layout.textview_layout, namesList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pnameTv.setThreshold(1);
            pnameTv.setAdapter(dataAdapter);

            pnameTv.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                SName = String.valueOf(arg0.getItemAtPosition(arg2));
                int ii = namesList.indexOf(SName);
                SId = idsList.get(ii);


                pnameTv.clearFocus();
            });

        } catch (JSONException|IOException e) {
            //e.printStackTrace();
            Toast.makeText(this,"something wrong",Toast.LENGTH_SHORT).show();
        }
    }

    public void stdate(View v) {

        new Date_Picker_Dialog(this,minDate,System.currentTimeMillis() - 1000+31536000000L).DateDialog(date -> {

            FromDate = date;

            Calendar newcal = Calendar.getInstance();
            newcal.setTime(Utils.StrtoDate(0,date));
            newcal.add(Calendar.DATE, 1);

            ToDate = Utils.DatetoStr(newcal.getTime(),0);
            minDate = newcal.getTimeInMillis();

        });
    }

    public void setdate(){

        Date fdate = Utils.StrtoDate(0,FromDate);
        Date ldate = Utils.StrtoDate(0,ToDate);

        CindayTv.setText(Utils.DatetoStr(fdate,8));ddayTv.setText(Utils.DatetoStr(ldate,8));
        CinmonthTv.setText(Utils.DatetoStr(fdate,7));dmonthTv.setText(Utils.DatetoStr(ldate,7));
        CindateTv.setText(Utils.DatetoStr(fdate,6));ddateTv.setText(Utils.DatetoStr(ldate,6));

        DispCin = (Utils.DatetoStr(fdate,6)+" "+Utils.DatetoStr(fdate,7));
        DispCout = (Utils.DatetoStr(ldate,6)+" "+Utils.DatetoStr(ldate,7));
    }

    public void etdate(View v) {
        new Date_Picker_Dialog(this,minDate,System.currentTimeMillis() - 1000+31536000000L).DateDialog(date -> {
                ToDate = date;
                setdate();
        });
    }

    public void roomselect(View v) {
        Intent intent = new Intent(darshan_main_act.this, Add_Room_New_Activity.class);
        intent.putExtra("roomcount",RCount);
        intent.putExtra("adult_list",adult_list);
        intent.putExtra("child_list",child_list);
        startActivityForResult(intent, Room_Select_Code);
    }

    public void submit(View view) {

        pnameTv.clearFocus();
         if( pnameTv.getText().toString().equals("")){
             Toast.makeText(this,"Select the Package",Toast.LENGTH_SHORT).show();
             return ;
         }
        Bundle bundle = new Bundle();
        Intent intent=new Intent(darshan_main_act.this,activity_hotel.class);
        bundle.putString("sightseen_name",SName);
        bundle.putIntArray("adult_list",adult_list);
        bundle.putIntArray("child_list",child_list);
        bundle.putInt("roomcount",RCount);
        bundle.putString("fromdate", FromDate);
        bundle.putString("todate", ToDate);
        bundle.putString("rcount", String.valueOf(RCount));
        bundle.putString("acount", String.valueOf(ACount));
        bundle.putString("ccount", String.valueOf(CCount));
        bundle.putString("inday", DispCin);
        bundle.putString("outday", DispCout);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Room_Select_Code && resultCode == RESULT_OK && data!=null){

            adult_list = data.getIntArrayExtra("adult_list");
            child_list = data.getIntArrayExtra("child_list");
            RCount = data.getIntExtra("roomcount",1);

            ACount = 0;for(int co : adult_list){ACount += co;}
            CCount = 0;for(int co : child_list){CCount += co;}

            ACountTv.setText(String.valueOf(ACount));
            CCountTv.setText(String.valueOf(CCount));
            RoomCounTv.setText(String.valueOf(RCount));

        }
    }

}