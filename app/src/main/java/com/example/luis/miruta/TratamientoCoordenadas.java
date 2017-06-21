package com.example.luis.miruta;

import android.content.Context;
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


   void tratarLnt(String j){


       List<String> lista_LatLong = new ArrayList<>();
       List<Double> listLat= new ArrayList<>();
       String ll;
       String [] lat;
       JSONArray re ;
       try {
           re = new JSONArray(j);
           for (int i = 0; i < re.length(); i++) {
               lista_LatLong.add(re.getString(i)); //creamos un objeto ruta y lo insertamos en la lista
           }


           for (int i = 0; i < lista_LatLong.size(); i++) {
//
               ll= lista_LatLong.get(0).replace("[","");
               ll= ll.replace("]","");
               ll= ll.replace("\"","");

               lat= ll.split(",");
//;
//

//
//                listLat.add(Double.parseDouble(lat));
//
           }




           // Toast.makeText(getBaseContext(),""+lat[0], Toast.LENGTH_LONG).show();
       }catch (Exception io){


       }



   }

    void tratarLng(String j){

        ArrayList<Double> arrayListLat;

    }

}
