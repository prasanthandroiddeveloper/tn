package com.tripnetra.tripnetra.payment;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.hotels.Hotel_Voucher_Act;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class Hotel_Payment_Act extends AppCompatActivity {

    Bundle Mbundle;
    Button Btncheckout,PayuBtn,PaytmBtn,PayPalBtn;
    String ORDER_ID=Utils.orderid(),CUST_ID,MOBILE_NO,EMAIL,TXN_AMOUNT,FName,LName,Paymode="PAYU_A";
    LoadingDialog loadDlg;
    G_Class g_class;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_payment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Payu.setInstance(this);

        Mbundle = getIntent().getExtras();
        assert Mbundle!= null;
        FName = Mbundle.getString("FName");
        LName = Mbundle.getString("LName");
        EMAIL = Mbundle.getString("Email");
        TXN_AMOUNT = Mbundle.getString("TotalPrice");
        MOBILE_NO = Mbundle.getString("Phoneno");

        CUST_ID = (FName+LName).trim().replaceAll(" ","");

        g_class = (G_Class) getApplicationContext();
        g_class.setPayType("hotel");

        PayuBtn = findViewById(R.id.payuBtn);
        PaytmBtn = findViewById(R.id.paytmBtn);
        PayPalBtn = findViewById(R.id.paypalBtn);
        Btncheckout = findViewById(R.id.checkoutBtn);
        Btncheckout.setEnabled(false);
        PaytmBtn.setBackgroundResource(R.drawable.boxfill_radius);
        PayPalBtn.setBackgroundResource(R.drawable.boxfill_radius);
        PayuBtn.setBackgroundResource(R.drawable.select);

        ((TextView)findViewById(R.id.nametv)).setText(FName+" "+LName);
        ((TextView)findViewById(R.id.emailtv)).setText(EMAIL);
        ((TextView)findViewById(R.id.bookdate)).setText(Mbundle.getString("Cindisp"));
        ((TextView)findViewById(R.id.totalamount)).setText("₹ "+TXN_AMOUNT);
        ((TextView)findViewById(R.id.nightstv)).setText(Mbundle.getString("NoofNights"));
        ((TextView)findViewById(R.id.cindate)).setText(Mbundle.getString("Cindisp"));
        ((TextView)findViewById(R.id.coutdate)).setText(Mbundle.getString("Coutdisp"));
        ((TextView)findViewById(R.id.roomstv)).setText(Mbundle.getString("NoofRooms"));
        ((TextView)findViewById(R.id.roomtype)).setText(Mbundle.getString("Roomname"));
        ((TextView)findViewById(R.id.gueststv)).setText(""+Mbundle.getString("Adults")+" Adults & "+Mbundle.getString("Childs")+" Childs");

        loadDlg = new LoadingDialog(this);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadDlg.show();

        sendprocessdata();
        Btncheckout.setEnabled(true);//todo

    }

    private void sendprocessdata() {

        Map<String, String> params = new HashMap<>();
        params.put("pnr_no", ORDER_ID);
        params.put("total_amount", TXN_AMOUNT);
        params.put("billing_email", EMAIL);
        params.put("billing_first_name", FName);
        params.put("billing_last_name", LName);
        params.put("billing_contact_number", MOBILE_NO);
        params.put("booking_status", "PROCESS");
        params.put("payment_status", "PROCESS");
        params.put("room_count", Mbundle.getString("NoofRooms"));
        params.put("night_count", Mbundle.getString("NoofNights"));
        params.put("adult_count", Mbundle.getString("Adults"));
        params.put("child_count", Mbundle.getString("Childs"));
        params.put("hotel_checkin", Mbundle.getString("Cindate"));
        params.put("hotel_checkout", Mbundle.getString("Coutdate"));
        params.put("room_type_name", Mbundle.getString("Rname"));
        params.put("hotel_details_id", Mbundle.getString("Hid"));
        params.put("hotel_name", Mbundle.getString("Hname"));
        params.put("hotel_room_type_id", Mbundle.getString("Rid"));
        params.put("hotel_room_rate_info_id", Mbundle.getString("RRateid"));
        params.put("expected_checkin", Mbundle.getString("Expectedtime"));
        params.put("sgl_extrabed_count", Mbundle.getString("Extrabed"));
        params.put("sgl_price", Mbundle.getString("Supplierprice"));
        params.put("gst_price", Mbundle.getString("Gst_price"));
        params.put("commission", Mbundle.getString("Commission"));
        params.put("gstin_status", Mbundle.getString("Gst_in_status"));
        params.put("userid", Mbundle.getString("userid"));

        new VolleyRequester(this).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL + "hotel_pay_process.php", loadDlg, params, true, response -> {
            loadDlg.dismiss();
            switch (response) {
                case "saved":
                    Btncheckout.setEnabled(true);
                    break;
                default:
                    Utils.setSingleBtnAlert(Hotel_Payment_Act.this, getResources().getString(R.string.something_wrong), "Ok", true);
                    break;
            }
        });
    }

    public void ppayu(View v){
        Paymode="PAYU_A";
        PaytmBtn.setBackgroundResource(R.drawable.boxfill_radius);
        PayPalBtn.setBackgroundResource(R.drawable.boxfill_radius);
        PayuBtn.setBackgroundResource(R.drawable.select);
    }

    public void ppaytm(View v){
        Paymode="PAYTM_A";
        PaytmBtn.setBackgroundResource(R.drawable.select);
        PayuBtn.setBackgroundResource(R.drawable.boxfill_radius);
        PayPalBtn.setBackgroundResource(R.drawable.boxfill_radius);
    }

    public void ppaypal(View v){
        Paymode="PAYPAl_A";
        PayPalBtn.setBackgroundResource(R.drawable.select);
        PaytmBtn.setBackgroundResource(R.drawable.boxfill_radius);
        PayuBtn.setBackgroundResource(R.drawable.boxfill_radius);
    }

    public void checkoutm(View v){

        if(Btncheckout.isEnabled()) {

            Btncheckout.setEnabled(false);

            Mbundle.putString("TXNAMOUNT", TXN_AMOUNT);
            Mbundle.putString("Paymode", Paymode);
            Mbundle.putString("ORDER_ID", ORDER_ID);

            if (Objects.equals(Paymode, "PAYPAl_A")) {
                g_class.setBundle(Mbundle);
                initpaypalmthd();
                return;
            }

            Bundle bb = new Bundle();
            bb.putString("TXN_AMOUNT", TXN_AMOUNT);
            bb.putString("CUST_ID", CUST_ID);
            bb.putString("EMAIL", EMAIL);
            bb.putString("MOBILE_NO", MOBILE_NO);
            bb.putString("ORDER_ID", ORDER_ID);
            bb.putString("BOOK_ACT", "Hotel");

            /*if (Objects.equals(Paymode, "PAYTM_A")) {
                new PayTM_Payment().getPaytmchecksum(this, bb);
            } else {
                new PayU_Payment().payuparse_data(this, bb);
            }*///todo

            Bundle bundl = new Bundle();
            bundl.putString("STATUS", "TXN_SUCCESS");
            bundl.putString("RESPCODE", "01");
            bundl.putString("TXNAMOUNT", TXN_AMOUNT);
            bundl.putString("TXNDATE", "2018-10-23 10:00:00");

            posttransaction(bundl);

        }else{
            Toast.makeText(this,"Error Processing Details\n\nPlease Check The Details Entered",Toast.LENGTH_LONG).show();
        }
    }

    private void initpaypalmthd() {
        try {

            String link_url = "https://tripnetra.com/mamatha/tours/android_paypal/"+ORDER_ID+"/"+TXN_AMOUNT;

            String packageName = "com.android.chrome";
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            customTabsIntent.intent.setData(Uri.parse(link_url));
            startActivityForResult(customTabsIntent.intent, 566);

        } catch(ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),"Chrome Browser Not Installed",Toast.LENGTH_LONG).show();
        }
    }

    public void posttransaction(Bundle bb){

        Mbundle.putString("STATUS", bb.getString("STATUS"));
        Mbundle.putString("RESPCODE", bb.getString("RESPCODE"));
        Mbundle.putString("TXNDATE", bb.getString("TXNDATE"));

        startActivity(new Intent(this, Hotel_Voucher_Act.class).putExtras(Mbundle));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {
              //  Log.d("PAYUDATA","Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"));
                try {
                    JSONObject jobj = new JSONObject(data.getStringExtra("payu_response"));
                    String stat = jobj.getString("status");
                    Bundle bundl = new Bundle();
                    if(Objects.equals(stat, "success")) {
                        bundl.putString("STATUS", "TXN_SUCCESS");
                        bundl.putString("RESPCODE", "01");
                    }else{
                        bundl.putString("STATUS", "TXN_FAILURE");
                        bundl.putString("RESPCODE", "02");
                    }
                    bundl.putString("TXNAMOUNT", TXN_AMOUNT);
                    bundl.putString("TXNDATE", jobj.getString("addedon"));
                    posttransaction(bundl);
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(this,"Technical Error Occurred...","Ok",true);
                }
            } else {
                Utils.setSingleBtnAlert(this,"Payment Transaction Failed","Ok",true);
            }
        }
    }

}