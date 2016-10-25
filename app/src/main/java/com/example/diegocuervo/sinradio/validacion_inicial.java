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

import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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

        new HttpGetDemo().execute();
    }


    public void btn_validar(View view) {

          num_text = (EditText) findViewById(R.id.editText);
        numero =(String.valueOf(num_text.getText()));

        new HttpGetDemotel().execute(numero);

    }
    public class HttpGetDemotel extends AsyncTask<String, Void, String> {

       String num;

        String result = "fail";
        @Override
        protected String doInBackground(String... params) {

            this.num = params[0];

            return GetSomething();
        }

        final String GetSomething()
        {

            String url = "http://API.SIN-RADIO.COM.AR/chofer/sendSMS/"+num;
            BufferedReader inStream = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpRequest = new HttpGet(url);
                HttpResponse response = httpClient.execute(httpRequest);
                int resCode = response.getStatusLine().getStatusCode();

                if(resCode==404 || resCode==410){

                    Toast.makeText(actividad, "Problemas con la coneccion. Pruebe mas tarde.", Toast.LENGTH_SHORT).show();
                }
                inStream = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent()));

                StringBuffer buffer = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = inStream.readLine()) != null) {
                    buffer.append(line + NL);
                }
                inStream.close();

                result = buffer.toString();
            } catch (Exception e) {

                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        protected void onPostExecute(String page)
        { Toast.makeText(actividad, result.toString(), Toast.LENGTH_SHORT).show();

            if (result.toString().contains("OK")) {
                Toast.makeText(actividad, "A continuación recibirá un codigo por SMS, igreselo para continuar", Toast.LENGTH_SHORT).show();
                Log.w("chofer","Resultado obtenido " + result);
                Intent i = new Intent(actividad, ValidacionSMS.class);
                i.putExtra("numero",num );
                startActivity(i);
            }
            else {
                Toast.makeText(actividad, "El numero ingresado no se encuentra registrado en el sistema.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class HttpGetDemo extends AsyncTask<Number, Void, String> {
        Integer resCode;
        String nombre;
        String result="fails";
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        @Override
        protected String doInBackground(Number... params) {

            return GetSomething();
        }

        final String GetSomething()
        {
            String url = "http://API.SIN-RADIO.COM.AR/chofer/"+id;
            BufferedReader inStream = null;
            Log.w("chofer","Codigo que da " + id);
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpRequest = new HttpGet(url);
                HttpResponse response = httpClient.execute(httpRequest);
                 resCode = response.getStatusLine().getStatusCode();


                inStream = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent()));

                StringBuffer buffer = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = inStream.readLine()) != null) {
                    buffer.append(line + NL);
                }
                inStream.close();

                result = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return result;
        }

        protected void onPostExecute(String page)
        {
            Log.w("chofer","Resultado obtenido " + result.toString());
            if (resCode.equals(200)) {

                try {
                    JSONObject array = new JSONObject(result);

                    this.nombre = array.getString("nombre");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(actividad, "Bienvenido "+nombre, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(actividad, MainActivity.class);

                i.putExtra("nombre",nombre );
                startActivity(i);
            }
            else {
                Toast.makeText(actividad, "Su cuenta no esta verificada. Ingrese su numero de celular.", Toast.LENGTH_SHORT).show();

            }

        }
    }


}
