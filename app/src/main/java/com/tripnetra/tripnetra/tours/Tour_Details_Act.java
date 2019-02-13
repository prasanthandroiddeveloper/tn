package com.tripnetra.tripnetra.tours;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.ViewPagerAdapter;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.tours.fragments.Cancel_policy_Frag;
import com.tripnetra.tripnetra.tours.fragments.Description_Frag;
import com.tripnetra.tripnetra.tours.fragments.Itinerary_Frag;
import com.tripnetra.tripnetra.tours.fragments.Terms_conds_Frag;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Tour_Details_Act extends AppCompatActivity {

    String Tdate, Tid, Cityname, Cityid;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewFlipper flipper;
    Button book;
    Bundle bundle;
    LoadingDialog LoadDlg;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_details);

        Tdate = Objects.requireNonNull(getIntent().getExtras()).getString("journeydate");
        Tid = getIntent().getExtras().getString("tourid");

        TextView Tnametv = findViewById(R.id.tnameTv), tdaysTv = findViewById(R.id.tdaysTv), tpriceTv = findViewById(R.id.tpriceTv);
        Tnametv.setText(getIntent().getExtras().getString("name"));
        tdaysTv.setText(getIntent().getExtras().getString("tourdays"));
        tpriceTv.setText("₹ " + getIntent().getExtras().getString("tourprice"));
        Cityid = getIntent().getExtras().getString("cityid");
        Cityname = getIntent().getExtras().getString("cityname");

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        LoadDlg = new LoadingDialog(this);
        LoadDlg.setCancelable(false);
        Objects.requireNonNull(LoadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LoadDlg.show();

        tabLayout = findViewById(R.id.tabs);
        book = findViewById(R.id.book);
        flipper = findViewById(R.id.viewflpr);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);

        findViewById(R.id.calbutton).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", getResources().getString(R.string.tnetramobile), null))));

        getdescrption();
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Description_Frag descf = new Description_Frag();
        Itinerary_Frag itef = new Itinerary_Frag();
        Terms_conds_Frag termsf = new Terms_conds_Frag();
        Cancel_policy_Frag canclf = new Cancel_policy_Frag();
        descf.setArguments(bundle);itef.setArguments(bundle);
        termsf.setArguments(bundle);canclf.setArguments(bundle);

        adapter.addFrag(descf,"Overview");
        adapter.addFrag(itef,"Itinerary");
        adapter.addFrag(termsf,"T & C");
        adapter.addFrag(canclf,"Cancellation");
        viewPager.setAdapter(adapter);

    }

    private void getdescrption() {

        new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TOUR_DETAILS_URL+"?tourid="+Tid, LoadDlg, null, true, response -> {
            if(Objects.equals(response, "No Result")){
                Utils.setSingleBtnAlert(Tour_Details_Act.this, getResources().getString(R.string.something_wrong), "Ok", true);
                LoadDlg.dismiss();
            }else {
                try {
                    JSONObject jObj = new JSONObject(response);
                    bundle = new Bundle();

                    bundle.putString("journeydate",Tdate);
                    bundle.putString("sightseen_id",jObj.getString("sightseen_id"));
                    bundle.putString("sightseen_name",jObj.getString("sightseen_name"));
                    bundle.putString("sightseen_supplier_id",jObj.getString("sightseen_supplier_id"));
                    bundle.putString("sightseen_category_id",jObj.getString("sightseen_category_id"));
                    bundle.putString("sightseen_destination",jObj.getString("sightseen_destination"));
                    bundle.putString("sightseen_duration",jObj.getString("sightseen_duration"));
                    bundle.putString("sightseen_duration_type",jObj.getString("sightseen_duration_type"));
                    bundle.putString("sightseen_price",jObj.getString("sightseen_price"));
                    bundle.putString("sightseen_description",jObj.getString("sightseen_description"));
                    bundle.putString("sightseen_itinerary",jObj.getString("sightseen_itinerary"));
                    bundle.putString("sightseen_inclusion",jObj.getString("sightseen_inclusion"));
                    bundle.putString("sightseen_terms_conditions",jObj.getString("sightseen_terms_conditions"));
                    bundle.putString("sightseen_images",jObj.getString("sightseen_images"));
                    bundle.putString("cancellation_policy",jObj.getString("cancellation_policy"));
                    bundle.putString("service_tax",jObj.getString("service_tax"));
                    bundle.putString("partial_details",jObj.getString("partial_details"));
                    bundle.putString("cityname",Cityname);
                    bundle.putString("cityid",Cityid);

                    String[] array_items = jObj.getString("sightseen_images").split(",");
                    if(array_items.length!=0) {
                        for (String array_item : array_items) {
                            ImageView imageView = new ImageView(Tour_Details_Act.this);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            Glide.with(Tour_Details_Act.this).load(((G_Class) getApplicationContext()).getImageurl()+Config.SIGHT_IMAGE + array_item).into(imageView);

                            flipper.addView(imageView);
                        }
                        if(array_items.length>1) {flipper.setAutoStart(true);flipper.setFlipInterval(3000);flipper.startFlipping();}
                    }

                    book.setOnClickListener(view -> {
                        Intent intent = new Intent(Tour_Details_Act.this,Tour_Review_Act.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    });

                    setupViewPager(viewPager);

                } catch (JSONException e) {
                    //e.printStackTrace();
                    LoadDlg.dismiss();
                    Utils.setSingleBtnAlert(Tour_Details_Act.this, getResources().getString(R.string.something_wrong), "Ok", true);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.postDelayed(() -> {
            if(LoadDlg.isShowing()) { LoadDlg.dismiss(); }
            viewPager.setCurrentItem(1);
        }, 1000);
    }

}