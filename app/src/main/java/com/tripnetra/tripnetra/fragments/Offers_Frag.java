package com.tripnetra.tripnetra.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Offers_Frag extends Fragment {

    public Offers_Frag() {}

    Activity activity;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offer, container, false);

        activity = getActivity();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        new VolleyRequester(activity).ParamsRequest(1, ((G_Class) activity.getApplicationContext()).getBaseurl()+Config.TRIP_URL+"hotel_offers.php", null, null, true, response -> {
            if (response.equals("No Result")) {
                Utils.setSingleBtnAlert(activity, "No Offers Found", "Ok", false);
            } else {
                try {
                    List<DataAdapter> list = new ArrayList<>();
                    JSONArray jarray = new JSONArray(response);

                    for (int i = 0; i < jarray.length(); i++) {

                        JSONObject jobj = jarray.getJSONObject(i);

                        DataAdapter adaptr = new DataAdapter();

                        adaptr.setText(jobj.getString("title"));
                        adaptr.setDescription(jobj.getString("validity_Date"));
                        adaptr.setImage(jobj.getString("offer_image"));
                        adaptr.setID(jobj.getString("complimentary_offers_id"));

                        list.add(adaptr);
                    }
                    recyclerView.setAdapter(new Offers_Recycle_Adapter(list));

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", false);
                }
            }
        });

        return view;

    }

    private class Offers_Recycle_Adapter extends RecyclerView.Adapter<Offers_Recycle_Adapter.ViewHolder> {

        private List<DataAdapter> listadap;
        Context context;

        Offers_Recycle_Adapter( List<DataAdapter> list) { this.listadap = list; }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView DescTv,ValidTv;
            ImageView offerIV;

            ViewHolder(View iv) {
                super(iv);

                DescTv = iv.findViewById(R.id.offertv);
                ValidTv = iv.findViewById(R.id.offervalidtv);
                offerIV = iv.findViewById(R.id.offerIv);
                context = iv.getContext();

                iv.setOnClickListener(v -> {
                    Intent intent = new Intent(context, Offer_Desc_Act.class);
                    intent.putExtra("offerid", listadap.get(getAdapterPosition()).getID());
                    context.startActivity(intent);
                });
            }
        }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_offer_recycler, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DataAdapter dobj = listadap.get(position);

            holder.DescTv.setText(dobj.getText());

            String sdate = Utils.ChangeDateFormat(dobj.getDescription(),5);
            holder.ValidTv.setText((sdate == null) ? "Valid Till : "+dobj.getDescription() : "Offer Validity : Valid Till "+ sdate);

            Glide.with(context).load(((G_Class) activity.getApplicationContext()).getImageurl()+Config.HOTEL_IMAGE+dobj.getImage()).into(holder.offerIV);
        }

        @Override
        public int getItemCount() {return listadap.size();}

    }

}