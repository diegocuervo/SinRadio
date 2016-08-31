package com.example.diegocuervo.sinradio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Diego Cuervo on 11/08/2016.
 */
public class ProbandoServer extends AppCompatActivity {
    //Tag para el control de trazas de LOG

    //Constante String con la URL de la imagen a descargar

    //Boton de Descargar Imagen
    public Button btnDownload;
    //Image View para mostrar la Imagen Descargada


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.probandoserver);

        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccesoRemoto acceso = new AccesoRemoto();

                acceso.execute();
            }
        });

    }

    private class AccesoRemoto extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... argumentos) {

            StringBuffer bufferCadena = new StringBuffer("");

            try {
                HttpClient cliente = new DefaultHttpClient();
                HttpGet peticion = new HttpGet(
                        "http://192.168.0.103:3000/");
// ejecuta una petición get
                HttpResponse respuesta = cliente.execute(peticion);

//lee el resultado
                BufferedReader entrada = new BufferedReader(new InputStreamReader(
                        respuesta.getEntity().getContent()));

                String separador = "";
                String NL = System.getProperty("line.separator");
//almacena el resultado en bufferCadena

                while ((separador = entrada.readLine()) != null) {
                    bufferCadena.append(separador + NL);
                }
                entrada.close();
            } catch (Exception e) {
                Toast.makeText(ProbandoServer.this, "No Anda", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return bufferCadena.toString();

        }

        protected void onPostExecute(String mensaje) {
            Toast.makeText(ProbandoServer.this, "anda", Toast.LENGTH_SHORT).show();
            Toast.makeText(ProbandoServer.this, mensaje, Toast.LENGTH_SHORT).show();

        }

    }

}