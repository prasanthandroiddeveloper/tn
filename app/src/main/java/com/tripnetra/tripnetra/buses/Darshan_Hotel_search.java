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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.DataAdapter;
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

public class Darshan_Hotel_search extends AppCompatActivity {

    Bundle M_bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses_search);

        M_bundle = getIntent().getExtras();
        //fromid toid fromname toname date roomcount personcount rcount1 rcount2 rcount3

        getdata();
    }

    @SuppressLint("SetTextI18n")
    private void getdata() {

        Map<String, String> params = new HashMap<>();
        params.put("type", "gethotels");

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL+"bus/details.php", null, params, true, response -> {
             //todo place below code here while webservices finished

            try {

           /*   InputStream is = getAssets().open("hotels.txt");
                byte[] buffer = new byte[is.available()]; //todo replace with above rest call
                //noinspection ResultOfMethodCallIgnored
                is.read(buffer);
                is.close();*/

                RecyclerView recyclerView = findViewById(R.id.recyclerView);

                if(response.equals("No Result")){
                    recyclerView.setVisibility(View.GONE);
                    TextView tv = findViewById(R.id.nopeTv);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("No Hotels Available For Selected Route");
                }else {
                    findViewById(R.id.nopeTv).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Darshan_Hotel_search.this));

                    JSONArray jarr = new JSONArray(new String(response));

                    List<DataAdapter> list = new ArrayList<>();

                    for(int i = 0; i< jarr.length(); i++) {
                        DataAdapter adapter = new DataAdapter();
                        JSONObject json = jarr.getJSONObject(i);

                        adapter.setID(json.getString("hotel_details_id"));
                        adapter.setImage(((G_Class) getApplicationContext()).getImageurl()+"hotel_images/" +json.getString("thumb_image"));
                        adapter.setName(json.getString("hotel_name"));
                        adapter.setDescription(json.getString("hotel_amenities"));

                        double sgl_price = Double.parseDouble(json.getString("min(hrr.sgl_price)")),
                                commission = Double.parseDouble((json.getString("commission")));
                        double tripprice = sgl_price - ((sgl_price * commission) / 100);

                        int TotalPrice;

                        if(Objects.equals(json.getString("markup_value"), "")||Objects.equals(json.getString("markup_value"), "0")){
                            TotalPrice = (int) Math.ceil(sgl_price+Double.parseDouble(json.getString("markup_fixed")));
                        }else{
                            tripprice += ((tripprice*Double.parseDouble(json.getString("markup_value")))/100);
                            TotalPrice = (int) Math.ceil(tripprice);
                        }

                        adapter.setAPrice(String.valueOf(TotalPrice));
                        TotalPrice += 700+900+500+700; //todo bus+ticket+profit+bus

                        adapter.setPrice(String.valueOf(TotalPrice));

                        if(json.getString("24_hrs").equals("ACTIVE")){
                            adapter.setText("24 Hours Check-In");
                        }else{
                            adapter.setText("Standard C-In "+json.getString("exc_checkin_time")+" - "+json.getString("exc_checkout_time"));
                        }
                        adapter.setBundle(M_bundle);

                        list.add(adapter);
                    }

                    recyclerView.setAdapter(new Hotel_Search_Recycler(list));
                }

            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(Darshan_Hotel_search.this, getResources().getString(R.string.something_wrong), "Ok", true);
            }



        });


    }

    private class Hotel_Search_Recycler extends RecyclerView.Adapter<Hotel_Search_Recycler.ViewHolder> {

        private List<DataAdapter> listadapter;
        private Context context;

        Hotel_Search_Recycler(List<DataAdapter> list) { super();this.listadapter = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_darshan_header, parent, false));
        }

        @SuppressLint({"SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {
            DataAdapter dobj = listadapter.get(pos);

            vh.NameTV.setText(dobj.getName());
            Glide.with(context).load(dobj.getImage()).into(vh.ImageIV);
            vh.CinTV.setText(dobj.getText());
            vh.PriceTV.setText("Starts From ₹ "+dobj.getPrice());

            String array_items[] = dobj.getDescription().split(",");
            for (String array_item : array_items) {
                ImageView imgView = new ImageView(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(90,90);
                layoutParams.setMargins(10,0,10,0);
                imgView.setLayoutParams(layoutParams);
                vh.amenLayt.addView(imgView);

                if (Objects.equals(array_item, "4")) {
                    imgView.setImageResource(R.drawable.ivroomser);
                } else if (Objects.equals(array_item, "5")) {
                    imgView.setImageResource(R.drawable.ivac);
                } else if (Objects.equals(array_item, "9")) {
                    imgView.setImageResource(R.drawable.ivwifi);
                } else if (Objects.equals(array_item, "10")) {
                    imgView.setImageResource(R.drawable.ivcar);
                } else if (Objects.equals(array_item, "22")) {
                    imgView.setImageResource(R.drawable.ivtv);
                }else if (Objects.equals(array_item, "32")) {
                    imgView.setImageResource(R.drawable.indtoi);
                }else if (Objects.equals(array_item, "31")) {
                    imgView.setImageResource(R.drawable.westtoi);
                }else {
                    imgView.setVisibility(View.GONE);
                }
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView NameTV,CinTV,PriceTV;
            ImageView ImageIV;
            LinearLayout amenLayt;
            ViewHolder(View iv) {
                super(iv);
                context = iv.getContext();

                NameTV = iv.findViewById(R.id.nametv);
                CinTV = iv.findViewById(R.id.descrttv);
                PriceTV = iv.findViewById(R.id.pricetv);
                ImageIV = iv.findViewById(R.id.ImageIv);
                amenLayt = iv.findViewById(R.id.weekLayt);

                iv.setOnClickListener(v -> {
                    Intent intent = new Intent(context, Buses_Search_Act.class);

                    intent.putExtra("hotelid", listadapter.get(getAdapterPosition()).getID());
                    intent.putExtra("hotelname", listadapter.get(getAdapterPosition()).getName());
                    intent.putExtra("bundle", listadapter.get(getAdapterPosition()).getBundle());
                    context.startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() { return listadapter.size(); }

    }

}