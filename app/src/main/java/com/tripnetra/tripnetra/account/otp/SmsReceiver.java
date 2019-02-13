package com.tripnetra.tripnetra.account.otp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.Objects;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Object[] pdusObj = (Object[]) Objects.requireNonNull(bundle).get("pdus");

        assert pdusObj != null;
        for (Object aPdusObj : pdusObj) {

            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String message = smsMessage.getMessageBody();

            if(!sender.contains("TRPNTA")){return;}

            String ss = message.replaceAll("[^0-9]","");

            Intent intnt = new Intent("smsotpbroadcast");
            intnt.putExtra("otp", ss);
            context.sendBroadcast(intnt);
        }
    }

}