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
    String numero;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validacion_sms);
        this.actividad=this;
        numero= getIntent().getExtras().getString("numero");
    }

    public void btn_validar(View view) {

        num_text = (EditText) findViewById(R.id.editText);
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        sms =(String.valueOf(num_text.getText()));
        JSONObject jsonObject = new JSONObject();


        try {

            jsonObject.put("tel", numero);
            jsonObject.put("claveSMS", sms);

        } catch (JSONException e) {

            e.printStackTrace();

        }


       String baseUrl= "http://API.SIN-RADIO.COM.AR/chofer/"+id;
        String data = jsonObject.toString();
        new HttpGetDemotel().execute(baseUrl, data);


    }
    public class HttpGetDemotel extends AsyncTask<String, Void, String> {

        Integer resCode=1;
        String result;

        @Override
        protected String doInBackground(String... params) {

            String baseUrl = params[0];
            String jsonData = params[1];


            String url = baseUrl;
            BufferedReader inStream = null;


            try {

                JSONObject obj = new JSONObject(jsonData);
                Log.w("chofer","mando " +  obj.getString("tel"));
                Log.w("chofer","mando " +  obj.getString("claveSMS"));
                List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
                nvp.add(new BasicNameValuePair("tel", obj.getString("tel")));
                nvp.add(new BasicNameValuePair("claveSMS", obj.getString("claveSMS")));

                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(url);
                httpPut.setEntity(new UrlEncodedFormEntity(nvp,"UTF-8"));
                HttpResponse response = httpClient.execute(httpPut);
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
                result="ERROR:Verifique su coneccion a internet.";
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
            Log.w("chofer","Resultado obtenido en el put " + result);
            if(resCode!=1) {

                if (resCode.equals(200)) {

                    Intent i = new Intent(actividad, Validacion_inicial.class);
                    startActivity(i);


                } else {
                    Toast.makeText(actividad, "El codigo ingresado no es correcto.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(actividad, result, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
