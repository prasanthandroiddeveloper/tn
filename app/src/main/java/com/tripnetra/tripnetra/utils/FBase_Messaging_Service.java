package com.tripnetra.tripnetra.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.fragments.BaseMain;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class FBase_Messaging_Service extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String Title,Message;
        if(remoteMessage.getNotification()!= null) {
            Title = remoteMessage.getNotification().getTitle();
            Message = remoteMessage.getNotification().getBody();
        }else{
            Title = (remoteMessage.getData().get("title") != null ) ? remoteMessage.getData().get("title") : "TripNetra";
            Message = (remoteMessage.getData().get("body") != null ) ? remoteMessage.getData().get("body") : "   ";
        }
        String imageUrl = remoteMessage.getData().get("image");


        Intent intent = new Intent(this, BaseMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri Sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (!TextUtils.isEmpty(imageUrl)) {

            Bitmap bitmap = null;

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                //e.printStackTrace();
            }

            showNotification(bitmap,   Title, Message,  pendingIntent, Sound);

        } else {
            showNotification( null,Title, Message, pendingIntent, Sound);
        }

    }

    private void showNotification(Bitmap bitmap, String title, String msg, PendingIntent intent, Uri sound) {
        int icon = R.drawable.notilogo;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getResources().getString(R.string.default_notification_channel_id));

        builder.setSmallIcon(icon)
                .setTicker(title)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(intent)
                .setSound(sound)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), icon))
                .setContentText(msg)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setLights(Color.YELLOW, 1000, 300);

        if(bitmap != null) {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
            bigPictureStyle.setSummaryText(msg);
            bigPictureStyle.bigPicture(bitmap);

            builder.setStyle(bigPictureStyle);

        }else{
            builder.setStyle(new NotificationCompat.InboxStyle().addLine(msg));
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.default_notification_channel_id),
                    "TripNetra", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(title);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
        assert notificationManager != null;
        notificationManager.notify((bitmap==null) ? 769 : 770, builder.build());
    }

    @Override
    public void onNewToken(String s) { super.onNewToken(s); }

}