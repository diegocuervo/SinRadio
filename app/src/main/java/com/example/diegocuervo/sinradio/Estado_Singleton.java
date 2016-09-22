package com.example.diegocuervo.sinradio;

/**
 * Created by Diego Cuervo on 22/09/16.
 */
public class Estado_Singleton {

    private static Estado_Singleton intancia= null;

    public int estado_actual;

    protected Estado_Singleton(){}

    public static synchronized Estado_Singleton getInstance(){
        if(null == intancia){
            intancia = new Estado_Singleton();
        }
        return intancia;
    }
}
