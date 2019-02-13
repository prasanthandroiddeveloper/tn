package com.tripnetra.tripnetra.buses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tripnetra.R;

import java.util.Objects;

public class Darshan_room_select extends AppCompatActivity implements View.OnClickListener{

    LinearLayout MainLayt;
    TextView CountTv,AddTv;
    int Total_rooms = 1,R1_Count=1;
    public Darshan_room_select() {}
    int[] per_count ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_darshan_room_select);

        MainLayt = findViewById(R.id.mainlay);
        CountTv = findViewById(R.id.countTv);
        AddTv = findViewById(R.id.addTv);

        CountTv.setText(String.valueOf(R1_Count));
        per_count = new int[3];
        per_count[0] = 1;per_count[1] = 0;per_count[2] = 0;

        Objects.requireNonNull(this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AddTv.setOnClickListener(this);
        findViewById(R.id.plusTv).setOnClickListener(this);
        findViewById(R.id.minusTv).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addTv:

                MainLayt.requestFocus();
                Total_rooms++;
                if(Total_rooms==3){ AddTv.setVisibility(View.GONE); }

                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.darshan_room_header, null);

                TextView r_NameTv = rowView.findViewById(R.id.nametv);
                TextView r_plusTv = rowView.findViewById(R.id.plustv);
                TextView r_minusTv = rowView.findViewById(R.id.minustv);
                final TextView r_countTv = rowView.findViewById(R.id.counttv);
                TextView r_closeTv = rowView.findViewById(R.id.closetv);
                final int flag = Total_rooms-1;

                r_NameTv.setText("Room "+Total_rooms);

                r_closeTv.setOnClickListener(vw -> {

                    if(Total_rooms==3){
                        AddTv.setVisibility(View.VISIBLE);
                    }
                    Total_rooms--;
                    per_count[flag] = 0;
                    MainLayt.removeView(rowView);
                });
                r_countTv.setText("1");
                per_count[flag] = 1;

                r_plusTv.setOnClickListener(v1 -> {
                    if(per_count[flag]<4){
                        int ii = per_count[flag];
                        ii++;
                        per_count[flag] = ii;
                        r_countTv.setText(String.valueOf(ii));
                    }
                });

                r_minusTv.setOnClickListener(v1 -> {
                    if(per_count[flag]>1){
                        int ii = per_count[flag];
                        ii--;
                        per_count[flag] = ii;
                        r_countTv.setText(String.valueOf(ii));
                    }
                });

                MainLayt.addView(rowView, MainLayt.getChildCount()-1);


                break;

            case R.id.plusTv:
                if(per_count[0]<4){
                    int ii = per_count[0];
                    ii++;
                    per_count[0] = ii;
                    CountTv.setText(String.valueOf(ii));
                }
                break;

            case R.id.minusTv:
                if(per_count[0]>1){
                    int ii = per_count[0];
                    ii--;
                    per_count[0] = ii;
                    CountTv.setText(String.valueOf(ii));
                }
                break;
            case R.id.submit:
                verifydata();
                break;
        }
    }

    private void verifydata() {

        int ii = 0;
        for (int Pcount : per_count) { ii += Pcount; }

        if(ii>6){
            Toast.makeText(this,"Only 6 persons Are Allowed to Book",Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent();
            intent.putExtra("persons",String.valueOf(ii));
            intent.putExtra("rooms",String.valueOf(Total_rooms));
            intent.putExtra("rcount1",String.valueOf(per_count[0]));
            intent.putExtra("rcount2",String.valueOf(per_count[1]));
            intent.putExtra("rcount3",String.valueOf(per_count[2]));

            setResult(326, intent);
            finish();
        }

    }

}
