package com.tripnetra.tripnetra.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.bumptech.glide.request.RequestOptions;
import com.tripnetra.tripnetra.BuildConfig;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.cars.Cars_Main_Act;
import com.tripnetra.tripnetra.darshan.darshan_main_act;
import com.tripnetra.tripnetra.hotels.Hotel_Main_Act;
import com.tripnetra.tripnetra.places.Current_Location_Act;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.tours.Tours_Main_Act;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Home_Frag extends Fragment implements View.OnClickListener{

    RecyclerView recyclerView;
    String[] CityNames,Cityimages;
    TextView RecmdTV;
    Button seemoreBtn;
    Activity activity;
    G_Class g_class;

    public Home_Frag() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        activity = getActivity();

        g_class = (G_Class) activity.getApplicationContext();
        Objects.requireNonNull(((AppCompatActivity) activity).getSupportActionBar()).hide();

        RecmdTV = view.findViewById(R.id.recommand);
        RecmdTV.requestFocus();
        seemoreBtn = view.findViewById(R.id.seeMore);

        recyclerView = view.findViewById(R.id.recycler_view);

        if(Utils.isDataConnectionAvailable(activity)){
            getimages();
        }else{
            recyclerView.setVisibility(View.GONE);
            RecmdTV.setVisibility(View.INVISIBLE);
            seemoreBtn.setVisibility(View.INVISIBLE);
            TextView nontv = view.findViewById(R.id.nonettv);
            nontv.setVisibility(View.VISIBLE);
            Utils.setSingleBtnAlert(activity, "Your Are Offline \nPlease Check Your Network Connection..", "Ok",false);
        }

        view.findViewById(R.id.llout1).setOnClickListener(this);
        view.findViewById(R.id.llout2).setOnClickListener(this);
        view.findViewById(R.id.llout3).setOnClickListener(this);
        view.findViewById(R.id.llout4).setOnClickListener(this);

        //todo darshan added here
        view.findViewById(R.id.llout5).setOnClickListener(this);


        return view;
    }

    public void getimages() {

        new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL+"banner.php", null, null, false, response -> {
            try {
                JSONArray jarray = new JSONObject(response).getJSONArray("banner_details");
                int len=jarray.length();
                Cityimages = new String[len];CityNames = new String[len];

                for(int i=0;i<len;i++) {
                    JSONObject json = jarray.getJSONObject(i);
                    Cityimages[i] = g_class.getImageurl()+"hotel_detinations/"+json.getString("image");
                    CityNames[i] = json.getString("city_name");
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(new Home_Recycle_Adapter(activity,Cityimages,CityNames));
                RecmdTV.setText(getResources().getString(R.string.remnd_places));
                seemoreBtn.setVisibility(View.VISIBLE);
                seemoreBtn.setOnClickListener(view -> startActivity(new Intent(activity, Hotel_Main_Act.class)));

                try {
                    String onlineVersion = new GetVersionCode().execute().get();
                    String Currentversion = BuildConfig.VERSION_NAME.replace(".", "");
                    if (onlineVersion != null) {
                        onlineVersion = onlineVersion.replace(".", "");
                        if (Integer.parseInt(Currentversion) < Integer.parseInt(onlineVersion)) {
                            new AlertDialog.Builder(activity).setMessage("A New Version is Available")
                                    .setPositiveButton("Update", (dialog, id) ->
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tripnetra.tripnetra"))))
                                    .setNegativeButton("Not Now", (dialogInterface, i) -> { })
                                    .setCancelable(true).create().show();
                        }
                    }
                } catch (InterruptedException|ExecutionException e) {
                    //  e.printStackTrace();
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(activity,getResources().getString(R.string.something_wrong),"Ok",false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llout1:
                startActivity(new Intent(activity, Hotel_Main_Act.class));
                break;
            case R.id.llout2:
                startActivity(new Intent(activity, Tours_Main_Act.class));
                break;
            case R.id.llout3:
                startActivity(new Intent(activity, Cars_Main_Act.class));//todo // Bus_Seat_Select_Act //Buses_Main_Act //Buses_Search_Act

                break;
            case R.id.llout4:
                startActivity(new Intent(activity, Current_Location_Act.class));
                break;
            case R.id.llout5:
                startActivity(new Intent(activity, darshan_main_act.class));//todo darshan added here

                break;
        }
    }

    public static class GetVersionCode extends AsyncTask<Void,String,String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Elements els = Jsoup.connect("https://play.google.com/store/apps/details?id=com.tripnetra.tripnetra&hl=en").timeout(60000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com").get().select("div[itemprop=softwareVersion]");

                if(els!=null&&els.size()>0){
                    return els.first().ownText();
                }else{
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;}
        }
    }

    class Home_Recycle_Adapter extends RecyclerView.Adapter<Home_Recycle_Adapter.MyViewHolder>{

        private Context context;
        private String[] HotelImages,HotelNames;

        Home_Recycle_Adapter(Context context, String[] HotelImgs, String[] HotelNms) {
            this.context=context;
            this.HotelImages=HotelImgs;
            this.HotelNames=HotelNms;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            if((getItemCount()%3)!=0 && getItemCount()==(position+1)) {
                if ((getItemCount() % 3) == 1) {
                    setval(holder.imgIV,holder.NameTV,holder.cView,position);
                    holder.LLayt.setVisibility(View.GONE);
                } else if ((getItemCount() % 3) == 2) {
                    setval(holder.imgIV,holder.NameTV,holder.cView,position-1);
                    setval(holder.imgIV1,holder.NameTV1,holder.cView1,position);
                    holder.cView2.setVisibility(View.GONE);
                }
            }else if(position%3==1){
                setval(holder.imgIV,holder.NameTV,holder.cView,position-1);
                setval(holder.imgIV1,holder.NameTV1,holder.cView1,position);
                setval(holder.imgIV2,holder.NameTV2,holder.cView2,position+1);
            } else {
                ViewGroup.LayoutParams params = holder.MainLayt.getLayoutParams();
                params.height = 0;
                params.width = 0;
                holder.MainLayt.setLayoutParams(params);
                holder.MainLayt.setVisibility(View.GONE);
            }
        }

        private void setval(ImageView imIV, TextView nametv, CardView cview, int ipos) {
            Glide.with(context).load(HotelImages[ipos]).apply(new RequestOptions().centerCrop()).into(imIV);
            nametv.setText(HotelNames[ipos]);
            cview.setTag(HotelNames[ipos]);
        }

        @Override
        public int getItemCount() {return HotelImages.length;}

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            ImageView imgIV, imgIV1, imgIV2;
            TextView NameTV, NameTV1, NameTV2;
            CardView cView, cView1, cView2;
            LinearLayout LLayt, MainLayt;

            MyViewHolder(View itemView) {
                super(itemView);

                imgIV = itemView.findViewById(R.id.thumbnail);
                imgIV1 = itemView.findViewById(R.id.img1);
                imgIV2 = itemView.findViewById(R.id.img2);

                NameTV = itemView.findViewById(R.id.name);
                NameTV1 = itemView.findViewById(R.id.name1);
                NameTV2 = itemView.findViewById(R.id.name2);

                cView = itemView.findViewById(R.id.card_view);
                cView1 = itemView.findViewById(R.id.card_view1);
                cView2 = itemView.findViewById(R.id.card_view2);

                LLayt = itemView.findViewById(R.id.linear);
                MainLayt = itemView.findViewById(R.id.main);

                cView.setOnClickListener(this);
                cView1.setOnClickListener(this);
                cView2.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Position " + getAdapterPosition()+"\n"+v.getTag(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, Hotel_Main_Act.class);
                intent.putExtra("cname", String.valueOf(v.getTag()));
                context.startActivity(intent);
            }
        }

    }

}