package com.tripnetra.tripnetra.account;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.fragments.Acc_main_Frag;
import com.tripnetra.tripnetra.fragments.FragmentChangeListener;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Constants;
import com.tripnetra.tripnetra.utils.SharedPrefs;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.widget.Toast.LENGTH_SHORT;

public class Acc_Login_Main_Act extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    TextInputLayout EmailTIL,PwdTIL;
    SharedPrefs session_prefs;
    LoadingDialog loadDlg;
    GoogleApiClient googleApiClient;
    LoginButton loginButton;
    CallbackManager callbackManager;
    View MainView;
    Activity activity;
    String FcmToken = "none";
    G_Class g_class;

    public Acc_Login_Main_Act() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainView = inflater.inflate(R.layout.act_login_main, container, false);

        activity = getActivity();

        g_class = (G_Class) activity.getApplicationContext();
        EmailTIL = MainView.findViewById(R.id.EmailTil);
        PwdTIL = MainView.findViewById(R.id.pwdTil);
        session_prefs = new SharedPrefs(activity);

        callbackManager = CallbackManager.Factory.create();
        loginButton = MainView.findViewById(R.id.fb_login_btn);

        loginButton.setReadPermissions("email","public_profile");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest data_request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (json_object, response) -> {
                    try {
                        String name  = json_object.getString("name");
                        String email  = json_object.getString("email");
                        String iamge  = json_object.getJSONObject("picture").getJSONObject("data").getString("url");

                        getvloin(email,name,iamge);

                    } catch (JSONException e) {
                        Toast.makeText(activity,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
                Bundle bb = new Bundle();
                bb.putString("fields", "id,name,email,picture.width(120).height(120)");
                data_request.setParameters(bb);
                data_request.executeAsync();

            }

            @Override
            public void onCancel() { Toast.makeText(activity,"Login Cancelled",Toast.LENGTH_LONG).show(); }

            @Override
            public void onError(FacebookException error) { Toast.makeText(activity,error.getMessage(),Toast.LENGTH_LONG).show(); }

        });

        MainView.findViewById(R.id.fb_btn).setOnClickListener(v -> loginButton.performClick());

        GoogleSignInOptions signInOptions= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        if (googleApiClient != null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage((FragmentActivity) activity, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
        }

        loadDlg = new LoadingDialog(activity);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if(session_prefs.getFcm_Token().equals("")) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(activity, instanceIdResult -> {
                FcmToken = instanceIdResult.getToken();
                session_prefs.setFcm_Token(FcmToken);
            });
        }else{
            FcmToken = session_prefs.getFcm_Token();
        }

        MainView.findViewById(R.id.btnLogin).setOnClickListener(v -> user_login());

        MainView.findViewById(R.id.btnForgotPassword).setOnClickListener(v ->
                new ForgotPasword_Frag().show(Objects.requireNonNull(getFragmentManager()),"forgot_Fragment"));
        MainView.findViewById(R.id.llout).setOnClickListener(v -> startActivity(new Intent(activity, Acc_Register_Act.class)));

        MainView.findViewById(R.id.signin).setOnClickListener(view -> startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient), Constants.GOOGLE_REQ_CODE));

        return MainView;
    }

    public void user_login(){

        final String Email = ((EditText) MainView.findViewById(R.id.emailEt)).getText().toString(),
                Passwd = ((EditText) MainView.findViewById(R.id.passEt)).getText().toString();

        EmailTIL.setErrorEnabled(false);PwdTIL.setErrorEnabled(false);
        if(Objects.equals(Email, "")){
            EmailTIL.setError(" Enter Email / Mobile");
        }else if(Objects.equals(Passwd, "")){
            PwdTIL.setError("Please Enter Password");
        }else {
            loadDlg.show();

            Map<String, String> params = new HashMap<>();
            params.put("username", Email);
            params.put("password", Passwd);
            params.put("token", FcmToken);
            params.put("type", "login");

            new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL + "account/loginreg.php", loadDlg, params, false, response -> {

                if (loadDlg.isShowing()) { loadDlg.dismiss(); }

                try {
                    JSONObject jobj = new JSONObject(response);
                    Boolean error = jobj.getBoolean("error");
                    if (!error) {
                        session_prefs.setPrefDetails(jobj.getString("user_name"),
                                Passwd, jobj.getString("login_det_id"), jobj.getString("user_details_id"));
                        session_prefs.setLoggedin(true);

                        ((FragmentChangeListener) activity).replaceFragment(new Acc_main_Frag());

                    } else {
                        Utils.setSingleBtnAlert(activity, "Invalid Credentials", "Ok", false);
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", false);
                }
            });
        }
    }

    public void getvloin(final String email,String name,String image){

        loadDlg.show();

        Map<String, String> params = new HashMap<>();
        params.put("useremail", email);
        params.put("name", name);
        params.put("image", image);
        params.put("token", FcmToken);
        params.put("type", "googlelogin");

        new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL + "account/loginreg.php", loadDlg, params, false, response -> {

            if (loadDlg.isShowing()) { loadDlg.dismiss(); }

            try {
                JSONObject jobj = new JSONObject(response);

                Boolean login = jobj.getBoolean("login");

                if (!login) { Utils.setSingleBtnAlert(activity, jobj.getString("message"), "Ok", false); }

                session_prefs.setPrefDetails(email, "", jobj.getString("login_det_id"), jobj.getString("user_details_id"));
                session_prefs.setLoggedin(true);

                ((FragmentChangeListener) activity).replaceFragment(new Acc_main_Frag());

            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", false);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { Toast.makeText(activity,"Login Cancelled",Toast.LENGTH_LONG).show(); }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.GOOGLE_REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (!result.isSuccess()) {
                Toast.makeText(activity, "Can't Sign In", LENGTH_SHORT).show();
                return;
            }

            GoogleSignInAccount account = result.getSignInAccount();

            String name = Objects.requireNonNull(account).getDisplayName();
            String email = account.getEmail();
            String image = Objects.requireNonNull(account.getPhotoUrl()).toString();

            getvloin(email,name,image);

        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}