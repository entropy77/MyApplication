package com.example.mkseo.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Produce;

/**
 * Created by mkseo on 2017. 4. 11..
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "firebaseMsgService";

    // start receive message
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // for phone number from push notification
        // trimming since message's original form is this
        // OO번 테이블에서 OOOOOOOOOOO님이 주문하였습니다
        // if phone number is not on same format(11digit) -> it will occur error
        // right now, phoneNumberFormat would be 11 digit
        int phoneNumberFormat = 11;
        String phoneNumberFromPushNotification = "";
        if (remoteMessage.getNotification() != null) {
            String MessageNotificationBody = remoteMessage.getNotification().getBody();
            String tempString = MessageNotificationBody.replaceAll("[^-?0-9]+", "");
            phoneNumberFromPushNotification = tempString.substring(tempString.length() - phoneNumberFormat, tempString.length());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "Trimmed phone number: " + phoneNumberFromPushNotification);
        }

        // bring-out local phone number
        SharedPreferences preferences = getSharedPreferences("IDPASSWORD", getApplicationContext().MODE_PRIVATE);
        String localPhone = preferences.getString("phone", null);
        Log.d(TAG, "local phone number: " + localPhone);

        // if push message isn't coming from myself
        // from another ID
        if (!phoneNumberFromPushNotification.equals(localPhone)) {
            // send notification
            sendNotification(remoteMessage);
            BusProvider.getInstance().post(new pushEvent());
        }

    }

    @Produce
    public pushEvent pushEvent() {
        return new pushEvent();
    }

    private void sendNotification(RemoteMessage messageBody) {
        Intent intent = new Intent(this, FirebaseMessagingService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        if (messageBody != null) {
            Log.d(TAG, messageBody.toString());
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                .setBigContentTitle("알림이 도착했습니다")
                .setSummaryText(messageBody.toString());

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(style)
                .setContentTitle("알림이 도착했습니다")
                .setContentText(messageBody.getNotification().getBody().toString())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }
}
