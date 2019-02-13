package com.tripnetra.tripnetra.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.SharedPrefs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Refer_earn_Activity extends AppCompatActivity {

    String ReferCode,Offerbody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_earn);

        ReferCode = Objects.requireNonNull(getIntent().getExtras()).getString("refercode");

        if(Objects.equals(ReferCode, "none")){
            getreferdetails();
        }else{
            ((TextView) findViewById(R.id.codetv)).setText(ReferCode);
        }

        Offerbody = getResources().getString(R.string.offer_body)+ReferCode+" to Avail ₹100 Bonus";
    }

    public void sharewhats(View v){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, Offerbody);
        shareIntent.setType("text/plain");
        shareIntent.setPackage("com.whatsapp");
        try {startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException e) {
            shareIntent.setPackage("com.gbwhatsapp");
            try {startActivity(shareIntent);} catch (android.content.ActivityNotFoundException ee) { shareall(v);}
        }
    }

    public void sharemsgr(View v){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, Offerbody);
        shareIntent.setType("text/plain");
        shareIntent.setPackage("com.facebook.orca");
        try {startActivity(shareIntent); } catch (android.content.ActivityNotFoundException e) { shareall(v); }
    }

    public void sharegmail(View v){

        String mailTo = "mailto: ?&subject=" + Uri.encode("Tripnetra Android Application") + "&body=" + Uri.encode(Offerbody);
        Intent emailIntent = new Intent(Intent.ACTION_VIEW);
        emailIntent.setData(Uri.parse(mailTo));
        try {startActivity(emailIntent); } catch (android.content.ActivityNotFoundException e) { shareall(v); }
    }

    public void shareall(View v){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, Offerbody);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public void getreferdetails(){

        Map<String, String> params = new HashMap<>();
        params.put("userid", new SharedPrefs(Refer_earn_Activity.this).getUserId());

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL + "account/referalcode.php", null, params, false, response -> {
            ReferCode = response;
            ((TextView) findViewById(R.id.codetv)).setText(ReferCode);
        });
    }

}