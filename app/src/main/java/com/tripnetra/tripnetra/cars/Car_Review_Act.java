package com.tripnetra.tripnetra.cars;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.hotels.fragments.Cancel_policy_dialogF;
import com.tripnetra.tripnetra.payment.Car_Payment_Act;
import com.tripnetra.tripnetra.utils.SharedPrefs;
import com.tripnetra.tripnetra.utils.Utils;

import java.util.Objects;

public class Car_Review_Act extends AppCompatActivity {

    String CarName,CarFeatures,Carsmalldes,Carprice, TripType, PickLoc, PickId, DropLoc,
            DropId, JDate, Jtime, Dispdate, CarTotalPrice, CarCompany, UserID = "0";
    LoadingDialog LoadDlg;
    Bundle mbundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_review);
        if (getIntent().getExtras() != null) {
            Bundle bb = getIntent().getExtras();
            mbundle = bb;

            CarName = bb.getString("CarName");
            CarFeatures = bb.getString("Carfeature");
            Carsmalldes = bb.getString("CarSmallDes");

            Carprice = bb.getString("CarPrice");

            CarCompany = bb.getString("CarCompany");

            Bundle bud = bb.getBundle("CarBundle");
            assert bud != null;
            TripType = bud.getString("type");
            PickLoc = bud.getString("pickup");
            PickId = bud.getString("pickupid");
            DropId = bud.getString("dropid");
            DropLoc = bud.getString("drop");
            JDate = bud.getString("date");
            Jtime = bud.getString("time");
            Dispdate = bud.getString("Dispdate");

            LoadDlg = new LoadingDialog(this);
            LoadDlg.setCancelable(false);
            Objects.requireNonNull(LoadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            LoadDlg.show();

            SharedPrefs sharedprefs = new SharedPrefs(this);
            if (sharedprefs.getLoggedin()) {
                EditText FNameET = findViewById(R.id.fnameEt), LNameET = findViewById(R.id.lnameEt),
                        EMailET = findViewById(R.id.emailEt), phoneET = findViewById(R.id.phoneEt);
                FNameET.setText(sharedprefs.getFName());
                LNameET.setText(sharedprefs.getLName());
                EMailET.setText(sharedprefs.getUMail());
                phoneET.setText(sharedprefs.getUMobile());
                UserID = sharedprefs.getUserId();
            }
            loaddata();
        }
    }

    @SuppressLint("SetTextI18n")
    public void loaddata() {

        Glide.with(this).load(mbundle.getString("CarImage")).into((ImageView) findViewById(R.id.carIV));

        ((TextView) findViewById(R.id.carnameTv)).setText(CarName);
        ((TextView) findViewById(R.id.cardescTv)).setText(Carsmalldes + "\n\n" + mbundle.getString("CarLongDes"));
        ((TextView) findViewById(R.id.pickdate)).setText(Dispdate);
        ((TextView) findViewById(R.id.picktime)).setText(Jtime);
        ((TextView) findViewById(R.id.pickuplocTV)).setText(PickLoc);
        ((TextView) findViewById(R.id.droplocTV)).setText(DropLoc);

        float subtotal = Float.parseFloat(Carprice), taxper = Float.parseFloat(mbundle.getString("CarTax"));
        int tax = (int) Math.ceil(subtotal * taxper / 100);
        int totalpri = (int) Math.ceil(subtotal + tax);

        CarTotalPrice = String.valueOf(totalpri);
        ((TextView) findViewById(R.id.subtotaltv)).setText("₹ " + Carprice);
        ((TextView) findViewById(R.id.procesfeeTv)).setText("₹ " + tax);
        ((TextView) findViewById(R.id.Gpricetv)).setText("₹ " + totalpri);
        ((TextView) findViewById(R.id.grandtotaltv)).setText("₹ " + totalpri);
        ((TextView) findViewById(R.id.termsTv)).setText(Html.fromHtml(getResources().getString(R.string.Roomterms)));

        LoadDlg.dismiss();
    }

    public void cancelpolcy(View v) {
        Bundle bb = new Bundle();
        bb.putString("CancelText", mbundle.getString("CancelPolicy"));
        Cancel_policy_dialogF cnlp = new Cancel_policy_dialogF();
        cnlp.setArguments(bb);
        cnlp.show(getSupportFragmentManager(), "Cancel policy Dialog");
    }

    public void viewdetails(View v) {

        TextView showText = new TextView(this);
        String CarFeature = CarFeatures.replaceAll("@", "\n");
        showText.setText(CarFeature);
        showText.setTextColor(Color.BLACK);
        showText.setTypeface(null, Typeface.NORMAL);
        showText.setAutoLinkMask(4);
        showText.setTextSize(17);
        //showText.setTextIsSelectable(false);
        showText.setGravity(Gravity.CENTER_HORIZONTAL);
        new AlertDialog.Builder(this).setTitle("Car Details")
                .setView(showText)
                .setPositiveButton("Ok", (dialog, id) -> { })
                .setCancelable(true).create().show();
    }

    public void verifydata(View v) {

        TextInputLayout fnameTL = findViewById(R.id.fnameTil), lnameTL = findViewById(R.id.lnameTil),
                emailTL = findViewById(R.id.emaiTil), phoneTL = findViewById(R.id.phoneTil);

        String FName = ((EditText)findViewById(R.id.fnameEt)).getText().toString(),
                LName = ((EditText)findViewById(R.id.lnameEt)).getText().toString(),
                EMail = ((EditText)findViewById(R.id.emailEt)).getText().toString(),
                Mobile = ((EditText)findViewById(R.id.phoneEt)).getText().toString();

        fnameTL.setErrorEnabled(false);
        lnameTL.setErrorEnabled(false);
        emailTL.setErrorEnabled(false);
        phoneTL.setErrorEnabled(false);

        if (Objects.equals(FName, "")) {
            fnameTL.setError("Enter First Name ");
        } else if (Objects.equals(LName, "")) {
            lnameTL.setError("Enter Last Name ");
        } else if (Objects.equals(EMail, "") || Utils.isEmailValid(EMail)) {
            emailTL.setError(Objects.equals(EMail, "") ? "Enter EMail " : "Enter Valid EMail");
        }else if (Objects.equals(Mobile, "")) {
            phoneTL.setError("Enter Mobile Number ");
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("FName", FName);
            bundle.putString("LName", LName);
            bundle.putString("EMail", EMail);
            bundle.putString("Mobile", Mobile);
            bundle.putString("CarName", CarName);
            bundle.putString("CarDetid", mbundle.getString("CarDetid"));
            bundle.putString("CarType", mbundle.getString("CarType"));
            bundle.putString("CarTypeid", mbundle.getString("CarTypeId"));
            bundle.putString("Suppid", mbundle.getString("Suppid"));
            bundle.putString("TotalPrice", CarTotalPrice);
            bundle.putString("CarFeatures", CarFeatures);
            bundle.putString("TripType", TripType);
            bundle.putString("PickLoc", PickLoc);
            bundle.putString("PickId", PickId);
            bundle.putString("DropLoc", DropLoc);
            bundle.putString("DropId", DropId);
            bundle.putString("JDate", JDate);
            bundle.putString("Jtime", Jtime);
            bundle.putString("Dispdate", Dispdate);
            bundle.putString("CarCompany", CarCompany);
            bundle.putString("SmallDes", Carsmalldes);
            bundle.putString("userid", UserID);

            Intent intent = new Intent(this, Car_Payment_Act.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

}