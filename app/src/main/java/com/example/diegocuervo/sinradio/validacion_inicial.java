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
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        numero =(String.valueOf(num_text.getText()));

        new HttpGetDemotel().execute(numero);

        /*JSONObject jsonObject = new JSONObject();


        try {

            jsonObject.put("numero_cel", numero);
            jsonObject.put("android_id", id);
        } catch (JSONException e) {

            e.printStackTrace();

        }
        String data = jsonObject.toString();
        String baseUrl = "http://sinradio.ddns.net:45507/";
        new MyHttpPostRequest().execute(baseUrl, data);*/


    }
    public class HttpGetDemotel extends AsyncTask<String, Void, String> {

       String num;

        String result = "fail";
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
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

    public class HttpGetDemo extends AsyncTask<Number, Void, String> {
        String resultado;
        String result = "fail";
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        @Override
        protected String doInBackground(Number... params) {
            // TODO Auto-generated method stub

            return GetSomething();
        }

        final String GetSomething()
        {
            String url = "http://API.SIN-RADIO.COM.AR/chofer/"+id;
            BufferedReader inStream = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpRequest = new HttpGet(url);
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
            resultado=result;
            return result;
        }

        protected void onPostExecute(String page)
        {
            Log.w("chofer","Resultado obtenido " + result);
            Toast.makeText(actividad, result.toString(), Toast.LENGTH_SHORT).show();


        }
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


                List<NameValuePair> nvp = new ArrayList<NameValuePair>(1);
               // nvp.add(new BasicNameValuePair("evento", "validar"));
               // nvp.add(new BasicNameValuePair("numero_cel", obj.getString("numero_cel")));
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
                return "Error al conectarse al servidor. Verifique su coneccion a internet" + e.getMessage();

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


                 //   Toast.makeText(actividad, result.toString(), Toast.LENGTH_SHORT).show();

         //   Toast.makeText(actividad, result.length(), Toast.LENGTH_SHORT).show();
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
                    if (numero != "0") {
                        Toast.makeText(actividad, "Erro:No existe ese numero en nuestra base de datos. Consulte con la central.", Toast.LENGTH_SHORT).show();
                    }
                }


        }

    }

}
