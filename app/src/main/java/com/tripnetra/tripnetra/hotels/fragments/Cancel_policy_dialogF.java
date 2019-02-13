package com.tripnetra.tripnetra.hotels.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.tripnetra.tripnetra.G_Class;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.rest.VolleyRequester;
import com.tripnetra.tripnetra.utils.Config;
import com.tripnetra.tripnetra.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Cancel_policy_dialogF extends DialogFragment {

    WebView canceltv;
    Activity activity;

    public Cancel_policy_dialogF() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_cancellation_policy, container, false);

        activity = getActivity();
        canceltv = view.findViewById(R.id.cancelWV);

        String sstext = "TripNetra follows the standard and encoded cancellation policy that helps the pilgrims to understand precisely on what they need to bear in mind.   Cancellation charges are centred on how many days before we receive your cancellation notice. We cannot be held responsible for the problems that take places as a result of events that are not under our control such as weather, political demonstrations, bus and train delays, traffic jams, floods etc.</p>\n" +
                "<p>It is true that all efforts will be made honestly to cancel your trip on your request but make sure that we require sufficient time to complete your request to cancel your trip. If you are cancelling the booking and applying for a refund, in that case, Please read carefully about our Policies in order to avoid further complications.</p>\n" +
                "<p> </p><p><strong>Cancellation Period  Percentage of Cancellation :</strong></p>\n" +
                "<ul><li><p>From booking date till 15 days prior to departure    - 30 % of Package Cost</p>\n" +
                "</li><li><p>7 Days prior to departure 50% of Package Cost</p>\n" +
                "</li><li><p>3 to 7 Days prior to departure  - 75 % of Package Cost</p></li><li>\n" +
                "<p>Less than 24 hrs to 3 Days prior to departure - 100% of Package Cost</p>\n" +
                "</li></ul><p><strong>  </strong></p><p><strong>Hotel Cancellation Policies /Charges</strong></p>\n" +
                "<p>It is definite that the cancellation policy is specific to the hotel and rate that you are booking. It has to be kept in mind that we are really indebted to keep the policies and customs of the hotel in terms of the cancellation policies as well as charges. So be thoroughly informative about the cancellation policies and charges that you are booking.</p>\n" +
                "<b>1. </b>Free Cancellation before 24 hours.<br> </br><b>2. </b>No refund within 24 hours.<br> </br><b>3. </b>No refund for trust based rooms.<p> </p>";

        canceltv.loadData(sstext,"text/html",null);
        if(getArguments() != null){
            canceltv.loadData(getArguments().getString("CancelText"),"text/html",null);
        }else{
            getcancel();
        }

        view.findViewById(R.id.closecancel).setOnClickListener(v -> dismiss());

        return view;
    }

    public void getcancel() {

        new VolleyRequester(activity).ParamsRequest(1, ((G_Class) activity.getApplicationContext()).getBaseurl()+Config.TRIP_URL+"cancellationpolicy.php", null, null, false, response -> {
            try {
                JSONObject jobj = new JSONObject(response);
                String sss = jobj.getString("page_content_body").replace(" &nbsp;","");
                canceltv.loadData(sss,"text/html",null);
            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(activity,getResources().getString(R.string.something_wrong),"Ok",false);
            }
        });
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