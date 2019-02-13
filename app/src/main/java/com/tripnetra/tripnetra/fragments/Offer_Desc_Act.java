package com.tripnetra.tripnetra.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tobiasrohloff.view.NestedScrollWebView;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.hotels.Hotel_Main_Act;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Offer_Desc_Act extends AppCompatActivity {

    String Offerid,Ccode;
    LoadingDialog loadDlg;
    TextView TermsTv;
    NestedScrollWebView webview;
    G_Class g_class ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_description);

        Offerid = Objects.requireNonNull(getIntent().getExtras()).getString("offerid");

        g_class = (G_Class) getApplicationContext();
        TermsTv = findViewById(R.id.termsTv);
        webview = findViewById(R.id.longdeswv);

        loadDlg = new LoadingDialog(this);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadDlg.show();

        loaddata();
    }

    private void loaddata() {

        Map<String, String> params = new HashMap<>();
        params.put("type", "getdetails");
        params.put("offerid", Offerid);

        new VolleyRequester(this).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL+"hotel_offers.php", loadDlg, params, false, response -> {
            if (response.equals("No Result")) {
                if(loadDlg.isShowing()){loadDlg.dismiss();}
                Utils.setSingleBtnAlert(Offer_Desc_Act.this, "Offer Details Not Found", "Ok", false);
            } else {
                try {
                    JSONObject jobj = new JSONArray(response).getJSONObject(0);

                    Glide.with(Offer_Desc_Act.this).load(g_class.getImageurl()+Config.HOTEL_IMAGE+jobj.getString("offer_image")).
                            into((ImageView) findViewById(R.id.offerIv));

                    ((TextView) findViewById(R.id.applcbleTv)).setText(jobj.getString("description"));
                    ((TextView) findViewById(R.id.validTv)).setText(Utils.ChangeDateFormat(jobj.getString("validity_Date"),1));
                    Ccode = jobj.getString("offer_code");
                    ((TextView) findViewById(R.id.codetv)).setText(Ccode);
                    ((TextView) findViewById(R.id.descrttv)).setText(jobj.getString("title"));

                    webview.setNestedScrollingEnabled(false);
                    webview.loadData(jobj.getString("terms_&_conditions"), "text/html", null);

                } catch (JSONException e) {
                    //e.printStackTrace();
                    if(loadDlg.isShowing()){loadDlg.dismiss();}
                    Utils.setSingleBtnAlert(Offer_Desc_Act.this, getResources().getString(R.string.something_wrong), "Ok", false);
                }
                if(loadDlg.isShowing()){loadDlg.dismiss();}
            }
        });

    }

    public void copycode(View v) {
        if (!Ccode.equals("")) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("promocode", Ccode);
            Objects.requireNonNull(clipboard).setPrimaryClip(clip);

            Toast.makeText(this, "Promo Code Copied", Toast.LENGTH_SHORT).show();
        }
    }

    public void bookaccomdation(View v){ startActivity(new Intent(this, Hotel_Main_Act.class)); }

    public void hidewv(View v){

        if(webview.getVisibility() == View.GONE){
            TermsTv.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.arrow1u,0);
            webview.setVisibility(View.VISIBLE);
        }else{
            TermsTv.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.arrow1d,0);
            webview.setVisibility(View.GONE);
        }

    }

}