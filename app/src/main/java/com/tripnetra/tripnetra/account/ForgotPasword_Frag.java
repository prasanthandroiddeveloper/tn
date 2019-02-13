package com.tripnetra.tripnetra.account;

import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.LoadingDialog;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ForgotPasword_Frag extends DialogFragment {

    EditText unameEt;
    TextInputLayout UnameTIL;
    LoadingDialog loadDlg;

    public ForgotPasword_Frag() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_forgot_password, container, false);

        unameEt = view.findViewById(R.id.emailEt);
        UnameTIL = view.findViewById(R.id.EmailTil);

        loadDlg = new LoadingDialog(getActivity());
        loadDlg.setCancelable(false);
        Objects.requireNonNull(loadDlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        view.findViewById(R.id.btnsend).setOnClickListener(v -> senddetails());
        return view;
    }

    private void senddetails() {
        final String Uname = unameEt.getText().toString();

        UnameTIL.setErrorEnabled(false);
        if (Objects.equals(Uname, "")) {
            UnameTIL.setError(" Enter Email / Mobile");
        } else {
            loadDlg.show();

            Map<String, String> params = new HashMap<>();
            params.put("username", Uname);
            params.put("type", "forgot");

            new VolleyRequester(getActivity()).ParamsRequest(1, ((G_Class) getActivity().getApplicationContext()).getBaseurl()+Config.TRIP_URL + "account/loginreg.php", loadDlg, params, false, response -> {
                if(loadDlg.isShowing()){loadDlg.dismiss();}
                Utils.setSingleBtnAlert(getActivity(), response, "Ok", false);
                dismiss();
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