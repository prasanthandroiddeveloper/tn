package com.tripnetra.tripnetra.payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.payu.india.Payu.PayuConstants;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.cars.Car_Voucher_Act;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class Car_Payment_Act extends AppCompatActivity {

    Bundle Mbundle;
    Button Btncheckout,PayuBtn,PaytmBtn;
    String CUST_ID,MOBILE_NO,EMAIL,TXN_AMOUNT,FName,LName,Paymode="PAYU_A",ORDER_ID = Utils.orderid();
    LoadingDialog loadDlg;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car_payment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        assert getIntent().getExtras()!= null;
        Mbundle = getIntent().getExtras();
        FName = Mbundle.getString("FName");
        LName = Mbundle.getString("LName");
        EMAIL = Mbundle.getString("EMail");
        MOBILE_NO = Mbundle.getString("Mobile");
        TXN_AMOUNT = Mbundle.getString("TotalPrice");

        CUST_ID = (FName+LName).trim().replaceAll(" ","");

        PayuBtn = findViewById(R.id.payuBtn);
        PaytmBtn = findViewById(R.id.paytmBtn);
        Btncheckout = findViewById(R.id.checkoutBtn);
        Btncheckout.setEnabled(false);
        PaytmBtn.setBackgroundResource(R.drawable.boxfill_radius);
        PayuBtn.setBackgroundResource(R.drawable.select);

        ((TextView)findViewById(R.id.nametv)).setText(FName+" "+LName);
        ((TextView)findViewById(R.id.emailtv)).setText(EMAIL);
        ((TextView)findViewById(R.id.totalamount)).setText("₹ "+TXN_AMOUNT);
        ((TextView)findViewById(R.id.bookdate)).setText(Mbundle.getString("Dispdate"));
        ((TextView)findViewById(R.id.fromplacetv)).setText(Mbundle.getString("PickLoc"));
        ((TextView)findViewById(R.id.toplacetv)).setText(Mbundle.getString("DropLoc"));
        ((TextView)findViewById(R.id.carnameTv)).setText(Mbundle.getString("CarName"));
        ((TextView)findViewById(R.id.cartypeTv)).setText(Mbundle.getString("CarType"));
        ((TextView)findViewById(R.id.picktime)).setText(Mbundle.getString("Jtime"));

        loadDlg = new LoadingDialog(this);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadDlg.show();

        sendprocessdata();
    }

    private void sendprocessdata() {

        Map<String, String> params = new HashMap<>();
        params.put("pnrno",ORDER_ID);
        params.put("cardetid",Mbundle.getString("CarDetid"));
        params.put("pickdate",Mbundle.getString("JDate"));
        params.put("fromloc",Mbundle.getString("PickLoc"));
        params.put("toloc",Mbundle.getString("DropLoc"));
        params.put("picktime",Mbundle.getString("Jtime"));
        params.put("fname",FName);
        params.put("lname",LName);
        params.put("email",EMAIL);
        params.put("mobile",MOBILE_NO);
        params.put("cartype",Mbundle.getString("CarType"));
        params.put("carsupid",Mbundle.getString("Suppid"));
        params.put("carfeature",Mbundle.getString("CarFeatures"));
        params.put("caroptions","Manual");
        params.put("carcompany",Mbundle.getString("CarCompany"));
        params.put("paytype",Paymode);
        params.put("totalamount",TXN_AMOUNT);
        params.put("userid",Mbundle.getString("userid"));
        params.put("carname",Mbundle.getString("CarName"));

        String sss = Mbundle.getString("SmallDes");
        assert sss != null;
        params.put("capacity",String.valueOf(sss.charAt(11)));
        params.put("doors",String.valueOf(sss.charAt(1)));
        params.put("airbag",String.valueOf(sss.charAt(21)));
        params.put("bookstatus","PROCESS");
        params.put("paystatus","PROCESS");

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIPCAR_URL+"car_pay_process.php", loadDlg, params, true, response -> {
            loadDlg.dismiss();
            switch (response) {
                case "saved":
                    Btncheckout.setEnabled(true);
                    break;
                default:
                    Utils.setSingleBtnAlert(Car_Payment_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
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

        Btncheckout.setEnabled(false);
        Bundle bb = new Bundle();
        bb.putString("TXN_AMOUNT",TXN_AMOUNT);
        bb.putString("CUST_ID",CUST_ID);
        bb.putString("EMAIL",EMAIL);
        bb.putString("MOBILE_NO",MOBILE_NO);
        bb.putString("ORDER_ID",ORDER_ID);
        bb.putString("BOOK_ACT","Car");

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

        startActivity(new Intent(this, Car_Voucher_Act.class).putExtras(Mbundle));
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