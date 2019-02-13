package com.tripnetra.tripnetra.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.tripnetra.tripnetra.R;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.tripnetra.tripnetra.utils.Constants.MULTIPLE_PERMISSIONS_CODE;

public class Utils {

    private static AlertDialog myAlertDialog;
    private static AlertDialog.Builder alertDialogBuilder = null;

    public static void setSingleBtnAlert(final Activity activity, String msg, String btnName, final boolean finshact) {
        if (myAlertDialog != null && myAlertDialog.isShowing()) {
            myAlertDialog.dismiss();
            myAlertDialog = null;
        }
        alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setCancelable(false).setMessage(msg).setPositiveButton(btnName, (dialog, which) -> {
            dialog.dismiss();
            if (finshact) {
                activity.finish();
            }
        });
        activity.runOnUiThread(() -> {
            if (!activity.isFinishing()) {
                Utils.myAlertDialog = Utils.alertDialogBuilder.create();
                Utils.myAlertDialog.show();
            }
        });
    }

    public static boolean isDataConnectionAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String orderid(){return "TRIP"+System.currentTimeMillis()/1000+new Random().nextInt(8)+5;}

    public static void clearfocus(final Activity activity){
        View vw = activity.getCurrentFocus();
        if (vw != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {imm.hideSoftInputFromWindow(vw.getWindowToken(), 0);}
            vw.performClick();
        }
    }

    public static boolean isEmailValid(CharSequence email){return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();}

   /* public static void SendNotification(final Activity activity,String msg){
        NotificationManager notmgr = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notfctn = new Notification.Builder(activity)
                .setContentTitle("Tripnetra Booking Confirmation")
                .setContentText(msg)
                .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.drawable.logo1))
                .setSmallIcon(R.drawable.load5)
                .build();

        notfctn.flags |= Notification.FLAG_AUTO_CANCEL;
        assert notmgr != null;
        notmgr.notify(0, notfctn);
    }*/



    public static void SendNotification(Activity activity,String msg) {

       // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notificationBuilder = new Notification.Builder(activity)
                .setContentTitle("Tripnetra Booking Confirmation")
                .setContentText(msg)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.drawable.logo1))
                .setSmallIcon(R.drawable.load5);


        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        // Log.i("m", String.valueOf(m));
        notificationManager.notify(m, notificationBuilder.build());
        //Objects.requireNonNull(notificationManager).notify(0, notificationBuilder.build());
    }

    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, Constants.Compress_Quality, baos);

        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    public static int getscreenwidth(Activity activity){

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //int width=dm.widthPixels;
        return dm.widthPixels;
    }

    public static boolean checkPermissions(Activity activity) {

        String[] permissions= new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ActivityCompat.checkSelfPermission(activity,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[0]),MULTIPLE_PERMISSIONS_CODE );
            return false;
        }
        return true;

    }

    public static boolean CheckGooglePlayServices(Activity activity) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(activity);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(activity, result,0).show();
            }
            return false;
        }
        return true;
    }

    public static void RequestPermissions(Activity activity, String permissionsList[], int[] grantResults, final Callback callback) {

        Boolean nevercheck = false;

        for (int i = 0 ; i < permissionsList.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                String permission = permissionsList[i];

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && !activity.shouldShowRequestPermissionRationale( permission )){
                    nevercheck = true;
                }
            }
        }

        StringBuilder Permibuildr = new StringBuilder();
        Permibuildr.append("TripNetra App Needs SMS,Location Permissions to run");
        if(nevercheck){
            Permibuildr.append(" ,Enable them in Settings");
        }

        final Boolean finalNevercheck = nevercheck;
        final Activity factivity = activity;
        new android.app.AlertDialog.Builder(activity).setMessage(String.valueOf(Permibuildr))
                .setPositiveButton("Accept", (dialog, id) -> {
                    if(!finalNevercheck){
                        callback.getResponse("accept");
                    }else{
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", factivity.getPackageName(), null);
                        intent.setData(uri);
                        factivity.startActivityForResult(intent, MULTIPLE_PERMISSIONS_CODE);
                    }
                })
                .setNegativeButton("Deny", (dialog, id) -> callback.getResponse("deny")).setCancelable(true).create().show();

    }

    public interface Callback{ void getResponse(String response);}

    public static String ChangeDateFormat(String dates, int  format) {

        String ss;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dates);

            switch (format) {
                case 1: //20-SEP-2018
                    sdf.applyPattern("dd-MMM-yyyy");
                    break;
                case 2: //WED, SEP 20, 2018
                    sdf.applyPattern("EEE, MMM dd, yyyy");
                    break;
                case 3: //SEP 20
                    sdf.applyPattern("MMM dd");
                    break;
                case 4: //SEP 20 1994
                    sdf.applyPattern("MMM dd yyyy");
                    break;
                case 5: //20 SEP
                    sdf.applyPattern("dd MMM");
                    break;
                case 6:
                    sdf.applyPattern("dd");
                    break;
                case 7:
                    sdf.applyPattern("MMM");
                    break;
                case 8:
                    sdf.applyPattern("EEE");
                    break;
                default://2018-09-22
                    sdf.applyPattern("yyyy-MM-dd");
                    break;
            }
            ss = sdf.format(date);
        } catch (ParseException e) {
            //e.printStackTrace();
            ss = dates;
        }

        return ss;

    }

    public static Date StrtoDate (int format,String string) {
        SimpleDateFormat sdf ;

        switch (format) {
            case 1:
                sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                break;
            case 2:
                sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.getDefault());
                break;
            default: // case 0
                sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                break;
        }
        try {
            return sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DatetoStr (Object date,int  format) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String ss ;
        switch (format) {
            case 1: //20-SEP-2018
                sdf.applyPattern("dd-MMM-yyyy");
                break;
            case 2: //WED, SEP 20, 2018
                sdf.applyPattern("EEE, MMM dd, yyyy");
                break;
            case 3: //SEP 20
                sdf.applyPattern("MMM dd");
                break;
            case 4: //10:20 AM
                sdf.applyPattern("hh:mm aa");
                break;
            case 5: //Sep 20,1994
                sdf.applyPattern("MMM dd, yyyy");
                break;
            case 6:
                sdf.applyPattern("dd");
                break;
            case 7:
                sdf.applyPattern("MMM");
                break;
            case 8:
                sdf.applyPattern("EEE");
                break;
            default://2018-09-22
                sdf.applyPattern("yyyy-MM-dd");
                break;
        }
        ss = sdf.format(date);

        return ss;

    }

}