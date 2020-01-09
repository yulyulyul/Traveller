package jso.kpl.traveller.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import jso.kpl.traveller.R;
import jso.kpl.traveller.ui.EditingMsg;
import jso.kpl.traveller.ui.MainTab;
import jso.kpl.traveller.ui.StartScreen;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "FCM.Service";

    public MyFirebaseMessagingService() {
        Log.d(TAG, "서비스 실행");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "메시지: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "메시지 노티" +
                    remoteMessage.getNotification().getBody());
        }

        sendNotification(remoteMessage);

    }

    private void sendNotification(RemoteMessage remoteMessage) {

        Log.d(TAG, "sendNotification");
        Intent intent = new Intent(this, EditingMsg.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.airplane)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000})
                .setLights(Color.WHITE, 1500, 1500)
                .setContentIntent(contentIntent)
                .setChannelId("notice");

        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel
                    (remoteMessage.getData().get("channel_id"), "채널", NotificationManager.IMPORTANCE_DEFAULT);

            nManager.createNotificationChannel(channel);
        }
        nManager.notify(0 /* ID of notification */, nBuilder.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d("Trav.Token", "onNewToken: " + s);

        sendTokenToServer(s);
    }

    private void sendTokenToServer(String token){

        Log.d(TAG, "sendTokenToServer: ");

    }


}