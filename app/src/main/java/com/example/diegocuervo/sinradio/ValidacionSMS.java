package com.example.diegocuervo.sinradio;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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
public class ValidacionSMS extends AppCompatActivity {
    public Activity actividad;
    String sms;
    EditText num_text;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validacion_sms);
        this.actividad=this;



    }


    public void btn_validar(View view) {

        num_text = (EditText) findViewById(R.id.editText);

        sms =(String.valueOf(num_text.getText()));

        new HttpGetDemotel().execute(sms);


    }
    public class HttpGetDemotel extends AsyncTask<String, Void, String> {

        String sms;

        String result = "fail";
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            this.sms = params[0];

            return GetSomething();
        }

        final String GetSomething()
        {

            String url = "http://API.SIN-RADIO.COM.AR/chofer/sendSMS/"+id;
            BufferedReader inStream = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpRequest = new HttpPut(url);
                HttpResponse response = httpClient.execute(httpRequest);
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
                Toast.makeText(actividad, "no rompio dentro", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        Toast.makeText(actividad, "no rompio dentro", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        protected void onPostExecute(String page)
        {
            Log.w("chofer","Resultado obtenido " + result);
            Toast.makeText(actividad, result.toString(), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(actividad, ValidacionSMS.class);

            startActivity(i);
            Toast.makeText(actividad, "A continuación recibirá un codigo por SMS, igreselo para continuar", Toast.LENGTH_SHORT).show();
        }
    }


}
