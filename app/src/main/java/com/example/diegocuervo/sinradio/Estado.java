package com.example.diegocuervo.sinradio;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
    private TextView estado_enviado;
     String estado_text;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estados);
       String nom= Estado_Singleton.getInstance().chofer;
        TextView nombre = (TextView)findViewById(R.id.textView);
        nombre.setText("Chofer: "+nom);

        estado_text=Estado_Singleton.getInstance().estado_texto;
        estado_enviado = (TextView)findViewById(R.id.textView2);
        estado_enviado.setText("Estado: "+estado_text);
        this.actividad=this;
    }

    public void btn_libre(View view) {
        Estado_Singleton.getInstance().estado_texto="Libre";
        Estado_Singleton.getInstance().estado_actual=0;
        actividad.onBackPressed();

    }
    public void btn_ocupado(View view) {
        Estado_Singleton.getInstance().estado_texto="Ocupado";
        Estado_Singleton.getInstance().estado_actual=1;
        actividad.onBackPressed();
    }

    public void btn_llegando(View view) {
        Estado_Singleton.getInstance().estado_texto="LLegando";
        Estado_Singleton.getInstance().estado_actual=2;
        actividad.onBackPressed();
    }
    public void btn_esperando(View view) {
        Estado_Singleton.getInstance().estado_texto="Esperando";
        Estado_Singleton.getInstance().estado_actual=3;

        actividad.onBackPressed();
    }


    public void btn_inhabilitado(View view) {
        Estado_Singleton.getInstance().estado_texto="Inhabilitado";
        Estado_Singleton.getInstance().estado_actual=4;
        actividad.onBackPressed();
    }

}
