package com.tripnetra.tripnetra.buses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.DataAdapter;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Buses_Search_Act extends AppCompatActivity {

    String FcityID,TcityID,JDate,HotelId,Hname;
    Bundle M_bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses_search);

        M_bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("bundle");
        HotelId = getIntent().getExtras().getString("hotelid");
        Hname = getIntent().getExtras().getString("hotelname");
        FcityID = Objects.requireNonNull(M_bundle).getString("fromid");
        TcityID = M_bundle.getString("toid");
        JDate = M_bundle.getString("date");

        getroutes();

    }

    @SuppressLint("SetTextI18n")
    private void getroutes() {

        Map<String, String> params = new HashMap<>();
        params.put("type", "gettrips");
        params.put("from_id", FcityID);
        params.put("to_id", TcityID);
        params.put("date", JDate);

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL+"bus/details.php", null, params, true, response -> {
               //todo place below code here while webservices finished
            try {

       /* InputStream is = getAssets().open("routes.txt");//todo replace with above rest call
        byte[] buffer = new byte[is.available()];
        //noinspection ResultOfMethodCallIgnored
        is.read(buffer);
        is.close();
        String response = new String(buffer);*/

                RecyclerView recyclerView = findViewById(R.id.recyclerView);

                if(!new JSONObject(response).getJSONObject("status").getString("status_message").equals("Success")){
                    recyclerView.setVisibility(View.GONE);
                    TextView tv = findViewById(R.id.nopeTv);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("No Buses Available For Selected Route");
                }else{
                    findViewById(R.id.nopeTv).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Buses_Search_Act.this));

                    JSONArray jarr = new JSONObject(response).getJSONArray("availabletrips");

                    List<DataAdapter> list = new ArrayList<>();

                    for(int i = 0; i< jarr.length(); i++) {
                        DataAdapter adapter = new DataAdapter();
                        JSONObject json = jarr.getJSONObject(i);

                        adapter.setID(json.getString("routeid"));
                        adapter.setTypeId(json.getString("tripid"));
                        adapter.setAdult(json.getString("srcorder"));
                        adapter.setChild(json.getString("dstorder"));

                        adapter.setName("TripNetra Tours & Travels");//todo
                        adapter.setType(json.getString("bustype"));
                        adapter.setAvail(json.getString("availseats"));
                        adapter.setPrice(json.getString("fare"));

                        adapter.setRId(json.getJSONObject("boardingpoint").getJSONArray("bpDetails").getJSONObject(0).getString("id"));
                        adapter.setSuppid(json.getJSONObject("droppingpoint").getJSONArray("dpDetails").getJSONObject(0).getString("id"));

                        Date d1 = Utils.StrtoDate(2,json.getString("deptime"));
                        Date d2 = Utils.StrtoDate(2,json.getString("arrtime"));

                        if (d1 == null || d2 == null) {
                            adapter.setAdult("--:--");
                            adapter.setChild("--:--");
                            adapter.setDate("--:--");
                        }else {
                            adapter.setAdult(Utils.DatetoStr(d1, 4));
                            adapter.setChild(Utils.DatetoStr(d2, 4));

                            long diff = d2.getTime() - d1.getTime();
                            int hours = (int) diff / 3600000;
                            int min = (int) (diff / 60000) % 60;

                            adapter.setDate(new DecimalFormat("00").format(hours) + " : " + new DecimalFormat("00").format(min) + " Hrs");

                        }

                        list.add(adapter);
                    }

                    recyclerView.setAdapter(new Seat_Search_Recycler(list));
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(Buses_Search_Act.this, getResources().getString(R.string.something_wrong), "Ok", true);
            }



        });


    }

    private class Seat_Search_Recycler extends RecyclerView.Adapter<Seat_Search_Recycler.ViewHolder> {

        private List<DataAdapter> listadapter;
        private Context context;

        Seat_Search_Recycler(List<DataAdapter> list) {
            super();
            this.listadapter = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item_recycler, parent, false));
        }

        @SuppressLint({"SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {
            DataAdapter dobj = listadapter.get(pos);

            vh.Nametv.setText(dobj.getName());
            vh.Timetv.setText(dobj.getAdult());
            vh.Typetv.setText(dobj.getType());
            vh.Droptv.setText(dobj.getChild());
            vh.Counttv.setText(dobj.getAvail()+" Seats");
            vh.Durationtv.setText(dobj.getDate());
            vh.Pricetv.setText("₹ "+dobj.getPrice());
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            TextView Nametv,Timetv,Typetv,Droptv,Counttv,Durationtv,Pricetv;

            ViewHolder(View iv) {
                super(iv);
                context = iv.getContext();

                Nametv = iv.findViewById(R.id.nametv);
                Timetv = iv.findViewById(R.id.timetv);
                Typetv = iv.findViewById(R.id.typetv);
                Droptv = iv.findViewById(R.id.droptimetv);
                Counttv = iv.findViewById(R.id.seatcounttv);
                Durationtv = iv.findViewById(R.id.durationtv);
                Pricetv = iv.findViewById(R.id.pricetv);

                iv.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Bus_Seat_Select_Act.class);
                intent.putExtra("routeid",listadapter.get(getAdapterPosition()).getID());
                intent.putExtra("tripid",listadapter.get(getAdapterPosition()).getTypeId());
                intent.putExtra("srcorder",listadapter.get(getAdapterPosition()).getAdult());
                intent.putExtra("dstorder",listadapter.get(getAdapterPosition()).getChild());
                intent.putExtra("bpid",listadapter.get(getAdapterPosition()).getRId());
                intent.putExtra("dpid",listadapter.get(getAdapterPosition()).getSuppid());
                context.startActivity(intent);
            }
        }

        @Override
        public int getItemCount() { return listadapter.size(); }

    }

}