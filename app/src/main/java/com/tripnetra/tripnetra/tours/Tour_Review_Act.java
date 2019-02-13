package com.tripnetra.tripnetra.tours;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.payment.Tour_Payment_Act;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.SharedPrefs;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;

@SuppressWarnings("ConstantConditions")
@SuppressLint("SetTextI18n")
public class Tour_Review_Act extends AppCompatActivity {

    Button spinner,spinner1,VtypeBtn;
    TextView SubTotTV,ProFeeTV,PriceTV,TermsTV,GTotalTV,ActPriceTv,PartPriceTv;
    String FName,LName,EMail,Mobile,CarId="",TourType,UserID="0",Categoryid;
    TextInputLayout ctypeTIL;
    Bundle bundle;
    String[] CIds,CNames,Cprices,CActprices;
    int TotalPrice,Actprice=0,Partprice=0,ACount = 1,CCount = 0;
    float ServiceTax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_review);

        SubTotTV = findViewById(R.id.subtotaltv);
        ProFeeTV = findViewById(R.id.procesfeeTv);
        PriceTV = findViewById(R.id.Gpricetv);
        TermsTV = findViewById(R.id.termsTv);
        GTotalTV = findViewById(R.id.GTotalTv);
        ActPriceTv = findViewById(R.id.actpriceTv);
        PartPriceTv = findViewById(R.id.partpriceTv);

        ctypeTIL = findViewById(R.id.ctypeTil);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        bundle = getIntent().getExtras();
        if(bundle!=null){
            setmaindata();
        }
    }

    private void setmaindata() {

        String[] array_items = bundle.getString("sightseen_images").split(",");

        Glide.with(this).load(((G_Class) getApplicationContext()).getImageurl()+Config.SIGHT_IMAGE + array_items[0]).into((ImageView) findViewById(R.id.tourIv));

        ((TextView)findViewById(R.id.tnameTv)).setText(bundle.getString("sightseen_name"));

        ((TextView)findViewById(R.id.TdateTv)).setText(Utils.ChangeDateFormat(bundle.getString("journeydate"),1));
        Categoryid=bundle.getString("sightseen_category_id");

        Float pric = (!Objects.equals(bundle.getString("sightseen_price"), "")) ? Float.valueOf(bundle.getString("sightseen_price")) : 0 ;
        ServiceTax = (!Objects.equals(bundle.getString("service_tax"), "")) ? Float.valueOf(bundle.getString("service_tax")) : 0 ;

        Actprice = (int) Math.ceil(pric);
        Partprice = Actprice;
        setdata();

        TermsTV.setText(Html.fromHtml(getResources().getString(R.string.Roomterms)));

        SharedPrefs sharedprefs = new SharedPrefs(this);
        if(sharedprefs.getLoggedin()){
            ((EditText) findViewById(R.id.fnameEt)).setText(sharedprefs.getFName());
            ((EditText) findViewById(R.id.lnameEt)).setText(sharedprefs.getLName());
            ((EditText) findViewById(R.id.emailEt)).setText(sharedprefs.getUMail());
            ((EditText) findViewById(R.id.phoneEt)).setText(sharedprefs.getUMobile());
            UserID = sharedprefs.getUserId();
        }

        if(Objects.equals(bundle.getString("partial_details"), "")||Objects.equals(bundle.getString("partial_details"), "null")){
            TourType = "pooja";
            ctypeTIL.setVisibility(View.GONE);
            TextView packTV = findViewById(R.id.packTv);
            CardView packCard = findViewById(R.id.packCard);
            packTV.setVisibility(View.GONE);
            packCard.setVisibility(View.GONE);
        }else {
            setjsondata();
        }

        if(Categoryid.equals("8")){
            findViewById(R.id.spinerLayt).setVisibility(View.VISIBLE);
            setspiiner();
        }else{
            findViewById(R.id.spinerLayt).setVisibility(View.GONE);
        }

    }

    private void setjsondata() {

        VtypeBtn = findViewById(R.id.vtypeBtn);

        try {
            JSONArray jarr = new JSONArray(bundle.getString("partial_details"));
            CIds = new String[jarr.length()];
            CNames = new String[jarr.length()];
            Cprices = new String[jarr.length()];
            CActprices = new String[jarr.length()];

            TourType = "car";

            for (int i = 0; i < jarr.length(); i++) {
                CIds[i] = jarr.getJSONObject(i).getString("car_id");
                CNames[i] = jarr.getJSONObject(i).getString("car_name") + " - " + jarr.getJSONObject(i).getString("max_capacity") + " Seater";
                Cprices[i] = jarr.getJSONObject(i).getString("car_price");
                CActprices[i] = jarr.getJSONObject(i).getString("actual_price");
            }

            VtypeBtn.setOnClickListener(view -> {
                CarId = "";
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Tour_Review_Act.this, R.layout.textview_layout, CNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                new AlertDialog.Builder(Tour_Review_Act.this).setTitle("Select Car Type").setAdapter(adapter, (dialog, pos) -> {
                    VtypeBtn.setText(CNames[pos]);
                    CarId = CIds[pos];

                   /* Float price = Float.valueOf(Cprices[pos]);
                    int taxper = (int) Math.ceil((price*ServiceTax)/100);
                    TotalPrice = (int) (price+taxper);*/

                    Actprice = (!Objects.equals(CActprices[pos], "")) ? Integer.parseInt(CActprices[pos]) :
                            Integer.parseInt(Objects.requireNonNull(bundle.getString("sightseen_price")));
                    Partprice = Integer.parseInt(Cprices[pos]);

                    setdata();

                    dialog.dismiss();
                }).create().show();
            });
        } catch (JSONException e) {
            //e.printStackTrace();
            Utils.setSingleBtnAlert(Tour_Review_Act.this, getResources().getString(R.string.something_wrong), "Ok", true);
        }
    }

    private void setspiiner() {

        spinner = findViewById(R.id.spinner1);
        spinner1 = findViewById(R.id.spinner2);

        final String[] items = new String[]{"1", "2", "3","4","5","6","7","8","9","10"};
        final String[] items1 = new String[]{"0", "1", "2"};

        spinner.setOnClickListener(view -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Tour_Review_Act.this, R.layout.textview_layout, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            new AlertDialog.Builder(Tour_Review_Act.this).setTitle("Select Adult Count").setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    spinner.setText(items[pos]);
                    ACount = Integer.parseInt(items[pos]);

                    setdata();

                }
            }).create().show();
        });

        spinner1.setOnClickListener(view -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Tour_Review_Act.this, R.layout.textview_layout, items1);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            new AlertDialog.Builder(Tour_Review_Act.this).setTitle("Select Child Count").setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    spinner1.setText(items1[pos]);
                    CCount = Integer.parseInt(items1[pos]);

                    setdata();

                }
            }).create().show();
        });

    }

    public void verifydata(View v){

        FName = ((EditText)findViewById(R.id.fnameEt)).getText().toString();LName = ((EditText)findViewById(R.id.lnameEt)).getText().toString();
        EMail = ((EditText)findViewById(R.id.emailEt)).getText().toString();Mobile = ((EditText)findViewById(R.id.phoneEt)).getText().toString();

        TextInputLayout fnameTIL = findViewById(R.id.fnameTil), lnameTIL = findViewById(R.id.lnameTil),
                emailTIL = findViewById(R.id.emaiTil), phoneTIL = findViewById(R.id.phoneTil);
        ctypeTIL = findViewById(R.id.ctypeTil);

        fnameTIL.setErrorEnabled(false);        lnameTIL.setErrorEnabled(false);        emailTIL.setErrorEnabled(false);        phoneTIL.setErrorEnabled(false);

        if(Objects.equals(FName, "")){
            fnameTIL.setError("Please Enter First Name ");
        }else if(Objects.equals(LName, "")){
            lnameTIL.setError("Please Enter Last Name ");
        }else if(Objects.equals(EMail, "") || Utils.isEmailValid(EMail)){
            emailTIL.setError(Objects.equals(EMail, "") ? "Please Enter EMail" : "Please Enter Valid EMail");
        }else if(Objects.equals(Mobile, "")){
            phoneTIL.setError("Please Enter Mobile Number ");
        }else if(Objects.equals(CarId, "") && Objects.equals(TourType, "car")){
            findViewById(R.id.phoneEt).clearFocus();
            ctypeTIL.setError("Select Car Type");
        }else {
            Bundle bb = new Bundle();
            bb.putString("fname",FName);
            bb.putString("lname",LName);
            bb.putString("email",EMail);
            bb.putString("mobile",Mobile);
            bb.putString("totalprice", String.valueOf(TotalPrice));
            bb.putString("carid",CarId);
            bb.putString("cityid",bundle.getString("cityid"));
            bb.putString("cityname",bundle.getString("cityname"));
            bb.putString("tourid",bundle.getString("sightseen_id"));
            bb.putString("tourname",bundle.getString("sightseen_name"));
            bb.putString("tourdest",bundle.getString("sightseen_destination"));
            bb.putString("tourcat_id",bundle.getString("sightseen_category_id"));
            bb.putString("actprice", String.valueOf(Actprice));
            bb.putString("jourdate",bundle.getString("journeydate"));
            bb.putString("partprice", String.valueOf(Partprice));
            bb.putString("tourtype",TourType);
            bb.putString("userid",UserID);

            startActivity(new Intent(this, Tour_Payment_Act.class).putExtras(bb));
        }

    }

    public void setdata(){

        int i = ACount * Actprice;
        int j = CCount * Partprice;
        TotalPrice = i+j;

        int pric = TotalPrice;
        int taxperc = (int) Math.ceil((pric*ServiceTax)/100);
        TotalPrice = pric+taxperc;

        ActPriceTv.setText("₹" +i);
        PartPriceTv.setText("₹ " +j);

        SubTotTV.setText("₹ " +pric);
        ProFeeTV.setText("₹ " +taxperc);
        PriceTV.setText("₹ " +TotalPrice);
        GTotalTV.setText("₹ " +TotalPrice);
        PriceTV.setText("₹ " +TotalPrice);
        GTotalTV.setText("₹ " +TotalPrice);
    }
}