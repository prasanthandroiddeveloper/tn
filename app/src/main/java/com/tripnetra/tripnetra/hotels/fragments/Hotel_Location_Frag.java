package com.tripnetra.tripnetra.hotels.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Hotel_Location_Frag extends Fragment implements OnMapReadyCallback {

    MapView mapView = null;
    String HID,Hname;
    GoogleMap mMap;
    double lat=13.683273,lang=79.347227;
    G_Class gcv;
    public Hotel_Location_Frag() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotel_location, container, false);

        mapView = view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        gcv = (G_Class) Objects.requireNonNull(getActivity()).getApplicationContext();
        HID = gcv.getHotelId();
        Hname = gcv.getHotelName();
        return view;
    }

    private void getlocation() {

        Map<String, String> params = new HashMap<>();
        params.put("hotelid", HID);

        new VolleyRequester(getActivity()).ParamsRequest(1, gcv.getBaseurl()+Config.TRIP_URL+"getlocation.php",
                null, params, false, response -> {
            try {
                JSONObject jObj = new JSONObject(response);
                lat = Double.parseDouble(jObj.getString("latitude"));
                lang = Double.parseDouble(jObj.getString("longitude"));

                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lang)).title(Hname));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lang), 14.0f));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lang), 15));

            } catch (JSONException e) {
                //e.printStackTrace();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getlocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}