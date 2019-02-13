package com.tripnetra.tripnetra.places;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.DataAdapter;
import com.tripnetra.tripnetra.hotels.Hotel_Main_Act;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Marker_Details_Act extends AppCompatActivity {

    TextView TVTitle,TVtimings,TVdescptn;
    ImageView placeimage;
    String nearcity,Placeid;
    RecyclerView recview;
    Event_Recycle_Adapter eventrec;
    G_Class g_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_details);

        g_class = (G_Class) getApplicationContext();
        TVTitle = findViewById(R.id.TitleTV);
        TVtimings = findViewById(R.id.TimeTV);
        TVdescptn = findViewById(R.id.DescpTV);
        placeimage = findViewById(R.id.placeimageIV);
        recview = findViewById(R.id.eventrecycler);
        recview.setLayoutManager(new LinearLayoutManager(this));

        try {
            JSONObject jobject = new JSONObject(Objects.requireNonNull(getIntent().getExtras()).getString("JsonObject"));
            TVTitle.setText(jobject.getString("place_name"));
            TVtimings.setText(jobject.getString("timings"));
            TVdescptn.setText(jobject.getString("long_description"));
            Placeid = jobject.getString("place_id");

            if (!Objects.equals(jobject.getString("image_url"), "")) {
                Glide.with(this).load(g_class.getBaseurl()+"triptravel/images/" + jobject.getString("image_url")).into(placeimage);
                //apply(new RequestOptions().placeholder(R.drawable.loader).centerCrop())
            }

            nearcity = jobject.getString("near_city");

            geteventsdata();

        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    private void geteventsdata() {

        Map<String, String> params = new HashMap<>();
        params.put("Placeid", Placeid);

        new VolleyRequester(this).ParamsRequest(1, g_class.getBaseurl()+Config.EVENT_DETAILS_URL, null, params, false, response -> {
            if (!Objects.equals(response, "NO result")) {

                try {
                    List<DataAdapter> list = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        DataAdapter dataAdapter = new DataAdapter();
                        JSONObject jobject = (JSONObject) jsonArray.get(i);

                        dataAdapter.setDate(jobject.getString("event_date"));
                        dataAdapter.setText(jobject.getString("event_text"));
                        dataAdapter.setName(jobject.getString("event_name"));

                        list.add(dataAdapter);
                    }

                    eventrec = new Event_Recycle_Adapter(list);
                    recview.setAdapter(eventrec);

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(Marker_Details_Act.this,getResources().getString(R.string.something_wrong),"Ok",false);
                }
            }else {
                TextView evTV = findViewById(R.id.eventid);
                evTV.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void bookaccomdation(View v){
        startActivity(new Intent(this, Hotel_Main_Act.class).putExtra("cname",nearcity));
        finish();
    }

    class Event_Recycle_Adapter extends RecyclerView.Adapter<Event_Recycle_Adapter.ViewHolder> {

        private Context context;
        private List<DataAdapter> listadapter;

        Event_Recycle_Adapter(List<DataAdapter> list) {
            super();
            this.listadapter = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DataAdapter dataAdapter =  listadapter.get(position);

            holder.dateTV.setText(Utils.ChangeDateFormat(dataAdapter.getDate(),4));

            holder.EnameTV.setText(dataAdapter.getName());

        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView dateTV,EnameTV;

            ViewHolder(View itemView) {
                super(itemView);
                dateTV = itemView.findViewById(R.id.dateTV);
                EnameTV = itemView.findViewById(R.id.EnameTV);
                context = itemView.getContext();

                itemView.setOnClickListener(v ->
                        new AlertDialog.Builder(context).setTitle(listadapter.get(getAdapterPosition()).getName())
                                .setMessage(listadapter.get(getAdapterPosition()).getDate() + "\n" + listadapter.get(getAdapterPosition()).getText())
                                .setPositiveButton("OK", (dialog, id) -> {})
                                .setCancelable(true).create().show());
            }
        }

        @Override
        public int getItemCount() { return listadapter.size(); }

    }
}
