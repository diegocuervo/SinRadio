package com.example.diegocuervo.sinradio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;

/**
 * Created by Diego Cuervo on 07/08/2016.
 */
public class Notificacion extends AppCompatActivity {
    int notificationID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_example);
    }

    public void onClick(View v){
        displayNotification();
    }

    protected void displayNotification(){
        Intent i = new Intent(this, NotificationView.class);
        i.putExtra("notificationID", notificationID);
        long[] pattern = new long[]{1000,1000,2000};

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent deleteIntent = PendingIntent.getActivity(this, 0, i, 0);
        PendingIntent shareIntent = PendingIntent.getActivity(this, 0, i, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        CharSequence ticker ="Tienes un Potencial Viaje Cerca!";
        CharSequence contentTitle = "Tienes un Potencial Viaje Cerca!";
        CharSequence contentText = "aca iria la ubicacion del viaje nuevo";
        Notification noti = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setTicker(ticker)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.taxi)
                .setLargeIcon((((BitmapDrawable)getResources()
                        .getDrawable(R.drawable.iconotaxi)).getBitmap()))
             //   .addAction(R.drawable.taxi, ticker, pendingIntent)

        .addAction(android.R.drawable.ic_menu_send, "Aceptar", deleteIntent)
        .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Rechazar", shareIntent)
                .setSound(defaultSound)
                .setLights(Color.BLUE, 1, 0)
                .setVibrate(pattern)
                .build();
        nm.notify(notificationID, noti);
    }

}