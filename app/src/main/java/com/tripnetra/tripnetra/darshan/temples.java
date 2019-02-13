package com.tripnetra.tripnetra.darshan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tripnetra.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class temples extends AppCompatActivity {

    public static ArrayList<DDataAdapters> checkedplayers ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temples);

        getdata();
    }

    private void getdata() {

        try {

            InputStream is = getAssets().open("addon.txt");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String response = new String(buffer);

            RecyclerView recyclerView = findViewById(R.id.recyclerView);

            if((response).equals("No Result")) {
                Toast.makeText(temples.this,"No Results Found" +
                        "",Toast.LENGTH_SHORT).show();

            } else {

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(temples.this));

                JSONArray jarr = new JSONArray(response);
                ArrayList<DDataAdapters> list = new ArrayList<>();

                for(int i = 0; i< jarr.length(); i++) {
                    DDataAdapters adapter = new DDataAdapters();
                    JSONObject json = jarr.getJSONObject(i);

                    adapter.setID(json.getString("id"));
                    adapter.setText(json.getString("duration"));
                    adapter.setName(json.getString("name"));

                    adapter.setDescription(json.getString("description"));
                    adapter.setAPrice(json.getString("price"));

                    adapter.setType("0");

                    if(checkedplayers != null && checkedplayers.size()>0){
                        for(int ii = 0; ii< checkedplayers.size(); ii++) {
                            if(checkedplayers.get(ii).getID().equals(json.getString("id"))){
                                adapter.setType("1");
                                break;
                            }
                        }
                    }

                    list.add(adapter);
                }

                recyclerView.setAdapter(new temple_recycle_adapter(list));
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();

        }
    }

    public void done(View view) {

        ArrayList<String> Id=new ArrayList<>();
        ArrayList<String> name=new ArrayList<>();
        ArrayList<String> price=new ArrayList<>();
        ArrayList<String> duration=new ArrayList<>();

        for (DDataAdapters d: checkedplayers){
            Id.add(d.getID());
            name.add(d.getName());
            price.add(d.getAPrice());
            duration.add(d.getText());
        }


        Intent intent = new Intent();
        intent.putStringArrayListExtra("ids", Id);
        intent.putStringArrayListExtra("name", name);
        intent.putStringArrayListExtra("price", price);
        intent.putStringArrayListExtra("duration", duration);

        setResult(Activity.RESULT_OK ,intent);
        finish();
    }

    class temple_recycle_adapter extends RecyclerView.Adapter<temple_recycle_adapter.ViewHolder>  {

        private ArrayList<DDataAdapters> listadapter;

        temple_recycle_adapter(ArrayList<DDataAdapters> list) {
            this.listadapter = list;
            checkedplayers = new ArrayList<>();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_temple, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {
            DDataAdapters DDataAdapters =  listadapter.get(pos);
            vh.nameTv.setText(DDataAdapters.getName());
            vh.duration.setText(DDataAdapters.getText());
            vh.price.setText(DDataAdapters.getAPrice());
            vh.discpTV.setText(DDataAdapters.getDescription());
            // Glide.with(context).load(DDataAdapters.getImage()).into(vh.himageiv);

            if(!DDataAdapters.getType().equals("0")){
                vh.chk.setChecked(true);
                checkedplayers.add(listadapter.get(pos));
            }

        }

        @Override
        public int getItemCount() {
            return listadapter.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            TextView nameTv,discpTV,duration,price;

            ImageView himageiv;

            CheckBox chk;

            ViewHolder(View iv) {
                super(iv);

                nameTv = iv.findViewById(R.id.nameTv);
                price = iv.findViewById(R.id.priceTv);
                duration = iv.findViewById(R.id.duration);
                discpTV = iv.findViewById(R.id.descTv);
                himageiv=iv.findViewById(R.id.image);

                chk=iv.findViewById(R.id.chk);

                iv.setOnClickListener(v -> {
                    if(chk.isChecked()){
                        chk.setChecked(false);
                        chk.setSelected(false);

                        checkedplayers.remove(listadapter.get(getAdapterPosition()));
                    }else{
                        checkedplayers.add(listadapter.get(getAdapterPosition()));
                        chk.setChecked(true);
                        chk.setSelected(true);

                    }
                });
            }

        }
    }

}
