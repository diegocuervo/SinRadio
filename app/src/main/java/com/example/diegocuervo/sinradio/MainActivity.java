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
    public Integer estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        nom= getIntent().getExtras().getString("nombre");
        nombre = (TextView)findViewById(R.id.textView);
        nombre.setText(nom);

        this.actividad=this;
        timer = new Timer();
        EnviarPosicion enviarPos = new EnviarPosicion();

        timer.scheduleAtFixedRate(enviarPos,0,5000);


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
                    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    Location loc =
                            locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateWithNewLocation(loc);
                    locListener = new LocationListener() {
                        public void onLocationChanged(Location location) {
                            updateWithNewLocation(location);
                        }
                        public void onProviderDisabled(String provider){

                        }
                        public void onProviderEnabled(String provider){

                        }
                        public void onStatusChanged(String provider, int status, Bundle extras){

                        }
                    };
                    locManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 1000, 0,
                            locListener);


                    JSONObject jsonObject = new JSONObject();


                    try {

                        jsonObject.put("lat", latitude);
                        jsonObject.put("lon", longitude);
                        jsonObject.put("estado", estado);

                    } catch (JSONException e) {

                        e.printStackTrace();

                    }
                    String data = jsonObject.toString();
                    String baseUrl = "http://API.SIN-RADIO.COM.AR/posicion/:"+id;
                    new MyHttpPostRequest().execute(baseUrl, data);
                    Log.w(APP_TAG, "Mensaje cada 5 segundos de main activity "+latitude);


                    Toast.makeText(actividad, "longitud "+longitude, Toast.LENGTH_SHORT).show();

                }

        });
        }

    private void updateWithNewLocation(Location location) {
        String latLongString = "";
        try {
            if (location != null) {

                Log.e("test", "gps is on send");
                latitude = (location.getLatitude());
                longitude = (location.getLongitude());
                Log.w(APP_TAG, "Mensaje cada 5 segundos de main activity "+latitude);
                Log.e("test", "location send");

               // locManager.removeUpdates(locationListener);


                latLongString = "Lat:" + latitude + "\nLong:" + longitude;
                Log.w("CurrentLocLatLong", latLongString);
            } else {
                latLongString = "No location found";
            }
        } catch (Exception e) {
        }

    }

    /*private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };*/
    };
    private class MyHttpPostRequest extends AsyncTask<String, Integer, String> {

        public String APP_TAG = "AppChofer";
        protected String doInBackground(String... params) {
            BufferedReader in = null;
            String baseUrl = params[0];
            String jsonData = params[1];



            try {
                JSONObject obj = new JSONObject(jsonData);
                //Creamos un objeto Cliente HTTP para manejar la peticion al servidor
                HttpClient httpClient = new DefaultHttpClient();
                //Creamos objeto para armar peticion de tipo HTTP POST
                HttpPost post = new HttpPost(baseUrl);

                //Configuramos los parametos que vaos a enviar con la peticion HTTP POST
                List<NameValuePair> nvp = new ArrayList<NameValuePair>(3);
                nvp.add(new BasicNameValuePair("lat", obj.getString("lat")));
                nvp.add(new BasicNameValuePair("lon", obj.getString("lon")));
                nvp.add(new BasicNameValuePair("estado", obj.getString("estado")));

                // post.setHeader("Content-type", "application/json");
                post.setEntity(new UrlEncodedFormEntity(nvp,"UTF-8"));

                //Se ejecuta el envio de la peticion y se espera la respuesta de la misma.
                HttpResponse response = httpClient.execute(post);
                Log.w(APP_TAG, response.getStatusLine().toString());
                int resCode = response.getStatusLine().getStatusCode();

                if(resCode==404 || resCode==410){

                    Toast.makeText(actividad, "Problemas con la coneccion. Pruebe mas tarde.", Toast.LENGTH_SHORT).show();
                }
                //Obtengo el contenido de la respuesta en formato InputStream Buffer y la paso a formato String
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
                return "Comienze a moverse para reportar posicion" + e.getMessage();
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
            //Se obtiene el progreso de la peticion
            Log.w(APP_TAG,"Indicador de pregreso " + progress[0].toString());
        }

        protected void onPostExecute(String result) {
            //Se obtiene el resultado de la peticion Asincrona
            Log.w(APP_TAG,"Resultado obtenido " + result);
       /* try {
            JSONArray array = new JSONArray(result);

            JSONObject jsonObject = array.getJSONObject(0);


            Log.w(APP_TAG,"Anduvo el parseo puto? " + jsonObject.getString("apellido"));
            Toast.makeText(actividad, jsonObject.getString("apellido"), Toast.LENGTH_SHORT).show();
        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }*/

            Toast.makeText(actividad, result, Toast.LENGTH_SHORT).show();


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
        Toast.makeText(actividad,FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_SHORT).show();
        Log.w("asd",FirebaseInstanceId.getInstance().getToken());
        Intent i = new Intent(this, Notificacion.class );
        startActivity(i);
    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
    }

}
