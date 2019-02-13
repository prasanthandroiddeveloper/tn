package com.tripnetra.tripnetra.payment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
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

public class PayTM_Payment extends Fragment {

    Map<String, String> PaytmParams;
    String ORDER_ID,CUST_ID,TXN_AMOUNT,MOBILE_NO,EMAIL,Paytm_CSum,BookAct;
    Activity activity;

    public void getPaytmchecksum(Activity actt,Bundle bb){
        activity = actt;

        G_Class gcv = (G_Class) activity.getApplicationContext();
        ORDER_ID = bb.getString("ORDER_ID");
        EMAIL = bb.getString("EMAIL");
        TXN_AMOUNT = bb.getString("TXN_AMOUNT");
        CUST_ID = bb.getString("CUST_ID");
        MOBILE_NO = bb.getString("MOBILE_NO");
        BookAct = bb.getString("BOOK_ACT");

        PaytmParams = new HashMap<>();
        PaytmParams.put("MID", gcv.getPaytmMid());
        PaytmParams.put("ORDER_ID", ORDER_ID);
        PaytmParams.put("CUST_ID", CUST_ID);
        PaytmParams.put("INDUSTRY_TYPE_ID", "Retail109");
        PaytmParams.put("CHANNEL_ID", "WAP");
        PaytmParams.put("TXN_AMOUNT", TXN_AMOUNT);
        PaytmParams.put("WEBSITE","NaeTecWAP");
        PaytmParams.put("MOBILE_NO",MOBILE_NO);
        PaytmParams.put("EMAIL",EMAIL);
        PaytmParams.put("CALLBACK_URL" ,Config.PAYTM_CALLBACK_URL+ORDER_ID);

        new VolleyRequester(activity).ParamsRequest(1, gcv.getBaseurl()+Config.PAYTM_CSUM_URL, null, PaytmParams, false, response -> {
            try {
                Paytm_CSum = new JSONObject(response).getString("CHECKSUMHASH");
                startPayTM_transctn();
            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(activity,getResources().getString(R.string.something_wrong),"Ok",true);
            }
        });

    }

    public void startPayTM_transctn() {

        PaytmPGService Service = PaytmPGService.getProductionService();
        //PaytmPGService Service = PaytmPGService.getStagingService();

        PaytmParams.put("CHECKSUMHASH" , Paytm_CSum);

        //PaytmOrder Order = new PaytmOrder(PaytmParams);

        Service.initialize(new PaytmOrder(PaytmParams), null);

        Service.startPaymentTransaction(activity, true, true, new PaytmPaymentTransactionCallback() {

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                Utils.setSingleBtnAlert(activity,"Error Occured While Payment","Ok",true);
            }

            @Override
            public void onTransactionResponse(Bundle bb) {
                //Log.d("LOG", "Payment_Act Transaction : " + bb);
                //Toast.makeText(getApplicationContext(), "Car_Payment_Act Transaction response "+bb.toString(), Toast.LENGTH_LONG).show();

                if(Objects.equals(BookAct, "Hotel")) {
                    ((Hotel_Payment_Act)activity).posttransaction(bb);
                }else if(Objects.equals(BookAct, "Car")) {
                    ((Car_Payment_Act)activity).posttransaction(bb);
                }else if(Objects.equals(BookAct, "Tour")) {
                    ((Tour_Payment_Act)activity).posttransaction(bb);
                }
            }

            @Override
            public void networkNotAvailable() {
                Utils.setSingleBtnAlert(activity,"Please Check Your Internet Connection","Ok",true);
            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                Utils.setSingleBtnAlert(activity,"Authentication Failed","Ok",true);
            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode,String inErrorMessage, String inFailingUrl) {
                Utils.setSingleBtnAlert(activity,"Error Loading Requested WebPage","Ok",true);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                //Toast.makeText(activity,"post Transact",Toast.LENGTH_SHORT).show();
                Utils.setSingleBtnAlert(activity,"Payment Transaction Failed","Ok",true);
                /*Bundle bundl = new Bundle();
                bundl.putString("STATUS", "TXN_SUCCESS");
                bundl.putString("RESPCODE", "01");
                bundl.putString("TXNAMOUNT", TXN_AMOUNT);
                bundl.putString("TXNDATE", "2018-03-06 10:05:59");

                if(Objects.equals(BookAct, "Hotel")) {
                    ((Hotel_Payment_Act)activity).posttransaction(bundl);
                }else if(Objects.equals(BookAct, "Car")) {
                    ((Car_Payment_Act)activity).posttransaction(bundl);
                }else if(Objects.equals(BookAct, "Tour")) {
                    ((Tour_Payment_Act)activity).posttransaction(bundl);
                }*/

            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                //Log.d("LOG", " Payment_Act Transaction Failed " + inErrorMessage);
                Utils.setSingleBtnAlert(activity," Payment Transaction Failed Due to"+inErrorMessage,"Ok",true);
                /*Bundle bundl = new Bundle();
                bundl.putString("STATUS", "TXN_SUCCESS");
                bundl.putString("RESPCODE", "01");
                bundl.putString("TXNAMOUNT", TXN_AMOUNT);
                bundl.putString("TXNDATE", "2017-12-11 10:05:59");
                posttransaction(bundl);*/
            }

        });
    }

}