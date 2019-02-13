package com.tripnetra.tripnetra.cars;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.DataAdapter;
import com.tripnetra.tripnetra.cars.fragments.Car_Filter_Frag;
import com.tripnetra.tripnetra.cars.fragments.Car_Sort_DFrag;
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

public class Car_Search_Act extends AppCompatActivity {

    Bundle mainbundle;
    String TripType,CityName="",PickLoc,DropLoc,Jdate,Jtime,lowprice,highprice,JDispdate,Pickupid,Dropid,Cityid="";
    JSONArray MainJarray,SortJarray,FilerJarray;
    int sorter;
    Car_Search_Recycle_Adapter car_rec_adapter ;
    RecyclerView recyclerView;
    LoadingDialog LoadDlg;
    Button SortBtn,FilterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_search);

        recyclerView = findViewById(R.id.CarRecyclerView);
        SortBtn = findViewById(R.id.sortBtn);
        FilterBtn = findViewById(R.id.filterBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LoadDlg = new LoadingDialog(this);
        LoadDlg.setCancelable(false);
        Objects.requireNonNull(LoadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (getIntent().getExtras() != null) {
            mainbundle = getIntent().getExtras();
            TripType = mainbundle.getString("type");
            if (Objects.equals(TripType, "local")) {
                CityName = mainbundle.getString("cityname");
                Cityid = mainbundle.getString("cityid");
            }
            PickLoc = mainbundle.getString("pickup");
            Pickupid = mainbundle.getString("pickupid");
            Dropid = mainbundle.getString("dropid");
            DropLoc = mainbundle.getString("drop");
            Jdate = mainbundle.getString("date");
            Jtime = mainbundle.getString("time");
            JDispdate = mainbundle.getString("Dispdate");

            ((TextView)findViewById(R.id.picktv)).setText(PickLoc);
            ((TextView)findViewById(R.id.droptv)).setText(DropLoc);
            ((TextView)findViewById(R.id.datetv)).setText(JDispdate);
            ((TextView)findViewById(R.id.timetv)).setText(Jtime);

            SortBtn.setEnabled(false);
            FilterBtn.setEnabled(false);
            LoadDlg.show();
            getcarsearchdata();

        }
    }

    public void getcarsearchdata() {

        Map<String, String> params = new HashMap<>();
        params.put("type", TripType);
        params.put("pickupid", Pickupid);
        params.put("dropid", Dropid);

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIPCAR_URL+"carsearch.php", LoadDlg, params, true, response -> {
            if (Objects.equals(response, "No Result")) {
                if(LoadDlg.isShowing()){LoadDlg.dismiss();}
                Utils.setSingleBtnAlert(Car_Search_Act.this,"No cars Found\n\nPlease Modify Search Criteria","Ok",true);
            } else {
                try {
                    MainJarray = new JSONArray(response);
                    SortJarray = MainJarray;
                    parsedata();
                    sorter = 1;
                    SortBtn.setEnabled(true);
                    FilterBtn.setEnabled(true);
                } catch (JSONException e) {
                    if(LoadDlg.isShowing()){LoadDlg.dismiss();}
                    Utils.setSingleBtnAlert(Car_Search_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
                    //e.printStackTrace();
                }
            }
        });
    }

    public void sortcars(View v){
        Car_Sort_DFrag cs = new Car_Sort_DFrag();
        cs.show(getSupportFragmentManager(),"Sort cars");
        Bundle bb = new Bundle();
        bb.putString("type","car");
        cs.setArguments(bb);
    }

    public void filtercars(View v){ new Car_Filter_Frag().show(getSupportFragmentManager(),"Filter cars"); }

    public void parsedata(){
        try {
            List<DataAdapter> list = new ArrayList<>();
            for(int i = 0; i<SortJarray.length(); i++) {
                DataAdapter datadapter = new DataAdapter();
                JSONObject json = SortJarray.getJSONObject(i);

                datadapter.setName(json.getString("car_name"));
                datadapter.setID(json.getString("car_details_id"));
                datadapter.setTypeId(json.getString("car_type_id"));
                datadapter.setSuppid(json.getString("supplier_id"));
                datadapter.setFeature(json.getString("car_feature"));
                datadapter.setDescription(" "+json.getString("no_of_doors")+" Doors | "+json.getString("max_capacity")+" Seats | "+json.getString("small_bag_capacity")+" Air Bags ");
                if(json.getString("description").equals("")){
                    datadapter.setText(" Comfortable With Quality Interiors");
                }else{
                    datadapter.setText(json.getString("description"));
                }
                String ss = ((G_Class) getApplicationContext()).getImageurl();
                ss = ss.replace("uploads/","");
                datadapter.setImage(ss+json.getString("car_image"));
                datadapter.setPrice(json.getString("car_price_day"));
                datadapter.setTax(json.getString("tax"));
                datadapter.setStatus(json.getString("car_status"));
                datadapter.setType(json.getString("car_type"));
                datadapter.setPolicy(json.getString("cancellation_policy"));
                datadapter.setCompany(json.getString("car_company_name"));
                datadapter.setBundle(mainbundle);

                list.add(datadapter);
            }

            if(LoadDlg.isShowing()){LoadDlg.dismiss();}
            car_rec_adapter = new Car_Search_Recycle_Adapter(list);
            recyclerView.setAdapter(car_rec_adapter);

        } catch (JSONException e) {
            //e.printStackTrace();
            if(LoadDlg.isShowing()){LoadDlg.dismiss();}
            Utils.setSingleBtnAlert(Car_Search_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
        }
    }

    public  void getsort(final String sortby){
        List<JSONObject> jsonList = new ArrayList<>();
        SortJarray = new JSONArray(new ArrayList<String>());
        try {
            if(sorter==1) {for (int i = 0; i < MainJarray.length(); i++) {jsonList.add(MainJarray.getJSONObject(i));}
            }else{for (int i = 0; i < FilerJarray.length(); i++) {jsonList.add(FilerJarray.getJSONObject(i));}}

            Collections.sort(jsonList, (a, b) -> {
                int sort = 0;
                try {
                    if(Objects.equals(sortby, "namelow")) {
                        String valA = (String) a.get("car_name");
                        String valB = (String) b.get("car_name");
                        sort = valA.compareTo(valB);
                    }else if(Objects.equals(sortby, "namehigh")){
                        String valA = (String) a.get("car_name");
                        String valB = (String) b.get("car_name");
                        sort = valB.compareTo(valA);
                    }else if(Objects.equals(sortby, "pricelow")){
                        int valA = Integer.parseInt((String) a.get("car_price_day"));
                        int valB = Integer.parseInt((String) b.get("car_price_day"));
                        sort = Integer.compare(valA,valB);
                    }else if(Objects.equals(sortby, "pricehigh")){
                        int valA = Integer.parseInt((String) a.get("car_price_day"));
                        int valB = Integer.parseInt((String) b.get("car_price_day"));
                        sort = Integer.compare(valB,valA);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.setSingleBtnAlert(Car_Search_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
                }
                return sort;
            });


            if(sorter==1) {for (int i = 0; i < MainJarray.length(); i++) {SortJarray.put(jsonList.get(i));}
            }else{for (int i = 0; i < FilerJarray.length(); i++) {SortJarray.put(jsonList.get(i));}}

        } catch (JSONException e) {
            //e.printStackTrace();
            Utils.setSingleBtnAlert(Car_Search_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
        }

        LoadDlg.show();
        parsedata();

    }

    public void getfilters(Intent bb){
        if(bb.getExtras() != null) {
            LoadDlg.show();
            String[] CarTypeids = bb.getExtras().getStringArray("cartypeids");
            lowprice = bb.getExtras().getString("lowprice");
            highprice = bb.getExtras().getString("highprice");
            //Toast.makeText(getApplicationContext(),""+ Arrays.toString(CarTypeids) +"\n"+lowprice+"\n"+highprice,Toast.LENGTH_SHORT).show();

            FilerJarray = new JSONArray();
            try {
                for(int i = 0; i< MainJarray.length(); i++) {
                    JSONObject json = MainJarray.getJSONObject(i);
                    assert CarTypeids != null;
                    if(CarTypeids.length==0 || Arrays.asList(CarTypeids).contains(json.getString("car_type_id"))){
                        int TotalPrice  = Integer.parseInt(json.getString("car_price_day"));
                        if(TotalPrice>=Integer.parseInt(lowprice) && TotalPrice<=Integer.parseInt(highprice)){
                            FilerJarray.put(json);
                        }
                    }
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(Car_Search_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
            }
            if(FilerJarray.length()>0){
                SortJarray = FilerJarray;
                sorter=2;
                parsedata();
            }else{
                if(car_rec_adapter!= null){car_rec_adapter.notifyDataSetChanged();}
                if(LoadDlg.isShowing()){LoadDlg.dismiss();}
                Utils.setSingleBtnAlert(Car_Search_Act.this,"No Results Found\nPlease Modify Your Search","Ok",true);
            }
        }
    }

    class Car_Search_Recycle_Adapter extends RecyclerView.Adapter<Car_Search_Recycle_Adapter.ViewHolder>  {

        private Context context;
        private List<DataAdapter> listadapter;

        Car_Search_Recycle_Adapter(List<DataAdapter> adapterList){
            super();
            this.listadapter = adapterList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView CarnameTv,CarTypeTv,CarPriceTv;
            Button CarBookbtn;
            ImageView CarImageIv ;
            ViewHolder(View itemView) {
                super(itemView);

                CarImageIv = itemView.findViewById(R.id.carIV);
                CarnameTv = itemView.findViewById(R.id.carnameTv);
                CarTypeTv = itemView.findViewById(R.id.cartypeTv);
                CarPriceTv = itemView.findViewById(R.id.carpriceTv);
                CarBookbtn = itemView.findViewById(R.id.carbookbtn);
                context = itemView.getContext();

                CarBookbtn.setOnClickListener(v -> {

                    Intent intent = new Intent(context, Car_Review_Act.class);
                    intent.putExtra("CarName", listadapter.get(getAdapterPosition()).getName());
                    intent.putExtra("CarDetid", listadapter.get(getAdapterPosition()).getID());
                    intent.putExtra("CarType", listadapter.get(getAdapterPosition()).getType());
                    intent.putExtra("CarTypeId", listadapter.get(getAdapterPosition()).getTypeId());
                    intent.putExtra("Suppid", listadapter.get(getAdapterPosition()).getSuppid());
                    intent.putExtra("Carfeature", listadapter.get(getAdapterPosition()).getFeature());
                    intent.putExtra("CarSmallDes", listadapter.get(getAdapterPosition()).getDescription());
                    intent.putExtra("CarLongDes", listadapter.get(getAdapterPosition()).getText());
                    intent.putExtra("CarImage", listadapter.get(getAdapterPosition()).getImage());
                    intent.putExtra("CancelPolicy", listadapter.get(getAdapterPosition()).getPolicy());
                    intent.putExtra("CarPrice", listadapter.get(getAdapterPosition()).getPrice());
                    intent.putExtra("CarTax", listadapter.get(getAdapterPosition()).getTax());
                    intent.putExtra("CarCompany", listadapter.get(getAdapterPosition()).getCompany());
                    intent.putExtra("CarBundle", listadapter.get(getAdapterPosition()).getBundle());

                    context.startActivity(intent);
                });
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.car_slist_recycler, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder vh, int position) {

            DataAdapter datadapter =  listadapter.get(position);
            Glide.with(context).load(datadapter.getImage()).into(vh.CarImageIv);

            vh.CarnameTv.setText(datadapter.getName());
            vh.CarTypeTv.setText("Car Type : "+datadapter.getType());
            vh.CarPriceTv.setText(" ₹ "+ datadapter.getPrice()+"");

            if(!Objects.equals(datadapter.getStatus(), "1")){
                vh.CarBookbtn.setText("Sold Out");
                vh.CarBookbtn.setBackgroundResource(R.color.soldco);
                vh.CarBookbtn.setClickable(false);

            }
        }

        @Override
        public int getItemCount() { return listadapter.size(); }

    }

}