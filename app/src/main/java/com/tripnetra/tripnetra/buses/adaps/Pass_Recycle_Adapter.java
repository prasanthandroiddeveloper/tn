package com.tripnetra.tripnetra.buses.adaps;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.DataAdapter;

import java.util.ArrayList;
import java.util.List;

public class Pass_Recycle_Adapter extends RecyclerView.Adapter<Pass_Recycle_Adapter.ViewHolder> {

    public static ArrayList<DataAdapter> PassDetailsList;
    private List<String> seatList;

    public Pass_Recycle_Adapter(ArrayList<DataAdapter> list,List<String> slist) {
        PassDetailsList = list;
        this.seatList = slist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pass_details, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {
        vh.pssTv.setText("Passenger "+seatList.get(pos+1)+" Details");
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView pssTv;
        EditText NameEt,AgeEt;
        RadioGroup GenderRG;

        ViewHolder(View itemView) {
            super(itemView);
            pssTv = itemView.findViewById(R.id.passngrTV);
            NameEt = itemView.findViewById(R.id.nameEt);
            AgeEt = itemView.findViewById(R.id.ageEt);
            GenderRG = itemView.findViewById(R.id.genderRG);

            NameEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    PassDetailsList.get(getAdapterPosition()).setName(String.valueOf(s));
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            AgeEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    PassDetailsList.get(getAdapterPosition()).setPrice(String.valueOf(s));
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            GenderRG.setOnCheckedChangeListener((group, cId) -> {
                String ss = (cId==R.id.maleRB) ? "Male":"Female";
                PassDetailsList.get(getAdapterPosition()).setType(ss);
            });

        }

    }

    @Override
    public int getItemCount() { return PassDetailsList.size()-1; }

}