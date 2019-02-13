package com.tripnetra.tripnetra.hotels;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.hotels.fragments.Cancel_policy_dialogF;
import com.tripnetra.tripnetra.hotels.fragments.LoginUser;
import com.tripnetra.tripnetra.payment.Hotel_Payment_Act;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.SharedPrefs;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressLint("SetTextI18n")
public class Hotel_Room_Review_Act extends AppCompatActivity implements View.OnClickListener {

    LinearLayout Couplay1,Couplay2;
    EditText FNameET,LNameET,EMailET,phoneET;
    int extrabed=0,dbebed,Roomdiscper,Grandtotal,ebedprice=0,FinalRprice,maxpersons,AdultCount,Roomtotprice,Old_Discper,
            Supprice,supgst,Roomtax,suppebed,Hoteltypeid,Commission,NightCount,RoomCount,NcoupMax,
            UsableTCash,NCoupDiscPer,TCashDiscPer=0,TcashMax,AvailTCash,ReturnTcash=0,CouponDiscount=0,TcashUsed=0;

    TextView ebedtv,Cintv,Couttv,ExpectHead,Hnametv,coupcodetv,CoupValidTV;

    String Hid,Hname,Rid,Rname,Rimage,Cindate,Hoteladdr,postcode,HotelMob,hourly_base,H24_hrs,cinexpect,
            coutexpect,Hourcin,Hourcout,RRateid,UserID="0",Coutdate,ChildCount,expecttime,Coupcode,ebedavail,
            Hgst_in,Gst_in_status,Old_Coupon,TcashStatus,CouponID="0",CouponType="";

