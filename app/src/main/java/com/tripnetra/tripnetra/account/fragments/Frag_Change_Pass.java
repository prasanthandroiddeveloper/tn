package com.tripnetra.tripnetra.account.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tripnetra.tripnetra.R;

import java.util.Objects;

import static com.tripnetra.tripnetra.utils.Constants.Pass_length;

public class Frag_Change_Pass extends DialogFragment {

    TextInputLayout OldTIL,NewTIL,ReTIL;
    EditText OldET,NewEt,ReEt;
    Button SubmitBtn;

    public Frag_Change_Pass() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vv = inflater.inflate(R.layout.fragment_change_pass, container, false);

        OldTIL = vv.findViewById(R.id.oldTil);NewTIL = vv.findViewById(R.id.newTil);ReTIL = vv.findViewById(R.id.reTil);
        OldET = vv.findViewById(R.id.oldpassEt);NewEt = vv.findViewById(R.id.newpassEt);ReEt = vv.findViewById(R.id.reenterEt);
        SubmitBtn = vv.findViewById(R.id.submitbutton);

        SubmitBtn.setOnClickListener(v -> {
            String OldPass = OldET.getText().toString(),
                    NewPass  = NewEt.getText().toString(),
                    ConfirmPass = ReEt.getText().toString();

            OldTIL.setErrorEnabled(false);NewTIL.setErrorEnabled(false);ReTIL.setErrorEnabled(false);

            if(Objects.equals(OldPass, "") || OldPass.length() < Pass_length){
                OldTIL.setError((Objects.equals(OldPass, ""))?"Enter Password":"Enter Password more than 8 Characters");
            }else if(Objects.equals(NewPass, "") || NewPass.length() < Pass_length){
                NewTIL.setError((Objects.equals(NewPass, ""))?"Enter Password":"Enter Password more than 8 Characters");
            }else if(Objects.equals(ConfirmPass, "") || ConfirmPass.length() < Pass_length){
                ReTIL.setError((Objects.equals(ConfirmPass, ""))?"Enter Password":"Enter Password more than 8 Characters");
            }else if(Objects.equals(NewPass, OldPass)){
                NewTIL.setError("Old Password & New Password Cannot be Same");
            }else if(!Objects.equals(NewPass, ConfirmPass)){
                ReTIL.setError("Passwords Dont Match");
            } else{
                Bundle bb = new Bundle();
                bb.putString("oldpass",OldPass);
                bb.putString("newpass",NewPass);

                ((Profile_Fragment) getActivity().getFragmentManager().findFragmentByTag("profile_fragment")).change_pass(bb);
                dismiss();
            }
        });

        return vv;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

}