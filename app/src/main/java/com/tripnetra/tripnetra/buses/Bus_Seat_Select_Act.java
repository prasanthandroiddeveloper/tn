package com.tripnetra.tripnetra.buses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.buses.adaps.OnSeatSelected;
import com.tripnetra.tripnetra.buses.adaps.SeatDataAdapter;
import com.tripnetra.tripnetra.buses.adaps.SeatRecycleAdapter;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bus_Seat_Select_Act extends AppCompatActivity implements OnSeatSelected {

    int COLUMNS,ROWS,Levels,Current_Level=1,TotalPrice=0,Gst=0,Ctax=0,Pass_count;
    String Routeid,Tripid,SrcOrder,DstOrder,BpId,DpId;
    TextView SeatsTv;
    JSONArray SeatJArray;
    List<String> SeatList,FareList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_seat_select);

        SeatsTv = findViewById(R.id.seatsTv);

        Routeid = Objects.requireNonNull(getIntent().getExtras()).getString("routeid");
        Tripid = getIntent().getExtras().getString("tripid");
        SrcOrder = getIntent().getExtras().getString("srcorder");
        DstOrder = getIntent().getExtras().getString("dstorder");
        BpId = getIntent().getExtras().getString("bpid");
        DpId = getIntent().getExtras().getString("dpid");

        getlayout();

    }

    public void choosedeck(View v){
        if(v.getId() == R.id.lowerTv){
            if(Current_Level != 1) {
                Current_Level = 1;
                findViewById(R.id.lowerTv).setBackgroundResource(R.drawable.select_seat_deck);
                ((TextView) findViewById(R.id.lowerTv)).setTextColor(getResources().getColor(R.color.white));
                findViewById(R.id.upperTv).setBackgroundResource(R.drawable.empty_seat_deck);
                ((TextView) findViewById(R.id.upperTv)).setTextColor(getResources().getColor(R.color.colorPrimary));
                arrange_layout();
            }
        }else{
            if(Current_Level != 2) {
                Current_Level = 2;
                findViewById(R.id.upperTv).setBackgroundResource(R.drawable.select_seat_deck);
                ((TextView) findViewById(R.id.upperTv)).setTextColor(getResources().getColor(R.color.white));
                findViewById(R.id.lowerTv).setBackgroundResource(R.drawable.empty_seat_deck);
                ((TextView) findViewById(R.id.lowerTv)).setTextColor(getResources().getColor(R.color.colorPrimary));
                arrange_layout();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void getlayout() {

        /*Map<String, String> params = new HashMap<>();
        params.put("routeid", Routeid);
        params.put("tripid", Tripid);
        params.put("srcorder", SrcOrder);
        params.put("dstorder", DstOrder);
        params.put("type", "seats");

        new VolleyRequester(this).ParamsRequest(1, Config.TRIP_URL+"bus/details.php", null, params, false, new VolleyCallback() {
            @Override
            public void getResponse(String response) {
                //todo place below code here while webservices finished
            }
        });*/

        try {
            InputStream is = getAssets().open("seats3.txt");
            byte[] buffer = new byte[is.available()];
            //noinspection ResultOfMethodCallIgnored //todo replace with above rest call
            is.read(buffer);
            is.close();

            String response = new String(buffer);
            JSONObject jobj1 = new JSONObject(response).getJSONObject("seatLayout");

            if(!jobj1.getJSONObject("status").getString("status_message").equals("Success")){
                findViewById(R.id.mainlay).setVisibility(View.GONE);
                TextView tv = findViewById(R.id.nopeTv);
                tv.setVisibility(View.VISIBLE);
                tv.setText("Selected Bus is Temporarily UnAvailable");
            }else{

                JSONObject jobj = jobj1.getJSONObject("layoutInfo");
                COLUMNS = jobj.getInt("maxcols");
                ROWS = jobj.getInt("maxrows");
                Levels = jobj.getString("decktype").equals("UpperAndLower") ? 2 : 1;

                if(Levels==2){findViewById(R.id.deckLayt).setVisibility(View.VISIBLE);}
                SeatJArray = jobj.getJSONArray("seatInfo");
                arrange_layout();
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public void arrange_layout(){
        SeatsTv.setText("");
        ((TextView) findViewById(R.id.pricetv)).setText("");

        try {

            JSONArray jarr = SeatJArray;
            List<SeatDataAdapter> slist = new ArrayList<>();

            for (int i = 0; i < jarr.length(); i++) {
                SeatDataAdapter sadp = new SeatDataAdapter();

                JSONObject jobj = jarr.getJSONObject(i);

                sadp.setApos(i);
                sadp.setName(jobj.getString("seatNo"));
                sadp.setPosition(jobj.getString("position"));
                sadp.setStatus(jobj.getString("seatstatus"));
                sadp.setSeattype(jobj.getString("seattype"));
                sadp.setGender(jobj.getString("gender"));

                if (jobj.getString("seattype").equals("gangway") || jobj.getString("seatNo").equals("-")) {
                    sadp.setType(SeatDataAdapter.TYPE_EMPTY);
                }else {
                    sadp.setType(SeatDataAdapter.TYPE_SEAT);
                }

                if(Current_Level==1) {
                    if (jobj.getString("berth").equals("lower")) { slist.add(sadp); }
                }else{
                    if (jobj.getString("berth").equals("upper")) { slist.add(sadp); }
                }
            }

            RecyclerView recyclerView = findViewById(R.id.lst_items);
            GridLayoutManager gridmangr = new GridLayoutManager(this, COLUMNS, LinearLayoutManager.HORIZONTAL,true);
            recyclerView.setLayoutManager(gridmangr);
            recyclerView.setAdapter(new SeatRecycleAdapter(this, slist));

        } catch (JSONException e) {
           // e.printStackTrace();
            Utils.setSingleBtnAlert(this,getResources().getString(R.string.something_wrong),"Ok",false);
        }
    }

    @SuppressLint("SetTextI18n") @Override
    public void onSeatSelected(List<Integer> count) {
        TotalPrice=0;Gst=0;Ctax=0;
        try {
            Pass_count = count.size();
            SeatList = new ArrayList<>();FareList = new ArrayList<>();
            JSONArray jarr = SeatJArray;

            for (int i = 0; i < Pass_count; i++) {
                JSONObject jobj = jarr.getJSONObject(count.get(i));
                SeatList.add(jobj.getString("seatNo"));
                FareList.add(jobj.getString("fare"));
                int tt = jobj.getInt("fare");

                TotalPrice += tt;
                Gst += (int) Math.ceil((tt*jobj.getInt("servicetax"))/100);
                Ctax += (int) Math.ceil((tt*jobj.getInt("convenienceChargePercent"))/100);
            }

            SeatsTv.setText(String.valueOf(SeatList).replace("\\[","").replace("]",""));
            ((TextView) findViewById(R.id.pricetv)).setText("₹ "+(TotalPrice+Gst+Ctax));

        } catch (JSONException e) {Pass_count=0;e.printStackTrace(); }
    }

    public void booknow(View v) {
        if(Pass_count>0){
            Intent intent = new Intent(this,Bus_pass_details_Act.class);

            intent.putExtra("pass_count",Pass_count);
            intent.putExtra("price",TotalPrice);
            intent.putExtra("gst",Gst);
            intent.putExtra("ctax",Ctax);
            intent.putExtra("routeid",Routeid);
            intent.putExtra("tripid",Tripid);
            intent.putExtra("bpid",BpId);
            intent.putExtra("dpid",DpId);
            intent.putExtra("seatlist",String.valueOf(SeatList));
            intent.putExtra("farelist",String.valueOf(FareList));

            startActivity(intent);
        }
    }

}