    Button expectbutton;
    String[] expectedtimes = {"12:00 AM - 3:00 AM","3:00 AM - 6:00 AM","6:00 AM - 9:00 AM","9:00 AM - 12:00 PM",
            "12:00 PM - 3:00 PM","3:00 PM - 6:00 PM","6:00 PM - 9:00 PM","9:00 PM - 12:00 AM"};
    LoadingDialog loadDlg;
    SharedPrefs sharedprefs;
    CheckBox TcashCB;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_review);

        G_Class gcv = (G_Class) getApplicationContext();
        Hid = gcv.getHotelId();
        Hname = gcv.getHotelName();
        Cindate = gcv.getCheckin();
        Coutdate = gcv.getCheckout();
        RoomCount = Integer.parseInt(gcv.getRoomCount());
        AdultCount = Integer.parseInt(gcv.getAdultCount());
        ChildCount = gcv.getChildCount();
        NightCount = Integer.parseInt(gcv.getNights());
        Coupcode = gcv.getCoupcode();

        Rid = getIntent().getExtras().getString("Rid");
        Rname = getIntent().getExtras().getString("Rname");
        Rimage = getIntent().getExtras().getString("Rimage");
        Roomtax = Integer.parseInt(getIntent().getExtras().getString("Roomtax"));
        Hoteltypeid = Integer.parseInt(getIntent().getExtras().getString("HtypeId"));
        Commission = Integer.parseInt(getIntent().getExtras().getString("commission"));
        if(!Objects.equals(getIntent().getExtras().getString("Rdiscper"), "")) {
            Roomdiscper = Integer.parseInt(getIntent().getExtras().getString("Rdiscper"));
            Roomtotprice = Integer.parseInt(getIntent().getExtras().getString("Rtotprice")) ;//* (RoomCount*NightCount);
        }
        RRateid = getIntent().getExtras().getString("Rrateid");
        Supprice = Integer.parseInt(getIntent().getExtras().getString("SuppSgl"));
        maxpersons = Integer.parseInt(getIntent().getExtras().getString("maxpersons")); //change

        Couplay1 = findViewById(R.id.offerlay1);
        Couplay2 = findViewById(R.id.offerlay2);

        ebedtv = findViewById(R.id.extrabedcont);
        Cintv = findViewById(R.id.cindate);
        Couttv = findViewById(R.id.coutdate);
        Hnametv = findViewById(R.id.hotelname);
        coupcodetv= findViewById(R.id.couponcode);
        ExpectHead = findViewById(R.id.expecthead);
        CoupValidTV = findViewById(R.id.coupvalidTv);
        TcashCB = findViewById(R.id.cashCB);

        FNameET = findViewById(R.id.fnameEt);
        LNameET = findViewById(R.id.lnameEt);
        EMailET = findViewById(R.id.emailEt);
        phoneET = findViewById(R.id.phoneEt);

        expectbutton = findViewById(R.id.expectedspinner);

        findViewById(R.id.bedplus).setOnClickListener(this);
        findViewById(R.id.bedminus).setOnClickListener(this);
        findViewById(R.id.selectbed).setOnClickListener(this);
        findViewById(R.id.coupremove).setOnClickListener(this);
        expectbutton.setOnClickListener(this);

        findViewById(R.id.reqlayt).requestFocus();

        loadDlg = new LoadingDialog(Hotel_Room_Review_Act.this);
        loadDlg.setCancelable(false);
        loadDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadDlg.show();

        getdetails();
    }

    private void getdetails() {

        Map<String, String> params = new HashMap<>();
        params.put("Hotelid", Hid);
        params.put("Roomid", Rid);
        params.put("type", "booking");
        params.put("rcount", String.valueOf(RoomCount));
        params.put("ncount", String.valueOf(NightCount));
        params.put("sglprice", String.valueOf(Supprice));
        params.put("room_total", String.valueOf(Roomtotprice));
        params.put("htype_id", String.valueOf(Hoteltypeid));
        params.put("h_commission", String.valueOf(Commission));

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL+"hotelbook.php", loadDlg, params, true, response -> {

            try {
                JSONObject jObj = new JSONObject(response);
                Hoteladdr = jObj.getString("hotel_address");
                postcode = jObj.getString("postal_code");
                HotelMob = jObj.getString("phone_number");
                hourly_base = jObj.getString("hourly_base");
                H24_hrs= jObj.getString("24_hrs");
                cinexpect = jObj.getString("exc_checkin_time");
                coutexpect = jObj.getString("exc_checkout_time");
                dbebed = Integer.parseInt(jObj.getString("extra_bed_count"));

                Hgst_in = jObj.getString("hotel_gstin");
                Gst_in_status = Objects.equals(Hgst_in, "") ? "NotAvailable" : "Available" ;

                if(dbebed!=0) {
                    ebedprice = Integer.parseInt(jObj.getString("extra_bed_price_per_room"));
                    suppebed = ebedprice;
                    ebedprice -= Math.ceil(ebedprice*Commission*0.01);

                    if(Objects.equals(jObj.getString("markup_value"), "")||Objects.equals(jObj.getString("markup_value"), "0")){
                        ebedprice += (Integer.parseInt(jObj.getString("markup_fixed"))/5);
                    }else{
                        ebedprice += Math.ceil(ebedprice*Integer.parseInt(jObj.getString("markup_value"))*0.01);
                    }
                }

                sharedprefs = new SharedPrefs(Hotel_Room_Review_Act.this);
                if(sharedprefs.getLoggedin()){
                    findViewById(R.id.usetv).setVisibility(View.GONE);
                    FNameET.setText(sharedprefs.getFName());
                    LNameET.setText(sharedprefs.getLName());
                    EMailET.setText(sharedprefs.getUMail());
                    phoneET.setText(sharedprefs.getUMobile());
                    AvailTCash = ((sharedprefs.getTripcash().equals("")) ? 0 : Integer.parseInt(sharedprefs.getTripcash()));
                    UserID = sharedprefs.getUserId();

                }

                TCashDiscPer = Integer.parseInt(jObj.getString("trip_discount"));
                TcashMax = Integer.parseInt(jObj.getString("trip_amount"));
                TcashStatus = ((jObj.getString("trip_cash_usage").equals("ACTIVE"))? "ACTIVE" : "INACTIVE");

                ebedavail = jObj.getString("extra_bed");
                Hourcin = jObj.getString("hourly_checkin_time");
                Hourcout = jObj.getString("hourly_checkout_time");

                if(Objects.equals(jObj.getString("coupan_discount"), "")){
                    Old_Discper = 0;
                    findViewById(R.id.offertv).setVisibility(View.GONE);
                    findViewById(R.id.offerRL).setVisibility(View.GONE);
                }else {
                    if (Old_Discper!=0){
                    Old_Coupon = jObj.getString("coupan_code");
                    Old_Discper = Integer.parseInt(jObj.getString("coupan_discount"));
                }}

                parsedetails();
                load_bill_data(jObj.getJSONObject("hotel_price_data"));

            } catch (JSONException e){
//                e.printStackTrace();
                if(loadDlg.isShowing()){loadDlg.dismiss();}
                Utils.setSingleBtnAlert(Hotel_Room_Review_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
            }

        });
    }

    public void parsedetails(){

        Cintv.setText(Utils.ChangeDateFormat(Cindate,1));
        Couttv.setText(Utils.ChangeDateFormat(Coutdate,1));

        ((TextView) findViewById(R.id.hotelname)).setText(Hname);
        ((TextView) findViewById(R.id.roomname)).setText(Rname);
        ((TextView) findViewById(R.id.roomcount)).setText(""+ RoomCount +" Rooms");
        ((TextView) findViewById(R.id.personscount)).setText("->"+AdultCount+" Guests ("+AdultCount+" Adults & "+ChildCount+" Childs )");

        if(!Objects.equals(Rimage, "") || !Objects.equals(Rimage, "null")) {
            Glide.with(getApplicationContext()).load(Rimage).into((ImageView) findViewById(R.id.roomimage));
        }
        ((TextView) findViewById(R.id.roomtermsTv)).setText(Html.fromHtml(getResources().getString(R.string.Roomterms)));

        if(!Objects.equals(Coupcode, "null")){
            Couplay1.setVisibility(View.VISIBLE);
            Couplay2.setVisibility(View.GONE);
            coupcodetv.setText(Coupcode);
        }else{
            Couplay1.setVisibility(View.GONE);
            Couplay2.setVisibility(View.VISIBLE);
        }

        if(Objects.equals(hourly_base, "Available")){
            ExpectHead.setText("CheckIn-Out Time");
            expecttime = "C-In,C-Out Time ("+Hourcin+"-"+Hourcout+")";
        }else if(Objects.equals(H24_hrs, "ACTIVE")) {
            ExpectHead.setText("Expected Check-In Time");
            expecttime = "";
        }else{
            ExpectHead.setText("Standard CheckIn Time");
            expecttime = "Std C-In ("+cinexpect+"-"+coutexpect+")";
            expectbutton.setText(cinexpect+"-"+coutexpect);
        }
        ((TextView) findViewById(R.id.hoteladdr)).setText(Hoteladdr);

        int ii = AdultCount/RoomCount;
        if(ii>maxpersons){
            extrabed = ii-maxpersons;
            Toast.makeText(this,extrabed+" ExtraBed(s) Selected by Default",Toast.LENGTH_LONG).show();
            ebedtv.setText(String.valueOf(extrabed));
            Roomtotprice += ebedprice*extrabed;
            Supprice += suppebed*extrabed;
            loadDlg.show();
            refresh_data();
        }

    }

    @Override
    public void onClick(View vw) {
        switch(vw.getId()){
            case R.id.bedplus:
                if(extrabed < dbebed){
                    loadDlg.show();
                    extrabed++;
                    AdultCount++;
                    ebedtv.setText(String.valueOf(extrabed));
                    Roomtotprice += ebedprice;
                    Supprice += suppebed;
                    refresh_data();
                }
                break;
            case R.id.bedminus:
                if(extrabed > 0){
                    loadDlg.show();
                    extrabed--;AdultCount--;
                    ebedtv.setText(String.valueOf(extrabed));
                    Roomtotprice -= ebedprice;
                    Supprice -= suppebed;
                    refresh_data();
                }
                break;
            case R.id.selectbed:
                if(Objects.equals(ebedavail, "Available")) {
                    findViewById(R.id.llout1).setVisibility(View.GONE);
                    findViewById(R.id.llout2).setVisibility(View.VISIBLE);
                    ebedtv.setText(String.valueOf(extrabed));
                }else{
                    Toast.makeText(this, "Extra Bed Not Available", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.coupremove:
                Couplay1.setVisibility(View.GONE);
                Couplay2.setVisibility(View.VISIBLE);

                ((EditText)findViewById(R.id.coupontext)).setText("");
                CoupValidTV.setVisibility(View.GONE);
                CoupValidTV.setBackgroundColor(Color.TRANSPARENT);
                findViewById(R.id.couppriceLayt).setVisibility(View.GONE);

                int discount = Math.round(Roomtotprice*Roomdiscper/100);
                FinalRprice = Roomtotprice;
                Roomtotprice += discount;
                Roomdiscper = 0;CouponType="";
                loadDlg.show();
                refresh_data();

                break;
            case R.id.expectedspinner:
                phoneET.clearFocus();FNameET.clearFocus();LNameET.clearFocus();EMailET.clearFocus();
                if(Objects.equals(hourly_base, "Available")){
                    expectbutton.setText(Hourcin+"-"+Hourcout);
                }else if(Objects.equals(H24_hrs, "ACTIVE")) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.textview_layout, expectedtimes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    new AlertDialog.Builder(this).setTitle("Expected Check In time").setAdapter(adapter, (dialog, pos) -> {
                        expecttime = expectedtimes[pos];
                        expectbutton.setText(expecttime);
                        dialog.dismiss();
                    }).create().show();
                }else{
                    expectbutton.setText(cinexpect+"-"+coutexpect);
                }
                break;
            default:
                break;
        }
    }

    public void applycoupon(View v){

        String couptxt = ((EditText) findViewById(R.id.coupontext)).getText().toString();

        CoupValidTV.setVisibility(View.VISIBLE);
        CoupValidTV.setTextColor(Color.parseColor("#ff0000"));

        if (Objects.equals(couptxt, "")) {
            CoupValidTV.setText("Please Enter Coupon Code");
        }else if (Objects.equals(couptxt, Old_Coupon)) {
            Couplay2.setVisibility(View.GONE);
            Couplay1.setVisibility(View.VISIBLE);
            findViewById(R.id.couppriceLayt).setVisibility(View.VISIBLE);
            coupcodetv.setText(couptxt);

            CoupValidTV.setText("Coupon \""+Old_Coupon+"\" Applied");
            CoupValidTV.setTextColor(Color.parseColor("#FF137303"));

            Roomdiscper = Old_Discper;
            Roomtotprice = FinalRprice;

            loadDlg.show();
            refresh_data();
        }else if(UserID==null){
            CoupValidTV.setText("Please Login to Avail This Coupon Code");
        }else {

            Map<String, String> params = new HashMap<>();
            params.put("ccode", couptxt);
            params.put("userid", UserID);
            params.put("hotelid", Hid);

            new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL + "account/couponcode.php", loadDlg, params, true, response -> {
                try {
                    JSONObject jobj = new JSONObject(response);
                    Boolean error = jobj.getBoolean("error");
                    if (!error) {
                        JSONObject jbj = jobj.getJSONObject("coupon_details");

                        CouponID = jbj.getString("hotel_coupon_details_id");
                        NCoupDiscPer = Integer.parseInt(jbj.getString("coupon_discount"));
                        NcoupMax = Integer.parseInt(jbj.getString("coupon_max_amount"));
                        CouponType = jbj.getString("coupon_category");

                        Date CoupValid = Utils.StrtoDate(0,jbj.getString("coupon_valid"));
                        Date today = Calendar.getInstance().getTime();

                        if(CoupValid==null){

                            CoupValidTV.setText(R.string.please_enter_a_valid_coupon);
                        }else{

                            if (CoupValid.compareTo(today) <= 0) {
                                CoupValidTV.setText("Offer Expired");
                            } else {
                                Couplay2.setVisibility(View.GONE);
                                Couplay1.setVisibility(View.VISIBLE);
                                coupcodetv.setText(couptxt);
                                findViewById(R.id.couppriceLayt).setVisibility(View.VISIBLE);

                                CoupValidTV.setText(jbj.getString("coupon_name") + " Applied\n" + jbj.getString("coupon_description"));
                                CoupValidTV.setTextColor(Color.parseColor("#FF595959"));
                                CoupValidTV.setBackgroundColor(Color.parseColor("#FFE0F9CB"));

                                Roomdiscper = Old_Discper;
                                Roomtotprice = FinalRprice;

                                loadDlg.show();
                                refresh_data();

                            }
                        }
                    } else {
                        CoupValidTV.setText(R.string.please_enter_a_valid_coupon);
                    }
                } catch (JSONException e) {
//                    e.printStackTrace();
                    if(loadDlg.isShowing()){loadDlg.dismiss();}
                    Utils.setSingleBtnAlert(Hotel_Room_Review_Act.this, getResources().getString(R.string.something_wrong), "Ok", true);
                }
            });
        }
    }

    public void load_bill_data(JSONObject jobj){
        try {
            int dummprice = Roomtotprice * RoomCount * NightCount;
            final int DiscAmount = (int) Math.ceil(dummprice*Roomdiscper/100);
            supgst = Integer.parseInt(jobj.getString("supp_gst"));
            Grandtotal = Integer.parseInt(jobj.getString("grand_total"));

            ((TextView)findViewById(R.id.basefare)).setText("₹ "+String.valueOf(dummprice+DiscAmount));
            ((TextView)findViewById(R.id.coupdiscprice)).setText("₹ "+String.valueOf(DiscAmount));
            ((TextView)findViewById(R.id.savingstv)).setText("₹ "+String.valueOf(DiscAmount));
            ((TextView)findViewById(R.id.subtotal)).setText("₹ "+String.valueOf(dummprice));
            ((TextView)findViewById(R.id.servicecharge)).setText("₹ "+jobj.getString("service_charge"));
            ((TextView)findViewById(R.id.grandtotaltv)).setText("₹ "+String.valueOf(Grandtotal));
            ((TextView)findViewById(R.id.roompricetv)).setText("₹ "+ Grandtotal);
            ((TextView)findViewById(R.id.serviceextratv)).setText(jobj.getString("pricegst_of"));

            if(TcashStatus.equals("ACTIVE") && sharedprefs.getLoggedin() && AvailTCash >0 ){
                TcashCB.setVisibility(View.VISIBLE);
                UsableTCash = (int) Math.ceil(dummprice* TCashDiscPer /100);
                if (UsableTCash > TcashMax) { UsableTCash = TcashMax; }
                if (UsableTCash > AvailTCash) { UsableTCash = AvailTCash; }
                TcashCB.setText("Use Trip Cash ");
                TcashCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int disc = DiscAmount;
                    if(isChecked){Grandtotal -= UsableTCash; disc+= UsableTCash; TcashUsed = UsableTCash;}else {Grandtotal += UsableTCash; TcashUsed =0;}
                    ((TextView)findViewById(R.id.grandtotaltv)).setText("₹ "+String.valueOf(Grandtotal));
                    ((TextView)findViewById(R.id.roompricetv)).setText("₹ "+String.valueOf(Grandtotal));
                    ((TextView)findViewById(R.id.savingstv)).setText("₹ "+String.valueOf(disc));
                    if(CouponType.equals("CASHBACK")){
                        ((TextView)findViewById(R.id.savingstv)).setText("Discount ₹"+String.valueOf(DiscAmount)+" + CashBack ₹"+String.valueOf(ReturnTcash));
                    }
                });
            }else{ TcashCB.setVisibility(View.GONE); TcashUsed = 0; UsableTCash=0;}

            if(!CouponType.equals("")){load_Coupon_Data();}

        } catch (JSONException|NumberFormatException e) {
//            e.printStackTrace();
            Utils.setSingleBtnAlert(Hotel_Room_Review_Act.this, getResources().getString(R.string.error_occur), "Ok", true);
        }
        if(loadDlg.isShowing()){loadDlg.dismiss();}
    }

    public void continuepay(View v){
        String Fname = FNameET.getText().toString(),Lname = LNameET.getText().toString(),Email = EMailET.getText().toString(),
                Phone = phoneET.getText().toString();
        TextInputLayout fnameTL = findViewById(R.id.fnameTil), lnameTL = findViewById(R.id.lnameTil),
                        emailTL = findViewById(R.id.emaiTil), phoneTL = findViewById(R.id.phoneTil), ExpectLL = findViewById(R.id.expectll);

        fnameTL.setErrorEnabled(false);lnameTL.setErrorEnabled(false);
        emailTL.setErrorEnabled(false);phoneTL.setErrorEnabled(false);ExpectLL.setErrorEnabled(false);

        if(Objects.equals(Fname, "")){
            fnameTL.setError("Please Enter First Name ");
        }else if(Objects.equals(Lname, "")){
            lnameTL.setError("Please Enter Last Name ");
        }else if(Objects.equals(Email, "") || Utils.isEmailValid(Email)){
            emailTL.setError(Objects.equals(Email, "") ? "Please Enter Email" : "Please Enter Valid Email");
        }else if(Objects.equals(Phone, "")){
            phoneTL.setError("Please Enter Mobile Number ");
        }else if(Objects.equals(expecttime, "")){
            ExpectLL.setError("Please Select Expected Time ");
            phoneET.clearFocus();
        }else {
            phoneET.clearFocus();

            Bundle bundle = new Bundle();
            bundle.putString("FName", Fname);
            bundle.putString("LName", Lname);
            bundle.putString("Email", Email);
            bundle.putString("Cindisp", Cintv.getText().toString());
            bundle.putString("Coutdisp", Couttv.getText().toString());
            bundle.putString("Cindate", Cindate);
            bundle.putString("Coutdate", Coutdate);
            bundle.putString("TotalPrice", String.valueOf(Grandtotal));
            bundle.putString("NoofNights", String.valueOf(NightCount));
            bundle.putString("NoofRooms", String.valueOf(RoomCount));
            bundle.putString("Roomname", Rname);
            bundle.putString("Adults", String.valueOf(AdultCount));
            bundle.putString("Childs", ChildCount);
            bundle.putString("Phoneno", Phone);
            bundle.putString("Hid", Hid);
            bundle.putString("Hname", Hname);
            bundle.putString("Extrabed", String.valueOf(extrabed));
            bundle.putString("Rid", Rid);
            bundle.putString("Rname", Rname);
            bundle.putString("Expectedtime", expecttime);
            bundle.putString("HotelAddr", Hoteladdr);
            bundle.putString("postcode", postcode);
            bundle.putString("HotelPhone", HotelMob);
            bundle.putString("RRateid", RRateid);
            bundle.putString("Supplierprice", String.valueOf(Supprice * RoomCount * NightCount));
            bundle.putString("Gst_price", String.valueOf(supgst));
            bundle.putString("Commission", String.valueOf(Commission));
            bundle.putString("Gst_in_status", Gst_in_status);
            bundle.putString("tripcashused", String.valueOf(TcashUsed));
            bundle.putString("returntripcash", String.valueOf(ReturnTcash));
            bundle.putString("coupondiscount", String.valueOf(CouponDiscount));
            bundle.putString("couponid", CouponID);
            bundle.putString("userid", UserID);
            bundle.putString("coupontype", (CouponType.equals("")?"none":CouponType));

            bundle.putString("cin24", Objects.equals(H24_hrs, "ACTIVE") ? "24 HRS Check-In " : "Std Check-In ("+cinexpect+"-"+coutexpect+")");

            Intent intent = new Intent(this, Hotel_Payment_Act.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void cancelpolicy(View v){new Cancel_policy_dialogF().show(getSupportFragmentManager(),"Cancel policy");}

    public void refresh_data(){

        Map<String, String> params = new HashMap<>();
        params.put("type", "refresh");
        params.put("rcount", String.valueOf(RoomCount));
        params.put("ncount", String.valueOf(NightCount));
        params.put("sglprice", String.valueOf(Supprice));
        params.put("room_total", String.valueOf(Roomtotprice));
        params.put("htype_id", String.valueOf(Hoteltypeid));
        params.put("h_commission", String.valueOf(Commission));
        params.put("hotel_gstin", Hgst_in);

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL+"hotelbook.php", loadDlg, params, true, response -> {
            try {
                load_bill_data(new JSONObject(response));
            } catch (JSONException e) {
                //e.printStackTrace();
                if(loadDlg.isShowing()){loadDlg.dismiss();}
                Utils.setSingleBtnAlert(Hotel_Room_Review_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
            }
        });
    }

    public void loginuser(View v){ new LoginUser().show(getFragmentManager(),"Login_user_details"); }

    public void getlogindetails(Bundle bb){
        findViewById(R.id.usetv).setVisibility(View.GONE);
        FNameET.setText(bb.getString("fname"));
        LNameET.setText(bb.getString("lname"));
        EMailET.setText(bb.getString("email"));
        phoneET.setText(bb.getString("mobile"));
        UserID = bb.getString("userid");
        AvailTCash = Integer.parseInt(bb.getString("tripcash"));

        loadDlg.show();
        refresh_data();
    }

    public void load_Coupon_Data() {

        int discount = (int) Math.ceil(Roomtotprice * NCoupDiscPer / 100);
        int dummprice = Roomtotprice * RoomCount * NightCount;
        final int DiscAmount = (int) Math.ceil(dummprice*Roomdiscper/100);
        if(discount>NcoupMax){discount = NcoupMax;}

        if(CouponType.equals("CASHBACK")){
            ReturnTcash = discount;CouponDiscount = 0;
            Snackbar.make(findViewById(R.id.mainlay), "Make the Hotel Booking and Get CashBack to Your Trip Wallet", Snackbar.LENGTH_LONG)
                    .setAction("OK", view -> { })
                    .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                    .show();

            if(TcashCB.isChecked() && TcashStatus.equals("ACTIVE")){
                Grandtotal -= TcashUsed;
                ((TextView)findViewById(R.id.grandtotaltv)).setText("₹ "+String.valueOf(Grandtotal));
                ((TextView)findViewById(R.id.roompricetv)).setText("₹ "+String.valueOf(Grandtotal));
            }

            ((TextView)findViewById(R.id.savingstv)).setText("Discount ₹"+String.valueOf(DiscAmount)+" + CashBack ₹"+String.valueOf(discount));

        }else {//if(CouponType.equals("DISCOUNT")){

            ReturnTcash = 0;
            CouponDiscount = discount;
            TcashUsed = 0;
            TcashCB.setVisibility(View.GONE);

            ((TextView)findViewById(R.id.coupdiscprice)).setText("₹ "+String.valueOf(DiscAmount+discount));
            ((TextView)findViewById(R.id.savingstv)).setText("₹ "+String.valueOf(DiscAmount+discount));
            ((TextView)findViewById(R.id.subtotal)).setText("₹ "+String.valueOf(dummprice-discount));

            Grandtotal -= discount;
            ((TextView)findViewById(R.id.grandtotaltv)).setText("₹ "+String.valueOf(Grandtotal));
            ((TextView)findViewById(R.id.roompricetv)).setText("₹ "+ Grandtotal);

        }
    }

}