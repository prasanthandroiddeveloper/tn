package com.tripnetra.tripnetra.payment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.rest.VolleyCallback;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PayU_Payment extends Fragment {

    PayuConfig payuConfig;
    PaymentParams PayuParams;
    String ORDER_ID,EMAIL,TXN_AMOUNT,CUST_ID,product_info,MOBILE_NO,BookAct;
    Activity activity;
    G_Class gcv ;

    public void payuparse_data(Activity actt,Bundle bb){
        activity = actt;
        gcv = (G_Class) activity.getApplicationContext();
        ORDER_ID = bb.getString("ORDER_ID");
        EMAIL = bb.getString("EMAIL");
        TXN_AMOUNT = bb.getString("TXN_AMOUNT");
        CUST_ID = bb.getString("CUST_ID");
        MOBILE_NO = bb.getString("MOBILE_NO");
        BookAct = bb.getString("BOOK_ACT");

        //int environment = PayuConstants.STAGING_ENV;// Change MID
        int environment = PayuConstants.PRODUCTION_ENV;

        product_info = "Payment For "+ORDER_ID+" Booking";

        PayuParams = new PaymentParams();

        PayuParams.setKey(gcv.getPayuMid());
        PayuParams.setAmount(TXN_AMOUNT);
        PayuParams.setProductInfo(product_info);
        PayuParams.setFirstName(CUST_ID);
        PayuParams.setEmail(EMAIL);
        PayuParams.setPhone(MOBILE_NO);
        PayuParams.setTxnId(ORDER_ID);

        PayuParams.setSurl(Config.PAYU_SURL);
        PayuParams.setFurl(Config.PAYU_FURL);
        PayuParams.setNotifyURL(Config.PAYU_SURL);

        PayuParams.setUdf1("");PayuParams.setUdf2("");PayuParams.setUdf3("");PayuParams.setUdf4("");PayuParams.setUdf5("");

        PayuParams.setUserCredentials(gcv.getPayuMid() + ":" + EMAIL);

        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(environment);

        startPayU_transctn();

    }

    public void startPayU_transctn(){

        Map<String, String> params = new HashMap<>();
        params.put("txnid", ORDER_ID);
        params.put("user_credentials",gcv.getPayuMid()+":"+EMAIL);
        params.put("amount", TXN_AMOUNT);
        params.put("firstname", CUST_ID);
        params.put("email",EMAIL);
        params.put("productinfo",product_info);

        new VolleyRequester(activity).ParamsRequest(1, gcv.getBaseurl()+Config.PAYU_HASH_URL, null, params, false, new VolleyCallback() {
            @Override
            public void getResponse(String response) {
                try {
                    PayuHashes payuHashes = new PayuHashes();
                    payuHashes.setPaymentHash(new JSONObject(response).getString("payment_hash"));
                    payuHashes.setMerchantIbiboCodesHash(new JSONObject(response).getString("get_merchant_ibibo_codes_hash"));
                    payuHashes.setVasForMobileSdkHash(new JSONObject(response).getString("vas_for_mobile_sdk_hash"));
                    payuHashes.setPaymentRelatedDetailsForMobileSdkHash(new JSONObject(response).getString("payment_related_details_for_mobile_sdk_hash"));

                    Intent intent = new Intent(activity, PayUBaseActivity.class);
                    intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                    intent.putExtra(PayuConstants.PAYMENT_PARAMS, PayuParams);
                    intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);

                    activity.startActivityForResult(intent,PayuConstants.PAYU_REQUEST_CODE);

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(activity,"Technical Error Occurred","Ok",true);
                }
            }
        });
    }

}