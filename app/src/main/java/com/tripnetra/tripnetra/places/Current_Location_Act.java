package com.tripnetra.tripnetra.places;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.utils.Utils;

import java.text.DecimalFormat;

import static com.tripnetra.tripnetra.utils.Constants.MULTIPLE_PERMISSIONS_CODE;
import static com.tripnetra.tripnetra.utils.Constants.Place_Code;
import static com.tripnetra.tripnetra.utils.Constants.Place_Code2;

@SuppressWarnings("deprecation")
public class Current_Location_Act extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap gmap;
    GoogleApiClient googapiclient;
    LocationRequest locRequest;
    public static LatLng loclatlong;
    Double placelat, placelong, fplacelat, tplacelat, fplacelong, tplacelong;
    int Kms;
    View mapView;
    Location currlocation;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        loaddata();
    }

    private void loaddata() {

        if (!Utils.CheckGooglePlayServices(this)) {
            Utils.setSingleBtnAlert(this, "Google Play Services are needed to Run this App", "Ok", true);
            return;
        }

        if (!Utils.checkPermissions(this)) { return; }

        locRequest = new LocationRequest();
        locRequest.setInterval(5000);
        locRequest.setFastestInterval(1000);
        locRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //locRequest.setSmallestDisplacement(1);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LocationServices.FusedLocationApi.requestLocationUpdates(googapiclient, locRequest, this);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, locResp -> {});

            task.addOnFailureListener(this, e -> {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(Current_Location_Act.this, 0);
                        } catch (IntentSender.SendIntentException sendEx) {
                            //Log.d("Exception", "SendIntentException ");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            });
        }
    }

    @SuppressLint({"ResourceType"})
    @Override
    public void onMapReady(GoogleMap googleMap) {

        gmap = googleMap;
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 5));
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gmap.setMyLocationEnabled(false);

        if (mapView != null && mapView.findViewById(1) != null) {
            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            //ImageView locButton = mapView.findViewById(2);
            //locButton.setImageResource(R.drawable.locicon);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.setMargins(0, 0, 30, 30);

        }
        googapiclient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googapiclient.connect();
        gmap.setMyLocationEnabled(true);

    }

    @SuppressLint("MissingPermission")
    public void onLocationChanged(Location location) {
        currlocation = location;

        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setMapToolbarEnabled(false);

        loclatlong = new LatLng(location.getLatitude(), location.getLongitude());

    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    public static LatLng getlocation(){return loclatlong;}

    public void selectfrom(View v){
        try {
            startActivityForResult(new PlaceAutocomplete.IntentBuilder(2).build(this), Place_Code);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            //e.printStackTrace();
        }
    }

    public void selectto(View v){
        try {
            startActivityForResult(new PlaceAutocomplete.IntentBuilder(2).build(this), Place_Code2);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            //e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && (requestCode == Place_Code || requestCode == Place_Code2)) {

            DecimalFormat df6 = new DecimalFormat("0.000000");
            Place place = PlaceAutocomplete.getPlace(this, data);

            LatLng lattlan = place.getLatLng();

            if (requestCode == Place_Code) {
                ((TextView) findViewById(R.id.fromplacetv)).setText(place.getAddress());
                fplacelat = Double.valueOf(df6.format(lattlan.latitude + 0.00000001));
                fplacelong = Double.valueOf(df6.format(lattlan.longitude + 0.00000001));
            } else {
                ((TextView) findViewById(R.id.toplacetv)).setText(place.getAddress());
                tplacelat = Double.valueOf(df6.format(lattlan.latitude + 0.00000001));
                tplacelong = Double.valueOf(df6.format(lattlan.longitude + 0.00000001));
            }
        }

        if(fplacelat != null && tplacelat != null){

            Location lattlng1 = new Location("");
            lattlng1.setLatitude((fplacelat+tplacelat)/2);
            lattlng1.setLongitude((fplacelong+tplacelong)/2);

            Location lattlng2 = new Location("");
            lattlng2.setLatitude(fplacelat);
            lattlng2.setLongitude(fplacelong);

            placelat = lattlng1.getLatitude();
            placelong = lattlng1.getLongitude();

            Kms = Math.round((lattlng1.distanceTo(lattlng2)+15000)/1000);
            getplaces();
        }

    }

    public void getplaces(){

        gmap.clear();

        if (placelat == 0 || placelong == 0) {
            placelat = currlocation.getLatitude();
            placelong = currlocation.getLongitude();
        }

        Bundle bundle = new Bundle();
        bundle.putDouble("Fplacelat",fplacelat);
        bundle.putDouble("Fplacelong",fplacelong);
        bundle.putDouble("Tplacelat",tplacelat);
        bundle.putDouble("Tplacelong",tplacelong);

        Object[] obj = new Object[5];obj[0] = gmap;obj[1] = placelat;obj[2] = placelong;obj[3] = Kms;obj[4] = bundle;

        GetNearbyPlacesData getNearbyplacesdata = new GetNearbyPlacesData(Current_Location_Act.this);
        getNearbyplacesdata.getplaces(obj);

    }

    public void placesnearme(View v){
        if(currlocation!=null){
            placelat = 0d;
            placelong = 0d;
            fplacelat=0d;
            fplacelong=0d;
            tplacelat=0d;
            tplacelong=0d;
            Kms=30;
            getplaces();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissionsList[], @NonNull int[] grantResults)  {

        if (requestCode == MULTIPLE_PERMISSIONS_CODE &&  grantResults.length > 0) {

            Utils.RequestPermissions(this, permissionsList, grantResults, response -> {
                if(response.equals("accept")){
                    loaddata();
                }else{
                    finish();
                }
            });

        }
    }

}