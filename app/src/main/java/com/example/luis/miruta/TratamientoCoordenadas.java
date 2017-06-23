package com.example.luis.miruta;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ADMIN on 21/06/2017.
 */

public class TratamientoCoordenadas {

    ArrayList<String> puntos = new ArrayList<>();



   public ArrayList<String> tratarPuntos(String j,double latUsu, double lngUsu){
            JSONArray re;
       ArrayList<String> puntos = new ArrayList<>();
       ArrayList<String> cadenaPuntos = new ArrayList<>();
        String ll;
       try {
           re = new JSONArray(j);
           for (int i = 0; i < re.length(); i++) {
               puntos.add(re.getString(i)); //creamos un objeto ruta y lo insertamos en la lista
           }
           for (int i = 0; i < puntos.size(); i++) {

               ll= puntos.get(i).replace("[","");
               ll= ll.replace("]","");
               ll= ll.replace("\"","");

               String [] lat= ll.split(",");

               double distanci = distanciaUsu(latUsu,lngUsu,Double.parseDouble(lat[5]),Double.parseDouble(lat[6]));
               Double tiempo = distanci/Double.parseDouble(lat[9]);

               String datos = lat[2]+","+lat[5]+","+lat[6]+","+String.valueOf(distanci)+","+tiempo+","+lat[9];//  placa - latitud - logitud - distancia- tiempo - velocidad


               cadenaPuntos.add(datos);
           }
           //  Toast.makeText(getBaseContext(),">"+listLat.get(1), Toast.LENGTH_SHORT).show();


          // return cadenaPuntos;
       }catch (Exception io){


       }
       return cadenaPuntos;

   }

    public double distanciaUsu(double lat1, double lon1, double lat2, double lon2) {
        double haverdistanceKM;
        double Rad = 6372.8; //Earth's Radius In kilometers
        // TODO Auto-generated method stub
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return haverdistanceKM = (Rad * c);

    }


}
