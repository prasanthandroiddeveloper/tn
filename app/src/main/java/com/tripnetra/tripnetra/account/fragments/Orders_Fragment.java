package com.tripnetra.tripnetra.account.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.account.vouchers.Car_Voucher;
import com.tripnetra.tripnetra.account.vouchers.Hotel_Voucher;
import com.tripnetra.tripnetra.account.vouchers.Tour_Voucher;
import com.tripnetra.tripnetra.adapters.DataAdapter;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.SharedPrefs;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Orders_Fragment extends Fragment {

    RecyclerView recyclerView;
    String Status,User_id,BookType="all";
    FloatingActionButton Mainfab;
    Boolean isFABOpen = false;
    LinearLayout FabLay1,FabLay2,FabLay3,FabLay4;
    Activity activity;
    LoadingDialog loadDlg;

    public Orders_Fragment() {}

    public static Orders_Fragment newInstance(String status) {
        Orders_Fragment order_frag = new Orders_Fragment();

        Bundle args = new Bundle();
        args.putString("status",status);
        order_frag.setArguments(args);

        return order_frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View vw = inflater.inflate(R.layout.fragment_orders, container, false);

        assert getArguments() != null;
        Status = getArguments().getString("status");
        activity = getActivity();

        User_id = new SharedPrefs(Objects.requireNonNull(activity)).getUserId();

        recyclerView = vw.findViewById(R.id.recyclerView);
        Mainfab = vw.findViewById(R.id.fab);

        FabLay1 = vw.findViewById(R.id.fabLayout1);
        FabLay2 = vw.findViewById(R.id.fabLayout2);
        FabLay3 = vw.findViewById(R.id.fabLayout3);
        FabLay4 = vw.findViewById(R.id.fabLayout4);

        loadDlg = new LoadingDialog(activity);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        Close_Fab_menu();
        Mainfab.setOnClickListener(view -> {
            if(!isFABOpen){
                Open_Fab_menu();
            }else{
                Close_Fab_menu();
            }
        });

        FabLay1.setOnClickListener(view -> { BookType="hotel";getdata(); });

        FabLay2.setOnClickListener(view -> { BookType="car";getdata(); });

        FabLay3.setOnClickListener(view -> { BookType="tour";getdata(); });

        FabLay4.setOnClickListener(view -> { BookType="all";getdata(); });

        getdata();
        return vw;
    }

    private void getdata() {

        loadDlg.show();

        Map<String, String> params = new HashMap<>();
        params.put("userid", User_id);
        params.put("status", Status);
        params.put("type", BookType);

        new VolleyRequester(activity).ParamsRequest(1,
                ((G_Class) activity.getApplicationContext()).getBaseurl()+Config.TRIP_URL+"account/history.php", loadDlg,
                params, true, response -> {

            if(Objects.equals(response, "No Result")){
                Utils.setSingleBtnAlert(activity, "No Results Found", "Ok", false);
            }else {
                try {
                    JSONObject jobj = new JSONObject(response);
                    List<DataAdapter> list = new ArrayList<>();

                    if(jobj.getJSONArray("hotel_book_data")!=null){

                        JSONArray jsonArray = jobj.getJSONArray("hotel_book_data");
                        for(int i = 0; i<jsonArray.length(); i++) {
                            DataAdapter adaptr = new DataAdapter();
                            JSONObject json = jsonArray.getJSONObject(i);

                            adaptr.setType("hotel");
                            adaptr.setName(json.getString("hotel_name"));
                            adaptr.setID(json.getString("pnr_no"));
                            adaptr.setDate(json.getString("check_in_date"));
                            adaptr.setText(json.getString("contact_fname")+" "+json.getString("contact_lname"));
                            adaptr.setDescription(json.getString("booking_room_type"));
                            adaptr.setStatus((Objects.equals(Status, "Cancelled")) ? "Cancelled" : "Confirmed");

                            list.add(adaptr);
                            recyclerView.setAdapter(new Order_Recycler_Adapter(list));
                        }
                    }

                    if(jobj.getJSONArray("car_book_data")!=null){

                        JSONArray jsonArray = jobj.getJSONArray("car_book_data");
                        for(int i = 0; i<jsonArray.length(); i++) {
                            DataAdapter adaptr = new DataAdapter();
                            JSONObject json = jsonArray.getJSONObject(i);

                            adaptr.setType("car");
                            adaptr.setName(json.getString("from_city")+" "+json.getString("to_city"));
                            adaptr.setID(json.getString("pnr_no"));
                            adaptr.setDate(json.getString("pickup_date"));
                            adaptr.setText(json.getString("first_name")+" "+json.getString("last_name"));
                            adaptr.setDescription(json.getString("car_company_name")+" "+json.getString("car_name"));
                            adaptr.setStatus((Objects.equals(Status, "Cancelled")) ? "Cancelled" : "Confirmed");

                            list.add(adaptr);
                            recyclerView.setAdapter(new Order_Recycler_Adapter(list));
                        }
                    }

                    if(jobj.getJSONArray("tour_book_data")!=null){

                        JSONArray jsonArray = jobj.getJSONArray("tour_book_data");
                        for(int i = 0; i<jsonArray.length(); i++) {
                            DataAdapter adaptr = new DataAdapter();
                            JSONObject json = jsonArray.getJSONObject(i);

                            adaptr.setType("tour");
                            adaptr.setName(json.getString("sightseen_name"));
                            adaptr.setID(json.getString("pnr_no"));
                            adaptr.setDate(json.getString("travel_date"));
                            adaptr.setText(json.getString("firstname")+" "+json.getString("lastname"));
                            adaptr.setDescription(json.getString("search_city"));
                            adaptr.setStatus((Objects.equals(Status, "Cancelled")) ? "Cancelled" : "Confirmed");

                            list.add(adaptr);
                            recyclerView.setAdapter(new Order_Recycler_Adapter(list));
                        }
                    }

                } catch (JSONException e) {
                    Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", true);
                    //e.printStackTrace();
                }
            }
            if(loadDlg.isShowing()){loadDlg.dismiss();}

        });

        if(isFABOpen){Close_Fab_menu();}
    }

    private void Open_Fab_menu(){
        isFABOpen=true;
        FabLay1.setVisibility(View.VISIBLE);FabLay2.setVisibility(View.VISIBLE);FabLay3.setVisibility(View.VISIBLE);FabLay4.setVisibility(View.VISIBLE);
        FabLay1.animate().translationY(-160);FabLay2.animate().translationY(-320);FabLay3.animate().translationY(-480);FabLay4.animate().translationY(-640);
    }

    private void Close_Fab_menu(){
        isFABOpen=false;
        FabLay1.animate().translationY(0);FabLay2.animate().translationY(0);FabLay3.animate().translationY(0);FabLay4.animate().translationY(0);
        FabLay1.setVisibility(View.GONE);FabLay2.setVisibility(View.GONE);FabLay3.setVisibility(View.GONE);FabLay4.setVisibility(View.GONE);
    }

    class Order_Recycler_Adapter extends RecyclerView.Adapter<Order_Recycler_Adapter.MyViewHolder>{

        private Context context;
        private List<DataAdapter> listadapter;

        Order_Recycler_Adapter(List<DataAdapter> list) {this.listadapter = list;}

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_orders,parent,false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder vh, final int pos) {
            DataAdapter dobj = listadapter.get(pos);

            vh.NameTv.setText(dobj.getName());
            vh.GnameTv.setText(dobj.getText());
            vh.DetailsTv.setText(dobj.getDescription());
            vh.DateTv.setText(dobj.getDate());

            if(Objects.equals(dobj.getStatus(), "Cancelled")){
                vh.StatTv.setText("Cancelled");
                vh.StatTv.setTextColor(Color.parseColor("#C60404"));
            }else{
                vh.StatTv.setText("Confirmed");
            }
        }

        @Override
        public int getItemCount() {return listadapter.size();}

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView NameTv,DetailsTv,DateTv,StatTv,GnameTv;

            MyViewHolder(View itemView) {
                super(itemView);

                NameTv = itemView.findViewById(R.id.nametv);
                GnameTv = itemView.findViewById(R.id.gnametv);
                DetailsTv = itemView.findViewById(R.id.detailstv);
                DateTv = itemView.findViewById(R.id.datetv);
                StatTv = itemView.findViewById(R.id.statustv);
                context = itemView.getContext();

                itemView.setOnClickListener(v -> {
                    Intent intent = null;
                    switch (listadapter.get(getAdapterPosition()).getType()) {
                        case "hotel": {
                            intent = new Intent(context, Hotel_Voucher.class);
                            break;
                        }
                        case "car": {
                            intent = new Intent(context, Car_Voucher.class);
                            break;
                        }
                        case "tour": {
                            intent = new Intent(context, Tour_Voucher.class);
                            break;
                        }
                    }
                    if (intent != null) {
                        intent.putExtra("pnrno", listadapter.get(getAdapterPosition()).getID());
                        context.startActivity(intent);
                    }
                });
            }

        }

    }

}
