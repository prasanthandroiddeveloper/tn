package com.tripnetra.tripnetra.places;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class GetNearbyPlacesData implements GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Activity activity;
    private Polyline polyline;
    private LayoutInflater inflater;
    private Double fplacelat, tplacelat, fplacelong, tplacelong;

    GetNearbyPlacesData(Activity act) {
        this.activity = act;
        inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    void getplaces(Object... params) {

        mMap = (GoogleMap) params[0];
        String newlat = String.valueOf(params[1]),
                     newlang = String.valueOf(params[2]);
        final int Kms = (int) params[3];
        Bundle bundle = (Bundle) params[4];
        fplacelat = bundle.getDouble("Fplacelat");
        tplacelat = bundle.getDouble("Tplacelat");
        fplacelong = bundle.getDouble("Fplacelong");
        tplacelong = bundle.getDouble("Tplacelong");

        if(fplacelat != 0 && tplacelat != 0){
            String url = "https://maps.googleapis.com/maps/api/directions/json?"+"origin="+fplacelat+","+fplacelong+"&destination="+tplacelat+","+tplacelong+"&sensor=false";
            //Log.d("urllll", url);
            getdirections(url);
        }

        Map<String, String> paras = new HashMap<>();
        paras.put("lattitude", newlat);
        paras.put("langitude", newlang);
        paras.put("Radius", String.valueOf(Kms));

        new VolleyRequester(activity).ParamsRequest(1, ((G_Class) activity.getApplicationContext()).getBaseurl()+Config.GET_PLACES_URL, null, paras, false, response -> {
            if(Objects.equals(response, "NO result")){
                Utils.setSingleBtnAlert(activity,"No Places Found With Your Search Criteria","Ok",false);
            }else {
                ParseData(response);
            }
        });

    }

    private void ParseData(String result) {

        try {
            final JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {

                final JSONObject jobject = (JSONObject) jsonArray.get(i);

                MarkerOptions markerOptions = new MarkerOptions();
                double lat = Double.parseDouble(jobject.getString("latitude")),
                lng = Double.parseDouble(jobject.getString("longitude"));

                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(jobject.getString("place_name")).snippet(String.valueOf(i));

                if(jobject.getString("category_name").toLowerCase().contains("temple")){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icontemple));
                }else if(jobject.getString("category_name").toLowerCase().contains("waterfall")){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.iconfall));
                }else if(jobject.getString("category_name").toLowerCase().contains("seeing")){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.iconsite));
                }else {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                mMap.addMarker(markerOptions);

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public View getInfoContents(Marker marker) {
                        @SuppressLint("InflateParams")
                        View popup = inflater.inflate(R.layout.map_popup, null);

                        try {
                            TextView tv = popup.findViewById(R.id.title),body = popup.findViewById(R.id.body),times = popup.findViewById(R.id.times);
                            JSONObject jobj = (JSONObject) jsonArray.get(Integer.parseInt(marker.getSnippet()));
                            tv.setText(marker.getTitle());
                            body.setText(jobj.getString("short_description"));
                            times.setText(jobj.getString("timings"));

                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }

                        return popup;
                    }
                });

                mMap.setOnInfoWindowClickListener(marker -> {
                    Intent intent = new Intent(activity, Marker_Details_Act.class);
                    try {
                        JSONObject jobj = (JSONObject) jsonArray.get(Integer.parseInt(marker.getSnippet()));
                        intent.putExtra("JsonObject", String.valueOf(jobj));
                        activity.startActivity(intent);
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                });

                if(fplacelat == 0 && tplacelat == 0 && fplacelong == 0 && tplacelong == 0) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                }
            }

            mMap.setOnMarkerClickListener(this);

        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker){

        if(polyline != null){polyline.remove();}

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+ Current_Location_Act.getlocation().latitude+","+ Current_Location_Act.getlocation().longitude
                +"&destination="+marker.getPosition().latitude+","+marker.getPosition().longitude+"&sensor=false";

        getdirections(url);
        marker.showInfoWindow();

        return false;
    }

    @SuppressLint("SetTextI18n")
    private void getdirections(String url) {
        final ProgressDialog loading = ProgressDialog.show(activity, "Getting Route", "Please wait...", false, false);

        new VolleyRequester(activity).ParamsRequest(1, url, null, null, true, response -> {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray distarr = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

                String diststring = distarr.getJSONObject(0).getJSONObject("distance").getString("value"),
                        durastring = distarr.getJSONObject(0).getJSONObject("duration").getString("value");
                Double dist = Double.valueOf(diststring)/1000,
                        dura = Double.valueOf(durastring)/60;
                dist = Double.valueOf(new DecimalFormat("#.##").format(dist));
                int dur = (int) Math.ceil(dura);

                TextView distTV = ((Activity)activity).findViewById(R.id.distTV),
                        durTV = ((Activity)activity).findViewById(R.id.durTV);
                LinearLayout Roadlayt = ((Activity)activity).findViewById(R.id.roadlayt);
                Roadlayt.setVisibility(View.VISIBLE);

                distTV.setText(""+dist+" KM's");

                if(dur<60) {durTV.setText(" "+dur+" Min's");}else{if(dur%60==0){
                    if(dur/60==1){
                        durTV.setText(" 1 Hour");
                    } else {
                        durTV.setText(" "+dur/60+" Hour's");
                    }
                }else {
                    durTV.setText(" "+dur/60+" H "+dur%60+" Min's");}
                }

                String encodedString = json.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                List<LatLng> list = decodePoly(encodedString);
                polyline = mMap.addPolyline(new PolylineOptions().addAll(list).width(20).color(Color.parseColor("#4285F4")).geodesic(true));
                if(fplacelat != 0 && tplacelat != 0){
                    LatLng latlng = new LatLng((fplacelat+tplacelat)/2,(fplacelong+tplacelong)/2);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                }
                polyline.setTag("");
            }
            catch (JSONException e) {
                //e.printStackTrace();
            }
            loading.dismiss();
        });

    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length(),lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),(((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

}