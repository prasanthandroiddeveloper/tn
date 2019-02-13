package com.tripnetra.tripnetra.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.DataAdapter;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.SharedPrefs;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Cash_History_Frag extends Fragment {

    TextView NameTv,BalTv;
    RecyclerView recyclerView;
    String Userid;
    LoadingDialog loadDlg;
    Activity activity;

    public Cash_History_Frag() {}

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_history, container, false);

        activity = getActivity();
        Userid = new SharedPrefs(activity).getUserId();

        NameTv = view.findViewById(R.id.nametv);
        BalTv = view.findViewById(R.id.balTv);

        loadDlg = new LoadingDialog(activity);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadDlg.show();

        NameTv.setText(new SharedPrefs(activity).getUName());
        BalTv.setText("₹ "+new SharedPrefs(activity).getTripcash());
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        loaddata();

        return view;
    }

    public void loaddata(){

        Map<String, String> params = new HashMap<>();
        params.put("userid", Userid);

        new VolleyRequester(activity).ParamsRequest(1, ((G_Class) activity.getApplicationContext()).getBaseurl()+Config.TRIP_URL+"account/tripcashlog.php", loadDlg, params, false, response -> {

            if(response.equals("No Results")){
                if(loadDlg.isShowing()){loadDlg.dismiss();}
                Utils.setSingleBtnAlert(activity, "No Transactions Found", "Ok", false);
            }else {
                try {
                    JSONArray jarr = new JSONArray(response);
                    List<DataAdapter> list = new ArrayList<>();

                    for (int i = 0; i < jarr.length(); i++) {
                        DataAdapter adaptr = new DataAdapter();
                        JSONObject jobj = jarr.getJSONObject(i);
                        adaptr.setText(jobj.getString("trip_type"));
                        adaptr.setPrice(jobj.getString("amount"));
                        adaptr.setFeature(jobj.getString("description"));
                        adaptr.setType(jobj.getString("type").toLowerCase());
                        adaptr.setDate(jobj.getString("trip_date"));

                        list.add(adaptr);
                        recyclerView.setAdapter(new Transactn_Recycle_Adapter(list));
                    }
                    if(loadDlg.isShowing()){loadDlg.dismiss();}

                } catch (JSONException e) {
                    //e.printStackTrace();
                    if(loadDlg.isShowing()){loadDlg.dismiss();}
                    Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", false);
                }

            }

        });

    }

    private class Transactn_Recycle_Adapter extends RecyclerView.Adapter<Transactn_Recycle_Adapter.ViewHolder>{

        private List<DataAdapter> listadap;

        Transactn_Recycle_Adapter(List<DataAdapter> list) {
            super();
            this.listadap = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_recycler, parent, false));
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView PriceTv,TypeTv,DescTv,DateTv;
            ViewHolder(View vw) {
                super(vw);
                PriceTv = vw.findViewById(R.id.pricetv);
                TypeTv = vw.findViewById(R.id.btypetv);
                DescTv = vw.findViewById(R.id.usedtv);
                DateTv = vw.findViewById(R.id.datetv);
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {

            vh.TypeTv.setText(listadap.get(pos).getText());
            vh.DescTv.setText(listadap.get(pos).getFeature());

            vh.DateTv.setText(Utils.ChangeDateFormat(listadap.get(pos).getDate(),1));

            if(Objects.equals(listadap.get(pos).getType(), "credit")){
                vh.PriceTv.setText("+ ₹"+listadap.get(pos).getPrice());
                vh.PriceTv.setTextColor(Color.parseColor("#017121"));
            }else{
                vh.PriceTv.setText("- ₹"+listadap.get(pos).getPrice());
                vh.PriceTv.setTextColor(Color.parseColor("#FFCF0003"));
            }
        }

        @Override
        public int getItemCount() {return listadap.size();}

    }

}