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

public class Hotel_Voucher extends AppCompatActivity {

    String Pnrno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_voucher);

        Pnrno = Objects.requireNonNull(getIntent().getExtras()).getString("pnrno");

        getdetails();
    }

    @SuppressLint("SetTextI18n")
    private void getdetails() {

        Map<String, String> params = new HashMap<>();
        params.put("pnrno", Pnrno);

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.HOTEL_VOUCHER, null, params, true, response -> {

            if(response.equals("0 results")){
                Utils.setSingleBtnAlert(Hotel_Voucher.this, "No Data Found", "Ok", true);
            }else{
                try {
                    JSONObject jobj = new JSONObject(response);

                    ((TextView) findViewById(R.id.bookid)).setText(jobj.getString("pnr_no"));
                    ((TextView) findViewById(R.id.booktime)).setText(jobj.getString("book_date"));

                    TextView stattv = findViewById(R.id.bookstatus);
                    if(jobj.getString("booking_status").equals("CONFIRM")) {
                        stattv.setText(jobj.getString("booking_status"));
                        stattv.setTextColor(GREEN);
                    }else{
                        stattv.setText(jobj.getString("booking_status"));
                        stattv.setTextColor(RED);
                    }

                    ((TextView) findViewById(R.id.hotelname)).setText(jobj.getString("hotel_name"));
                    ((TextView) findViewById(R.id.hoteladdr)).setText(jobj.getString("hotel_address"));
                    ((TextView) findViewById(R.id.hotelmob)).setText(jobj.getString("phone_number"));
                    ((TextView) findViewById(R.id.postcode)).setText(jobj.getString("postal_code"));
                    ((TextView) findViewById(R.id.nametv)).setText(jobj.getString("contact_fname")+" "+jobj.getString("contact_lname"));
                    ((TextView) findViewById(R.id.mobiletv)).setText(jobj.getString("contact_mobile_number"));
                    ((TextView) findViewById(R.id.emailtv)).setText(jobj.getString("contact_email"));

                    ((TextView) findViewById(R.id.roomtype)).setText(jobj.getString("booking_room_type"));
                    ((TextView) findViewById(R.id.cindate)).setText(Utils.ChangeDateFormat(jobj.getString("check_in_date"),1));
                    ((TextView) findViewById(R.id.coutdate)).setText(Utils.ChangeDateFormat(jobj.getString("check_out_date"),1));

                    ((TextView) findViewById(R.id.cintime)).setText(jobj.getString("exp_checkin_time"));
                    ((TextView) findViewById(R.id.roomstv)).setText(jobj.getString("no_of_rooms"));
                    ((TextView) findViewById(R.id.gueststv)).setText(jobj.getString("guest_count"));
                    ((TextView) findViewById(R.id.nightstv)).setText(jobj.getString("no_of_nights"));
                    ((TextView) findViewById(R.id.ebedstv)).setText(jobj.getString("extra_bed_count"));
                    ((TextView) findViewById(R.id.pricetv)).setText("₹ "+jobj.getString("TotalPrice"));

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(Hotel_Voucher.this, "Technical Error Occurred Try Again", "Ok", true);
                }
            }
        });
    }

}
