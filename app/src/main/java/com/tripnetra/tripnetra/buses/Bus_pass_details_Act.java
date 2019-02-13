package com.tripnetra.tripnetra.buses;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.DataAdapter;
import com.tripnetra.tripnetra.buses.adaps.Pass_Recycle_Adapter;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.tripnetra.tripnetra.buses.adaps.Pass_Recycle_Adapter.PassDetailsList;

public class Bus_pass_details_Act extends AppCompatActivity {

    int Pass_count;
    List<String> SeatList;
    Bundle M_bundle;
    RecyclerView recyclerView;
    String[] NamesList,AgesList,GenderList;
    String PNR_Id;
    G_Class g_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_pass_details);

        g_class = (G_Class) getApplicationContext();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        M_bundle = getIntent().getExtras();
        Pass_count = Objects.requireNonNull(M_bundle).getInt("pass_count");
        SeatList = Arrays.asList(Objects.requireNonNull(Objects.requireNonNull(M_bundle.getString("seatlist"))
                .replaceAll("]","").replaceAll("\\[","")).split(","));

        if(Pass_count >1) {

            recyclerView = findViewById(R.id.Passrecycler);
            ArrayList<DataAdapter> list = new ArrayList<>();

            for(int i = 0; i < (Pass_count); i++){
                DataAdapter adapter = new DataAdapter();
                adapter.setName("");
                adapter.setPrice("");
                adapter.setType("");
                list.add(adapter);
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new Pass_Recycle_Adapter(list,SeatList));
        }

    }

    public void confirmbook(View view) {

        NamesList = new String[Pass_count];AgesList = new String[Pass_count];GenderList = new String[Pass_count];

        NamesList[0] = ((EditText) findViewById(R.id.nameEt)).getText().toString().trim();
        AgesList[0] = ((EditText) findViewById(R.id.ageEt)).getText().toString().trim();
        RadioButton rbtn = findViewById(((RadioGroup) findViewById(R.id.genderRG)).getCheckedRadioButtonId());

        GenderList[0] = (rbtn!=null) ? rbtn.getText().toString().trim() : "";

        if(Pass_count >1) {
            for (int i = 1; i < Pass_count; i++) {
                NamesList[i] = PassDetailsList.get(i-1).getName();
                AgesList[i] = PassDetailsList.get(i-1).getPrice();
                GenderList[i] = PassDetailsList.get(i-1).getType();
            }
        }
        String EMail = ((EditText)findViewById(R.id.emailEt)).getText().toString();
        String Mobile = ((EditText)findViewById(R.id.phoneEt)).getText().toString();

        TextInputLayout emailTIL = findViewById(R.id.emaiTil), phoneTIL = findViewById(R.id.phoneTil);
        emailTIL.setErrorEnabled(false);        phoneTIL.setErrorEnabled(false);

        if(Objects.equals(EMail, "") || Utils.isEmailValid(EMail)){
            emailTIL.setError(Objects.equals(EMail, "") ? "Enter EMail" : "Enter Valid EMail");
            return;
        }else if(Objects.equals(Mobile, "") || !Mobile.matches("[6-9][0-9]{9}")){
            phoneTIL.setError((Objects.equals(Mobile, "")) ? "Enter Mobile Number" : "Enter Valid Mobile Number");
            return;
        }

        for(int i = 0; i < Pass_count; i++){
            if(NamesList[i].equals("")){ Toast.makeText(this, "Enter Passenger "+(i+1)+" Name",Toast.LENGTH_SHORT).show();return; }
        }
        for(int i = 0; i < Pass_count; i++) {
            if (AgesList[i].equals("")) { Toast.makeText(this, "Enter Passenger "+(i+1)+" Age", Toast.LENGTH_SHORT).show();return; }
        }
        for(int i = 0; i < Pass_count; i++){
            if(GenderList[i].equals("")){ Toast.makeText(this, "Enter Passenger "+(i+1)+" Gender",Toast.LENGTH_SHORT).show();return; }
        }

        M_bundle.putString("nameslist",Arrays.toString(NamesList));
        M_bundle.putString("ageslist",Arrays.toString(AgesList));
        M_bundle.putString("genderlist", Arrays.toString(GenderList));
        M_bundle.putString("passemail",EMail);
        M_bundle.putString("passmobile",Mobile);

        String[] FareList = (Objects.requireNonNull(Objects.requireNonNull(M_bundle.getString("farelist"))
                .replaceAll("]","").replaceAll("\\[","")).split(","));

        int Price = M_bundle.getInt("price")+M_bundle.getInt("gst")+M_bundle.getInt("ctax");

        try {
            JSONArray passengerInfo = new JSONArray();

            for (int i=0;i<NamesList.length;i++){
                JSONObject jobj = new JSONObject();

                jobj.put("Name",NamesList[i]);
                jobj.put("gender",GenderList[i]);
                jobj.put("seatNo",SeatList.get(i));
                jobj.put("age",AgesList[i]);
                jobj.put("fare",FareList[i]);

                passengerInfo.put(jobj);
            }

            JSONObject seatInfo = new JSONObject();
            seatInfo.put("passengerInfo",passengerInfo);

            JSONObject MainObject = new JSONObject();
            MainObject.put("routeid",M_bundle.get("routeid"));
            MainObject.put("tripid",M_bundle.get("tripid"));
            MainObject.put("bpoint",M_bundle.get("bpid"));
            MainObject.put("dpoint",M_bundle.get("dpid"));
            MainObject.put("noofseats",Pass_count);
            MainObject.put("mobileno",Mobile);
            MainObject.put("email",EMail);
            MainObject.put("totalfare",Price);
            MainObject.put("seatInfo",seatInfo);

            Map<String, String> params = new HashMap<>();
            params.put("jsonobject", String.valueOf(MainObject));
            params.put("type", "reserve_seats");

            new VolleyRequester(this).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL+"bus/details.php", null, params, false, response -> {

                try {
                    JSONObject jobj = new JSONObject(response).getJSONObject("TentativeBooking");
                    if(jobj.getJSONObject("status").getString("status_message").equals("Success")){
                        PNR_Id = jobj.getJSONObject("BookingInfo").getString("PNR");
                        pay_now();
                    }else{
                        Utils.setSingleBtnAlert(Bus_pass_details_Act.this,jobj.getJSONObject("status").getString("status_message"),"Ok",false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void pay_now() {

        Map<String, String> params = new HashMap<>();
        params.put("pnr", PNR_Id);
        params.put("type", "book_seats");

        new VolleyRequester(this).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL+"bus/details.php", null, params, false, response -> {

            try {
                JSONObject jobj = new JSONObject(response).getJSONObject("ConfirmBooking");
                if(jobj.getJSONObject("status").getString("status_message").equals("Success")){
                    Utils.setSingleBtnAlert(Bus_pass_details_Act.this,"Booking Confirmed","Ok",false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }

}