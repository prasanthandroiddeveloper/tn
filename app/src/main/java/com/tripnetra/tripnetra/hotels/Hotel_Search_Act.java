package com.tripnetra.tripnetra.hotels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.DataAdapter;
import com.tripnetra.tripnetra.hotels.fragments.Hotel_Filter_Frag;
import com.tripnetra.tripnetra.hotels.fragments.Hotel_Sort_Frag;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Hotel_Search_Act extends AppCompatActivity {

    RecyclerView recyclerView;
    String horcname,fromdate,todate,acount,ccount,rcount,sinday,soutday,lowprice,highprice;
    Hotel_Search_Recycle_Adapter adapter;
    JSONArray MainJArray = null,SortJArray,FilterJArray;
    LoadingDialog LoadDlg;
    int sorter;
    LinearLayout NoResLayt;

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_search);

        LoadDlg = new LoadingDialog(this);
        LoadDlg.setCancelable(false);
        LoadDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        recyclerView = findViewById(R.id.recyclerView);
        if(getIntent().getExtras() != null) {
            horcname = getIntent().getExtras().getString("HorCname");
            fromdate = getIntent().getExtras().getString("fromdate");
            todate = getIntent().getExtras().getString("todate");
            acount = getIntent().getExtras().getString("acount");
            rcount = getIntent().getExtras().getString("rcount");
            ccount = getIntent().getExtras().getString("ccount");
            sinday = getIntent().getExtras().getString("inday");
            soutday = getIntent().getExtras().getString("outday");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((TextView) findViewById(R.id.loctv)).setText(horcname);
        ((TextView) findViewById(R.id.datetv)).setText(sinday+"\n-\n"+soutday);
        ((TextView) findViewById(R.id.roomtv)).setText(rcount+" Rooms");
        ((TextView) findViewById(R.id.adulttv)).setText(acount+" A");
        ((TextView) findViewById(R.id.childtv)).setText(ccount+" C");

        NoResLayt = findViewById(R.id.nopeLayt);

        hotelsearchlist();
    }

    public void hotelsearchlist(){
        LoadDlg.show();

        Map<String, String> params = new HashMap<>();
        params.put("hotel_place", horcname);
        params.put("hotel_checkin", fromdate);
        params.put("hotel_checkout", todate);
        params.put("room_count", rcount);

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL+"hotelsearch.php", LoadDlg, params, true, response -> {
            try {

                MainJArray = new JSONArray(response);
                SortJArray = MainJArray;
                parserecycle();
                sorter=1;
            }catch (JSONException e) {
                if(LoadDlg.isShowing()){LoadDlg.dismiss();}
                //e.printStackTrace();
                NoResLayt.setVisibility(View.VISIBLE);
            }
        });

    }

    private void parserecycle(){
        try {
            NoResLayt.setVisibility(View.GONE);
            List<DataAdapter> list = new ArrayList<>();
            for(int i = 0; i< SortJArray.length(); i++) {
                DataAdapter dataAdapter = new DataAdapter();
                JSONObject json = SortJArray.getJSONObject(i);

                dataAdapter.setID(json.getString("hotel_details_id"));
                dataAdapter.setTypeId(json.getString("hotel_type_id"));
                dataAdapter.setName(json.getString("hotel_name"));
                dataAdapter.setAdult(acount);
                dataAdapter.setAvail(rcount);

                if(Objects.equals(json.getString("24_hrs"), "ACTIVE")) {
                    dataAdapter.setText("24 Hours Check-In");
                }else{
                    dataAdapter.setText("Standard Checkin ("+json.getString("exc_checkin_time")+"-"+json.getString("exc_checkout_time")+")");
                }
                double sgl_price = Double.parseDouble(json.getString("min(hrr.sgl_price)")),
                    commission = Double.parseDouble((json.getString("commission")));
                double tripprice = sgl_price - ((sgl_price * commission) / 100);

                int DiscPrice,TotalPrice;

                if(Objects.equals(json.getString("markup_value"), "")||Objects.equals(json.getString("markup_value"), "0")){
                    TotalPrice = (int) Math.ceil(sgl_price+Double.parseDouble(json.getString("markup_fixed")));
                }else{
                    tripprice += ((tripprice*Double.parseDouble(json.getString("markup_value")))/100);
                    TotalPrice = (int) Math.ceil(tripprice);
                }

                dataAdapter.setPrice(String.valueOf(TotalPrice));

                if(json.getString("coupan_discount") == null || Objects.equals(json.getString("coupan_discount"), "null") ){
                    DiscPrice=0;
                }else{
                    DiscPrice = TotalPrice*Integer.parseInt(json.getString("coupan_discount"))/100;
                }
                dataAdapter.setAPrice(String.valueOf(DiscPrice+TotalPrice));
                dataAdapter.setDprice(String.valueOf(DiscPrice));
                dataAdapter.setType(json.getString("coupan_code"));
                dataAdapter.setDescription(json.getString("coupan_discount"));
                dataAdapter.setSMax(json.getString("hotel_amenities"));
                dataAdapter.setImage(((G_Class) getApplicationContext()).getImageurl()+"hotel_images/" + json.getString("thumb_image"));
                list.add(dataAdapter);
            }
            adapter = new Hotel_Search_Recycle_Adapter(list);
            recyclerView.setAdapter(adapter);

        } catch (JSONException|NumberFormatException e) {
            //e.printStackTrace();
            NoResLayt.setVisibility(View.VISIBLE);
        }
        if(LoadDlg.isShowing()){LoadDlg.dismiss();}
    }

    public void modifysearch(View v) {startActivity(new Intent(this,Hotel_Main_Act.class));}

    public void sortby(View v){if(MainJArray!=null){new Hotel_Sort_Frag().show(getSupportFragmentManager(),"Sort hotels");}}

    public void filter(View v){if(MainJArray!=null){new Hotel_Filter_Frag().show(getSupportFragmentManager(),"Filter hotels");}}

    public  void getsort(Intent bb){
        assert bb.getExtras()!= null;
        final String sortby = bb.getExtras().getString("sortby");
        List<JSONObject> jsonList = new ArrayList<>();

        SortJArray = new JSONArray(new ArrayList<String>());
        try {
            if(sorter==1) {
                for (int i = 0; i < MainJArray.length(); i++) {
                    jsonList.add(MainJArray.getJSONObject(i));
                }
            }else{
                for (int i = 0; i < FilterJArray.length(); i++) {
                    jsonList.add(FilterJArray.getJSONObject(i));
                }
            }

            Collections.sort(jsonList, (a, b) -> {
                int sort = 0;
                try {
                    switch (Objects.requireNonNull(sortby)) {
                        case "namelow": {
                            String valA = (String) a.get("hotel_name");
                            String valB = (String) b.get("hotel_name");
                            sort = valA.compareTo(valB);
                            break;
                        }
                        case "namehigh": {
                            String valA = (String) a.get("hotel_name");
                            String valB = (String) b.get("hotel_name");
                            sort = valB.compareTo(valA);
                            break;
                        }
                        case "pricelow": {
                            int valA = Integer.parseInt((String) a.get("min(hrr.sgl_price)"));
                            int valB = Integer.parseInt((String) b.get("min(hrr.sgl_price)"));
                            sort = Integer.compare(valA, valB);
                            break;
                        }
                        case "pricehigh": {
                            int valA = Integer.parseInt((String) a.get("min(hrr.sgl_price)"));
                            int valB = Integer.parseInt((String) b.get("min(hrr.sgl_price)"));
                            sort = Integer.compare(valB, valA);
                            break;
                        }
                        case "starlow": {
                            int valA = Integer.parseInt((String) a.get("star_rating"));
                            int valB = Integer.parseInt((String) b.get("star_rating"));
                            sort = Integer.compare(valA, valB);
                            break;
                        }
                        case "starhigh": {
                            int valA = Integer.parseInt((String) a.get("star_rating"));
                            int valB = Integer.parseInt((String) b.get("star_rating"));
                            sort = Integer.compare(valB, valA);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
                return sort;
            });


            if(sorter==1) {
                for (int i = 0; i < MainJArray.length(); i++) {
                    SortJArray.put(jsonList.get(i));
                }
            }else{
                for (int i = 0; i < FilterJArray.length(); i++) {
                    SortJArray.put(jsonList.get(i));
                }
            }


        } catch (JSONException e) {
            //e.printStackTrace();
        }
        LoadDlg.show();

        parserecycle();
    }

    public void getfilters(Intent bb) {

        if (bb.getExtras() != null) {
            String[] g1 = bb.getExtras().getStringArray("g1");
            String[] g2 = bb.getExtras().getStringArray("g2");
            String[] g3 = bb.getExtras().getStringArray("g3");
            String[] g4 = bb.getExtras().getStringArray("g4");
            lowprice = bb.getExtras().getString("lowprice");
            highprice = bb.getExtras().getString("highprice");

            LoadDlg.show();

            FilterJArray = new JSONArray();
            try {
                for (int i = 0; i < MainJArray.length(); i++) {
                    JSONObject json = MainJArray.getJSONObject(i);

                    if (Objects.requireNonNull(g1).length == 0 || Arrays.asList(g1).contains(json.getString("hotel_type_id"))) {
                        if (Objects.requireNonNull(g2).length == 0 || Arrays.asList(g2).contains(json.getString("24_hrs"))) {
                            String[] Amensitems = json.getString("hotel_amenities").split(",");
                            boolean temp = false;

                            for (String aG3 : Objects.requireNonNull(g3)) {
                                if (Arrays.asList(Amensitems).contains(aG3)) {
                                    temp = true;
                                } else {
                                    temp = false;
                                    break;
                                }
                            }

                            if (g3.length == 0 || temp) {
                                if (Objects.requireNonNull(g4).length == 0 || Arrays.asList(g4).contains(json.getString("star_rating"))) {
                                    int TotalPrice;

                                    double sgl_price = Double.parseDouble(json.getString("min(hrr.sgl_price)")),
                                            commission = Double.parseDouble(json.getString("commission"));
                                    double tripprice = sgl_price - ((sgl_price * commission) / 100);

                                    if (Objects.equals(json.getString("markup_value"), "") || Objects.equals(json.getString("markup_value"), "0")) {
                                        TotalPrice = (int) Math.ceil(sgl_price + Double.parseDouble(json.getString("markup_fixed")));
                                    } else {
                                        tripprice = tripprice + ((tripprice * Double.parseDouble(json.getString("markup_value"))) / 100);
                                        TotalPrice = (int) Math.ceil(tripprice);
                                    }

                                    if (TotalPrice >= Integer.parseInt(lowprice) && TotalPrice <= Integer.parseInt(highprice)) {
                                        FilterJArray.put(json);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                if(LoadDlg.isShowing()){LoadDlg.dismiss();}
                Utils.setSingleBtnAlert(Hotel_Search_Act.this, getResources().getString(R.string.something_wrong), "Ok", true);
            }

            if (FilterJArray.length() > 0) {
                SortJArray = FilterJArray;
                sorter = 2;
                parserecycle();
            } else {
                if(LoadDlg.isShowing()){LoadDlg.dismiss();}
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                NoResLayt.setVisibility(View.VISIBLE);
            }
        }
    }

    public class Hotel_Search_Recycle_Adapter extends RecyclerView.Adapter<Hotel_Search_Recycle_Adapter.ViewHolder>  {

        private Context context;
        private List<DataAdapter> listadapter;

        Hotel_Search_Recycle_Adapter(List<DataAdapter> adapterList){
            super();
            this.listadapter = adapterList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView hnametv,cintv,actualtv,pricetv,dealitv,dealstv;
            ImageView hotelimage ;
            LinearLayout ImageLLout;
            ImageView iv;
            ViewHolder(View itemView) {
                super(itemView);
                hnametv = itemView.findViewById(R.id.hnametv);
                cintv = itemView.findViewById(R.id.cintv);
                actualtv = itemView.findViewById(R.id.actualtv);
                pricetv = itemView.findViewById(R.id.pricetv);
                hotelimage = itemView.findViewById(R.id.himageiv);
                dealitv = itemView.findViewById(R.id.dealitv);
                dealstv = itemView.findViewById(R.id.Dealstv);

                ImageLLout = itemView.findViewById(R.id.imagellout);
                iv = itemView.findViewById(R.id.iv);
                context = itemView.getContext();
                actualtv.setPaintFlags(actualtv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                itemView.setOnClickListener(v -> {

                    DataAdapter dadap = listadapter.get(getAdapterPosition());
                    if (dadap.getAdult().equals(dadap.getAvail()) && dadap.getTypeId().equals("4")) {
                        Toast.makeText(context, "Single Person cannot Book Trust Rooms", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(context, Hotel_Details_Act.class);
                    intent.putExtra("HotelId", dadap.getID());
                    intent.putExtra("HotelName", dadap.getName());
                    intent.putExtra("Couponcode", dadap.getType());
                    context.startActivity(intent);
                });
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_slist_recycler, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder vh, int position) {
            DataAdapter dataAdapter =  listadapter.get(position);

            Glide.with(context).load(dataAdapter.getImage()).into(vh.hotelimage);

            vh.hnametv.setText(dataAdapter.getName());
            vh.cintv.setText(dataAdapter.getText());

            vh.setIsRecyclable(false);
            String array_items[] = dataAdapter.getSMax().split(",");
            for (String array_item : array_items) {
                ImageView imgView = new ImageView(context);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(55,55);
                layoutParams.setMargins(10,0,10,0);
                imgView.setLayoutParams(layoutParams);
                vh.ImageLLout.addView(imgView);

                switch (array_item) {
                    case "4":
                        imgView.setImageResource(R.drawable.trmsrvc);
                        break;
                    case "5":
                        imgView.setImageResource(R.drawable.tsac);
                        break;
                    case "9":
                        imgView.setImageResource(R.drawable.twifi);
                        break;
                    case "10":
                        imgView.setImageResource(R.drawable.tcarp);
                        break;
                    case "22":
                        imgView.setImageResource(R.drawable.tled);
                        break;
                    case "32":
                        imgView.setImageResource(R.drawable.tinto);
                        break;
                    case "31":
                        imgView.setImageResource(R.drawable.twsto);
                        break;
                    default:
                        imgView.setVisibility(View.GONE);
                        break;
                }
            }
            Log.i("amenities",dataAdapter.getDescription());

            if(!Objects.equals(dataAdapter.getType(), "null")){
                vh.dealstv.setText("Applied \""+ dataAdapter.getType() + "\" for upto "+ dataAdapter.getDescription()+"% Discount");
                // vh.disctv.setText(" Save ₹ "+ dataAdapter.getDprice()+"");
                vh.actualtv.setText("₹ "+ dataAdapter.getAPrice()+"");
            }else{
                vh.dealitv.setVisibility(View.GONE);
                vh.dealstv.setVisibility(View.GONE);
                // vh.disctv.setVisibility(View.GONE);
                vh.actualtv.setVisibility(View.GONE);
            }
            vh.pricetv.setText(" ₹ "+ dataAdapter.getPrice()+"");

            if (Objects.equals(dataAdapter.getAvail(),"1")){
                vh.iv.setImageResource(R.drawable.sold);
            }
        }

        @Override
        public int getItemCount() {return listadapter.size();}

    }

}