package com.tripnetra.tripnetra.tours;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Tour_Voucher_Act extends AppCompatActivity {

    TextView BookStatTv;
    String FName,EMAIL,BookStatus,RespCode,PnrNum,JourDate,Paymode;
    LoadingDialog loadDlg;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_voucher);

        BookStatTv = findViewById(R.id.bookstatus);
        LinearLayout BalLayt = findViewById(R.id.balLAyt);

        if(getIntent().getExtras()!=null) {

            loadDlg = new LoadingDialog(this);
            loadDlg.setCancelable(false);
            Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loadDlg.show();

            Bundle bb = getIntent().getExtras();

            FName = bb.getString("fname");
            EMAIL = bb.getString("email");
            BookStatus = bb.getString("STATUS");
            RespCode = bb.getString("RESPCODE");
            PnrNum = bb.getString("ORDER_ID");
            Paymode = bb.getString("Paymode");
            JourDate = bb.getString("jourdate");

            if(Objects.equals(bb.getString("tourtype"), "pooja")){
                BalLayt.setVisibility(View.GONE);
                ((TextView)findViewById(R.id.partnameTv)).setText(R.string.total_price);
            }else{
                ((TextView)findViewById(R.id.balTv)).setText(bb.getString("balprice"));
            }
            ((TextView)findViewById(R.id.partpriceTv)).setText(bb.getString("TXNAMOUNT"));

            ((TextView)findViewById(R.id.booktime)).setText(bb.getString("TXNDATE"));
            ((TextView)findViewById(R.id.bookid)).setText(PnrNum);
            ((TextView)findViewById(R.id.nametv)).setText(FName+" "+bb.getString("lname"));
            ((TextView)findViewById(R.id.phonetv)).setText(bb.getString("mobile"));
            ((TextView)findViewById(R.id.emailtv)).setText(EMAIL);
            ((TextView)findViewById(R.id.citynameTV)).setText(bb.getString("cityname"));
            ((TextView)findViewById(R.id.tnameTv)).setText(bb.getString("tourname"));
            ((TextView)findViewById(R.id.dateTV)).setText(Utils.ChangeDateFormat(JourDate,1));

            send_booking_data();
        }
    }

    private void send_booking_data() {

        Map<String, String> params = new HashMap<>();
        params.put("pnr_no", PnrNum);
        if(Objects.equals(BookStatus, "TXN_SUCCESS") && Objects.equals(RespCode, "01")) {
            params.put("booking_status", "CONFIRMED");
            params.put("payment_status", "CONFIRMED");
        }else{
            params.put("booking_status", "CANCELLED");
            params.put("payment_status", "CANCELLED");
        }
        params.put("payment_type",Paymode);
        params.put("type", "CONFIRM");

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TOUR_PAYMENT_URL, loadDlg, params, true, response -> {

            if(loadDlg.isShowing()){loadDlg.dismiss();}

            BookStatTv.setText(R.string.cancelled);
            BookStatTv.setTextColor(RED);

            switch (response) {
                case "MAILSENT":
                    if(Objects.equals(BookStatus, "TXN_SUCCESS") && Objects.equals(RespCode, "01")) {
                        BookStatTv.setText(R.string.confirmed);
                        BookStatTv.setTextColor(GREEN);
                        Utils.SendNotification(Tour_Voucher_Act.this,FName+" Booked on "+JourDate);
                        Utils.setSingleBtnAlert(Tour_Voucher_Act.this,"Booking Confirmed\n\nVoucher Sent to " + EMAIL + "\n Thanks for Booking With Us","Ok",false);
                    }else{
                        Utils.setSingleBtnAlert(Tour_Voucher_Act.this,"Booking Not Confirmed\n\nPlease Book Again","Ok",false);
                    }
                    break;
                case "MAILNOTSENT":
                    Utils.setSingleBtnAlert(Tour_Voucher_Act.this,"Booking  Not Confirmed\n\nPlease Book Again","Ok",false);
                    break;
                default:
                    Utils.setSingleBtnAlert(Tour_Voucher_Act.this,"Booking Cancelled due To\n\nTechnical Error","Ok",false);
                    break;
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, Tours_Main_Act.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}