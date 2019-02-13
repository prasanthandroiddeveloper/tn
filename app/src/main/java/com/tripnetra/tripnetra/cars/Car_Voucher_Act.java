package com.tripnetra.tripnetra.cars;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class Car_Voucher_Act extends AppCompatActivity {

    TextView BookStatusTv;
    LoadingDialog loadDlg;
    String EMAIL,CarName,BookStatus,RespCode,OrderId,JDate,Paymode;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_voucher);

        if(getIntent().getExtras()!=null){
            Bundle bb = getIntent().getExtras();

            loadDlg = new LoadingDialog(this);
            loadDlg.setCancelable(false);
            Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loadDlg.show();

            EMAIL = bb.getString("EMail");
            CarName = bb.getString("CarName");
            JDate = bb.getString("JDate");
            BookStatus = bb.getString("STATUS");
            RespCode = bb.getString("RESPCODE");
            OrderId = bb.getString("ORDER_ID");
            Paymode = bb.getString("Paymode");
            String smallDes = bb.getString("SmallDes");
            assert smallDes != null;

            BookStatusTv = findViewById(R.id.bookstatus);

            ((TextView)findViewById(R.id.bookid)).setText(OrderId);
            ((TextView)findViewById(R.id.booktime)).setText(bb.getString("TXNDATE"));
            ((TextView)findViewById(R.id.carnameTv)).setText(CarName);
            ((TextView)findViewById(R.id.cartypeTv)).setText(bb.getString("CarType"));
            ((TextView)findViewById(R.id.carcompTv)).setText(bb.getString("CarCompany"));
            ((TextView)findViewById(R.id.capacityTv)).setText(String.valueOf(smallDes.charAt(11)));
            ((TextView)findViewById(R.id.doorsTv)).setText(String.valueOf(smallDes.charAt(1)));
            ((TextView)findViewById(R.id.Gnametv)).setText(bb.getString("FName")+" "+bb.getString("LName"));
            ((TextView)findViewById(R.id.phonetv)).setText(bb.getString("Mobile"));
            ((TextView)findViewById(R.id.emailtv)).setText(EMAIL);
            ((TextView)findViewById(R.id.pickuplocTV)).setText(bb.getString("PickLoc"));
            ((TextView)findViewById(R.id.pickuptimeTv)).setText(bb.getString("Jtime"));
            ((TextView)findViewById(R.id.pickupdateTv)).setText(JDate);
            ((TextView)findViewById(R.id.pricetv)).setText(bb.getString("TXNAMOUNT"));

            loaddata();

        }
    }

    private void loaddata() {

        Map<String, String> params = new HashMap<>();
        params.put("pnr_no", OrderId);
        if(Objects.equals(BookStatus, "TXN_SUCCESS") && Objects.equals(RespCode, "01")) {
            params.put("booking_status", "CONFIRM");
            params.put("payment_status", "CONFIRM");
        }else{
            params.put("booking_status", "CANCELLED");
            params.put("payment_status", "CANCELLED");
        }
        params.put("payment_type",Paymode);

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIPCAR_URL+"carpayment.php", loadDlg, params, false, response -> {

            if(loadDlg.isShowing()){loadDlg.dismiss();}

            BookStatusTv.setText(R.string.cancelled);
            BookStatusTv.setTextColor(RED);


            switch (response) {
                case "MAILSENT":
                    if(Objects.equals(BookStatus, "TXN_SUCCESS") && Objects.equals(RespCode, "01")) {
                        BookStatusTv.setText(R.string.confirmed);
                        BookStatusTv.setTextColor(GREEN);

                        Utils.SendNotification(Car_Voucher_Act.this,CarName+" Booked on "+JDate);

                        Utils.setSingleBtnAlert(Car_Voucher_Act.this,"Booking Confirmed\n\nVoucher Sent to " + EMAIL + "\n Thanks for Booking With Us","Ok",false);
                    }else{
                        Utils.setSingleBtnAlert(Car_Voucher_Act.this,"Booking Not Confirmed\n\nPlease Book Again","Ok",false);
                    }
                    break;
                case "MAILNOTSENT":
                    Utils.setSingleBtnAlert(Car_Voucher_Act.this,"Booking  Not Confirmed\n\nPlease Book Again","Ok",false);
                    break;
                default:
                    Utils.setSingleBtnAlert(Car_Voucher_Act.this,"Booking Cancelled due To\n\nTechnical Error","Ok",false);
                    break;
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, Cars_Main_Act.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}