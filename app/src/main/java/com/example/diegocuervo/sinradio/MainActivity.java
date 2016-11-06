package com.example.diegocuervo.sinradio;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.ActivityChooserView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
private Timer timer;
public Activity actividad;
    private LocationManager locManager;
    private LocationListener locListener;
    private TextView nombre;
    private String nom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Estado_Singleton.getInstance().android_id=Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        nom= getIntent().getExtras().getString("nombre");
        nombre = (TextView)findViewById(R.id.textView);
        nombre.setText(nom);

        this.actividad=this;

        String baseUrl = "http://API.SIN-RADIO.COM.AR/chofer/token/"+Estado_Singleton.getInstance().android_id;
        new MyHttpPostRequestToken().execute(baseUrl);

        timer = new Timer();
        EnviarPosicion enviarPos = new EnviarPosicion();

        timer.scheduleAtFixedRate(enviarPos,5000,5000);
    }

    class EnviarPosicion extends TimerTask {
        public String APP_TAG = "SinRadio-Chofer";
        Double latitude;
        Double longitude;
        Integer estado;
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                   estado=Estado_Singleton.getInstance().estado_actual;





                    GPSTracker gps = new GPSTracker(actividad);


                    if(gps.canGetLocation()){

                        latitude = gps.getLatitude();
                        longitude= gps.getLongitude();

                    }else{

                        gps.showSettingsAlert();
                    }
                    Toast.makeText(actividad,latitude.toString(), Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put("lat", latitude);
                        jsonObject.put("lon", longitude);
                        jsonObject.put("estado", estado);

                    } catch (JSONException e) {

                        e.printStackTrace();

                    }
                    String data = jsonObject.toString();
                    String baseUrl = "http://API.SIN-RADIO.COM.AR/posicion/"+id;
                    new MyHttpPostRequest().execute(baseUrl, data);
                    Log.w(APP_TAG, "Mensaje cada 5 segundos de main activity "+latitude);
                }

        });
        }


    };
    private class MyHttpPostRequest extends AsyncTask<String, Integer, String> {
        Integer resCode =1;
        public String APP_TAG = "AppChofer";
        protected String doInBackground(String... params) {
            BufferedReader in = null;
            String baseUrl = params[0];
            String jsonData = params[1];



            try {
                JSONObject obj = new JSONObject(jsonData);

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost post = new HttpPost(baseUrl);

                List<NameValuePair> nvp = new ArrayList<NameValuePair>(3);
                nvp.add(new BasicNameValuePair("lat", obj.getString("lat")));
                nvp.add(new BasicNameValuePair("lon", obj.getString("lon")));
                nvp.add(new BasicNameValuePair("estado", obj.getString("estado")));

                post.setEntity(new UrlEncodedFormEntity(nvp,"UTF-8"));

                HttpResponse response = httpClient.execute(post);
                Log.w(APP_TAG, response.getStatusLine().toString());
                resCode = response.getStatusLine().getStatusCode();


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
                    return "Comienze a moverse para reportar posicion";
            }finally {
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

            Log.w(APP_TAG,resCode.toString());
            if(resCode.equals(1)) {

                Toast.makeText(actividad, result, Toast.LENGTH_SHORT).show();
            }

            if(resCode.equals(200)) {
                Toast.makeText(actividad, "Reportando Posicion.", Toast.LENGTH_SHORT).show();
            }

        }

    }


    public void btn_estado(View view) {
        Intent i = new Intent(this, Estado.class );

        startActivity(i);
    }

    public void btn_salir(View view) {

        moveTaskToBack(true);


    }

    public void btn_viaje(View view) {
        Intent i = new Intent(this, Viaje.class );
        startActivity(i);
    }

    public void btn_emergencia(View view) {

    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(true);

    }

    private class MyHttpPostRequestToken extends AsyncTask<String, Integer, String> {

        public String APP_TAG = "token_envio";
        Integer resCode = 1;

        protected String doInBackground(String... params) {
            BufferedReader in = null;
            String baseUrl = params[0];

            try {

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost post = new HttpPost(baseUrl);

                List<NameValuePair> nvp = new ArrayList<NameValuePair>(1);
                nvp.add(new BasicNameValuePair("token", FirebaseInstanceId.getInstance().getToken()));
                Log.w(APP_TAG, FirebaseInstanceId.getInstance().getToken());

                post.setEntity(new UrlEncodedFormEntity(nvp, "UTF-8"));

                HttpResponse response = httpClient.execute(post);
                Log.w(APP_TAG, response.getStatusLine().toString());
                resCode = response.getStatusLine().getStatusCode();


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
                return "ERROR:Verifique su coneccion a internet.";
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

            if(resCode!=1){

            }
            else{
                Toast.makeText(actividad,"ERROR:Verifique su coneccion a internet.", Toast.LENGTH_SHORT).show();
            }

        }

    }
}



