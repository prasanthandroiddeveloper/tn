package com.tripnetra.tripnetra.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.account.Order_Log_Activity;
import com.tripnetra.tripnetra.account.Refer_earn_Activity;
import com.tripnetra.tripnetra.account.fragments.Profile_Fragment;
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

public class Acc_main_Frag extends Fragment {

    CircleImageView ImageTv;
    TextView NameTv, BalTv;
    String User_det_id,ReferCode="";
    LoadingDialog loadDlg;
    Activity activity;

    public Acc_main_Frag() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_acc_main, container, false);

        activity = getActivity();

        ImageTv = view.findViewById(R.id.gimagetv);
        NameTv = view.findViewById(R.id.Gnametv);
        BalTv = view.findViewById(R.id.balTv);

        loadDlg = new LoadingDialog(activity);
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadDlg.show();

        User_det_id = new SharedPrefs(activity).getUserId();

        getdetails();

        view.findViewById(R.id.llout1).setOnClickListener(v -> ((FragmentChangeListener) activity).replaceFragment(new Profile_Fragment()));
        view.findViewById(R.id.llout2).setOnClickListener(v ->
                startActivity(new Intent(activity,Refer_earn_Activity.class).putExtra("refercode",(ReferCode.equals("")?"none":ReferCode))));
        view.findViewById(R.id.llout3).setOnClickListener(v -> startActivity(new Intent(activity, Order_Log_Activity.class)));

        //String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.e("FCM TOKEN",refreshedToken);

        return view;
    }

    @SuppressLint("SetTextI18n")
    public void getdetails(){

        Map<String, String> params = new HashMap<>();
        params.put("userid", User_det_id);
        params.put("type", "getdetails");

        new VolleyRequester(activity).ParamsRequest(1, ((G_Class) activity.getApplicationContext()).getBaseurl()+Config.TRIP_URL+"account/updateprofile.php", loadDlg, params, false, response -> {
            try {
                JSONObject jobj = new JSONObject(response);
                String Name = jobj.getString("user_name");
                Name = Name.replaceAll("-"," ").trim();
                NameTv.setText(Name);

                String ss = jobj.getString("user_profile_pic");
                if(!Objects.equals(ss, "user-avatar.jpg")){
                    Glide.with(activity).load(((G_Class) activity.getApplicationContext()).getImageurl()+"users/"+ss).apply(new RequestOptions().centerCrop()).into(ImageTv);
                }

                int ind = Name.lastIndexOf(' ');

                if(ind>=0){
                    new SharedPrefs(activity).setUserNames(Name,Name.substring(0, ind),Name.substring(ind+1),jobj.getString("user_cell_phone"));
                }else{
                    new SharedPrefs(activity).setUserNames(Name,Name," ",jobj.getString("user_cell_phone"));
                }

                String tripcash = jobj.getString("trip_cash");
                BalTv.setText("₹ "+tripcash);
                new SharedPrefs(activity).setTripCash(tripcash);
                ReferCode = jobj.getString("referal_code");

                if(loadDlg.isShowing()){loadDlg.dismiss();}
            } catch (JSONException e) {
                if(loadDlg.isShowing()){loadDlg.dismiss();}
                Utils.setSingleBtnAlert(activity, getResources().getString(R.string.something_wrong), "Ok", true);
                //e.printStackTrace();
            }
        });
    }

}
