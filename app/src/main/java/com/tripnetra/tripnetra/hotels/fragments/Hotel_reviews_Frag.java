package com.tripnetra.tripnetra.hotels.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Hotel_reviews_Frag extends Fragment {

    HorizontalBarChart barChart;
    String Hid;
    float S5=0,S4=0,S3=0,S2=0,S1=0;
    RecyclerView recyclerView;
    int[] ratingarray;
    TextView startv,ratrevtv;
    Activity activity;
    G_Class g_class;

    public Hotel_reviews_Frag() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotel_reviews, container, false);

        activity = getActivity();
        assert activity != null;
        g_class = (G_Class) activity.getApplicationContext();
        barChart = view.findViewById(R.id.chart);
        startv = view.findViewById(R.id.startv);
        ratrevtv = view.findViewById(R.id.ratrevtv);
        recyclerView = view.findViewById(R.id.reviewrecyclr);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        Hid = g_class.getHotelId();

        view.findViewById(R.id.writrev).setOnClickListener(v -> {
            Write_Review_Dialog wrd = new Write_Review_Dialog();
            wrd.setTargetFragment(this, 125);
            assert getFragmentManager() != null;
            wrd.show(getFragmentManager()," ");
        });

        getreviews();

        return  view;
    }

    private void rating() {
        if(ratingarray!= null && ratingarray.length !=0) {
            for (int aRatingarray : ratingarray) {
                if (aRatingarray == 1) {
                    S1++;
                } else if (aRatingarray == 2) {
                    S2++;
                } else if (aRatingarray == 3) {
                    S3++;
                } else if (aRatingarray == 4) {
                    S4++;
                } else if (aRatingarray == 5) {
                    S5++;
                }
            }
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(S1, 0));
        entries.add(new BarEntry(S2, 1));
        entries.add(new BarEntry(S3, 2));
        entries.add(new BarEntry(S4, 3));
        entries.add(new BarEntry(S5, 4));

        BarDataSet dataset = new BarDataSet(entries,"");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setBarSpacePercent(60f);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("1 ★");
        labels.add("2 ★");
        labels.add("3 ★");
        labels.add("4 ★");
        labels.add("5 ★");

        BarData data = new BarData(labels, dataset);
        data.setValueTextSize(9f);
        barChart.invalidate();
        barChart.setData(data);
        barChart.getXAxis().setTextSize(9f);
        barChart.setTouchEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.animateY(1000);
        barChart.setDescription("");
    }

    @SuppressLint("SetTextI18n")
    private void getreviews(){

        Map<String, String> params = new HashMap<>();
        params.put("hotelid", Hid);

        new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL+"hotelreviews.php",
                null, params, false, response -> {
            if(!Objects.equals(response, "No Result")) {
                try {
                    float frate =0,rating=0;
                    List<DataAdapter> list = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    ratingarray = new int[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DataAdapter adaptr = new DataAdapter();
                        JSONObject json = jsonArray.getJSONObject(i);

                        adaptr.setName(json.getString("user_name"));
                        adaptr.setText(json.getString("review_description"));
                        adaptr.setDate("Posted on " + json.getString("posted_date"));

                        rating = (Integer.parseInt(json.getString("review_rating")) +
                                Integer.parseInt(json.getString("quality_rating")) +
                                Integer.parseInt(json.getString("facility_rating")) +
                                Integer.parseInt(json.getString("condition_rating"))) / 4;

                        adaptr.setType(String.format(Locale.US, "%.1f", rating));
                        frate = frate + rating;
                        ratingarray[i] = Math.round(rating);

                        list.add(adaptr);
                        recyclerView.setAdapter(new Hotel_Reviews_Recycle_Adapter(list));
                        rating();
                    }
                    startv.setText(String.format(Locale.US, "%.1f", rating));
                    ratrevtv.setText(jsonArray.length()+" Ratings");
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", false);
                }
            }
        });
    }

    class Hotel_Reviews_Recycle_Adapter extends RecyclerView.Adapter<Hotel_Reviews_Recycle_Adapter.ViewHolder> {

        private List<DataAdapter> listadap;

        Hotel_Reviews_Recycle_Adapter(List<DataAdapter> list) {
            super();
            this.listadap = list;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView guestname,starcount,revdesc,posted;
            ViewHolder(View iv) {
                super(iv);
                guestname = iv.findViewById(R.id.nametv);
                starcount = iv.findViewById(R.id.starcounttv);
                revdesc = iv.findViewById(R.id.descrttv);
                posted = iv.findViewById(R.id.posttv);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_review_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
            DataAdapter dobj = listadap.get(pos);
            holder.guestname.setText(dobj.getName());
            holder.starcount.setText(dobj.getType());
            holder.revdesc.setText(dobj.getText());
            holder.posted.setText(dobj.getDate());
        }

        @Override
        public int getItemCount() {
            return listadap.size();
        }
    }
}