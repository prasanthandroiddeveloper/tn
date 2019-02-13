package com.tripnetra.tripnetra.account;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.account.otp.Verify_Otp_frag;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.tripnetra.tripnetra.utils.Constants.MULTIPLE_PERMISSIONS_CODE;

public class Acc_Register_Act extends AppCompatActivity {

    TextInputLayout FnameTL,LnameTL,EmailTL,PhoneTL,PassTL;
    LoadingDialog loadDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register_main);

        loaddata();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void loaddata() {
        if(!Utils.checkPermissions(this)){return;}

        FnameTL = findViewById(R.id.fnameTil);
        LnameTL = findViewById(R.id.lnameTil);
        EmailTL = findViewById(R.id.emaiTil);
        PhoneTL = findViewById(R.id.phoneTil);
        PassTL = findViewById(R.id.pwdTil);

        loadDlg = new LoadingDialog(this);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public void user_reg(View v){
        final String Fname = ((EditText)findViewById(R.id.fnameEt)).getText().toString(),
                Lname = ((EditText)findViewById(R.id.lnameEt)).getText().toString(),
                Email = ((EditText)findViewById(R.id.emailEt)).getText().toString(),
                Pass = ((EditText)findViewById(R.id.passEt)).getText().toString(),
                Phone = ((EditText)findViewById(R.id.phoneEt)).getText().toString(),
                State = ((EditText)findViewById(R.id.stateEt)).getText().toString(),
                Address = ((EditText)findViewById(R.id.addressEt)).getText().toString(),
                City = ((EditText)findViewById(R.id.cityEt)).getText().toString(),
                Pincode = ((EditText)findViewById(R.id.pincodeEt)).getText().toString(),
                Refercode = ((EditText)findViewById(R.id.referEt)).getText().toString();

        FnameTL.setErrorEnabled(false);LnameTL.setErrorEnabled(false);EmailTL.setErrorEnabled(false);
        PhoneTL.setErrorEnabled(false);PassTL.setErrorEnabled(false);

        if(Objects.equals(Fname, "")){
            FnameTL.setError("Enter First Name");
        }else if(Objects.equals(Lname, "")){
            LnameTL.setError("Enter Last Name");
        }else if(Objects.equals(Phone, "") || !Phone.matches("[6-9][0-9]{9}")){
            PhoneTL.setError((Objects.equals(Phone, ""))?"Enter Mobile Number" : "Enter Valid Mobile Number");
        }else if(Objects.equals(Email, "") || Utils.isEmailValid(Email)){
            EmailTL.setError((Objects.equals(Email, ""))?"Enter Email":"Enter Valid Email");
        }else if(Objects.equals(Pass, "") || Pass.length()<8){
            PassTL.setError((Objects.equals(Phone, ""))?"Enter Password":"Enter Password more than 8 Characters");
        }else if(Objects.equals(State, "")) {
            Toast.makeText(this,"Enter State",Toast.LENGTH_SHORT).show();
        }else if(Objects.equals(Address, "")) {
            Toast.makeText(this,"Enter Address",Toast.LENGTH_SHORT).show();
        }else if(Objects.equals(City, "")) {
            Toast.makeText(this,"Enter City",Toast.LENGTH_SHORT).show();
        }else if(Objects.equals(Pincode, "")) {
            Toast.makeText(this,"Enter Pincode",Toast.LENGTH_SHORT).show();
        }else {
            loadDlg.show();

            Map<String, String> params = new HashMap<>();
            params.put("email", Email);
            params.put("fname", Fname);
            params.put("lname", Lname);
            params.put("mobile", Phone);
            params.put("password", Pass);
            params.put("city", City);
            params.put("state", State);
            params.put("address", Address);
            params.put("pincode", Pincode);
            params.put("refercode", (Objects.equals(Refercode, ""))? "null" : Refercode);
            params.put("type", "register");

            new VolleyRequester(this).ParamsRequest(1, ((G_Class) getApplicationContext()).getBaseurl()+Config.TRIP_URL + "account/loginreg.php", loadDlg, params, true, response -> {
                if(loadDlg.isShowing()){loadDlg.dismiss();}
                try {
                    JSONObject jobj = new JSONObject(response);
                    if(jobj.getString("user_exists").equals("true")){
                        Utils.setSingleBtnAlert(Acc_Register_Act.this, jobj.getString("message"), "Ok", true);
                    }else{
                        if(jobj.getString("usercreated").equals("false")){
                            Toast.makeText(Acc_Register_Act.this, "Verify Your Mobile Number", Toast.LENGTH_SHORT).show();
                        }
                        if(jobj.getJSONObject("otp_result").getString("error").equals("false")){
                            Bundle bundle = new Bundle();
                            bundle.putString("email", Email);
                            bundle.putString("phone", Phone);
                            Verify_Otp_frag vof = new Verify_Otp_frag();
                            vof.setArguments(bundle);
                            vof.show(getFragmentManager(),"Verify OTP Fragment");
                        }else{
                            Toast.makeText(Acc_Register_Act.this, "Something Wrong WIth OTP\nPlease Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(Acc_Register_Act.this, "Technical Error Occurred", "Ok", true);
                }
            });
        }

    }

    public void user_login(View v){finish();}

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