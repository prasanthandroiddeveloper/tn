package com.tripnetra.tripnetra.darshan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tripnetra.tripnetra.R;

import java.util.ArrayList;

public class Darshan_payment_act extends AppCompatActivity {

    Bundle bundle;
    LinearLayout MainLayt, MainLayt1, addonId;
    String[] NamesList, AgesList, GenderList;

    ArrayList<String> namess, price, duration, ids;
    TextView names;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_darshan_payment);

        names = (findViewById(R.id.passTv));
        MainLayt = (findViewById(R.id.recycle));
        MainLayt1 = (findViewById(R.id.addon));
        addonId = (findViewById(R.id.addonID));

        assert getIntent().getExtras() != null;
        bundle = getIntent().getExtras();
        StringBuilder string = new StringBuilder("Bundle{");

        for (String key : bundle.keySet()) {
            string.append(" ").append(key).append(" => ").append(bundle.get(key)).append(";");
        }
        string.append(" }");

        NamesList = bundle.getStringArray("nameslist");
        AgesList = bundle.getStringArray("ageslist");
        GenderList = bundle.getStringArray("genderlist");

        price = bundle.getStringArrayList("price");
        duration = bundle.getStringArrayList("duration");
        ids = bundle.getStringArrayList("ids");
        namess = bundle.getStringArrayList("name");
        /* addonnameslist =  bundle.getStringArray("addonnameslist");*/

        for (int i = 0; i < NamesList.length; i++) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.view_recycler, null);
            ((TextView) rowView.findViewById(R.id.passTv)).setText(NamesList[i]);
            ((TextView) rowView.findViewById(R.id.ageTv)).setText(AgesList[i] + " - " + GenderList[i].toUpperCase().charAt(0));
            MainLayt.addView(rowView, MainLayt.getChildCount());
        }

        ((TextView) findViewById(R.id.htlnameTV)).setText(bundle.getString("hotelname"));
        ((TextView) findViewById(R.id.roomnightTv)).setText(bundle.getInt("roomcount") + " Room");
        ((TextView) findViewById(R.id.FromTV)).setText(bundle.getString("fromdate"));
        ((TextView) findViewById(R.id.toTv)).setText(bundle.getString("todate"));
        ((TextView) findViewById(R.id.PassmblTv)).setText(bundle.getString("passmobile"));
        ((TextView) findViewById(R.id.PassemlTv)).setText(bundle.getString("passemail"));
        ((TextView) findViewById(R.id.darshandtTv)).setText(bundle.getString("darshandt"));

        if (ids != null && ids.size() > 0) {

            for (int i = 0; i < ids.size(); i++) {


                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.selectedaddondetails, null);
                ((TextView) rowView.findViewById(R.id.templenameTV)).setText(namess.get(i));
                ((TextView) rowView.findViewById(R.id.durationTv)).setText(duration.get(i));
                ((TextView) rowView.findViewById(R.id.priceTv)).setText(price.get(i));

                MainLayt1.addView(rowView, MainLayt1.getChildCount());
            }
        } else {
            MainLayt1.setVisibility(View.GONE);
            addonId.setVisibility(View.GONE);
        }
    }

}
