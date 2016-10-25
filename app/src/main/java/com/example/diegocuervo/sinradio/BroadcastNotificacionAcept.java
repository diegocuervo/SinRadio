package com.example.diegocuervo.sinradio;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego Cuervo on 23/10/16.
 */
public class BroadcastNotificacionAcept extends BroadcastReceiver {
    Context contexto;
    public void onReceive(Context context, Intent intent) {
        Bundle answerBundle = intent.getExtras();
        int notificationID = answerBundle.getInt("notificationID");
        String  s = Context.NOTIFICATION_SERVICE;
        NotificationManager mNM = (NotificationManager) context.getSystemService(s);
        mNM.cancel(notificationID);
        String id_viaje = answerBundle.getString("id_vi");
    this.contexto=context;

        Log.w("agarro parametro", answerBundle.getString("id_vi"));


        String baseUrl = "http://API.SIN-RADIO.COM.AR/viaje/"+id_viaje;
        new MyHttpPostRequestAceptaViaje().execute(baseUrl);
    }

    private class MyHttpPostRequestAceptaViaje extends AsyncTask<String, Integer, String> {

        public String APP_TAG = "acepto_viaje";
        protected String doInBackground(String... params) {
            BufferedReader in = null;
            String baseUrl = params[0];




            try {

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost post = new HttpPost(baseUrl);

                List<NameValuePair> nvp = new ArrayList<NameValuePair>(1);
                nvp.add(new BasicNameValuePair("android_id",  Estado_Singleton.getInstance().android_id));


                // post.setHeader("Content-type", "application/json");
                post.setEntity(new UrlEncodedFormEntity(nvp,"UTF-8"));

                HttpResponse response = httpClient.execute(post);
                Log.w(APP_TAG, response.getStatusLine().toString());
                int resCode = response.getStatusLine().getStatusCode();

                if(resCode==404 || resCode==410){

                    //  Toast.makeText(getApplicationContext(), "Problemas con la coneccion. Pruebe mas tarde.", Toast.LENGTH_SHORT).show();
                }

                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }
                in.close();
                return sb.toString();

            } catch (Exception e) {
                return "confirmaa ao no" + e.getMessage();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        protected void onProgressUpdate(Integer... progress) {

            Log.w(APP_TAG,"Indicador de pregreso " + progress[0].toString());
        }

        protected void onPostExecute(String result) {
            Log.w(APP_TAG,"Resultado obtenido " + result);
            Toast.makeText(contexto, result, Toast.LENGTH_SHORT).show();


        }

    }
}
