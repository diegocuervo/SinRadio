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

import java.util.Map;
import java.util.TimerTask;

/**
 * Created by Diego Cuervo on 10/10/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private Map<String, String> cuerpo;
    private String from;
    private String titulo;
    private RemoteMessage remoteMensage;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        remoteMensage = remoteMessage;
        from = remoteMessage.getFrom();
        cuerpo = remoteMessage.getData();
        titulo = remoteMessage.getNotification().getTitle();

       String id_viaje= cuerpo.get("idViaje");
        Log.w("cuerpo noti", id_viaje);
        sendNotification(id_viaje,titulo);

    }
        private void sendNotification(String id_viaje,String titulo) {
            int notificationID = 1;
            Log.w("cuerpo noti", id_viaje);
            Intent i = new Intent(this, BroadcastNotificacionAcept.class);
            i.putExtra("notificationID", notificationID);
            i.putExtra("id_vi",id_viaje );
            Intent c = new Intent(this, BroadcastNotificacionCancel.class);
            c.putExtra("notificationID", notificationID);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent aceptIntent = PendingIntent.getBroadcast(this, 0,  i , PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent cancelIntent = PendingIntent.getBroadcast(this, 0,  c , 0);
            long[] pattern = new long[]{1000,2000,2000};

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            PendingIntent deleteIntent = PendingIntent.getActivity(this, 0, i, 0);


            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            CharSequence ticker ="Nuevo Viaje:"+titulo;
            CharSequence contentTitle = "Nuevo Viaje:"+titulo;
            CharSequence contentText = "aca iria la ubicacion del viaje nuevo";
            Notification noti = new android.support.v7.app.NotificationCompat.Builder(this)

                    .setTicker(ticker)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.taxi)
                    .setLargeIcon((((BitmapDrawable)getResources()
                            .getDrawable(R.drawable.iconotaxi)).getBitmap()))
                    //  .addAction(R.drawable.taxi, ticker, pendingIntent)

                    .addAction(android.R.drawable.ic_menu_send, "Aceptar", aceptIntent)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Rechazar", cancelIntent)
                    .setSound(defaultSound)
                    .setLights(Color.BLUE, 1, 0)
                    .setVibrate(pattern)
                    .build();
            nm.notify(notificationID, noti);
        }


    }
