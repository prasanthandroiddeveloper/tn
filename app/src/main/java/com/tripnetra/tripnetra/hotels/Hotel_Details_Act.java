package com.tripnetra.tripnetra.hotels;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.adapters.ViewPagerAdapter;
import com.tripnetra.tripnetra.hotels.fragments.Accomodation_Frag;
import com.tripnetra.tripnetra.hotels.fragments.Amenities_Frag;
import com.tripnetra.tripnetra.hotels.fragments.Hotel_Location_Frag;
import com.tripnetra.tripnetra.hotels.fragments.Hotel_OverView_Frag;
import com.tripnetra.tripnetra.hotels.fragments.Hotel_reviews_Frag;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.tripnetra.tripnetra.utils.Constants.Pager_Timeout;

public class Hotel_Details_Act extends AppCompatActivity {

    String HotelId,HoteName;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewFlipper flipper;
    Bundle bundle;
    LoadingDialog LoadDlg;
    G_Class gcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details);
        HotelId = Objects.requireNonNull(getIntent().getExtras()).getString("HotelId");
        HoteName = getIntent().getExtras().getString("HotelName");

        gcv = (G_Class) getApplicationContext();
        gcv.setHotelId(HotelId);
        gcv.setHotelname(HoteName);
        gcv.setCoupcode(getIntent().getExtras().getString("Couponcode"));

        flipper = findViewById(R.id.pager);
        setSupportActionBar(findViewById(R.id.toolbar));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        viewPager = findViewById(R.id.viewpager);
        final Button button = findViewById(R.id.showroom);

        tabLayout = findViewById(R.id.tabs);

        LoadDlg = new LoadingDialog(this);
        LoadDlg.setCancelable(false);
        Objects.requireNonNull(LoadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LoadDlg.show();

        getdescrption();

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(5);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(position == 1) {
                    button.setVisibility(View.INVISIBLE);
                } else {
                    button.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Hotel_OverView_Frag hofr = new Hotel_OverView_Frag();
        hofr.setArguments(bundle);

        adapter.addFrag(hofr, "OverView");
        adapter.addFrag(new Accomodation_Frag(), "Accomodation");
        adapter.addFrag(new Amenities_Frag(), "Amenities");
        adapter.addFrag(new Hotel_reviews_Frag(), "Reviews");
        adapter.addFrag(new Hotel_Location_Frag(), "Location");
        viewPager.setAdapter(adapter);
    }

    public  void gotobtn(View v){viewPager.setCurrentItem(1);}

    private void getdescrption() {

        Map<String, String> params = new HashMap<>();
        params.put("hotelid", HotelId);

        new VolleyRequester(this).ParamsRequest(1, gcv.getBaseurl()+Config.TRIP_URL+"overviewdata.php", LoadDlg, params, true, response -> {
            try {
                JSONObject jObj1 = new JSONObject(response);
                String[] array_items = jObj1.getString("hotel_images").split(",");
                if(array_items.length!=0){
                    for (String array_item : array_items) {
                        ImageView imageView = new ImageView(Hotel_Details_Act.this);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        Glide.with(Hotel_Details_Act.this).load(gcv.getImageurl()+"hotel_images/" + array_item).into(imageView);

                        flipper.addView(imageView);
                    }
                    if(array_items.length>1) {flipper.setAutoStart(true);flipper.setFlipInterval(3000);flipper.startFlipping();}
                }

                bundle = new Bundle();
                bundle.putString("hoteldesc",jObj1.getString("hotel_info"));

                setupViewPager(viewPager);
            } catch (JSONException e) {
                //e.printStackTrace();
                if(LoadDlg.isShowing()) { LoadDlg.dismiss(); }
                Utils.setSingleBtnAlert(Hotel_Details_Act.this,getResources().getString(R.string.something_wrong),"Ok",true);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.postDelayed(() -> {
            if(LoadDlg.isShowing()) { LoadDlg.dismiss(); }
            viewPager.setCurrentItem(1);
        }, Pager_Timeout);
    }

}