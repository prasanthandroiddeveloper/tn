package com.tripnetra.tripnetra.account.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.account.Acc_Login_Main_Act;
import com.tripnetra.tripnetra.fragments.BaseMain;
import com.tripnetra.tripnetra.fragments.FragmentChangeListener;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.SharedPrefs;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.tripnetra.tripnetra.utils.Constants.Image_Pick;

public class Profile_Fragment extends Fragment implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    EditText FnameEt, LnameEt, EmailEt, MobileEt, CityEt, StateEt, AdressEt, PincodeEt;
    TextView NameTv, MobileTv, ChangePassTv, SignoutTv, EditTv;
    CircleImageView ImageIv;
    int flag = 1;
    Activity activity;
    String Fname, Lname, Email, Phone, City, State, Address, Pincode, User_id, addr_id, change_image = "dontchange", ImageString = "nulll";
    LoadingDialog loadDlg;
    G_Class g_class;

    public Profile_Fragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle aa) {

        View view = inflater.inflate(R.layout.fragment_profile_main, container, false);

        activity = getActivity();

        g_class = (G_Class) activity.getApplicationContext();

        FnameEt = view.findViewById(R.id.fnameEt);
        LnameEt = view.findViewById(R.id.lnameEt);
        EmailEt = view.findViewById(R.id.emailEt);
        MobileEt = view.findViewById(R.id.phoneEt);
        CityEt = view.findViewById(R.id.cityEt);
        StateEt = view.findViewById(R.id.stateEt);
        AdressEt = view.findViewById(R.id.addressEt);
        PincodeEt = view.findViewById(R.id.pincodeEt);

        NameTv = view.findViewById(R.id.nametv);
        MobileTv = view.findViewById(R.id.mobiletv);
        ChangePassTv = view.findViewById(R.id.changePassTv);
        SignoutTv = view.findViewById(R.id.signouttv);
        EditTv = view.findViewById(R.id.edittv);

        ImageIv = view.findViewById(R.id.ImageIv);


        set_et(false);
        EmailEt.setEnabled(false);
        MobileEt.setEnabled(false);

        loadDlg = new LoadingDialog(activity);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadDlg.show();

        EditTv.setOnClickListener(this);
        SignoutTv.setOnClickListener(this);
        ChangePassTv.setOnClickListener(this);
        User_id = new SharedPrefs(activity).getUserId();

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getdetails();

        return view;
    }

    private void getdetails() {

        Map<String, String> params = new HashMap<>();
        params.put("userid", User_id);
        params.put("type", "getdetails");

        new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL + "account/updateprofile.php", loadDlg, params, true, response -> {

            try {
                JSONObject jobj = new JSONObject(response);
                String Name = jobj.getString("user_name");
                Name = Name.replaceAll("-", " ").trim();
                NameTv.setText(Name);
                MobileTv.setText(jobj.getString("user_cell_phone"));

                int ind = Name.lastIndexOf(' ');

                SharedPrefs sfprefs = new SharedPrefs(activity);
                if (ind >= 0) {
                    FnameEt.setText(Name.substring(0, ind));
                    LnameEt.setText(Name.substring(ind + 1));

                    sfprefs.setUserNames(Name, Name.substring(0, ind), Name.substring(ind + 1), jobj.getString("user_cell_phone"));
                } else {
                    FnameEt.setText(Name);
                    LnameEt.setText(" ");
                    sfprefs.setUserNames(Name, Name, " ", jobj.getString("user_cell_phone"));
                }

                EmailEt.setText(jobj.getString("user_email"));
                MobileEt.setText(jobj.getString("user_cell_phone"));
                CityEt.setText(jobj.getString("city_name"));
                StateEt.setText(jobj.getString("state_name"));
                AdressEt.setText(jobj.getString("address"));
                PincodeEt.setText(jobj.getString("zip_code"));
                addr_id = jobj.getString("address_details_id");

                String ss = jobj.getString("user_profile_pic");
                if (!Objects.equals(ss, "user-avatar.jpg")) {
                    Glide.with(activity).load(((G_Class) activity.getApplicationContext()).getImageurl()+ "users/" + ss)
                            .apply(new RequestOptions().centerCrop()).into(ImageIv);
                }

            } catch (JSONException e) {
                Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", true);
                //e.printStackTrace();
            }
            if (loadDlg.isShowing()) { loadDlg.dismiss(); }

        });
    }

    public void set_et(boolean bool) {
        FnameEt.setEnabled(bool);
        LnameEt.setEnabled(bool);
        CityEt.setEnabled(bool);
        StateEt.setEnabled(bool);
        AdressEt.setEnabled(bool);
        PincodeEt.setEnabled(bool);
        ImageIv.setEnabled(bool);
    }

    private void uploaddata() {

        Map<String, String> params = new HashMap<>();
        params.put("userid", User_id);
        params.put("name", Fname + " " + Lname);
        params.put("email", Email);
        params.put("phone", Phone);
        params.put("city", City);
        params.put("state", State);
        params.put("address", Address);
        params.put("pincode", Pincode);
        params.put("addid", addr_id);
        params.put("changeimage", change_image);
        params.put("imagestring", ImageString);
        params.put("type", "uploaddata");

        strrequest_data(params);
    }

    public void change_pass(Bundle bb) {

        Map<String, String> params = new HashMap<>();
        params.put("oldpass", Objects.requireNonNull(bb.getString("oldpass")));
        params.put("newpass", Objects.requireNonNull(bb.getString("newpass")));
        params.put("userid", User_id);
        params.put("type", "changepass");

        strrequest_data(params);

    }

    public void strrequest_data(final Map<String, String> params) {
        loadDlg.show();
        new VolleyRequester(activity).ParamsRequest(1, g_class.getBaseurl()+Config.TRIP_URL + "account/updateprofile.php", loadDlg, params, true, response -> {
            if (loadDlg.isShowing()) {
                loadDlg.dismiss();
            }
            Utils.setSingleBtnAlert(activity, response, "Ok", false);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Pick && resultCode == RESULT_OK) {
            Bitmap bitmap = ImagePicker.getImageFromResult(activity, resultCode, data);

            if (bitmap != null) {
                ImageIv.setImageBitmap(bitmap);
                ImageString = Utils.getStringImage(bitmap);
                change_image = "change";
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {

        if (v == EditTv) {
            if (flag == 1) {
                set_et(true);
                EditTv.setText("Save");
                flag = 2;
                ImageIv.setOnClickListener(this);
            } else if (flag == 2) {
                set_et(false);
                EditTv.setText("Edit");
                flag = 1;

                Fname = FnameEt.getText().toString();
                Lname = LnameEt.getText().toString();
                Email = EmailEt.getText().toString();
                Phone = MobileEt.getText().toString();
                City = CityEt.getText().toString();
                State = StateEt.getText().toString();
                Address = AdressEt.getText().toString();
                Pincode = PincodeEt.getText().toString();

                uploaddata();

            }
        }else if (v == ImageIv) {
            startActivityForResult(ImagePicker.getPickImageIntent(activity), Image_Pick);
        }else if (v == ChangePassTv) {
            new Frag_Change_Pass().show(getFragmentManager(), "change_pass_frag");
        }else if (v == SignoutTv) {
            new AlertDialog.Builder(activity).setMessage("Do You Want to SignOut")
                    .setPositiveButton("Yes", (dialog, id) -> {


                        new SharedPrefs(activity).setLoggedin(false);
                        new SharedPrefs(activity).clearall();

                        // Google Sign Out

                        GoogleSignInOptions signInOptions= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail().build();
                        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                                .enableAutoManage((FragmentActivity) activity,Profile_Fragment.this)
                                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();



                        Auth.GoogleSignInApi.signOut(googleApiClient);

                        Intent intent = new Intent(activity, BaseMain.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        ((FragmentChangeListener) activity).replaceFragment(new Acc_Login_Main_Act());
                    })
                    .setNegativeButton("No", (dialog, id) -> { })
                    .setCancelable(true).create().show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

}