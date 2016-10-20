package com.example.diegocuervo.sinradio;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Diego Cuervo on 03/08/2016.
 */
public class Viaje extends AppCompatActivity {
    private Activity actividad;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viajes);
        this.actividad=this;
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String baseUrl = "http://api.sin-radio.com.ar/chofer/"+id+"/viajes";
        new MyHttpGetRequest().execute(baseUrl);



    }






private class MyHttpGetRequest extends AsyncTask<String, Integer, String> {

    private String APP_TAG= "SinRadio-appChofer";
    protected String doInBackground(String... params) {
        BufferedReader in = null;
        String baseUrl = params[0];

        try {


            HttpClient httpClient = new DefaultHttpClient();

            HttpGet get = new HttpGet(baseUrl);

            HttpResponse response = httpClient.execute(get);
            Log.w(APP_TAG, response.getStatusLine().toString());

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
            return "Exception happened: " + e.getMessage();
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



        try {
            JSONArray array = new JSONArray(result);
            Integer cantidad = array.length();
            Integer k=0;
            Tabla tabla = new Tabla(actividad, (TableLayout) findViewById(R.id.tabla));
            tabla.agregarCabecera(R.array.cabecera_tabla);
            Log.w(APP_TAG, "Resultado obtenido " + result + cantidad);
            while(k<cantidad){
            JSONObject jsonObject = array.getJSONObject(k);





                ArrayList<String> elementos = new ArrayList<String>();

                elementos.add(jsonObject.getString("id"));
                elementos.add(jsonObject.getString("lat"));
                elementos.add(jsonObject.getString("lon"));
                elementos.add(jsonObject.getString("dir"));
                elementos.add(jsonObject.getString("monto"));
                tabla.agregarFilaTabla(elementos);
                k++;

            }
        }
            catch (JSONException e) {

                e.printStackTrace();

            }

    }

}
}
