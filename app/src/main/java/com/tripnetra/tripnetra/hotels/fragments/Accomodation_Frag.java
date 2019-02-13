package com.tripnetra.tripnetra.hotels.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.DataAdapter;
import com.tripnetra.tripnetra.hotels.Hotel_Room_Review_Act;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Accomodation_Frag extends Fragment {

    String Hid,Rcount,Cin,Cout,yesterday;
    RecyclerView recyclerview;

    public Accomodation_Frag() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotel_accomodation, container, false);

        G_Class gcv = (G_Class) Objects.requireNonNull(getActivity()).getApplicationContext();
        Hid = gcv.getHotelId();
        Rcount = gcv.getRoomCount();
        Cin = gcv.getCheckin();
        Cout = gcv.getCheckout();

        recyclerview = view.findViewById(R.id.accomrecyc);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(Cin!=null) {

            Date date = Utils.StrtoDate(0,Cin);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -1);
            yesterday = Utils.DatetoStr(calendar.getTime(),5);

        }

        getrooms();

        return view;
    }

    private void getrooms() {

        Map<String, String> params = new HashMap<>();
        params.put("hotel_id", Hid);
        params.put("from_date", Cin);
        params.put("to_date", Cout);
        params.put("room_count", Rcount);

        new VolleyRequester(getActivity()).ParamsRequest(1, ((G_Class) getActivity().getApplicationContext()).getBaseurl()+Config.TRIP_URL+"hoteldetails.php", null, params, true, response -> {
            try {
                List<DataAdapter> list = new ArrayList<>();
                int DiscPrice,TotalPrice;

                JSONArray jsonArray = new JSONArray(response);
                for(int i = 0; i<jsonArray.length(); i++) {

                    DataAdapter adaptr = new DataAdapter();
                    JSONObject json = jsonArray.getJSONObject(i);
                    adaptr.setID(json.getString("hotel_room_type_id"));
                    adaptr.setName(json.getString("room_type_name"));
                    adaptr.setAdult(json.getString("adult"));
                    adaptr.setChild(json.getString("child"));
                    adaptr.setTypeId(json.getString("hotel_type_id"));
                    adaptr.setText(json.getString("commission"));
                    adaptr.setTax(json.getString("room_tax"));
                    adaptr.setFeature(yesterday);
                    adaptr.setDescription(json.getString("room_amenities"));
                    adaptr.setRId(json.getString("hotel_room_rate_info_id"));

                    double sgl_price = Integer.parseInt(json.getString("sgl_price")),
                            commission = Integer.parseInt(json.getString("commission"));
                    double tripprice = sgl_price - ((sgl_price*commission)/100);

                    if(Objects.equals(json.getString("markup_value"), "")||Objects.equals(json.getString("markup_value"), "0")){
                        TotalPrice = (int) Math.ceil(sgl_price+Integer.parseInt(json.getString("markup_fixed")));
                    }else{
                        tripprice = tripprice + ((tripprice*Integer.parseInt(json.getString("markup_value")))/100);
                        TotalPrice = (int) Math.ceil(tripprice);
                    }

                    adaptr.setSuppid(json.getString("sgl_price"));
                    adaptr.setPrice(String.valueOf(TotalPrice));
                    if(Objects.equals(json.getString("coupan_discount"), "null") ||json.getString("coupan_discount")==null){
                        DiscPrice=0;
                        adaptr.setPolicy("0");
                    }else{
                        DiscPrice = TotalPrice*Integer.parseInt(json.getString("coupan_discount"))/100;
                        adaptr.setPolicy(json.getString("coupan_discount"));
                    }
                    adaptr.setAPrice(String.valueOf(DiscPrice+TotalPrice));
                    adaptr.setDprice(String.valueOf(DiscPrice));

                    if((Integer.parseInt(json.getString("no_of_room_available")) < Integer.parseInt(Rcount)) || Objects.equals(json.getString("hotel_status"), "INACTIVE") || Objects.equals(json.getString("room_status"), "INACTIVE")) {
                        adaptr.setAvail("0");
                    }else {
                        adaptr.setAvail(json.getString("no_of_room_available"));
                    }

                    G_Class gcv = ((G_Class) Objects.requireNonNull(getActivity()).getApplicationContext());
                    if(Objects.equals(json.getString("room_images"), "") || Objects.equals(json.getString("room_images"), "null")) {
                        adaptr.setImage(gcv.getImageurl()+"hotel_images/" + json.getString("thub_image"));
                    }else{
                        adaptr.setImage(gcv.getImageurl()+"hotel_images/" + json.getString("room_images"));
                    }

                    int acount = Integer.parseInt(gcv.getAdultCount())/Integer.parseInt(Rcount);
                    int maxcount = Integer.parseInt(json.getString("adult"));

                    if(Objects.equals(json.getString("extra_bed"), "Available")) {
                        maxcount += Integer.parseInt(json.getString("extra_bed_count"));
                    }
                    adaptr.setSMax((acount>maxcount) ? "change" : "dontchange");

                    list.add(adaptr);
                    recyclerview.setAdapter(new Accomdatn_Recycle_Adapter(list));
                }
            } catch (JSONException|NumberFormatException e) {
                e.printStackTrace();
                Utils.setSingleBtnAlert(getActivity(),getResources().getString(R.string.something_wrong),"Ok",true);
            }
        });
    }

    public class Accomdatn_Recycle_Adapter extends RecyclerView.Adapter<Accomdatn_Recycle_Adapter.ViewHolder> {
        private Context context;
        private List<DataAdapter> listadapter;

        Accomdatn_Recycle_Adapter(List<DataAdapter> list) {
            super();
            this.listadapter = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_accom_recycler, parent, false));
        }

        @SuppressLint({"SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {
            DataAdapter dobj = listadapter.get(pos);
            vh.rname.setText(dobj.getName());
            vh.rocup.setText("Upto "+dobj.getAdult()+" Guests ("+dobj.getAdult()+" A & "+dobj.getChild()+" C )");

            vh.setIsRecyclable(false);
            String array_items[] = dobj.getDescription().split(",");
            for (String array_item : array_items) {
                ImageView imgView = new ImageView(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50,50);
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

            if(Objects.equals(dobj.getAPrice(), dobj.getPrice())) {
                vh.aprice.setVisibility(View.GONE);
                vh.dprice.setVisibility(View.GONE);
            }else{
                vh.aprice.setText("₹ " + dobj.getAPrice());
                vh.dprice.setText(" Save ₹ " + dobj.getDprice());
            }
            vh.tprice.setText("₹ "+dobj.getPrice());
            if(Objects.equals(dobj.getTypeId(), "4")){
                vh.canceltv.setText(R.string.nocancel);
            }else{
                vh.canceltv.setText("Free cancellation upto \n"+dobj.getFeature()+" 12:00PM ");
            }

            Glide.with(context).load(dobj.getImage()).into(vh.hotelimage);

            if(Objects.equals(dobj.getTypeId(),"4")){
                vh.bookbutton.setText("Donate");
            }

            vh.bookbutton.setBackgroundResource(R.drawable.green_btn);

            if(Integer.parseInt(dobj.getAvail()) == 0){
                vh.hurry.setText("No Rooms left");
                vh.hurry.setTextColor(Color.BLACK);
                vh.bookbutton.setText(R.string.soldout);
                vh.bookbutton.setBackgroundResource(R.drawable.btn);
                vh.bookbutton.setClickable(false);
            }else if(Integer.parseInt(dobj.getAvail()) <= 5){
                vh.hurry.setText("Hurry! "+dobj.getAvail()+" Rooms left");
            }else{
                vh.hurry.setText(dobj.getAvail()+" Rooms left");
                vh.hurry.setTextColor(Color.BLACK);
            }

            if(Objects.equals(dobj.getSMax(), "change")){
                vh.bookbutton.setClickable(false);
                vh.bookbutton.setBackgroundColor(Color.parseColor("#919191"));
            }
        }

        @Override
        public int getItemCount() { return listadapter.size(); }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            TextView rname,rocup,aprice,dprice,tprice,hurry,canceltv;
            Button bookbutton;
            LinearLayout ImageLLout;
            ImageView hotelimage;

            ViewHolder(View iv) {
                super(iv);
                context = iv.getContext();
                rname = iv.findViewById(R.id.roomtv);
                rocup = iv.findViewById(R.id.maxoc);
                aprice = iv.findViewById(R.id.ractprice);
                dprice = iv.findViewById(R.id.rdisctv);
                tprice = iv.findViewById(R.id.rfinalptv);
                canceltv = iv.findViewById(R.id.canceltv);
                hurry = iv.findViewById(R.id.hurrytv);
                bookbutton = iv.findViewById(R.id.bookbtn);
                hotelimage = iv.findViewById(R.id.hoteliv);
                ImageLLout = iv.findViewById(R.id.imagellout);
                bookbutton.setOnClickListener(this);
                aprice.setPaintFlags(aprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            @Override
            public void onClick(View v) {
                if(v == bookbutton){
                    DataAdapter dadap = listadapter.get(getAdapterPosition());
                    Bundle bundle = new Bundle();

                    bundle.putString("Rid", dadap.getID());
                    bundle.putString("Rname", dadap.getName());
                    bundle.putString("Rimage", dadap.getImage());
                    bundle.putString("Rdiscper", dadap.getPolicy());
                    bundle.putString("Rtotprice", dadap.getPrice());
                    bundle.putString("Rrateid", dadap.getRId());
                    bundle.putString("Roomtax", dadap.getTax());
                    bundle.putString("SuppSgl", dadap.getSuppid());
                    bundle.putString("HtypeId", dadap.getTypeId());
                    bundle.putString("commission", dadap.getText());
                    bundle.putString("maxpersons", dadap.getAdult());

                    Intent intent =  new Intent(context, Hotel_Room_Review_Act.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        }

    }

}