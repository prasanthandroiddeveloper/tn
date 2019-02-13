package com.tripnetra.tripnetra.utils;

/* Created by G46567 */

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SharedPrefs(Context ctx){
        prefs = ctx.getSharedPreferences("TripnetraPrefs", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean logdin){
        editor.putBoolean("loggedInmode",logdin);
        editor.apply();
    }

    public void setPrefDetails(String Email,String Passwd,String LoginId,String DetID){
        editor.clear();
        editor.putString("usermail", Email);
        editor.putString("userpass", Passwd);
        editor.putString("user_login_id", LoginId);
        editor.putString("user_details_id", DetID);
        editor.apply();
    }

    public void setUserNames(String Name,String Fname,String Lname,String mobile){
        editor.putString("username", Name);
        editor.putString("fname", Fname);
        editor.putString("lname", Lname);
        editor.putString("mobile", mobile);
        editor.apply();
    }

    public void setTripCash(String string){
        editor.putString("tripcash", string);
        editor.apply();
    }

    public String getTripcash(){return  prefs.getString("tripcash","");}

    public void setFcm_Token(String string){
        editor.putString("fcm_token", string);
        editor.apply();
    }

    public String getFcm_Token(){return  prefs.getString("fcm_token","");}

    public boolean getLoggedin(){return prefs.getBoolean("loggedInmode", false);}

    public String getUserId(){return  prefs.getString("user_details_id","");}

    public String getUName(){return  prefs.getString("username","");}

    public String getFName(){return  prefs.getString("fname","");}

    public String getLName(){return  prefs.getString("lname","");}

    public String getUMail(){return  prefs.getString("usermail","");}

    public String getUMobile(){return  prefs.getString("mobile","");}

    public void clearall(){
        editor.clear();
        editor.apply();
    }
}