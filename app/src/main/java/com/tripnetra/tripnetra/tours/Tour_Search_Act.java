package com.tripnetra.tripnetra.tours;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.DataAdapter;
import com.tripnetra.tripnetra.cars.fragments.Car_Sort_DFrag;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.tours.fragments.Tour_Filter_Frag;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Tour_Search_Act extends AppCompatActivity {

    String CityName,CityId,JourDate;
    RecyclerView recyclerView;
    Tour_Search_Recycle_Adapter tsrAdapter;
    int sorter;
    JSONArray MainJarray,SortJarray,FilerJarray;
    List<DataAdapter> list;
    LoadingDialog LoadDlg;
    G_Class g_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_search);

        CityName = Objects.requireNonNull(getIntent().getExtras()).getString("cityname");
        CityId = getIntent().getExtras().getString("cityid");
        JourDate = getIntent().getExtras().getString("cindate");

        g_class = (G_Class) getApplicationContext();
        recyclerView = findViewById(R.id.recycler);
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LoadDlg = new LoadingDialog(this);
        LoadDlg.setCancelable(false);
        Objects.requireNonNull(LoadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LoadDlg.show();

        gettourresults();

    }

    private void gettourresults() {

        Map<String, String> params = new HashMap<>();
        params.put("cityid", CityId);

        new VolleyRequester(this).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL+"tours/toursearch.php", LoadDlg, params, true, response -> {
            if(Objects.equals(response, "No Result")){
                if(LoadDlg.isShowing()){LoadDlg.dismiss();}
                Utils.setSingleBtnAlert(Tour_Search_Act.this, "No Results Found \nPlease Modify the Search Criteria", "Ok", true);
            }else {
                try {
                    MainJarray = new JSONArray(response);
                    SortJarray = MainJarray;
                    parsedata();
                    sorter = 1;
                } catch (JSONException e) {
                    //e.printStackTrace();
                    if(LoadDlg.isShowing()){LoadDlg.dismiss();}
                    Utils.setSingleBtnAlert(Tour_Search_Act.this, getResources().getString(R.string.something_wrong), "Ok", true);
                }
            }
        });
    }

    private void parsedata() {
        try {
            for(int i = 0; i< SortJarray.length(); i++) {

                DataAdapter adapter = new DataAdapter();
                JSONObject jobj = SortJarray.getJSONObject(i);

                adapter.setName(jobj.getString("sightseen_name"));
                adapter.setID(jobj.getString("sightseen_id"));
                adapter.setDate(JourDate);
                adapter.setSuppid(CityId);
                adapter.setFeature(CityName);
                adapter.setPrice(jobj.getString("sightseen_price"));
                String[] images = jobj.getString("sightseen_images").split(",");
                adapter.setImage(images[0]);

                if (Objects.equals(jobj.getString("sightseen_duration"), "1")) {
                    adapter.setText("1 DAY");
                } else {
                    adapter.setText(jobj.getString("sightseen_duration") + " DAYS");
                }

                list.add(adapter);
            }
            tsrAdapter = new Tour_Search_Recycle_Adapter(list);
            recyclerView.setAdapter(tsrAdapter);

        } catch (JSONException e) {
            //e.printStackTrace();
            if(LoadDlg.isShowing()){LoadDlg.dismiss();}
            Utils.setSingleBtnAlert(Tour_Search_Act.this, getResources().getString(R.string.something_wrong), "Ok", true);
        }
        if(LoadDlg.isShowing()){LoadDlg.dismiss();}
    }

    public void sorttours(View v){
        Car_Sort_DFrag cs = new Car_Sort_DFrag();
        cs.show(getSupportFragmentManager(),"Sort tours");
        Bundle bb = new Bundle();
        bb.putString("type","tours");
        cs.setArguments(bb);
    }

    public  void getsort(final String sortby){
        List<JSONObject> jsonList = new ArrayList<>();
        list.clear();
        SortJarray = new JSONArray(new ArrayList<String>());
        try {
            if(sorter==1) {
                for (int i = 0; i < MainJarray.length(); i++) { jsonList.add(MainJarray.getJSONObject(i)); }
            }else{
                for (int i = 0; i < FilerJarray.length(); i++) { jsonList.add(FilerJarray.getJSONObject(i)); }
            }

            Collections.sort(jsonList, (a, b) -> {
                int sort = 0;
                try {
                    if(Objects.equals(sortby, "namelow")) {
                        String valA = (String) a.get("sightseen_name");
                        String valB = (String) b.get("sightseen_name");
                        sort = valA.compareTo(valB);
                    }else if(Objects.equals(sortby, "namehigh")){
                        String valA = (String) a.get("sightseen_name");
                        String valB = (String) b.get("sightseen_name");
                        sort = valB.compareTo(valA);
                    }else if(Objects.equals(sortby, "pricelow")){
                        int valA = Integer.parseInt((String) a.get("sightseen_price"));
                        int valB = Integer.parseInt((String) b.get("sightseen_price"));
                        sort = Integer.compare(valA,valB);
                    }else if(Objects.equals(sortby, "pricehigh")){
                        int valA = Integer.parseInt((String) a.get("sightseen_price"));
                        int valB = Integer.parseInt((String) b.get("sightseen_price"));
                        sort = Integer.compare(valB,valA);
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(Tour_Search_Act.this, getResources().getString(R.string.something_wrong),"Ok",true);
                }
                return sort;
            });

            if(sorter==1) {for (int i = 0; i < MainJarray.length(); i++) {SortJarray.put(jsonList.get(i));}
            }else{for (int i = 0; i < FilerJarray.length(); i++) {SortJarray.put(jsonList.get(i));}}

        } catch (JSONException e) {
            //e.printStackTrace();
            Utils.setSingleBtnAlert(Tour_Search_Act.this, getResources().getString(R.string.something_wrong),"Ok",true);
        }

        LoadDlg.show();
        parsedata();

    }

    public void filtertour(View v){ new Tour_Filter_Frag().show(getSupportFragmentManager(),"Filter Tours"); }

    @SuppressWarnings("ConstantConditions")
    public void getfilter(Bundle bb){

        LoadDlg.show();

        int lowprice = Integer.parseInt(bb.getString("lowprice")),
            highprice = Integer.parseInt(bb.getString("highprice")),
            incity = Integer.parseInt(bb.getString("incity")),
            outcity = Integer.parseInt(bb.getString("outcity"));

        list.clear();
        FilerJarray = new JSONArray();

        try {
            for(int i = 0; i< MainJarray.length(); i++) {
                JSONObject json = MainJarray.getJSONObject(i);

                int price = Integer.parseInt(json.getString("sightseen_price")),
                        city = Integer.parseInt(json.getString("sightseen_category_id"));

                if(lowprice <= price && price <= highprice &&
                        (city == incity || city == outcity )){ FilerJarray.put(json); }

            }
        } catch (JSONException e) {
            //e.printStackTrace();
            Utils.setSingleBtnAlert(Tour_Search_Act.this, getResources().getString(R.string.something_wrong),"Ok",true);
        }

        if(FilerJarray.length()>0){

            SortJarray = FilerJarray;
            sorter=2;
            parsedata();

        }else{

            if(tsrAdapter!= null){tsrAdapter.notifyDataSetChanged();}
            LoadDlg.dismiss();
            Utils.setSingleBtnAlert(Tour_Search_Act.this, "No Results Found\nPlease Modify Your Search","Ok",true);
            SortJarray = MainJarray;

        }
    }

    public class Tour_Search_Recycle_Adapter extends RecyclerView.Adapter<Tour_Search_Recycle_Adapter.ViewHolder> {

        private Context context;
        private List<DataAdapter> listadapter;

        Tour_Search_Recycle_Adapter(List<DataAdapter> list) {
            super();
            this.listadapter = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tour_search_recycler, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {
            DataAdapter dataAdapter =  listadapter.get(pos);

            Glide.with(context).load(g_class.getImageurl()+Config.SIGHT_IMAGE+dataAdapter.getImage()).into(vh.TourIv);

            vh.TnameTv.setText(dataAdapter.getName());
            vh.TdaysTv.setText(dataAdapter.getText());
            vh.TpriceTv.setText("₹ "+dataAdapter.getPrice());
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView TourIv;
            TextView TnameTv,TdaysTv,TpriceTv,TbookTv;

            ViewHolder(View itemView) {
                super(itemView);
                context = itemView.getContext();
                TourIv = itemView.findViewById(R.id.tourIv);
                TnameTv = itemView.findViewById(R.id.tnameTv);
                TdaysTv = itemView.findViewById(R.id.tdaysTv);
                TpriceTv = itemView.findViewById(R.id.tpricetTv);
                TbookTv = itemView.findViewById(R.id.bookbtn);


                itemView.setOnClickListener( v ->  {

                    Intent intent = new Intent(context, Tour_Details_Act.class);
                    intent.putExtra("name",listadapter.get(getAdapterPosition()).getName());
                    intent.putExtra("journeydate",listadapter.get(getAdapterPosition()).getDate());
                    intent.putExtra("tourid",listadapter.get(getAdapterPosition()).getID());
                    intent.putExtra("tourdays",listadapter.get(getAdapterPosition()).getText());
                    intent.putExtra("tourprice",listadapter.get(getAdapterPosition()).getPrice());
                    intent.putExtra("cityid",listadapter.get(getAdapterPosition()).getSuppid());
                    intent.putExtra("cityname",listadapter.get(getAdapterPosition()).getFeature());
                    context.startActivity(intent);

                });
            }
        }

        @Override
        public int getItemCount() {return listadapter.size();}

    }
}