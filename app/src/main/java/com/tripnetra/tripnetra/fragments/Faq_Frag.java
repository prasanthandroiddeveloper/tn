package com.tripnetra.tripnetra.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Faq_Frag extends Fragment {

    public Faq_Frag() {}

    EditText MailEt, TripEt;
    Activity activity;
    String Email, PnrNo, MainURL, PnrID, Voutype;
    WebView VouWebview;
    View Mainview;
    LoadingDialog loadDlg;
    G_Class g_class;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Mainview = inflater.inflate(R.layout.fragment_faq, container, false);

        activity = getActivity();

        g_class = (G_Class) activity.getApplicationContext();

        loadDlg = new LoadingDialog(activity);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MailEt = Mainview.findViewById(R.id.emailEt);
        TripEt = Mainview.findViewById(R.id.tripEt);
        VouWebview = Mainview.findViewById(R.id.webview);

        Mainview.findViewById(R.id.submit).setOnClickListener(v -> verifydata());

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return Mainview;

    }

    private void verifydata() {

        Email = MailEt.getText().toString().trim();
        PnrNo = TripEt.getText().toString().trim();

        if (Objects.equals(Email, "") || Utils.isEmailValid(Email)) {
            String ss = (Objects.equals(Email, "")) ? "Enter Email" : "Enter Valid Email";
            Toast.makeText(activity, ss, Toast.LENGTH_SHORT).show();
        } else if (Objects.equals(PnrNo, "") || !PnrNo.contains("TRIP")) {
            String ss = (Objects.equals(PnrNo, "")) ? "Enter Trip ID" : "Enter Valid Trip ID";
            Toast.makeText(activity, ss, Toast.LENGTH_SHORT).show();
        } else {

            loadDlg.show();

            Map<String, String> params = new HashMap<>();
            params.put("tripid", PnrNo);
            params.put("email", Email);
            params.put("type", "view");

            new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL + "account/getbooking.php", loadDlg, params, false, response -> {

                try {
                    JSONObject jobj = new JSONObject(response);

                    Voutype = jobj.getString("type");//hotel car tour none

                    if (Voutype.equals("none")) {
                        if (loadDlg.isShowing()) { loadDlg.dismiss(); }
                        Utils.setSingleBtnAlert(activity, "Enter Valid Booking Details", "Ok", false);
                        return;
                    }

                    MainURL = jobj.getString("url");
                    PnrID = jobj.getJSONObject("info").getString("id");

                    TextView CancelTv = Mainview.findViewById(R.id.cancelTv);

                    Date cindate, currentdate = Calendar.getInstance().getTime();


                    if (Voutype.equals("car")) {
                        cindate = Utils.StrtoDate(1,jobj.getJSONObject("info").getString("in_date"));
                    } else {
                        cindate = Utils.StrtoDate(0,jobj.getJSONObject("info").getString("in_date"));
                    }


                    String HTId = Voutype.equals("hotel") ? jobj.getJSONObject("info").getString("hotel_type_id") : "";

                    if (cindate == null || cindate.compareTo(currentdate) <= 0 || HTId.equals("4")) {
                        CancelTv.setVisibility(View.GONE);
                    }

                    getvoudata();

                } catch (JSONException e) {
                    //e.printStackTrace();
                    if (loadDlg.isShowing()) { loadDlg.dismiss(); }
                    Utils.setSingleBtnAlert(activity, "Technical Error Occurred", "Ok", false);
                }
            });
        }
    }

    private void getvoudata() {

        Map<String, String> params = new HashMap<>();
        params.put("trip_id", PnrNo);
        params.put("email", Email);

        new VolleyRequester(activity).ParamsRequest(1, MainURL, loadDlg, params, false, response -> {

            Mainview.findViewById(R.id.mainLayt).setVisibility(View.GONE);
            Mainview.findViewById(R.id.voucherLayt).setVisibility(View.VISIBLE);

            VouWebview.loadData(response, "text/html; charset=UTF-8", null);
            WebSettings webs = VouWebview.getSettings();
            webs.setSupportZoom(true);
            webs.setBuiltInZoomControls(true);
            webs.setDisplayZoomControls(false);
            webs.setLoadWithOverviewMode(true);
            webs.setUseWideViewPort(true);
            VouWebview.setInitialScale(1);

            Mainview.findViewById(R.id.resendTv).setOnClickListener(v -> getcancelreq("send"));
            Mainview.findViewById(R.id.cancelTv).setOnClickListener(v -> getcancelreq("cancel"));

            if (loadDlg.isShowing()) { loadDlg.dismiss(); }
        });
    }

    private void getcancelreq(final String Type) {//cancel  send

        loadDlg.show();

        Map<String, String> params = new HashMap<>();
        params.put("tripid", ( Type.equals("cancel") && !Voutype.equals("hotel")) ? PnrID : PnrNo );
        params.put("voucher", Voutype);
        params.put("type", Type);

        new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL + "account/getbooking.php", null, params, false, response -> {
            if (loadDlg.isShowing()) { loadDlg.dismiss(); }

            String ss;
            if (Type.equals("cancel")) {
                ss = response.equals("success") ? "Cancellation Requested" : "Error While Requesting Cancellation";
            } else {
                ss = response.equals("success") ? "Voucher Sent" : "Voucher Sending Failed";
            }

            Utils.setSingleBtnAlert(activity, ss, "Ok", false);
        });
    }

}