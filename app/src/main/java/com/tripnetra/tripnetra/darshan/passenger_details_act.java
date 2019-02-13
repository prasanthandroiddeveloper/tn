package com.tripnetra.tripnetra.darshan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.utils.Utils;

import java.util.ArrayList;


public class passenger_details_act extends AppCompatActivity implements passdetails {

    int Tot_count;
    RecyclerView recyclerView;
    String[] NamesList, AgesList, GenderList;

    Bundle bundle;
    String Pname, Hname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_details);
        bundle = getIntent().getExtras();

        Hname = bundle.getString("hotel_name");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        StringBuilder string = new StringBuilder("Bundle{");
        for (String key : bundle.keySet()) {
            string.append(" ").append(key).append(" => ").append(bundle.get(key)).append(";");
        }
        string.append(" }");
        Log.i("response1", String.valueOf(string));


        Pname = bundle.getString("sightseen_name");
        Tot_count = Integer.parseInt(bundle.getString("acount")) + Integer.parseInt(bundle.getString("ccount"));

        NamesList = new String[Tot_count];
        AgesList = new String[Tot_count];
        GenderList = new String[Tot_count];

        if (Tot_count > 1) {

            recyclerView = findViewById(R.id.Passrecycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new Pass_Recycle_Adapter(this, Tot_count));

        }
    }

    public void confirmbook(View view) {

        NamesList[0] = ((EditText) findViewById(R.id.nametv)).getText().toString().trim();
        AgesList[0] = ((EditText) findViewById(R.id.Age)).getText().toString().trim();
        RadioButton rbtn = findViewById(((RadioGroup) findViewById(R.id.genderRG)).getCheckedRadioButtonId());

        GenderList[0] = (rbtn != null) ? rbtn.getText().toString().trim() : "";


        String EMail = ((EditText) findViewById(R.id.emailTv)).getText().toString();
        String Mobile = ((EditText) findViewById(R.id.phn)).getText().toString();

        TextInputLayout emailTIL = findViewById(R.id.emaiTil), phoneTIL = findViewById(R.id.phoneTil);
        emailTIL.setErrorEnabled(false);
        phoneTIL.setErrorEnabled(false);

        if (EMail.equals("") || Utils.isEmailValid(EMail)) {
            emailTIL.setError(EMail.equals("") ? "Enter EMail" : "Enter Valid EMail");
            return;
        } else if (Mobile.equals("") || !Mobile.matches("[6-9][0-9]{9}")) {
            phoneTIL.setError((Mobile.equals("")) ? "Enter Mobile Number" : "Enter Valid Mobile Number");
            return;
        }

        for (int i = 0; i < Tot_count; i++) {
            if (NamesList[i].equals("")) {
                Toast.makeText(this, "Enter Passenger " + (i + 1) + " Name", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        for (int i = 0; i < Tot_count; i++) {
            if (AgesList[i].equals("")) {
                Toast.makeText(this, "Enter Passenger " + (i + 1) + " Age", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        for (int i = 0; i < Tot_count; i++) {
            if (GenderList[i].equals("")) {
                Toast.makeText(this, "Enter Passenger " + (i + 1) + " Gender", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        bundle.putStringArray("nameslist", NamesList);
        bundle.putStringArray("ageslist", AgesList);
        bundle.putStringArray("genderlist", GenderList);
        bundle.putString("passemail", EMail);
        bundle.putString("hotelname", Hname);
        bundle.putString("passmobile", Mobile);
      /*  bundle.putString("Addons", "LocalTemples");
        bundle.putString("Addduration", "2");
        bundle.putString("Adddate", "2018-09-17");
        bundle.putString("Addprice", "2999");
        bundle.putString("darshandt", "2018-09-16");*/

        Intent intent = new Intent(this, Darshan_payment_act.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void getResponse(ArrayList<DDataAdapter> PassDetailsList) {

        for (int i = 1; i < Tot_count; i++) {
            NamesList[i] = PassDetailsList.get(i - 1).getName();
            AgesList[i] = PassDetailsList.get(i - 1).getPrice();
            GenderList[i] = PassDetailsList.get(i - 1).getType();

        }

    }

    class Pass_Recycle_Adapter extends RecyclerView.Adapter<Pass_Recycle_Adapter.ViewHolder> {

        int Tot_count;
        Context context;
        ArrayList<DDataAdapter> PassDetailsList;

        Pass_Recycle_Adapter(Context cont,int tc ) {
            this.Tot_count=tc;
            this.context=cont;
            PassDetailsList=new ArrayList<>();

            for(int i = 0; i < Tot_count-1; i++){
                DDataAdapter adapter = new DDataAdapter();
                adapter.setName("");
                adapter.setPrice("");
                adapter.setType("");
                PassDetailsList.add(adapter);
            }

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rrecycler_pass_details, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {
            vh.psstv.setText("Passenger "+(pos+2)+" Details");
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            EditText NameEt,AgeEt;
            TextView psstv;
            RadioGroup GenderRG;

            ViewHolder(View itemView) {
                super(itemView);
                psstv=itemView.findViewById(R.id.passtv);
                NameEt = itemView.findViewById(R.id.nametv);
                AgeEt = itemView.findViewById(R.id.Age);
                GenderRG = itemView.findViewById(R.id.genderRG);

                NameEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        PassDetailsList.get(getAdapterPosition()).setName(String.valueOf(s));

                        ((passdetails) context).getResponse(PassDetailsList);
                    }

                    @Override
                    public void afterTextChanged(Editable s) { }
                });

                AgeEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        PassDetailsList.get(getAdapterPosition()).setPrice(String.valueOf(s));
                        ((passdetails) context).getResponse(PassDetailsList);
                    }

                    @Override
                    public void afterTextChanged(Editable s) { }
                });

                GenderRG.setOnCheckedChangeListener((group, cId) -> {
                    String ss = (cId==R.id.maleRB) ? "Male":"Female";
                    PassDetailsList.get(getAdapterPosition()).setType(String.valueOf(ss));
                    ((passdetails) context).getResponse(PassDetailsList);

                });
            }
        }

        @Override
        public int getItemCount() { return Tot_count-1; }

    }
}
