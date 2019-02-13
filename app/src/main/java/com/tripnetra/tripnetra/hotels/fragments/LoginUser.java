package com.tripnetra.tripnetra.hotels.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.account.ForgotPasword_Frag;
import com.tripnetra.tripnetra.hotels.Hotel_Room_Review_Act;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.SharedPrefs;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginUser extends DialogFragment {

    public LoginUser(){}

    LoadingDialog loadDlg;
    TextInputLayout EmailTIL,PwdTIL;
    EditText EmailET,PassET;
    String FcmToken;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_card_view, container, false);

        activity = getActivity();

        EmailTIL = view.findViewById(R.id.EmailTil);
        PwdTIL = view.findViewById(R.id.pwdTil);
        EmailET = view.findViewById(R.id.emailEt);
        PassET = view.findViewById(R.id.passEt);

        loadDlg = new LoadingDialog(activity);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        view.findViewById(R.id.btnLogin).setOnClickListener(v -> userlogin());

        view.findViewById(R.id.btnForgotPassword).setOnClickListener(v -> new ForgotPasword_Frag().show(getFragmentManager(),"forgot_Fragment"));

        SharedPrefs session_prefs = new SharedPrefs(activity);
        FcmToken = session_prefs.getFcm_Token();

        if(session_prefs.getFcm_Token().equals("")) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(activity, instanceIdResult -> {
                FcmToken = instanceIdResult.getToken();
                session_prefs.setFcm_Token(FcmToken);
            });
        }else{
            FcmToken = session_prefs.getFcm_Token();
        }

        return view;
    }

    private void userlogin() {

        final String Email = EmailET.getText().toString(), Passwd = PassET.getText().toString();

        EmailTIL.setErrorEnabled(false);PwdTIL.setErrorEnabled(false);

        if (Objects.equals(Email, "") ) {
            EmailTIL.setError(" Enter Email / Mobile");
        } else if (Objects.equals(Passwd, "")) {
            PwdTIL.setError("Please Enter Password");
        } else {
            loadDlg.show();

            Map<String, String> params = new HashMap<>();
            params.put("username", Email);
            params.put("password", Passwd);
            params.put("token", FcmToken);
            params.put("type", "login");
            params.put("getlogin", "true");

            new VolleyRequester(activity).ParamsRequest(1, ((G_Class) activity.getApplicationContext()).getBaseurl()+Config.TRIP_URL + "account/loginreg.php", loadDlg, params, false, response -> {
                try {
                    JSONObject jobj = new JSONObject(response);
                    Boolean error = jobj.getBoolean("error");
                    if (!error) {
                        SharedPrefs session_prefs = new SharedPrefs(activity);
                        Bundle bb = new Bundle();
                        session_prefs.setPrefDetails(jobj.getString("user_name"), Passwd, jobj.getString("login_det_id"), jobj.getString("user_details_id"));
                        session_prefs.setLoggedin(true);
                        session_prefs.setTripCash(jobj.getJSONObject("user_details_main").getString("trip_cash"));
                        bb.putString("tripcash",jobj.getJSONObject("user_details_main").getString("trip_cash"));
                        bb.putString("email",Email);
                        bb.putString("userid",jobj.getString("user_details_id"));

                        String Name = jobj.getJSONObject("user_details_main").getString("user_name");
                        Name = Name.replaceAll("-"," ").trim();
                        int ind = Name.lastIndexOf(' ');

                        if(ind>=0){
                            session_prefs.setUserNames(Name,Name.substring(0, ind),
                                    Name.substring(ind+1),jobj.getJSONObject("user_details_main").getString("user_cell_phone"));
                            bb.putString("fname",Name.substring(0, ind));
                            bb.putString("lname",Name.substring(ind+1));
                        }else{
                            session_prefs.setUserNames(Name,Name," ",jobj.getJSONObject("user_details_main").getString("user_cell_phone"));
                            bb.putString("fname",Name);
                            bb.putString("lname"," ");
                        }
                        bb.putString("mobile",jobj.getJSONObject("user_details_main").getString("user_cell_phone"));

                        if(loadDlg.isShowing()){loadDlg.dismiss();}
                        Hotel_Room_Review_Act hrm = (Hotel_Room_Review_Act)activity;
                        hrm.getlogindetails(bb);
                        dismiss();
                    } else {
                        if(loadDlg.isShowing()){loadDlg.dismiss();}
                        Utils.setSingleBtnAlert(activity, "Invalid Credentials", "Ok", false);
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    if(loadDlg.isShowing()){loadDlg.dismiss();}
                    Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", false);
                }
            });

        }
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = Objects.requireNonNull(getDialog().getWindow()).getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

}