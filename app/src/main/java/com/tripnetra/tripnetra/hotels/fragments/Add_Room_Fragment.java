package com.tripnetra.tripnetra.hotels.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.hotels.Hotel_Main_Act;

public class Add_Room_Fragment extends DialogFragment implements View.OnClickListener {

    int rno=1,ano=1,cno=0,toa1=1,toa2=1,toa3=1;
    TextView rtv, atv, ctv,radd, rless, aadd, aless, cadd, cless;
    Button apply;

    public Add_Room_Fragment() {}

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle aa) {

        View view = inflater.inflate(R.layout.fragment_add_room, container, false);

        Bundle bundle = getArguments();
        if(bundle != null) {
            rno = Integer.parseInt(bundle.getString("rno"));
            ano = Integer.parseInt(bundle.getString("ano"));
            cno = Integer.parseInt(bundle.getString("cno"));
        }

        getDialog().setTitle("Select Room and Persons");

        radd = view.findViewById(R.id.roomplus);
        rless = view.findViewById(R.id.roomminus);
        aadd = view.findViewById(R.id.adultplus);
        aless = view.findViewById(R.id.adultminus);
        cadd = view.findViewById(R.id.childplus);
        cless = view.findViewById(R.id.childminus);
        rtv = view.findViewById(R.id.roomcout);
        atv = view.findViewById(R.id.adultcout);
        ctv = view.findViewById(R.id.childcout);
        apply = view.findViewById(R.id.submit);

        rtv.setText(String.valueOf(rno));
        atv.setText(String.valueOf(ano));
        ctv.setText(String.valueOf(cno));

        apply.setOnClickListener(this);
        radd.setOnClickListener(v -> {
            if(toa1==1) {
                if (rno < 5) {
                    rno++;
                    rtv.setText(String.valueOf(rno));
                    if (rno > ano) {
                        ano = rno;
                        atv.setText(String.valueOf(rno));
                    }
                } else {
                    toa1=2;
                    Toast.makeText(getActivity(), "Not Allowed to Book \n More than 5 Roooms ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rless.setOnClickListener(v -> {
            if(rno>1) {
                rno--;
                if(ano>rno*7) {
                    ano = rno*7 ;
                    atv.setText(String.valueOf(ano));
                }
                if(cno>rno*3) {
                    cno = rno*3 ;
                    ctv.setText(String.valueOf(cno));
                }
                rtv.setText(String.valueOf(rno));
            }
        });
        aadd.setOnClickListener(v -> {
            if(toa2==1) {
                if (ano < rno * 7) {
                    ano++;
                    atv.setText(String.valueOf(ano));
                } else {
                    toa2=2;
                    Toast.makeText(getActivity(), "Not Allowed to Book \n More than 7 Adults in Each room ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        aless.setOnClickListener(v -> {
            if(ano>1) {
                ano--;
                atv.setText(String.valueOf(ano));
                if(ano<rno) {
                    rno=ano;
                    rtv.setText(String.valueOf(ano));
                }
            }
        });
        cadd.setOnClickListener(v -> {
            if(toa3==1) {
                if (cno < rno * 3) {
                    cno++;
                    ctv.setText(String.valueOf(cno));
                } else {
                    toa3=2;
                    Toast.makeText(getActivity(), "Not Allowed to Book \n More than 3 Children ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cless.setOnClickListener(v -> {
            if(cno>0) {
                cno--;
                ctv.setText(String.valueOf(cno));
            }
        });
    return view;
    }

    @Override
    public void onClick(View view) {
        /*Bundle bundle=new Bundle();
        bundle.putString("rcount", String.valueOf(rno));
        bundle.putString("acount", String.valueOf(ano));
        bundle.putString("ccount", String.valueOf(cno));
        Hotel_Main_Act hm =(Hotel_Main_Act)getActivity();
        assert hm != null;
        hm.getval(bundle);*/
        dismiss();

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = 1100;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

}