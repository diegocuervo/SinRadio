package com.example.diegocuervo.sinradio;
import android.app.Notification;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.TimerTask;

/**
 * Created by Diego Cuervo on 10/10/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String cuerpo;
    private String from;
    private RemoteMessage remoteMensage;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        remoteMensage = remoteMessage;
        cuerpo = remoteMessage.getFrom();
        from = remoteMessage.getNotification().getBody();
        sendNotification(remoteMessage.getNotification().getBody());

    }
        private void sendNotification(String messageBody) {
            int notificationID = 1;
            Intent i = new Intent(this, NotificationView.class);
            i.putExtra("notificationID", notificationID);
            long[] pattern = new long[]{1000,2000,2000};

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            PendingIntent deleteIntent = PendingIntent.getActivity(this, 0, i, 0);
            PendingIntent shareIntent = PendingIntent.getActivity(this, 0, i, 0);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            CharSequence ticker ="Tienes un Potencial Viaje Cerca!";
            CharSequence contentTitle = "Tienes un Potencial Viaje Cerca!";
            CharSequence contentText = "aca iria la ubicacion del viaje nuevo";
            Notification noti = new android.support.v7.app.NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setTicker(ticker)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.taxi)
                    .setLargeIcon((((BitmapDrawable)getResources()
                            .getDrawable(R.drawable.iconotaxi)).getBitmap()))
                    //  .addAction(R.drawable.taxi, ticker, pendingIntent)

                    .addAction(android.R.drawable.ic_menu_send, "Aceptar", deleteIntent)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Rechazar", shareIntent)
                    .setSound(defaultSound)
                    .setLights(Color.BLUE, 1, 0)
                    .setVibrate(pattern)
                    .build();
            nm.notify(notificationID, noti);
        }


    }

