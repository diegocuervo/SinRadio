package com.example.diegocuervo.sinradio;

/**
 * Created by Diego Cuervo on 15/10/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoArranque extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context,  MyFirebaseMessagingService.class);
        context.startService(service);
    }

}
