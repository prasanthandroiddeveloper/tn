package com.tripnetra.tripnetra.account.otp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.tripnetra.tripnetra.utils.Constants.Otp_length;

@SuppressLint("SetTextI18n")
public class Verify_Otp_frag extends DialogFragment {

    EditText OtpET;TextView prgsTv;
    TextInputLayout OtpTil;
    String Phone,Email;
    Button ResendBtn;
    int flag = 0;
    Boolean timerrunning = true;
    CountDownTimer timer;
    Activity activity;

    public Verify_Otp_frag() {}

    SmsReceiver receiversms = new SmsReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            String Code = Objects.requireNonNull(intent.getExtras()).getString("otp");
            OtpET.setText(Code);

            prgsTv.setText("verifying");
            flag = 0;
            Map<String, String> params = new HashMap<>();
            params.put("otp", Objects.requireNonNull(Code));
            params.put("mobile", Phone);
            params.put("type", "verifyotp");
            sendrequest(params);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_verify_otp_frag, container, false);

        Phone = getArguments().getString("phone");
        Email = getArguments().getString("email");

        OtpTil = view.findViewById(R.id.OtpTil);
        OtpET = view.findViewById(R.id.otpEt);
        prgsTv = view.findViewById(R.id.prgsTv);
        ResendBtn = view.findViewById(R.id.btnresend);

        view.findViewById(R.id.btnverify).setOnClickListener(v -> {
            String OTP = OtpET.getText().toString();

            OtpTil.setErrorEnabled(false);
            if(OTP.equals("")){
                OtpTil.setError("Enter OTP");
            }else if(OTP.length() != Otp_length){
                OtpTil.setError("Invalid OTP");
            }else{
                flag = 0;
                prgsTv.setText("verifying");

                ResendBtn.setText("");
                ResendBtn.setEnabled(false);
                timer.cancel();
                timerrunning = false;

                Map<String, String> params = new HashMap<>();
                params.put("otp", OTP);
                params.put("mobile", Phone);
                params.put("type", "verifyotp");
                sendrequest(params);
            }
        });

        ResendBtn.setEnabled(false);
        countdown();

        activity.registerReceiver(receiversms, new IntentFilter("smsotpbroadcast"));

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        Utils.checkPermissions(activity);
    }

    private void sendrequest(final Map<String, String> params) {

        new VolleyRequester(activity).ParamsRequest(1, ((G_Class) activity.getApplicationContext()).getBaseurl()+Config.TRIP_URL+"account/loginreg.php", null, params, true, response -> {

            try {
                if(flag==0) {
                    Boolean error = Boolean.valueOf(new JSONObject(response).getString("error"));
                    if(error){
                        OtpTil.setError("Invalid OTP");
                    }else {
                        Utils.setSingleBtnAlert(activity, "User Profile Created ", "Ok", true);
                        dismiss();
                    }
                }else{
                    Boolean error = Boolean.valueOf(new JSONObject(response).getJSONObject("otp_result").getString("error"));
                    if(error){
                        Toast.makeText(activity, "Technical Error Occurred", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(activity, "OTP Resent", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                Toast.makeText(activity, "Technical Error Occurred", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void countdown(){
        timer = new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);

                int hours = seconds / (60 * 60);
                int tempMint = (seconds - (hours * 60 * 60));
                int minutes = tempMint / 60;
                seconds = tempMint - (minutes * 60);

                ResendBtn.setText(String.format(Locale.getDefault(),"%02d", minutes) + " : " + String.format(Locale.getDefault(),"%02d", seconds));
            }

            public void onFinish() {
                ResendBtn.setText("Resend OTP");
                ResendBtn.setEnabled(true);
                ResendBtn.setOnClickListener(v -> {
                    flag = 1;
                    ResendBtn.setText("");
                    ResendBtn.setEnabled(false);
                    Map<String, String> params = new HashMap<>();
                    params.put("email", Email);
                    params.put("mobile", Phone);
                    params.put("type", "resend");
                    sendrequest(params);

                });
                timerrunning = false;

            }
        };
        timer.start();

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        getActivity().registerReceiver(receiversms, new IntentFilter("smsotpbroadcast"));
        timer.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if(timerrunning) { timer.wait(); }
        } catch (InterruptedException e) {
            timer.cancel();
            e.printStackTrace();
        }
        getActivity().unregisterReceiver(receiversms);
    }
}
