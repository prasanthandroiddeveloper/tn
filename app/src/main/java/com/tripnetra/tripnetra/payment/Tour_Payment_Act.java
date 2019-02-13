package com.tripnetra.tripnetra.payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.payu.india.Payu.PayuConstants;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.tours.Tour_Voucher_Act;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Tour_Payment_Act extends AppCompatActivity {

    String FName,LName,CUST_ID,EMAIL,MOBILE_NO,TXN_AMOUNT,CarId,TourId,TourName,TDest,T_CatID,ActPrice,PartPrice,TourDate,Paymode="PAYU_A",ORDER_ID=Utils.orderid();
    Bundle Mbundle;
    Button CheckoutBtn,PayuBtn,PaytmBtn;
    int BalPrice;
    LoadingDialog loadDlg;
    G_Class g_class;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_payment);

        PayuBtn = findViewById(R.id.payuBtn);
        PaytmBtn = findViewById(R.id.paytmBtn);
        PaytmBtn.setBackgroundResource(R.drawable.boxfill_radius);
        PayuBtn.setBackgroundResource(R.drawable.select);

        assert getIntent().getExtras()!= null;
        Mbundle = getIntent().getExtras();

        g_class = (G_Class) getApplicationContext();

        CheckoutBtn = findViewById(R.id.checkoutBtn);

        FName = Mbundle.getString("fname");
        LName = Mbundle.getString("lname");
        EMAIL = Mbundle.getString("email");
        MOBILE_NO = Mbundle.getString("mobile");
        TXN_AMOUNT = Mbundle.getString("totalprice");
        CarId = Mbundle.getString("carid");
        TourId = Mbundle.getString("tourid");
        TourName = Mbundle.getString("tourname");
        TDest = Mbundle.getString("tourdest");
        T_CatID = Mbundle.getString("tourcat_id");
        ActPrice = Mbundle.getString("actprice");
        TourDate = Mbundle.getString("jourdate");
        PartPrice = Mbundle.getString("partprice");

        CheckoutBtn.setText("CheckOut To Pay ₹ "+TXN_AMOUNT);
        CheckoutBtn.setEnabled(false);

        ((TextView)findViewById(R.id.nametv)).setText(FName+" "+LName);
        ((TextView)findViewById(R.id.emailtv)).setText(EMAIL);
        ((TextView)findViewById(R.id.tnameTv)).setText(TourName);
        ((TextView)findViewById(R.id.TdateTv)).setText(Utils.ChangeDateFormat(TourDate,1));

        LinearLayout PartLayt = findViewById(R.id.partLayt);
        if(Objects.equals(Mbundle.getString("tourtype"), "pooja")){
            PartLayt.setVisibility(View.GONE);
            BalPrice = Integer.parseInt(TXN_AMOUNT);
            TextView namtv = findViewById(R.id.balNameTv);
            namtv.setText("Total Price");
        }else{
            ((TextView)findViewById(R.id.actpriceTv)).setText("₹ "+ActPrice);
            ((TextView)findViewById(R.id.partpriceTv)).setText("₹ "+PartPrice);
            BalPrice = Integer.parseInt(ActPrice) - Integer.parseInt(PartPrice);
        }

        ((TextView)findViewById(R.id.balamountTv)).setText("₹ "+BalPrice);

        CUST_ID = (FName+LName).trim().replaceAll(" ","");

        loadDlg = new LoadingDialog(this);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadDlg.show();
        sendprocessdata();

    }

    private void sendprocessdata() {

        Map<String, String> params = new HashMap<>();
        params.put("pnr_no", ORDER_ID);
        params.put("travel_date", TourDate);
        params.put("cityname", Mbundle.getString("cityname"));
        params.put("fname", FName);
        params.put("lname", LName);
        params.put("email", EMAIL);
        params.put("mobile", MOBILE_NO);
        params.put("sightseen_name", TourName);
        params.put("bal_price", String.valueOf(BalPrice));
        params.put("part_price", TXN_AMOUNT);
        params.put("sightseen_id", TourId);
        params.put("type", "PROCESS");
        params.put("userid", Mbundle.getString("userid"));

        new VolleyRequester(this).ParamsRequest(1, g_class.getBaseurl()+Config.TOUR_PAYMENT_URL, loadDlg, params, true, response -> {
            loadDlg.dismiss();
            switch (response) {
                case "Saved":
                    CheckoutBtn.setEnabled(true);
                    break;
                default:
                    Utils.setSingleBtnAlert(Tour_Payment_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
                    break;
            }
        });
    }

    public void ppayu(View v){
        Paymode="PAYU_A";
        PaytmBtn.setBackgroundResource(R.drawable.boxfill_radius);
        PayuBtn.setBackgroundResource(R.drawable.select);
    }

    public void ppaytm(View v){
        Paymode="PAYTM_A";
        PaytmBtn.setBackgroundResource(R.drawable.select);
        PayuBtn.setBackgroundResource(R.drawable.boxfill_radius);
    }

    public void checkoutm(View v){

        CheckoutBtn.setEnabled(false);
        Bundle bb = new Bundle();
        bb.putString("TXN_AMOUNT",TXN_AMOUNT);
        bb.putString("CUST_ID",CUST_ID);
        bb.putString("EMAIL",EMAIL);
        bb.putString("MOBILE_NO",MOBILE_NO);
        bb.putString("ORDER_ID",ORDER_ID);
        bb.putString("BOOK_ACT","Tour");

        /*if(Objects.equals(Paymode, "PAYTM_A")){
            new PayTM_Payment().getPaytmchecksum(this,bb);
        }else{
            new PayU_Payment().payuparse_data(this,bb);
        }*///todo

        Bundle bundl = new Bundle();
        bundl.putString("STATUS", "TXN_SUCCESS");
        bundl.putString("RESPCODE", "01");
        bundl.putString("TXNAMOUNT", TXN_AMOUNT);
        bundl.putString("TXNDATE", "2018-10-23 10:00:00");

        posttransaction(bundl);
    }

    public void posttransaction(Bundle bb) {

        Mbundle.putString("TXNAMOUNT", bb.getString("TXNAMOUNT"));
        Mbundle.putString("STATUS", bb.getString("STATUS"));
        Mbundle.putString("RESPCODE", bb.getString("RESPCODE"));
        Mbundle.putString("Paymode", Paymode);
        Mbundle.putString("TXNDATE", bb.getString("TXNDATE"));
        Mbundle.putString("ORDER_ID", ORDER_ID);
        Mbundle.putString("balprice", String.valueOf(BalPrice));

        Intent intent = new Intent(Tour_Payment_Act.this, Tour_Voucher_Act.class);
        intent.putExtras(Mbundle);
        startActivity(intent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {
                //Log.d("PAYUDATA","Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"));
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