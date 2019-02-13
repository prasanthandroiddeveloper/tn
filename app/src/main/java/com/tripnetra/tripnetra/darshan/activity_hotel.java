package com.tripnetra.tripnetra.darshan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class activity_hotel extends AppCompatActivity {

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        bundle = getIntent().getExtras();

        StringBuilder string = new StringBuilder("Bundle{");
        for (String key : bundle.keySet()) {
            string.append(" ").append(key).append(" => ").append(bundle.get(key)).append(";");
        }

        string.append(" }");
        Log.i("response", String.valueOf(string));

        getdata();
    }

    private void getdata() {

        try {

            InputStream is = getAssets().open("recycle.txt");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String response = new String(buffer);

            RecyclerView recyclerView = findViewById(R.id.recyclerView);

            if((response).equals("No Result")) {
                Toast.makeText(activity_hotel.this,"No Results Found" + "",Toast.LENGTH_SHORT).show();
            } else {

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity_hotel.this));
                JSONArray jarr = new JSONArray(response);
                List<DDataAdapter> list = new ArrayList<>();

                for(int i = 0; i< jarr.length(); i++) {
                    DDataAdapter adapter = new DDataAdapter();
                    JSONObject json = jarr.getJSONObject(i);

                    adapter.setID(json.getString("package_name"));
                    adapter.setName(json.getString("hotel_name"));
                    adapter.setDescription(json.getString("hotel_amenities"));
                    adapter.setaddress(json.getString("discount_price"));
                    adapter.setAPrice(json.getString("hotel_price"));
                    list.add(adapter);
                }

                recyclerView.setAdapter(new hotel_recycle_adapter(list));
            }

        } catch (JSONException | IOException e) {
            //e.printStackTrace();
            Utils.setSingleBtnAlert(activity_hotel.this, getResources().getString(R.string.something_wrong), "Ok", true);
        }
    }

    public class hotel_recycle_adapter  extends RecyclerView.Adapter<hotel_recycle_adapter.ViewHolder>  {

        private Context context;
        private List<DDataAdapter> listadapter;

        hotel_recycle_adapter(List<DDataAdapter> list) {
            super();
            this.listadapter = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_hotel, parent, false));
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull hotel_recycle_adapter.ViewHolder vh, int pos) {

            DDataAdapter DDataAdapter =  listadapter.get(pos);

            vh.hnameTv.setText(DDataAdapter.getName());
            vh.pnameTv.setText(DDataAdapter.getID());//STRIKE_THRU_TEXT_FLAG
            vh.tpriceTv.setText("₹: "+ DDataAdapter.getAPrice());
            vh.dpriceTV.setText(DDataAdapter.getaddress());
            vh.dpriceTV.setPaintFlags(vh.dpriceTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            // Glide.with(context).load(DDataAdapter.getImage()).into(vh.himageiv);

            String array_items[] = DDataAdapter.getDescription().split(",");

            for (String array_item : array_items) {
                ImageView imgView = new ImageView(context);
                int hh = Utils.getscreenwidth(activity_hotel.this)/12;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(hh,hh);
                layoutParams.setMargins(10,0,10,0);
                imgView.setLayoutParams(layoutParams);
                vh.imagellout.addView(imgView);

                switch (array_item) {
                    case "4":
                        imgView.setImageResource(R.drawable.nroomser);
                        break;
                    case "5":
                        imgView.setImageResource(R.drawable.nac);
                        break;
                    case "9":
                        imgView.setImageResource(R.drawable.nwifi);
                        break;
                    case "10":
                        imgView.setImageResource(R.drawable.ncar);
                        break;
                    case "22":
                        imgView.setImageResource(R.drawable.ntv);
                        break;
                    case "32":
                        imgView.setImageResource(R.drawable.nindntoi);
                        break;
                    case "31":
                        imgView.setImageResource(R.drawable.nwestoilet);
                        break;
                    default:
                        imgView.setVisibility(View.GONE);
                        break;
                }
            }

        }

        @Override
        public int getItemCount() { return listadapter.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView hnameTv, pnameTv, tpriceTv, dpriceTV;
            LinearLayout imagellout;
            ImageView himageiv;

            ViewHolder(View iv) {
                super(iv);

                context = iv.getContext();
                hnameTv = iv.findViewById(R.id.hnameTv);
                pnameTv = iv.findViewById(R.id.pnameTv);
                tpriceTv = iv.findViewById(R.id.tpriceTv);
                dpriceTV = iv.findViewById(R.id.discpTv);
                imagellout = iv.findViewById(R.id.imagellout);
                himageiv = iv.findViewById(R.id.himageiv);

                iv.setOnClickListener(v -> {

                    Intent intent = new Intent(context, addons_activity.class);
                    bundle.putString("package_name", listadapter.get(getAdapterPosition()).getID());
                    bundle.putString("hotel_name", listadapter.get(getAdapterPosition()).getName());
                    bundle.putString("hotel_price", listadapter.get(getAdapterPosition()).getAPrice());
                    bundle.putString("discount_price", listadapter.get(getAdapterPosition()).getaddress());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                });
            }
        }
    }

}