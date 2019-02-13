package com.tripnetra.tripnetra.paypal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.hotels.Hotel_Voucher_Act;

import java.util.List;

public class paypal_final_act extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_final);

        if (getIntent().getData() != null) {

            List<String> params = getIntent().getData().getPathSegments();

            G_Class g_class = (G_Class) getApplicationContext();

            Bundle bundl = g_class.getBundle();

            if (params.get(0).equals("success")) {
                bundl.putString("STATUS", "TXN_SUCCESS");
                bundl.putString("RESPCODE", "01");
                bundl.putString("TXNDATE", "2018-03-06 10:05:59");

            } else {
                bundl.putString("STATUS", "TXN_FAILURE");
                bundl.putString("RESPCODE", "02");
                bundl.putString("TXNDATE", "2018-03-06 10:05:59");
            }

            Intent intent = null;
            switch (g_class.getPayType()) {
                case "hotel":
                    intent = new Intent(this, Hotel_Voucher_Act.class);
                    break;
                case "car":
                    break;
                case "tour":
                    break;
            }

            if (intent != null) {
                intent.putExtras(bundl);
                startActivity(intent);
            }
        }
    }

}