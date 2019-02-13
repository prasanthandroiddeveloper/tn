package com.tripnetra.tripnetra.hotels.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Amenities_Frag extends Fragment {

    String Hid,Hname;
    RecyclerView recyclerView;
    G_Class gcv;

    public Amenities_Frag() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotel_amenities, container, false);

        gcv = (G_Class) Objects.requireNonNull(getActivity()).getApplicationContext();
        Hid = gcv.getHotelId();
        Hname = gcv.getHotelName()+"Amenities";

        recyclerView = view.findViewById(R.id.amenrecyclr);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getamenities();

        return view;
    }

    private void getamenities() {

        Map<String, String> params = new HashMap<>();
        params.put("hotelid", Hid);

        new VolleyRequester(getActivity()).ParamsRequest(1, gcv.getBaseurl()+Config.TRIP_URL+"hotelamentites.php", null, params, false, response -> {
            try {
                List<DataAdapter> list = new ArrayList<>();

                JSONArray jsonArray=new JSONArray(response);
                for(int i = 0; i<jsonArray.length(); i++) {

                    DataAdapter adaptr = new DataAdapter();
                    adaptr.setText(jsonArray.getJSONObject(i).getString("amenities_name"));

                    list.add(adaptr);
                    recyclerView.setAdapter(new Amenities_Recycle_Adapter(list));
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(getActivity(),getResources().getString(R.string.something_wrong),"Ok",false);
            }
        });
    }

    public class Amenities_Recycle_Adapter extends RecyclerView.Adapter<Amenities_Recycle_Adapter.ViewHolder>{

        private List<DataAdapter> listadap;

        Amenities_Recycle_Adapter(List<DataAdapter> list){ super();this.listadap = list; }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView amens;
            ViewHolder(View itemView) {
                super(itemView);
                amens = itemView.findViewById(R.id.txtv);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_amens_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) { holder.amens.setText(listadap.get(position).getText()); }

        @Override
        public int getItemCount() {return listadap.size();}

    }

}