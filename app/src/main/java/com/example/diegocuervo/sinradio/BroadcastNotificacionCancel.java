package com.example.diegocuervo.sinradio;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Diego Cuervo on 23/10/16.
 */
public class BroadcastNotificacionCancel extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Bundle answerBundle = intent.getExtras();
        int notificationID = answerBundle.getInt("notificationID");
        String s = Context.NOTIFICATION_SERVICE;
        NotificationManager mNM = (NotificationManager) context.getSystemService(s);
        mNM.cancel(notificationID);



    }
}