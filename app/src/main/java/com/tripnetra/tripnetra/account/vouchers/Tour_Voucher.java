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

public class Tour_Voucher extends AppCompatActivity {

    String Pnrno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_voucher);

        Pnrno = Objects.requireNonNull(getIntent().getExtras()).getString("pnrno");

        getdetails();
    }

    @SuppressLint("SetTextI18n")
    private void getdetails() {

        Map<String, String> params = new HashMap<>();
        params.put("pnrno", Pnrno);

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TOUR_VOUCHER, null, params, true, response -> {
            if(response.equals("No Result")){
                Utils.setSingleBtnAlert(Tour_Voucher.this, "No Data Found", "Ok", true);
            }else{
                try {
                    JSONObject jobj = new JSONObject(response);

                    ((TextView) findViewById(R.id.bookid)).setText(jobj.getString("pnr_no"));
                    ((TextView) findViewById(R.id.booktime)).setText(jobj.getString("created_on"));

                    TextView stattv = findViewById(R.id.bookstatus);
                    if(jobj.getString("booking_status").equals("CONFIRM")) {
                        stattv.setText(jobj.getString("booking_status"));
                        stattv.setTextColor(GREEN);
                    }else{
                        stattv.setText(jobj.getString("booking_status"));
                        stattv.setTextColor(RED);
                    }

                    ((TextView) findViewById(R.id.tnameTv)).setText(jobj.getString("sightseen_name"));
                    ((TextView) findViewById(R.id.nametv)).setText(jobj.getString("firstname")+" "+jobj.getString("lastname"));
                    ((TextView) findViewById(R.id.phonetv)).setText(jobj.getString("phone"));
                    ((TextView) findViewById(R.id.emailtv)).setText(jobj.getString("email_id"));
                    ((TextView) findViewById(R.id.citynameTV)).setText(jobj.getString("search_city"));
                    ((TextView) findViewById(R.id.dateTV)).setText(jobj.getString("travel_date"));

                    int partprice,BalPrice;
                    try{
                        partprice = Integer.parseInt(jobj.getString("amount"));
                        BalPrice = Integer.parseInt(jobj.getString("sightseen_total_price"))-partprice;
                    }catch (NumberFormatException e){
                        partprice = 0;
                        BalPrice = 0;
                    }

                    ((TextView) findViewById(R.id.partpriceTv)).setText("₹ "+partprice);
                    ((TextView) findViewById(R.id.balTv)).setText("₹ "+BalPrice);

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(Tour_Voucher.this, "Technical Error Occurred Try Again", "Ok", true);
                }
            }
        });
    }

}