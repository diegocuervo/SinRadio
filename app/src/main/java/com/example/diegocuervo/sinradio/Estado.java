package com.example.diegocuervo.sinradio;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

/**
 * Created by Diego Cuervo on 02/08/2016.
 */
public class Estado  extends AppCompatActivity {
    private Activity actividad;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estados);
        this.actividad=this;
    }

    public void btn_libre(View view) {

        JSONObject jsonObject= new JSONObject();


        try {

            jsonObject.put("estadoElegido", "libre");
        }

        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        String data =  jsonObject.toString();
        String baseUrl = "http://sinradio.ddns.net:45507/";
        new MyHttpPostRequest().execute(baseUrl, data);
    }
    public void btn_ocupado(View view) {

        JSONObject jsonObject= new JSONObject();


        try {

            jsonObject.put("estadoElegido", "ocupado");
        }

        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        String data =  jsonObject.toString();
        String baseUrl = "http://sinradio.ddns.net:45507/";
        new MyHttpPostRequest().execute(baseUrl, data);
    }

    private class MyHttpPostRequest extends AsyncTask<String, Integer, String> {

        public String APP_TAG = "ECTUploadData";
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
                List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
                nvp.add(new BasicNameValuePair("evento", "cambioEstado"));
                nvp.add(new BasicNameValuePair("estado", obj.getString("estadoElegido")));

                // post.setHeader("Content-type", "application/json");
                post.setEntity(new UrlEncodedFormEntity(nvp,"UTF-8"));

                //Se ejecuta el envio de la peticion y se espera la respuesta de la misma.
                HttpResponse response = httpClient.execute(post);
                Log.w(APP_TAG, response.getStatusLine().toString());

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
                Log.w(APP_TAG, "no anda");
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
}
