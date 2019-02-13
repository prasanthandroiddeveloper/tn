package com.tripnetra.tripnetra.account.vouchers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Car_Voucher extends AppCompatActivity {

    String Pnrno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_voucher);

        Pnrno = Objects.requireNonNull(getIntent().getExtras()).getString("pnrno");

        getdetails();
    }

    @SuppressLint("SetTextI18n")
    private void getdetails() {

        Map<String, String> params = new HashMap<>();
        params.put("pnrno", Pnrno);

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.CAR_VOUCHER, null, params, true, response -> {

            if(response.equals("No Result")){
                Utils.setSingleBtnAlert(Car_Voucher.this, "No Data Found", "Ok", true);
            }else{
                try {
                    JSONObject jobj = new JSONObject(response);

                    ((TextView) findViewById(R.id.bookid)).setText(jobj.getString("pnr_no"));
                    ((TextView) findViewById(R.id.booktime)).setText(jobj.getString("created_on"));

                    TextView stattv = findViewById(R.id.bookstatus);
                    if(jobj.getString("booking_status").contains("CONFIRM")) {
                        stattv.setText(jobj.getString("booking_status"));
                        stattv.setTextColor(GREEN);
                    }else{
                        stattv.setText(jobj.getString("booking_status"));
                        stattv.setTextColor(RED);
                    }

                    ((TextView) findViewById(R.id.carnameTv)).setText(jobj.getString("car_name"));
                    ((TextView) findViewById(R.id.cartypeTv)).setText(jobj.getString("car_type"));
                    ((TextView) findViewById(R.id.carcompTv)).setText(jobj.getString("car_company_name"));
                    ((TextView) findViewById(R.id.capacityTv)).setText(jobj.getString("max_capacity"));
                    ((TextView) findViewById(R.id.doorsTv)).setText(jobj.getString("no_of_doors"));
                    ((TextView) findViewById(R.id.Gnametv)).setText(jobj.getString("first_name")+" "+jobj.getString("last_name"));
                    ((TextView) findViewById(R.id.phonetv)).setText(jobj.getString("phone_no"));
                    ((TextView) findViewById(R.id.emailtv)).setText(jobj.getString("email_id"));
                    ((TextView) findViewById(R.id.pickuplocTV)).setText(jobj.getString("from_city"));

                    ((TextView) findViewById(R.id.pickupdateTv)).setText(Utils.ChangeDateFormat(jobj.getString("pickup_date"),1));
                    ((TextView) findViewById(R.id.pickuptimeTv)).setText(jobj.getString("pickup_time"));
                    ((TextView) findViewById(R.id.pricetv)).setText("₹ "+jobj.getString("total_amount"));


                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(Car_Voucher.this, "Technical Error Occurred Try Again", "Ok", true);
                }
            }
        });
    }

}