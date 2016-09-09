package com.example.diegocuervo.sinradio;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

/**
 * Created by Diego Cuervo on 03/09/2016.
 */
public class Validacion_inicial extends AppCompatActivity {
    public Activity actividad;
    String numero;
    EditText num_text;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validacion_inicial);
        this.actividad=this;

        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        JSONObject jsonObject = new JSONObject();


        try {
            jsonObject.put("numero_cel", 0);
            jsonObject.put("android_id", id);
        } catch (JSONException e) {

            e.printStackTrace();

        }
        String data = jsonObject.toString();
        String baseUrl = "http://sinradio.ddns.net:45507/";
        new MyHttpPostRequest().execute(baseUrl, data);
        SystemClock.sleep(1000);

    }


    public void btn_validar(View view) {

          num_text = (EditText) findViewById(R.id.editText);
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        numero =(String.valueOf(num_text.getText()));


        JSONObject jsonObject = new JSONObject();


        try {

            jsonObject.put("numero_cel", numero);
            jsonObject.put("android_id", id);
        } catch (JSONException e) {

            e.printStackTrace();

        }
        String data = jsonObject.toString();
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

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost post = new HttpPost(baseUrl);


                List<NameValuePair> nvp = new ArrayList<NameValuePair>(3);
                nvp.add(new BasicNameValuePair("evento", "validar"));
                nvp.add(new BasicNameValuePair("numero_cel", obj.getString("numero_cel")));
                nvp.add(new BasicNameValuePair("android_id", obj.getString("android_id")));

                post.setEntity(new UrlEncodedFormEntity(nvp,"UTF-8"));


                HttpResponse response = httpClient.execute(post);
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
                return "Error al conectarse al servidor. Verifique su coneccion a internet " + e.getMessage();
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

                if(result==0){
                    Toast.makeText(actividad, result.toString(), Toast.LENGTH_SHORT).show();
                }
            Toast.makeText(actividad, result.length(), Toast.LENGTH_SHORT).show();
          //  Toast.makeText(actividad, result.toString(), Toast.LENGTH_SHORT).show();
                try {
                    JSONArray array = new JSONArray(result);

                    JSONObject jsonObject = array.getJSONObject(0);
                    String apellido = jsonObject.getString("apellido");
                    String nombre = jsonObject.getString("nombre");


                    Toast.makeText(actividad, "Validacion Exitosa!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(actividad, "Bienvenido " + nombre + " " + apellido, Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(actividad, MainActivity.class);
                        i.putExtra("apellido", apellido);
                        i.putExtra("nombre", nombre);
                        startActivity(i);

                } catch (JSONException e) {
                     e.printStackTrace();

                }


        }

    }

}
