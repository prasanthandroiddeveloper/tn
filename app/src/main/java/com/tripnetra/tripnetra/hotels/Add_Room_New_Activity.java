package com.tripnetra.tripnetra.hotels;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Add_Room_New_Activity extends AppCompatActivity{

    LinearLayout MainLayt;
    TextView AddTv,TA_countTv,TC_countTv,RoomCounTv;
    int Total_rooms = 0,MaxAcount = 6, MaxCcount = 3,EAcount = 3,ECcount = 1,MaxRooms = 3;
    int[] Adult_List, Child_list;
    List<View> Viewlist;
    List<TextView> Atvlist,Ctvlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addroom_new);

        TA_countTv = findViewById(R.id.tA_Tv);
        TC_countTv = findViewById(R.id.tC_Tv);
        RoomCounTv = findViewById(R.id.RoomCounTv);
        MainLayt = findViewById(R.id.mainlay);
        AddTv = findViewById(R.id.addTv);

        Viewlist = new ArrayList<>();Atvlist = new ArrayList<>();Ctvlist = new ArrayList<>();
        for(int i=0;i<MaxRooms;i++){Viewlist.add(null);Atvlist.add(null);Ctvlist.add(null); }

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){

            Adult_List = bundle.getIntArray("adult_list");
            Child_list = bundle.getIntArray("child_list");
            int total = bundle.getInt("roomcount");

            setA_count();setC_count();
            for(int i=0;i<total;i++){ Total_rooms++; addView(); }

        }else {
            Adult_List = new int[MaxRooms];Adult_List[0] = 1;
            Child_list = new int[MaxRooms];Child_list[0] = 0;

            for(int i=1;i<MaxRooms;i++){ Adult_List[i] = 0;Child_list[i] = 0; }

            Total_rooms++; addView();
        }


        RoomCounTv.setText(String.valueOf(Total_rooms));

        AddTv.setOnClickListener(v -> {Total_rooms++;addView();});
        findViewById(R.id.submit).setOnClickListener(v -> verifydata());

        Objects.requireNonNull(this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @SuppressLint({"SetTextI18n","InflateParams"})
    private void addView(){

        MainLayt.requestFocus();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = Objects.requireNonNull(inflater).inflate(R.layout.add_room_header, null);

        TextView r_NameTv = view.findViewById(R.id.nametv);
        TextView a_plusTv = view.findViewById(R.id.aplus);
        TextView a_minusTv = view.findViewById(R.id.aminus);
        TextView a_countTv = view.findViewById(R.id.acout);
        TextView c_plusTv = view.findViewById(R.id.cplus);
        TextView c_minusTv = view.findViewById(R.id.cminus);
        TextView c_countTv = view.findViewById(R.id.ccout);
        TextView RemoveTv = view.findViewById(R.id.closetv);

        final int position = Total_rooms-1;

        if(position==0){RemoveTv.setVisibility(View.GONE);}

        Viewlist.add(position,view);Atvlist.add(position,a_countTv);Ctvlist.add(position,c_countTv);


        RoomCounTv.setText(String.valueOf(Total_rooms));

        if(Total_rooms == MaxRooms){ AddTv.setVisibility(View.GONE); }

        a_countTv.setText(String.valueOf(Adult_List[position]));
        c_countTv.setText(String.valueOf(Child_list[position]));
        setA_count();setC_count();

        r_NameTv.setText("Room "+(position+1));

        RemoveTv.setOnClickListener(v -> {

            if(Total_rooms == MaxRooms){ AddTv.setVisibility(View.VISIBLE); }

            Total_rooms--;
            RoomCounTv.setText(String.valueOf(Total_rooms));

            if(position == Total_rooms){
                Adult_List[position] = 0;
                Child_list[position] = 0;
                MainLayt.removeViewAt(position);

            }else if(position >= 1 && Total_rooms >= 2) {

                int ii = position;
                for(int i=0;i<Total_rooms-1;i++){

                    Adult_List[ii] = Adult_List[ii+1];
                    Child_list[ii] = Child_list[ii+1];

                    Atvlist.get(ii).setText(String.valueOf(Adult_List[ii]));
                    Ctvlist.get(ii).setText(String.valueOf(Child_list[ii]));

                    ii++;

                }

                Adult_List[ii] = 0;
                Child_list[ii] = 0;
                MainLayt.removeView(Viewlist.get(ii));
                //MainLayt.removeViewAt(position);

            }else {

                Adult_List[position] = 0;
                Child_list[position] = 0;

                MainLayt.removeView(view);
            }

            setA_count();setC_count();

        });

        c_plusTv.setOnClickListener(v -> {
            if(Child_list[position] < ECcount){
                int ii = Child_list[position];
                ii++;
                Child_list[position] = ii;
                c_countTv.setText(String.valueOf(ii));
                setC_count();
            }
        });

        c_minusTv.setOnClickListener(v -> {
            if(Child_list[position]>0){
                int ii = Child_list[position];
                ii--;
                Child_list[position] = ii;
                c_countTv.setText(String.valueOf(ii));
                setC_count();
            }
        });

        a_plusTv.setOnClickListener(v -> {
            if(Adult_List[position] < EAcount){
                int ii = Adult_List[position];
                ii++;
                Adult_List[position] = ii;
                a_countTv.setText(String.valueOf(ii));
                setA_count();
            }
        });

        a_minusTv.setOnClickListener(v -> {
            if(Adult_List[position] > 1){
                int ii = Adult_List[position];
                ii--;
                Adult_List[position] = ii;
                a_countTv.setText(String.valueOf(ii));
                setA_count();
            }
        });

        MainLayt.addView(view);

    }

    private void verifydata() {

        int a_c = 0;
        int c_c = 0;
        for (int Pcount : Adult_List) { a_c += Pcount; }
        for (int Pcount : Child_list) { c_c += Pcount; }

        if(a_c > MaxAcount) {
            Toast.makeText(this, "Only 6 Persons are allowed to Book", Toast.LENGTH_SHORT).show();
        }else if(c_c > MaxCcount) {
            Toast.makeText(this, "Only 3 Children are allowed to Book", Toast.LENGTH_SHORT).show();
        }else {

            Intent intent = new Intent();
            intent.putExtra("roomcount",Total_rooms);
            intent.putExtra("adult_list",Adult_List);
            intent.putExtra("child_list",Child_list);
            setResult(RESULT_OK,intent);

            finish();

        }
    }

    private void setA_count(){
        int ii = 0;
        for(int co : Adult_List){ ii += co; }
        TA_countTv.setText(String.valueOf(ii));
    }

    private void setC_count(){
        int ii = 0;
        for(int co : Child_list){ ii += co; }
        TC_countTv.setText(String.valueOf(ii));
    }

}