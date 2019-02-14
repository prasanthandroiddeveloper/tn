package com.tripnetra.tripnetra.hotels;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.SharedPrefs;
import com.tripnetra.tripnetra.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Hotel_Voucher_Act extends AppCompatActivity {

    String STATUS,RESPCODE,HotelPhone,Rname,Cindate,Coutdate,EMAIL,ORDER_ID,Paymode,NoofRooms,
            NoofNights,Rid,Hid,Commission,TcashUsed,RetTcash,CoupDisc,CoupId,CoupType,UserID,xy;
    TextView BookstatTV,HmobileTV;
    LoadingDialog loadDlg;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_voucher);
        if(getIntent().getExtras()!= null) {

            loadDlg = new LoadingDialog(this);
            loadDlg.setCancelable(false);
            Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loadDlg.show();


            Bundle bb = getIntent().getExtras();

            EMAIL = bb.getString("Email");
            Cindate = bb.getString("Cindate");
            Coutdate = bb.getString("Coutdate");
            NoofRooms = bb.getString("NoofRooms");
            Hid = bb.getString("Hid");
            Rid = bb.getString("Rid");
            Rname = bb.getString("Rname");
            RESPCODE = bb.getString("RESPCODE");
            STATUS = bb.getString("STATUS");
            ORDER_ID = bb.getString("ORDER_ID");
            HotelPhone = bb.getString("HotelPhone");
            Paymode = bb.getString("Paymode");
            NoofNights = bb.getString("NoofNights");
            Commission = bb.getString("Commission");

            TcashUsed = bb.getString("tripcashused");
            RetTcash = bb.getString("returntripcash");
            CoupDisc = bb.getString("coupondiscount");
            CoupId = bb.getString("couponid");
            CoupType = bb.getString("coupontype");

            HmobileTV = findViewById(R.id.hotelmob);
            BookstatTV = findViewById(R.id.bookstatus);

            ((TextView) findViewById(R.id.bookid)).setText(ORDER_ID);
            ((TextView) findViewById(R.id.roomstv)).setText(NoofRooms);
            ((TextView) findViewById(R.id.mobiletv)).setText(bb.getString("Phoneno"));
            ((TextView) findViewById(R.id.emailtv)).setText(EMAIL);
            ((TextView) findViewById(R.id.roomtype)).setText(Rname);
            ((TextView) findViewById(R.id.cindate)).setText(Cindate);
            ((TextView) findViewById(R.id.coutdate)).setText(Coutdate);
            ((TextView) findViewById(R.id.hotelname)).setText(bb.getString("Hname"));
            ((TextView) findViewById(R.id.hoteladdr)).setText(bb.getString("HotelAddr"));
            ((TextView) findViewById(R.id.postcode)).setText(bb.getString("postcode"));
            ((TextView) findViewById(R.id.nametv)).setText(bb.getString("FName")+" "+bb.getString("LName"));
            ((TextView) findViewById(R.id.booktime)).setText(bb.getString("TXNDATE"));
            ((TextView) findViewById(R.id.cintime)).setText(bb.getString("cin24"));
            ((TextView) findViewById(R.id.gueststv)).setText(bb.getString("Adults")+" Guests \n("+bb.getString("Adults")+" Adults & "+bb.getString("Childs")+" Childs)");
            ((TextView) findViewById(R.id.nightstv)).setText(NoofNights);
            ((TextView) findViewById(R.id.ebedstv)).setText(bb.getString("Extrabed"));
            ((TextView) findViewById(R.id.pricetv)).setText(bb.getString("TXNAMOUNT"));
            HmobileTV.setText("Not Available");

            UserID = new SharedPrefs(this).getUserId();
            if(UserID.equals("")){ UserID = "0"; }
            sendmail();
        }
    }


    /*private void getid(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,"https://tripnetra.com/prasanth/androidphpfiles/extranet/get.php" , response -> {

            try {
                JSONArray jarr = new JSONArray(response);
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject json = jarr.getJSONObject(i);

                     xy=json.getString("id");
                    Log.i("id", String.valueOf(xy));



                }} catch (JSONException e) {
                e.printStackTrace();
            }


        },
                error -> {

                    Toast.makeText(Hotel_Voucher_Act.this, error.getMessage(),Toast.LENGTH_SHORT).show();
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pnr_no", ORDER_ID);
                return params;

            }

        };

        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(Hotel_Voucher_Act.this));
        requestQueue.add(stringRequest);
    }*/

    private void sendmail() {

        Map<String, String> params = new HashMap<>();
        params.put("pnr_no", ORDER_ID);
        if(Objects.equals(STATUS, "TXN_SUCCESS") && Objects.equals(RESPCODE, "01")) {
            params.put("booking_status", "CONFIRM");
            params.put("payment_status", "CONFIRM");
        }else{
            params.put("booking_status", "CANCELLED");
            params.put("payment_status", "CANCELLED");
        }
        params.put("payment_type",Paymode);
        params.put("room_count", NoofRooms);
        params.put("night_count", NoofNights);
        params.put("commission", Commission);
        params.put("hotel_checkin", Cindate);
        params.put("hotel_checkout", Coutdate);
        params.put("room_type_name", Rname);
        params.put("hotel_room_type_id", Rid);
        params.put("hotel_details_id", Hid);

        params.put("tripcashused", TcashUsed);
        params.put("returntripcash", RetTcash);
        params.put("coupondiscount", CoupDisc);
        params.put("couponid", CoupId);
        params.put("coupontype", CoupType);
        params.put("userid", UserID);
       // params.put("id",xy);

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL+"hotelpayment.php", loadDlg, params, false, response -> {

            if(loadDlg.isShowing()){loadDlg.dismiss();}

            BookstatTV.setText(R.string.cancelled);
            BookstatTV.setTextColor(RED);



            String resp=response;
            Log.i("resp",resp);
            Toast.makeText(this,"Response" +resp,Toast.LENGTH_LONG).show();
            //getid();

               switch (response) {
                case "MAILSENT":
                    if(Objects.equals(STATUS, "TXN_SUCCESS") && Objects.equals(RESPCODE, "01")) {
                        BookstatTV.setText(R.string.confirmed);
                        BookstatTV.setTextColor(GREEN);
                        HmobileTV.setText(HotelPhone);

                        Utils.SendNotification(Hotel_Voucher_Act.this,Rname+" Booked on "+Cindate);
                        Utils.setSingleBtnAlert(Hotel_Voucher_Act.this,"Booking Confirmed \nVoucher Sent to " + EMAIL + " \nThanks for Booking With Us","Ok",false);
                        break;
                    }
                default:
                    Utils.setSingleBtnAlert(Hotel_Voucher_Act.this,"Hotel Not Booked \nPlease Book Again","Ok",false);
                    break;
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, Hotel_Main_Act.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}